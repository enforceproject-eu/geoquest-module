package org.n52.project.enforce.geoquest.api.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImages;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImagesRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuests;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuestsRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestEnumsSubmissionStatus;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestImageDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@TestInstance(Lifecycle.PER_CLASS)
public class GeoquestDataRepositoryTest extends DBTest {

    @Autowired
    GeoquestSubmissionsRepository geoquestDataRepository;

    @Autowired
    GeoquestUtils geoquestUtils;

    @Autowired
    GeoquestQuestsRepository geoquestQuestsRepository;
    
    @Autowired
    GeoquestImagesRepository geoquestImagesRepository;

    @Autowired
    Environment environment;

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private UUID questId;

    @BeforeAll
    public void setUp() {
        questId = UUID.randomUUID();
        geoquestQuestsRepository.save(new GeoquestQuests(questId, "test", 0));
    }

    @Test
    void testCreateData() {
        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        geoquestSubmissions.setQuestSurveySubmissionId(UUID.randomUUID());
        geoquestSubmissions.setQuestId(questId);
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        geoquestDataRepository.saveAndFlush(geoquestSubmissions);
    }

    @Test
    void testUpdateDataNoUpdate() {
        UUID submissionId = createSubmission(questId);
        IIASAGeoQuestQuestQuestSurveySubmissionDto response = new IIASAGeoQuestQuestQuestSurveySubmissionDto();
        response.setId(submissionId);
        response.setImageCount(0);
        response.setStatus(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED);
        geoquestUtils.createUpdates(questId, response);
        Optional<GeoquestSubmissions> submissionFromDb = geoquestDataRepository.searchBySubmissionId(submissionId);
        assertTrue(submissionFromDb.isPresent());
        assertTrue(
                submissionFromDb.get().getStatus().equals(IIASAGeoQuestEnumsSubmissionStatus.NOTREVIEWED.getValue()));

    }

    @Test
    void testUpdateDataUpdate1() {
        UUID submissionId = createSubmission(questId);
        IIASAGeoQuestQuestQuestSurveySubmissionDto response = new IIASAGeoQuestQuestQuestSurveySubmissionDto();
        response.setId(submissionId);
        response.setImageCount(0);
        response.setStatus(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED);
        response.setLastModificationTime(LocalDateTime.now());
        geoquestUtils.createUpdates(questId, response);
        Optional<GeoquestSubmissions> submissionFromDb = geoquestDataRepository.searchBySubmissionId(submissionId);
        assertTrue(submissionFromDb.isPresent());
        assertTrue(
                submissionFromDb.get().getStatus().equals(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED.getValue()));
    }

    @Test
    void testUpdateDataUpdate2() {
        UUID submissionId = createSubmission(questId, LocalDateTime.now());
        IIASAGeoQuestQuestQuestSurveySubmissionDto response = new IIASAGeoQuestQuestQuestSurveySubmissionDto();
        response.setId(submissionId);
        response.setImageCount(0);
        response.setStatus(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED);
        response.setLastModificationTime(LocalDateTime.now().plusHours(2));
        geoquestUtils.createUpdates(questId, response);
        Optional<GeoquestSubmissions> submissionFromDb = geoquestDataRepository.searchBySubmissionId(submissionId);
        assertTrue(submissionFromDb.isPresent());
        assertTrue(
                submissionFromDb.get().getStatus().equals(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED.getValue()));
    }

    @Test
    void testUpdateDataUpdateImages() {
        
        UUID id = UUID.randomUUID();
        
        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        geoquestSubmissions.setQuestSurveySubmissionId(id);
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        geoquestSubmissions.setQuestId(questId);
        
        GeoquestImages geoquestImage = new GeoquestImages();
        
        geoquestImage.setImageId(1l);
        geoquestImage.setUrl("https://test.url/image1");
        geoquestImage.setQuestSurveySubmissionId(id);
        
        Set<GeoquestImages> images = new HashSet<GeoquestImages>();
        images.add(geoquestImage);
        geoquestSubmissions.setImages(images);
        
        geoquestImagesRepository.saveAndFlush(geoquestImage);
        
        geoquestSubmissions.setStatus(IIASAGeoQuestEnumsSubmissionStatus.NOTREVIEWED.getValue());
        
        geoquestDataRepository.saveAndFlush(geoquestSubmissions);
        
        UUID submissionId = geoquestSubmissions.getQuestSurveySubmissionId();
        
        IIASAGeoQuestQuestQuestSurveySubmissionDto response = new IIASAGeoQuestQuestQuestSurveySubmissionDto();
        response.setId(submissionId);
        response.setImageCount(0);
        response.setStatus(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED);
        response.setLastModificationTime(LocalDateTime.now().plusHours(2));
        
        IIASAGeoQuestQuestImageDto geoQuestQuestImageDto = new IIASAGeoQuestQuestImageDto();
        
        geoQuestQuestImageDto.setLastModificationTime(LocalDateTime.now());
        
        geoquestUtils.createUpdates(questId, response);
                
        Optional<GeoquestSubmissions> submissionFromDb = geoquestDataRepository.searchBySubmissionId(submissionId);
        assertTrue(submissionFromDb.isPresent());
        assertTrue(
                submissionFromDb.get().getStatus().equals(IIASAGeoQuestEnumsSubmissionStatus.ACCEPTED.getValue()));
    }

    private UUID createSubmission(UUID questId,
            LocalDateTime lastModificationTime) {
        GeoquestSubmissions geoquestSubmissions = new GeoquestSubmissions();
        geoquestSubmissions.setQuestSurveySubmissionId(UUID.randomUUID());
        geoquestSubmissions.setCreatorId(UUID.randomUUID());
        geoquestSubmissions.setCreationTime(LocalDateTime.now());
        geoquestSubmissions.setLastModificationTime(lastModificationTime);
        geoquestSubmissions.setQuestId(questId);
        geoquestSubmissions.setStatus(IIASAGeoQuestEnumsSubmissionStatus.NOTREVIEWED.getValue());
        geoquestDataRepository.saveAndFlush(geoquestSubmissions);
        return geoquestSubmissions.getQuestSurveySubmissionId();
    }

    private UUID createSubmission(UUID questId) {
        return createSubmission(questId, null);
    }

}
