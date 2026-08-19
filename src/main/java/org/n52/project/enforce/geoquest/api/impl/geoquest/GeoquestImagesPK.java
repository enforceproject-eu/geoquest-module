package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.util.UUID;

import jakarta.persistence.Column;

public class GeoquestImagesPK {

    @Column(
            name = "id")
    private Long id;

    @Column(
            name = "quest_survey_Submission_id")
    private UUID questSurveySubmissionId;
    
    public GeoquestImagesPK() {} 

    public GeoquestImagesPK(Long id, UUID questSurveySubmissionId) {
        super();
        this.id = id;
        this.questSurveySubmissionId = questSurveySubmissionId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }       

    public UUID getQuestSurveySubmissionId() {
        return questSurveySubmissionId;
    }

    public void setQuestSurveySubmissionId(UUID questSurveySubmissionId) {
        this.questSurveySubmissionId = questSurveySubmissionId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    
    
}
