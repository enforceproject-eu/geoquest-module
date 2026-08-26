package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * Data repository.
 * </p>
 *
 * @author Benjamin Pross 
 * @since 1.0.0
 */
public interface GeoquestSubmissionsRepository extends JpaRepository<GeoquestSubmissions, Integer> {
    
    
    @Query("select d from GeoquestSubmissions as d where d.questSurveySubmissionId  = :submissionId")
    Optional<GeoquestSubmissions> searchBySubmissionId(@Param("submissionId") UUID submissionId);
    
    /**
     * <p>
     * getGeoJson.
     * </p>
     * 
     * @return a {@link String} object
     */
    @Query("select ST_CS2DataToGeoJson()")
    String getGeoJson();
}
