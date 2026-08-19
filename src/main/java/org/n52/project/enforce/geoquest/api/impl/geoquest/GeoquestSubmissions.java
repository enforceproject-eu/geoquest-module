package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.time.LocalDateTime;
import java.util.UUID;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * <p>
 * Data DTO.
 * </p>
 *
 * @author Benjamin Pross (b.pross @52north.org)
 * @since 1.0.0
 */
@Entity
@Table(
        name = "geoquest_submissions")
public class GeoquestSubmissions {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "geoquest_submissions_generator")
    @SequenceGenerator(
            name = "geoquest_submissions_generator",
            sequenceName = "geoquest_submissions_seq",
            allocationSize = 1)
    private Integer id;

    @Column(
            name = "quest_survey_Submission_id")
    private UUID questSurveySubmissionId;

    @Column(
            name = "creator_id")
    private UUID creatorId;
    
    @Column(
            name = "location_id")
    private String locationId;
    
    @Column(
            name = "status")
    private String status;
    
    @Column(
            name = "user_name")
    private String userName;
    
    @Column(
            name = "assigned_score")
    private Integer assignedScore;
    
    @Column(
            name = "image_count")
    private Integer imageCount;
    
    @Column(
            name = "report_type")
    private String reportType;
    
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
            name = "submission_data")
    private String submissionData;

    @Column(
            name = "coordinate",
            columnDefinition = "geometry(Point,4326)")
    private Point coordinate;

    public GeoquestSubmissions() {
    }

    public GeoquestSubmissions(UUID questSurveySubmissionId, UUID creatorId, String locationId, String locationWkt,
            LocalDateTime date, String status, String submissionData, Point coordinate, String reportType) {
        this();
        this.questSurveySubmissionId = questSurveySubmissionId;
        this.creatorId = creatorId;
        this.locationId = locationId;
        this.status = status;
        this.creationTime = date;
        this.submissionData = submissionData;
        this.coordinate = coordinate;
        this.reportType = reportType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAssignedScore() {
        return assignedScore;
    }

    public void setAssignedScore(Integer assignedScore) {
        this.assignedScore = assignedScore;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
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

    public String getSubmissionData() {
        return submissionData;
    }

    public void setSubmissionData(String submissionData) {
        this.submissionData = submissionData;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: " + id + ", ");
        sb.append("questSurveySubmissionId: " + questSurveySubmissionId + ", ");
        sb.append("creatorId: " + creatorId + ", ");
        sb.append("date: " + creationTime + ", ");
        sb.append("submissionData: " + submissionData + ", ");
        sb.append("coordinate: " + coordinate + ", ");
        return sb.toString();
    }
}
