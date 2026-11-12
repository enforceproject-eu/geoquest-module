package org.n52.project.enforce.geoquest.api.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.n52.project.enforce.cdm.CommonDataModelRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestApiFetcher;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuests;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuestsRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissions;
import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@TestInstance(Lifecycle.PER_CLASS)
public class GeoquestUtilsTest extends DBTest{
    
    @Autowired
    GeoquestUtils geoquestUtils;

    @Autowired
    GeoquestQuestsRepository geoquestQuestsRepository;
    
    @Autowired
    GeoquestSubmissionsRepository geoquestDataRepository;
    
    @Autowired
    CommonDataModelRepository commonDataModelRepository;
    
    @Autowired
    GeoquestApiFetcher geoquestApiFetcher;

    private UUID questId;

    @BeforeAll
    public void setUp() {
        questId = UUID.randomUUID();
        geoquestQuestsRepository.save(new GeoquestQuests(questId, "test", 0));
    }
    
    @Test
    void testFetchApi() {
        try {
            geoquestApiFetcher.fetchAndStoreData();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    void testReadJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            IIASAGeoQuestQuestQuestSurveySubmissions input = mapper.readValue(getClass().getResourceAsStream("test_data.json"), IIASAGeoQuestQuestQuestSurveySubmissions.class);
            
            List<IIASAGeoQuestQuestQuestSurveySubmissionDto> submissions = input.getSubmissions();
            
            for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : submissions) {
                geoquestUtils.createUpdates(questId, iiasaGeoQuestQuestQuestSurveySubmissionDto);                
            }
            
            assertTrue(geoquestDataRepository.count() == 3);
            Optional<GeoquestSubmissions> submission = geoquestDataRepository.searchBySubmissionId(UUID.fromString("3a1af5bb-747c-0d29-59a1-800c8c04eee0"));
            assertTrue(submission.isPresent());
            assertTrue(submission.get().getStatus().equals("NotReviewed"));
            input = mapper.readValue(getClass().getResourceAsStream("test_data_updated.json"), IIASAGeoQuestQuestQuestSurveySubmissions.class);
            geoquestUtils.createUpdates(questId, input.getSubmissions().get(1));
            assertTrue(geoquestDataRepository.count() == 4);
            submission = geoquestDataRepository.searchBySubmissionId(UUID.fromString("3a1af5bb-747c-0d29-59a1-800c8c04eee0"));
            assertTrue(submission.isPresent());
            assertTrue(submission.get().getStatus().equals("Accepted"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    void testConvertToCdm() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            IIASAGeoQuestQuestQuestSurveySubmissions input = mapper.readValue(getClass().getResourceAsStream("test_data.json"), IIASAGeoQuestQuestQuestSurveySubmissions.class);
            
            List<IIASAGeoQuestQuestQuestSurveySubmissionDto> submissions = input.getSubmissions();
            
            for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : submissions) {
                geoquestUtils.convertToCdm(UUID.fromString("3a1e334a-373a-cd28-212d-699f7ab32153"), iiasaGeoQuestQuestQuestSurveySubmissionDto);                
            }
            
            System.out.println(commonDataModelRepository.getGeoJson());
            
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }    
    
}
