package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
@ConfigurationProperties
public class GeoquestApiFetcher {

    private URL outputDataUrl;

    private URL updateUrl;

    private ObjectMapper mapper;

    private String urlSpec;

    private GeoquestUtils utils;

    private boolean initialize = false;

    private ArrayNode questMapping;
    
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

    private static Logger LOG = LoggerFactory.getLogger(GeoquestApiFetcher.class);

    public GeoquestApiFetcher(GeoquestSubmissionsRepository dataRepository, GeoquestUtils utils, Environment environment) {

        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        this.utils = utils;
        urlSpec = environment.getProperty("geoquest.url.spec");
        initialize = Boolean.parseBoolean(environment.getProperty("geoquest.db.initialize"));
        String questMappingString = environment.getProperty("geoquest.quests.mapping");
        try {
            questMapping = (ArrayNode) mapper.readTree(questMappingString);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return;
        }
        try {
            updateUrl = new URI(String.format(environment.getProperty("geoquest.update.url.spec"),
                    dateTimeFormatter.format(OffsetDateTime.now()))).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            LOG.error(e.getMessage());
        }

        try {
            outputDataUrl = findEndpoint();
        } catch (IOException | URISyntaxException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

        if (initialize) {
            // fetch 1000 observations
            if (LOG.isInfoEnabled()) {
                LOG.info("Initialize database.");
            }
            for (int i = 1; i < 21; i++) {
                String initialUrlSpecString = String.format(environment.getProperty("geoquest.initialize.url.spec"), i);
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Fetching data from: %s.", initialUrlSpecString));
                }
                try {
                    fetchAndStoreData(new URI(initialUrlSpecString).toURL());
                    Thread.sleep(10000);
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }
        }

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
        Runnable runnableTask = () -> {
            try {
                fetchAndStoreData();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        };

        long initialDelay = 0;
        long period = Long.valueOf(environment.getProperty("geoquest.execution.period"));
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Starting ScheduledExecutorService with initialDelay: %d and period: %d.",
                    initialDelay, period));
        }
//        ses.scheduleAtFixedRate(runnableTask, initialDelay, period, TimeUnit.SECONDS);

        Runnable updateRunnableTask = () -> {
            try {
                checkForUpdates();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        };

        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Starting update task with initialDelay: %d and period: 5 hours.", initialDelay));
        }
        ses.scheduleAtFixedRate(updateRunnableTask, 1, 5, TimeUnit.HOURS);
    }

    private void checkForUpdates() throws Exception {
        utils.getSubmissions(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"));
        LOG.info("Fetched output.");
    }

    public void fetchAndStoreData() throws Exception {
        fetchAndStoreData(outputDataUrl);
    }

    private void fetchAndStoreData(URL url) throws Exception {
        for (JsonNode jsonNode : questMapping) {
            try {
                String name = jsonNode.get("name").asText();
                String id = jsonNode.get("quest_id").asText(); 
                utils.getSubmissions(UUID.fromString(id));
                LOG.info("Fetched output for " + name);                
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }

    }

    public <T> T get(URL url,
            Class<T> type) throws StreamReadException, DatabindException, IOException {
        return mapper.readValue(url, type);
    }

    URL findEndpoint() throws MalformedURLException, IOException, URISyntaxException {
        URLConnection urlConnection = null;
        URL outputDataUrl = new URI(urlSpec).toURL();
        LOG.info("Checking endpoint: " + urlSpec);
        urlConnection = outputDataUrl.openConnection();
        urlConnection.setReadTimeout(3000);
        try {
            urlConnection.connect();
        } catch (Exception e) {
            LOG.info("Endpoint not available.", e);
        }
        if (urlConnection instanceof HttpURLConnection
                && ((HttpURLConnection) urlConnection).getResponseCode() == 200) {
            LOG.info("Found endpoint at: " + urlSpec);
        }
        return outputDataUrl;
    }

}
