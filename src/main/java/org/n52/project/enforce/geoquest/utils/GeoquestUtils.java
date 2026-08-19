package org.n52.project.enforce.geoquest.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImages;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImagesPK;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImagesRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.n52.project.enforce.geoquest.remote.ApiClient;
import org.n52.project.enforce.geoquest.remote.ApiException;
import org.n52.project.enforce.geoquest.remote.api.QuestSurveySubmissionApi;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestCoordinate;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestImageDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class GeoquestUtils {

    private GeoquestSubmissionsRepository geoquestSubmissionsRepository;

    private GeoquestImagesRepository geoquestImagesRepository;

    private WKTReader wktReader;

    private ObjectMapper objectMapper;

    DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd'T'HH:MM:SS");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);

    ZoneId zoneIdEuropeRome = ZoneId.of("Europe/Rome");

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private HttpClient httpClient;

    private ApiClient apiClient;

    private QuestSurveySubmissionApi questSurveySubmissionApi;

    private static Logger LOG = LoggerFactory.getLogger(GeoquestUtils.class);

    public GeoquestUtils(GeoquestSubmissionsRepository geoquestSubmissionsRepository,
            GeoquestImagesRepository geoquestImagesRepository, Environment environment) {
        this.geoquestSubmissionsRepository = geoquestSubmissionsRepository;
        this.geoquestImagesRepository = geoquestImagesRepository;
        wktReader = new WKTReader(geometryFactory);
        objectMapper = new ObjectMapper();
        apiClient = new ApiClient();
        apiClient.getHttpClient().register(ResponseInterceptor.class);
        apiClient.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        try {
            apiClient.setBasePath(environment.getProperty("geoquest.basepath"));
            apiClient.setAccessToken(environment.getProperty("geoquest.token"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        questSurveySubmissionApi = new QuestSurveySubmissionApi(apiClient);
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClient.newHttpClient();
        }
        return httpClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public HttpRequest createRequest(String uri) {
        return HttpRequest.newBuilder().uri(URI.create(uri)).header("Authorization", "Bearer " + Token.getToken())
                .build();
    }

    public void getSubmissions(UUID questId) throws ApiException {
        IIASAGeoQuestQuestQuestSurveySubmissions subs =
                questSurveySubmissionApi.apiQuestsQuestIdSubmissionGet(questId, null, null, null);
        List<IIASAGeoQuestQuestQuestSurveySubmissionDto> subslist = subs.getSubmissions();

        for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : subslist) {
            createSubmissions(questId, iiasaGeoQuestQuestQuestSurveySubmissionDto);
        }
    }

    private GeoquestSubmissions createSubmissions(JsonNode input) {

        GeoquestSubmissions data = new GeoquestSubmissions();
        data.setQuestSurveySubmissionId(UUID.fromString(input.get("id").asText()));
        String submissonDataString = input.get("submissionData").asText().replace("\\\"", "\"");
        JsonNode submissionDataJson;
        data.setCoordinate(createPoint(input.get("location")));
        try {
            submissionDataJson = objectMapper.reader().readTree(submissonDataString);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return data;
        }
        JsonNode date = submissionDataJson.get("dateSurveyCreation");
        data.setCreationTime(LocalDateTime.from(formatter.parse(date.asText())));
        JsonNode reportType = submissionDataJson.get("selectType");
        if (reportType instanceof ArrayNode) {
            data.setReportType(((ArrayNode) reportType).elements().next().asText());
        } else {
            data.setReportType(reportType.asText());
        }
        data.setSubmissionData(submissonDataString);
        data = geoquestSubmissionsRepository.saveAndFlush(data);
        LOG.info("Added submission with query id: " + data.getQuestSurveySubmissionId());
        return data;
    }

    private GeoquestSubmissions createSubmissions(UUID questId,
            IIASAGeoQuestQuestQuestSurveySubmissionDto input) {

        GeoquestSubmissions data = new GeoquestSubmissions();
        UUID submissionId = input.getId();
        data.setQuestSurveySubmissionId(submissionId);
        Object submissionDataObj = input.getSubmissionData();
        String submissionDataString = "";
        if (submissionDataObj != null && (submissionDataObj instanceof String)) {
            submissionDataString = ((String) submissionDataObj).replace("\\\"", "\"");
        }
        JsonNode submissionDataJson;
        data.setCoordinate(createPoint(input.getLocation()));
        try {
            submissionDataJson = objectMapper.reader().readTree(submissionDataString);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return data;
        }
        data.setCreationTime(input.getCreationTime());
        JsonNode reportType = submissionDataJson.get("reportType");
        if (reportType instanceof ArrayNode) {
            data.setReportType(((ArrayNode) reportType).elements().next().asText());
        } else {
            if (reportType != null) {
                data.setReportType(reportType.asText());
            }
        }
        data.setAssignedScore(input.getAssignedScore());
        data.setCreatorId(input.getCreatorId());
        data.setLastModificationTime(input.getLastModificationTime());
        data.setSubmissionData(submissionDataString);
        data.setLastModifierId(input.getLastModifierId());
        data.setStatus(input.getStatus() != null ? input.getStatus().getValue() : "");
        data.setUserName(input.getUserName());

        int imageCount = input.getImageCount();

        if (imageCount > 0) {
            try {
                setImageUrls(data, questId, submissionId);
            } catch (ApiException e) {
                LOG.error("Could not set images for submission: " + submissionId);
                LOG.error(e.getMessage());
            }
        }
        data.setImageCount(imageCount);
        data = geoquestSubmissionsRepository.saveAndFlush(data);
        LOG.info("Added submission with query id: " + data.getQuestSurveySubmissionId());
        return data;
    }

    private void setImageUrls(GeoquestSubmissions data,
            UUID questId,
            UUID submissionId) throws ApiException {
        Set<GeoquestImages> geoquestImages = new HashSet<>();
        List<IIASAGeoQuestQuestImageDto> submissionImages =
                questSurveySubmissionApi.apiQuestsQuestIdSubmissionSubmissionIdImagesGet(questId, submissionId);
        for (IIASAGeoQuestQuestImageDto iiasaGeoQuestQuestImageDto : submissionImages) {
            GeoquestImages geoquestImage = new GeoquestImages();
            geoquestImage.setGeoquestImagesPK(new GeoquestImagesPK(iiasaGeoQuestQuestImageDto.getId(),
                    iiasaGeoQuestQuestImageDto.getQuestSurveySubmissionId()));
            geoquestImage.setBase64Data(iiasaGeoQuestQuestImageDto.getBase64Data());
            geoquestImage.setUrl(iiasaGeoQuestQuestImageDto.getUrl());
            geoquestImage.setCreationTime(iiasaGeoQuestQuestImageDto.getCreationTime());
            geoquestImage.setCreatorId(iiasaGeoQuestQuestImageDto.getCreatorId());
            geoquestImage.setLastModificationTime(iiasaGeoQuestQuestImageDto.getLastModificationTime());
            geoquestImage.setLastModifierId(iiasaGeoQuestQuestImageDto.getLastModifierId());
            geoquestImages.add(geoquestImage);
            geoquestImagesRepository.saveAndFlush(geoquestImage);
        }
        data.setImages(geoquestImages);

    }

    private Point createPoint(List<IIASAGeoQuestQuestCoordinate> location) {
        if (location != null) {
            if (!location.isEmpty()) {
                IIASAGeoQuestQuestCoordinate firstLocation = location.get(0);
                Double lat = firstLocation.getYLat();
                Double lng = firstLocation.getXLng();
                return geometryFactory.createPoint(new Coordinate(lat, lng));
            }
        }
        return geometryFactory.createPoint(new Coordinate(0, 0));
    }

    private Point createPoint(JsonNode location) {
        if (location != null) {
            if (location.isArray()) {
                JsonNode firstLocation = ((ArrayNode) location).elements().next();
                String latStrg = firstLocation.get("yLat").asText();
                String lngStrg = firstLocation.get("xLng").asText();
                double lat = Double.parseDouble(latStrg);
                double lng = Double.parseDouble(lngStrg);
                return geometryFactory.createPoint(new Coordinate(lat, lng));
            }
        }
        return geometryFactory.createPoint(new Coordinate(0, 0));
    }

}
