package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "geoquest_quests")
public class GeoquestQuests {

    @Id
    private UUID id;
    
    @Column(
            name = "name")
    private String name;
    
    @Column(
            name = "case_study_number")
    private int caseStudyNumber;   

    public GeoquestQuests() { }
    
    public GeoquestQuests(UUID id, String name, int caseStudyNumber) {
        this.id = id;
        this.name = name;
        this.caseStudyNumber = caseStudyNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCaseStudyNumber() {
        return caseStudyNumber;
    }

    public void setCaseStudyNumber(int caseStudyNumber) {
        this.caseStudyNumber = caseStudyNumber;
    }
}
