package org.n52.project.enforce.geoquest.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

@Provider
@Component
public class ResponseInterceptor implements ReaderInterceptor {
    
    @Autowired
    private ProvenanceService provenanceService;
    
    private static Logger LOG = LoggerFactory.getLogger(ResponseInterceptor.class);
    
    public void setProvenanceService(ProvenanceService provenanceService) {
        this.provenanceService = provenanceService;
    }
    
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) 
      throws IOException, WebApplicationException {
        InputStream is = context.getInputStream();
//        LOG.info("INTERCEPTED");
        LOG.info(""+(provenanceService == null));
//        OutputStream originalStream = context.getOutputStream();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("".getBytes());
//        context.setInputStream(byteArrayInputStream);
        try {
//            context.proceed();
        } finally {
            // search in the response, e.g.
            UUID uuid = UUID.randomUUID();
            File dataDirectory = new File("C:\\Users\\bpros\\Documents\\tmp\\" + System.currentTimeMillis());
            dataDirectory.mkdir();
            File file = new File(dataDirectory.getAbsolutePath() + "/" + uuid);
            try (BufferedInputStream in = new BufferedInputStream(is);
                    FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // handle exception
                return false;
            }
            byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            byte[] hash;
            try {
                hash = MessageDigest.getInstance("MD5").digest(data);
                String checksum = new BigInteger(1, hash).toString(16);
                LOG.info(checksum);
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            LOG.info("Intercepted");
            // ...
            // write to and restore the original Stream
//            is.read(byteArrayInputStream.readAllBytes());
//            byteArrayInputStream.close();
            context.setInputStream(new ByteArrayInputStream(data));
        }
        
        return context.proceed();
    }
}