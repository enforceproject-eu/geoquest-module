package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * GeoquestImages repository.
 * </p>
 *
 * @author Benjamin Pross 
 * @since 1.0.0
 */
public interface GeoquestImagesRepository extends JpaRepository<GeoquestImages, Integer> {

    @Query("select d from GeoquestImages as d where d.imageId = :imageId AND  d.questSurveySubmissionId = :questSurveySubmissionId")
    Optional<GeoquestImages> searchBySubmissionId(@Param("imageId") Long imageId, @Param("questSurveySubmissionId") UUID questSurveySubmissionId);
    
}
