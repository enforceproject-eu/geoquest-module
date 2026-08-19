package org.n52.project.enforce.geoquest.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class ProvenanceService {

    private String dataDir;
    
    public ProvenanceService(Environment environment) {
        dataDir = environment.getProperty("data.dir");
    }

    public boolean storeInitialData(InputStream dataStream) throws IOException, NoSuchAlgorithmException {
        UUID uuid = UUID.randomUUID();
        File dataDirectory = new File(dataDir + "/" + System.currentTimeMillis());
        dataDirectory.mkdir();
        File file = new File(dataDirectory.getAbsolutePath() + "/" + uuid);
        try (BufferedInputStream in = new BufferedInputStream(dataStream);
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
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        String checksum = new BigInteger(1, hash).toString(16);
        return true;
    }
    
}
