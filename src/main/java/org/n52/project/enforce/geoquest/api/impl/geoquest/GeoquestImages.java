package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "geoquest_images")
public class GeoquestImages {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "geoquest_images_generator")
    @SequenceGenerator(
            name = "geoquest_images_generator",
            sequenceName = "geoquest_images_seq",
            allocationSize = 1)
    private Integer id;

    @Column(
            name = "image_id")
    private Long imageId;

    @Column(
            name = "quest_survey_Submission_id")
    private UUID questSurveySubmissionId;    

    @Column(
            name = "creator_id")
    private UUID creatorId;

    @Column(
            name = "last_modifier_id")
    private UUID lastModifierId;

    @Column(
            name = "creation_time")
    private LocalDateTime creationTime;

    @Column(
            name = "last_modification_time")
    private LocalDateTime lastModificationTime;

    @Column(
            name = "url")
    private String url;

    @Column(
            name = "base_64_data")
    private String base64Data;
    
    @Column(
            name = "derived_by")
    private Integer derivedBy;
    
    @Column(
            name = "derives")
    private Integer derives;

    public GeoquestImages() {}
    
    public GeoquestImages(int id, Long imageId, UUID creatorId, UUID lastModifierId,
            LocalDateTime creationTime, LocalDateTime lastModificationTime, String url, String base64Data) {
        this.id = id;
        this.imageId = imageId;
        this.creatorId = creatorId;
        this.lastModifierId = lastModifierId;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.url = url;
        this.base64Data = base64Data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public UUID getQuestSurveySubmissionId() {
        return questSurveySubmissionId;
    }

    public void setQuestSurveySubmissionId(UUID questSurveySubmissionId) {
        this.questSurveySubmissionId = questSurveySubmissionId;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public UUID getLastModifierId() {
        return lastModifierId;
    }

    public void setLastModifierId(UUID lastModifierId) {
        this.lastModifierId = lastModifierId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(LocalDateTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public Integer getDerivedBy() {
        return derivedBy;
    }

    public void setDerivedBy(Integer derivedBy) {
        this.derivedBy = derivedBy;
    }

    public Integer getDerives() {
        return derives;
    }

    public void setDerives(Integer derives) {
        this.derives = derives;
    }

}
