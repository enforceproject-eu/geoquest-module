package org.n52.project.enforce.geoquest.api.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestApiFetcher;
import org.n52.project.enforce.geoquest.remote.ApiException;
import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoquestUtilsTest extends DBTest{
    
    @Autowired
    GeoquestUtils geoquestUtils;
    
    @Autowired
    GeoquestApiFetcher geoquestApiFetcher;
    
    @Test
    void testFetchApi() {
        try {
            geoquestApiFetcher.fetchAndStoreData();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
//    @Test
//    void testReadJson() {
//        try {
//            JsonNode input = new ObjectMapper().readTree(getClass().getResourceAsStream("test_data.json"));
//            geoquestUtils.readJsonNode(input.get("submissions"));
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//    }    
    
}
