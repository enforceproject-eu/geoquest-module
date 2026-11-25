package org.n52.project.enforce.geoquest.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.n52.project.enforce.cdm.CdmCategory;
import org.n52.project.enforce.cdm.CdmCategoryRepository;
import org.n52.project.enforce.cdm.CdmEvidenceTypeRepository;
import org.n52.project.enforce.cdm.CdmObservationType;
import org.n52.project.enforce.cdm.CdmObservationTypeRepository;
import org.n52.project.enforce.cdm.CdmRegulatoryDomain;
import org.n52.project.enforce.cdm.CdmRegulatoryDomainRepository;
import org.n52.project.enforce.cdm.CdmValidationStatus;
import org.n52.project.enforce.cdm.CdmValidationStatusRepository;
import org.n52.project.enforce.cdm.CommonDataModel;
import org.n52.project.enforce.cdm.CommonDataModelRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImages;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestImagesRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuests;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestQuestsRepository;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissions;
import org.n52.project.enforce.geoquest.api.impl.geoquest.GeoquestSubmissionsRepository;
import org.n52.project.enforce.geoquest.remote.ApiClient;
import org.n52.project.enforce.geoquest.remote.ApiException;
import org.n52.project.enforce.geoquest.remote.api.QuestSurveySubmissionApi;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestCoordinate;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestImageDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissions;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;
import org.openprovenance.prov.model.interop.Formats.ProvFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class GeoquestUtils {

    private GeoquestSubmissionsRepository geoquestSubmissionsRepository;

    private GeoquestImagesRepository geoquestImagesRepository;
    
    private GeoquestQuestsRepository geoquestQuestsRepository;
    
    private CdmValidationStatusRepository cdmValidationStatusRepository;
    
    private CdmObservationTypeRepository cdmObservationTypeRepository;
    
    private CdmRegulatoryDomainRepository cdmRegulatoryDomainRepository;
    
    private CdmCategoryRepository cdmCategoryRepository;
    
    private CdmEvidenceTypeRepository cdmEvidenceTypeRepository;
    
    private CommonDataModelRepository commonDataModelRepository;
    
    private ProvenanceService provenanceService;

    private ObjectMapper objectMapper;

    DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd'T'HH:MM:SS");

    ZoneId zoneIdEuropeRome = ZoneId.of("Europe/Rome");

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private HttpClient httpClient;

    private ApiClient apiClient;

    private QuestSurveySubmissionApi questSurveySubmissionApi;

    private ArrayNode questMapping;

    private String dataDir;
    
    private final ProvFactory pFactory;
    
    private final Namespace ns;

    public static final String ENFORCE_NS = "http://join-enforce.eu";
    public static final String ENFORCE_PREFIX = "enforce";
    
    public static final String GEOQUEST_PREFIX = "geoquest";
    public static final String GEOQUEST_NS = "https://geoquest.iiasa.ac.at/";
    
    private static Logger LOG = LoggerFactory.getLogger(GeoquestUtils.class);

    public GeoquestUtils(GeoquestSubmissionsRepository geoquestSubmissionsRepository,
            GeoquestQuestsRepository geoquestQuestsRepository, GeoquestImagesRepository geoquestImagesRepository,
            Environment environment, ProvenanceService provenanceService,
            CdmValidationStatusRepository cdmValidationStatusRepository,
            CommonDataModelRepository commonDataModelRepository,
            CdmObservationTypeRepository cdmObservationTypeRepository,
            CdmRegulatoryDomainRepository cdmRegulatoryDomainRepository, CdmCategoryRepository cdmCategoryRepository,
            CdmEvidenceTypeRepository cdmEvidenceTypeRepository) {
        this.geoquestSubmissionsRepository = geoquestSubmissionsRepository;
        this.geoquestImagesRepository = geoquestImagesRepository;
        this.geoquestQuestsRepository = geoquestQuestsRepository;
        this.cdmValidationStatusRepository = cdmValidationStatusRepository;
        this.cdmObservationTypeRepository = cdmObservationTypeRepository;
        this.cdmRegulatoryDomainRepository = cdmRegulatoryDomainRepository;
        this.cdmCategoryRepository = cdmCategoryRepository;
        this.cdmEvidenceTypeRepository = cdmEvidenceTypeRepository;
        this.commonDataModelRepository = commonDataModelRepository;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        apiClient = new ApiClient();
        apiClient.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        dataDir = environment.getProperty("data.dir");
        if(dataDir == null || dataDir.isEmpty()) {
            dataDir = System.getProperty("java.io.tmpdir");
        }
        String basePath = null;
        try {
            basePath = environment.getProperty("geoquest.basepath");
            apiClient.setBasePath(basePath);
        } catch (Exception e) {
            // TODO: handle exception
            LOG.error("Could not set API base path: " + basePath);
        }
        questSurveySubmissionApi = new QuestSurveySubmissionApi(apiClient);
        ResponseInterceptor responseFilter = new ResponseInterceptor();
        apiClient.getHttpClient().register(responseFilter);
        responseFilter.setProvenanceService(provenanceService);
        String questMappingString = environment.getProperty("geoquest.quests.mapping");
        LOG.debug(questMappingString);
        pFactory = InteropFramework.getDefaultFactory();
        ns=new Namespace();
        ns.addKnownNamespaces();
        ns.register(ENFORCE_PREFIX, ENFORCE_NS);
        ns.register(GEOQUEST_PREFIX, GEOQUEST_NS);
        try {
            questMapping = (ArrayNode) objectMapper.readTree(questMappingString);
            checkForNewQuests(questMapping);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return;
        }
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

    public void getUpdates(UUID questId) throws ApiException {
        IIASAGeoQuestQuestQuestSurveySubmissions subs =
                questSurveySubmissionApi.apiQuestsQuestIdSubmissionGet(questId, null, null, null);
        List<IIASAGeoQuestQuestQuestSurveySubmissionDto> subslist = subs.getSubmissions();

        for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : subslist) {
            createUpdates(questId, iiasaGeoQuestQuestQuestSurveySubmissionDto);
            convertToCdm(questId, iiasaGeoQuestQuestQuestSurveySubmissionDto);
        }

    }

    public void createUpdates(UUID questId,
            IIASAGeoQuestQuestQuestSurveySubmissionDto input) {
        UUID submissionId = input.getId();
        Optional<GeoquestSubmissions> submissionFromDb =
                geoquestSubmissionsRepository.searchBySubmissionId(submissionId);
        GeoquestSubmissions updatedSubmission = createSubmissions(questId, input);

        boolean updated = false;
        if (submissionFromDb.isPresent()) {
            GeoquestSubmissions currentSubmission = submissionFromDb.get();
            LocalDateTime currentLastModificationTime = currentSubmission.getLastModificationTime();
            LocalDateTime updatedLastModificationTime = updatedSubmission.getLastModificationTime();
            if (currentLastModificationTime == null && (updatedLastModificationTime == null)) {
                updated = false;
            } else if (currentLastModificationTime == null && (updatedLastModificationTime != null)) {
                updated = true;
            } else if (currentLastModificationTime != null && (updatedLastModificationTime == null)) {
                // can this happen!?
                updated = false;
            } else {
                if (currentLastModificationTime.isBefore(updatedLastModificationTime)) {
                    updated = true;
                } else if (currentLastModificationTime.isAfter(updatedLastModificationTime)) {
                    // can this happen?!
                    updated = false;
                }
            }
            if (updated) {
                // create new submission and set derives/derived_by properties
                updatedSubmission.setDerives(currentSubmission.getId());
                geoquestSubmissionsRepository.saveAndFlush(updatedSubmission);
                currentSubmission.setDerivedBy(updatedSubmission.getId());
                updatedSubmission.setQuestId(questId);
                geoquestSubmissionsRepository.saveAndFlush(currentSubmission);
            }
        } else {
            geoquestSubmissionsRepository.saveAndFlush(updatedSubmission);
        }
    }

    private GeoquestSubmissions createSubmissions(UUID questId,
            IIASAGeoQuestQuestQuestSurveySubmissionDto input) {

        GeoquestSubmissions data = new GeoquestSubmissions();
        data.setQuestId(questId);
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
            GeoquestImages geoquestImage = createGeoquestImage(iiasaGeoQuestQuestImageDto);
            geoquestImages.add(geoquestImage);
        }
        data.setImages(geoquestImages);
    }

    private GeoquestImages createGeoquestImage(IIASAGeoQuestQuestImageDto iiasaGeoQuestQuestImageDto) {
        Long imageId = iiasaGeoQuestQuestImageDto.getId();
        UUID questSurveySubmissionId = iiasaGeoQuestQuestImageDto.getQuestSurveySubmissionId();
        GeoquestImages geoquestImage = new GeoquestImages();
        Optional<GeoquestImages> imageFromDb =
                geoquestImagesRepository.searchBySubmissionId(imageId, questSurveySubmissionId);
        geoquestImage.setImageId(imageId);
        geoquestImage.setQuestSurveySubmissionId(questSurveySubmissionId);
        geoquestImage.setBase64Data(iiasaGeoQuestQuestImageDto.getBase64Data());
        geoquestImage.setUrl(iiasaGeoQuestQuestImageDto.getUrl());
        geoquestImage.setCreationTime(iiasaGeoQuestQuestImageDto.getCreationTime());
        geoquestImage.setCreatorId(iiasaGeoQuestQuestImageDto.getCreatorId());
        geoquestImage.setLastModificationTime(iiasaGeoQuestQuestImageDto.getLastModificationTime());
        geoquestImage.setLastModifierId(iiasaGeoQuestQuestImageDto.getLastModifierId());
        boolean updated = false;
        if (imageFromDb.isPresent()) {
            GeoquestImages currentImageFromDb = imageFromDb.get();
            LocalDateTime currentLastModificationTime = currentImageFromDb.getLastModificationTime();
            LocalDateTime updatedLastModificationTime = currentImageFromDb.getLastModificationTime();
            if (currentLastModificationTime == null && (updatedLastModificationTime == null)) {
                updated = false;
            } else if (currentLastModificationTime == null && (updatedLastModificationTime != null)) {
                updated = true;
            } else if (currentLastModificationTime != null && (updatedLastModificationTime == null)) {
                // can this happen!?
                updated = false;
            } else {
                if (currentLastModificationTime.isBefore(updatedLastModificationTime)) {
                    updated = true;
                } else if (currentLastModificationTime.isAfter(updatedLastModificationTime)) {
                    // can this happen?!
                    updated = false;
                }
            }
            if (updated) {
                geoquestImage.setDerives(currentImageFromDb.getId());
                geoquestImagesRepository.saveAndFlush(geoquestImage);
                currentImageFromDb.setDerivedBy(geoquestImage.getId());
                geoquestImagesRepository.saveAndFlush(currentImageFromDb);
            }

        } else {
            geoquestImagesRepository.saveAndFlush(geoquestImage);            
        }
        return geoquestImage;
    }

    private Point createPoint(List<IIASAGeoQuestQuestCoordinate> location) {
        if (location != null) {
            if (!location.isEmpty()) {
                IIASAGeoQuestQuestCoordinate firstLocation = location.get(0);
                Double lat = firstLocation.getYLat();
                Double lng = firstLocation.getXLng();
                return geometryFactory.createPoint(new Coordinate(lng, lat));
            }
        }
        return geometryFactory.createPoint(new Coordinate(0, 0));
    }
    
    public void convertToCdm(UUID questId,
            IIASAGeoQuestQuestQuestSurveySubmissionDto input) {

        File dataDirectory = new File(dataDir + "/" + System.currentTimeMillis());
        
        dataDirectory.mkdirs();
        
        Boolean authorityNotified;
        String authorityReference;
        String campaignId = questId.toString();
        int caseStudyId = 0;
        String chainOfCustodyReference;
        String contributorId = input.getCreatorId().toString();
        OffsetDateTime createdAt = OffsetDateTime.now();
        String description;
        OffsetDateTime endTime;
        String evidenceId = null;
        String geometry;
        Double locationAccuracyM;
        OffsetDateTime modifiedAt = fromLocalDateTime(input.getLastModificationTime());
        String observationId = input.getId().toString();
        OffsetDateTime observationTime = fromLocalDateTime(input.getCreationTime());
        String originalEvidenceHash = null;
        String originalRecordId = observationId;
        Boolean personalDataFlag = false;
        String processingActivityRef;
        String protocolId;
        String protocolVersion;
        String provenanceRecord;
        Double qualityScore;
        String recordVersion = "1";
        OffsetDateTime submissionTime = OffsetDateTime.now();
        String validatorId;
        String value;
        //N/A
        int sensitiveContentFlagId = 1;
        int observationTypeId = 1;
        int observedPropertyId = 1;
        int unitId;
        int categoryId = 1;
        int locationSourceId;
        int contributorRoleId;
        //mobile_app
        int collectionMethodId = 2;
        int evidenceTypeId = 1;
        int validationStatusId = 1;
        int validationMethodId;
        //restricted
        int accessLevelId = 1;
        int retentionClassId;
        int complianceRelevanceId;
        int suspectedViolationTypeId;
        int regulatoryDomainId = 1;
        int evidenceStatusId;
        int evidenceAccessRestrictionId;
        int deviceOrSensorId;
        //restricted
        int licenceId = 1;        
        
        for (JsonNode jsonNode : questMapping) {
            if(questId.toString().equals(jsonNode.get("quest_id").asText())) {
                caseStudyId = jsonNode.get("case_study").asInt();
            }
        }        
        
        Object submissionDataObj = input.getSubmissionData();
        String submissionDataString = "";
        if (submissionDataObj != null && (submissionDataObj instanceof String)) {
            submissionDataString = ((String) submissionDataObj).replace("\\\"", "\"");
        }
        JsonNode submissionDataJson;
        geometry = new GeoJsonWriter().write(createPoint(input.getLocation()));
        try {
            submissionDataJson = objectMapper.reader().readTree(submissionDataString);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return;
        }
        JsonNode reportType = submissionDataJson.get("reportType");
        
        String reportTypeString = "";
        
        if (reportType instanceof ArrayNode) {            
            reportTypeString = ((ArrayNode) reportType).elements().next().asText();            
        } else if (reportType != null){
            reportTypeString = reportType.asText();
        }        

        if (reportTypeString != null && !reportTypeString.isEmpty()) {
            
            reportTypeString = createCorrectType(reportTypeString);
            
            CdmObservationType observationType = cdmObservationTypeRepository.findByName(reportTypeString);
            if(observationType != null) {
                observationTypeId = observationType.getId();
            }
        }
        
        JsonNode selectType = submissionDataJson.get("selectType");
        
        String selectTypeString = "";
        
        if (selectType instanceof ArrayNode) {
            try {
                selectTypeString = ((ArrayNode) selectType).elements().next().asText();
            } catch (Exception e) {
                LOG.debug("Selecttype has no elements.");
            }
        } else if (selectType != null){
            selectTypeString = selectType.asText();
        }
        if (selectTypeString != null && !selectTypeString.isEmpty()) {
            
            selectTypeString = createCorrectType(selectTypeString);
            
            CdmObservationType observationType = cdmObservationTypeRepository.findByName(selectTypeString);
            if(observationType != null) {
                observationTypeId = observationType.getId();
            } else {
                observationTypeId = 1;
            }
        }
        
        JsonNode smellType = submissionDataJson.get("smellType");
        
        String smellTypeString = "";
        
        if (smellType instanceof ArrayNode) {
            try {
                smellTypeString = ((ArrayNode) smellType).elements().next().asText();
            } catch (Exception e) {
                LOG.debug("Selecttype has no elements.");
            }
        } else if (smellType != null){
            smellTypeString = smellType.asText();
        }
        if (smellTypeString != null && !smellTypeString.isEmpty()) {
            
            smellTypeString = createCorrectType(smellTypeString);
            
            CdmCategory category = cdmCategoryRepository.findByName(smellTypeString);
            if(category != null) {
                categoryId = category.getId();
            } else {
                categoryId = 1;
            }
        }
        
        if(input.getStatus() != null) {
            String status = input.getStatus().getValue();
            CdmValidationStatus cdmValidationStatus = cdmValidationStatusRepository.findByName(status.toLowerCase());
            if(cdmValidationStatus != null) {
                validationStatusId = cdmValidationStatus.getId();
            } else {
                //unreviewed
                validationStatusId = 1;
            }
        }
        
        CdmRegulatoryDomain cdmRegulatoryDomain = cdmRegulatoryDomainRepository.findByName("waste");
        if(cdmRegulatoryDomain != null) {
            regulatoryDomainId = cdmRegulatoryDomain.getId();
        }
        
        if (input.getImageCount() > 0) {
            try {
                List<IIASAGeoQuestQuestImageDto> submissionImages = questSurveySubmissionApi
                        .apiQuestsQuestIdSubmissionSubmissionIdImagesGet(questId, input.getId());

                File file = new File(dataDirectory.getAbsolutePath() + "/" + UUID.randomUUID().toString());
                String imageUrl = submissionImages.get(0).getUrl();
                String contentType = "";
                try (BufferedInputStream in = new BufferedInputStream(new URI(imageUrl).toURL().openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    contentType = findContentType(imageUrl);
                } catch (IOException | URISyntaxException e) {
                    LOG.error(e.getMessage());
                }
                byte[] data;
                try {
                    data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    byte[] hash = MessageDigest.getInstance("MD5").digest(data);
                    String evidenceType = getEvidenceType(contentType);
                    Optional<Integer> evidenceTypeIdFromDb = cdmEvidenceTypeRepository.getIdForName(evidenceType);
                    if(evidenceTypeIdFromDb.isPresent()) {
                        evidenceTypeId = evidenceTypeIdFromDb.get();
                    }
                    if(evidenceType.equals("photo")) {
                        evidenceId = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(data);
                    } else if(evidenceType.equals("video")) {
                        evidenceId = imageUrl;
                    }
                    String checksum = new BigInteger(1, hash).toString(16);
                    LOG.debug(checksum);
                    originalEvidenceHash = checksum;
                } catch (IOException | NoSuchAlgorithmException e) {
                    LOG.error(e.getMessage());
                }

            } catch (ApiException e) {
                LOG.debug("Could not get images for submission: " + input.getId().toString());
                LOG.error(e.getMessage());
            }
        }
        
        Document provDoc = makeDocument(originalRecordId, contributorId, campaignId);
        InteropFramework intF=new InteropFramework();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        intF.writeDocument(byteArrayOutputStream, provDoc, ProvFormat.JSONLD);
        CommonDataModel dataModel = new CommonDataModel(caseStudyId, createdAt, modifiedAt, observationId, observationTime, personalDataFlag, recordVersion, submissionTime, observationTypeId, observedPropertyId, collectionMethodId, validationStatusId, accessLevelId, licenceId);
        dataModel.setSensitiveContentFlagId(sensitiveContentFlagId);
        dataModel.setCampaignId(campaignId);
        dataModel.setContributorId(contributorId);
        dataModel.setGeometry(geometry);
        dataModel.setCategoryId(categoryId);
        dataModel.setRegulatoryDomainId(regulatoryDomainId);
        dataModel.setEvidenceId(evidenceId);
        dataModel.setOriginalEvidenceHash(originalEvidenceHash);
        dataModel.setEvidenceTypeId(evidenceTypeId);
        dataModel.setProvenanceRecord(byteArrayOutputStream.toString());
        commonDataModelRepository.saveAndFlush(dataModel);
    }
    
    private String getEvidenceType(String contentType) {

        String evidenceType = "";

        switch (contentType) {
        case MimeTypeUtils.IMAGE_JPEG_VALUE:
        case MimeTypeUtils.IMAGE_PNG_VALUE:
        case MimeTypeUtils.IMAGE_GIF_VALUE:
        case "image/jpg": {
            evidenceType = "photo";
            break;
        }
        case "video/quicktime": {
            evidenceType = "video";
            break;
        }
        default:
            evidenceType = "other";
        }
        return evidenceType;
    }

    private OffsetDateTime fromLocalDateTime(LocalDateTime dateTime) {
        OffsetDateTime result = null;
        if (dateTime != null) {
            result = OffsetDateTime.of(dateTime, ZoneOffset.UTC);
        }
        return result;
    }
    
    private String createCorrectType(String type) {
        if(Character.isDigit(type.charAt(0))) {
            type = type.substring(2);
        }
        return type;
    }
    
    private String findContentType(String url) throws IOException, URISyntaxException {
        String contentType = "";
        URLConnection connection = new URI(url).toURL().openConnection();
        contentType = connection.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            if (contentType.equals(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)) {
                String suffix = url.substring(url.lastIndexOf(".") + 1);
                if(suffix.equals("mov")) {
                    contentType = "video/quicktime";
                } else {
                    contentType = "image/" + suffix;
                }
            }
        }
        return contentType;
    }

    private void checkForNewQuests(ArrayNode questMapping) {
        for (JsonNode jsonNode : questMapping) {
            String questId = jsonNode.get("quest_id").asText();
            UUID questUuid = UUID.fromString(questId);
            Optional<GeoquestQuests> questFromDb = geoquestQuestsRepository.findById(questUuid);
            if(questFromDb.isEmpty()) {
                //create new quest in db
                String name = jsonNode.get("name").asText();
                int caseStudyId = jsonNode.get("case_study").asInt();
                GeoquestQuests geoquestQuest = new GeoquestQuests(questUuid, name, caseStudyId);
                geoquestQuestsRepository.saveAndFlush(geoquestQuest);
                LOG.info("New Quest created in database: " + questId);
            }
        }
        
    }
    
    public Document makeDocument(String title, String agent, String project) {     
        
        Entity entity = pFactory.newEntity(qn(title));
        entity.setValue(pFactory.newValue(title,
                                         pFactory.getName().XSD_STRING));
     
        Agent agentAgent = pFactory.newAgent(qn(agent), agent);
     
        WasAttributedTo attr1 = pFactory.newWasAttributedTo(null,
                                                            entity.getId(),
                                                            agentAgent.getId());
     
        Activity projectActivity = pFactory.newActivity(qn(project));
        
        WasGeneratedBy wsgb = pFactory.newWasGeneratedBy(entity, "collected", projectActivity);
        
        Document document = pFactory.newDocument();
        document.getStatementOrBundle()
                .addAll(Arrays.asList(new StatementOrBundle[] { entity, 
                                                                agentAgent,
                                                                attr1}));
        document.getStatementOrBundle().add(wsgb);
        document.setNamespace(ns);
        return document;
    }
    
    public QualifiedName qn(String n) {
        return ns.qualifiedName(GEOQUEST_PREFIX, n, pFactory);
    }

}
