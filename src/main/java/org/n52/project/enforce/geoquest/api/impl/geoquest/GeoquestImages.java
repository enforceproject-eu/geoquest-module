package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "geoquest_images")
public class GeoquestImages {

    @EmbeddedId
    private GeoquestImagesPK geoquestImagesPK;

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

    public GeoquestImages() {}
    
    public GeoquestImages(GeoquestImagesPK geoquestImagesPK, UUID creatorId, UUID lastModifierId,
            LocalDateTime creationTime, LocalDateTime lastModificationTime, String url, String base64Data) {
        this.geoquestImagesPK = geoquestImagesPK;
        this.creatorId = creatorId;
        this.lastModifierId = lastModifierId;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.url = url;
        this.base64Data = base64Data;
    }

    public GeoquestImagesPK getGeoquestImagesPK() {
        return geoquestImagesPK;
    }

    public void setGeoquestImagesPK(GeoquestImagesPK geoquestImagesPK) {
        this.geoquestImagesPK = geoquestImagesPK;
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

}
