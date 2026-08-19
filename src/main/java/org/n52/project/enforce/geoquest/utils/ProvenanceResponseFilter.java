package org.n52.project.enforce.geoquest.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Component
public class ProvenanceResponseFilter implements ClientResponseFilter {

    private static Logger LOG = LoggerFactory.getLogger(ProvenanceResponseFilter.class);    
    
    private ProvenanceService provenanceService;
    
//    @Autowired
//    public ProvenanceResponseFilter(ProvenanceService provenanceService) {
//        this.provenanceService = provenanceService;
//    }
    
    public void setProvenanceService(ProvenanceService provenanceService) {
        this.provenanceService = provenanceService;
    }

    @Override
    public void filter(ClientRequestContext requestContext,
            ClientResponseContext context) throws IOException {
        LOG.info("FILTERED");
//        InputStream originalStream = context.getEntityStream();
//        
//        try {
//            UUID uuid = UUID.randomUUID();
//            File dataDirectory = new File("C:\\Users\\bpros\\Documents\\tmp\\" + System.currentTimeMillis());
//            dataDirectory.mkdir();
//            File file = new File(dataDirectory.getAbsolutePath() + "/" + uuid);
//            try (BufferedInputStream in = new BufferedInputStream(originalStream);
//                    FileOutputStream fileOutputStream = new FileOutputStream(file)) {
//                byte dataBuffer[] = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//                    fileOutputStream.write(dataBuffer, 0, bytesRead);
//                }
//            } catch (IOException e) {
//                // handle exception
//            }
//            byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
//            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
//            String checksum = new BigInteger(1, hash).toString(16);
//            context.setEntityStream(new FileInputStream(file));
//        } catch (NoSuchAlgorithmException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        try {
//            provenanceService.storeInitialData(originalStream);
//        } catch (NoSuchAlgorithmException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        context.getEntityStream().reset();
//        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        context.setOutputStream(baos);
//        try {
//            context.proceed();
//        } finally {
//            // search in the response, e.g.
//            JsonNode response = new ObjectMapper().readTree(baos.toByteArray());
//            LOG.info("Intercepted");
//            // ...
//            // write to and restore the original Stream
//            baos.writeTo(originalStream);
//            baos.close();
//            context.setOutputStream(originalStream);
//        }

    }

}
