package org.n52.project.enforce.geoquest.api.impl;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestApiFetcher;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class GeoquestDataRepositoryTest extends DBTest {

    @Autowired
    GeoquestSubmissionsRepository geoquestDataRepository;
    
    @Autowired
    GeoquestUtils geoquestUtils;
    
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    
    Random random = new Random();
    
    @Test
    void testCreateData() {

        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        
        geoquestSubmissions.setQuestSurveySubmissionId(UUID.randomUUID());
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        
        geoquestDataRepository.saveAndFlush(geoquestSubmissions);
    }
    
    @Test
    void testUpdateData() {

        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        
        UUID submissionId = UUID.randomUUID();
        
        geoquestSubmissions.setQuestSurveySubmissionId(submissionId);
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        geoquestSubmissions.setQuestId(UUID.randomUUID());
        
        geoquestDataRepository.saveAndFlush(geoquestSubmissions);
        
        Integer currentId = geoquestSubmissions.getId();
                
        IIASAGeoQuestQuestQuestSurveySubmissionDto response = new IIASAGeoQuestQuestQuestSurveySubmissionDto();
        
        response.setId(submissionId);
        response.setImageCount(0);
        
        geoquestUtils.createUpdates(submissionId, response);
        
//        geoquestUtils.getUpdates(questSurveySubmissionId);
    }
    
}
