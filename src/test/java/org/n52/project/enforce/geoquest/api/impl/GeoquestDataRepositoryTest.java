package org.n52.project.enforce.geoquest.api.impl;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class GeoquestDataRepositoryTest extends DBTest {

    @Autowired
    GeoquestSubmissionsRepository cs1DataRepository;
    
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    
    Random random = new Random();
    
    @Test
    void testCreateData() {

        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        
        geoquestSubmissions.setQuestSurveySubmissionId(UUID.randomUUID());
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        
        cs1DataRepository.saveAndFlush(geoquestSubmissions);
    }
    
}
