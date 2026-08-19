package org.n52.project.enforce.geoquest.api.impl.geoquest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * <p>
 * Data repository.
 * </p>
 *
 * @author Benjamin Pross 
 * @since 1.0.0
 */
public interface GeoquestSubmissionsRepository extends JpaRepository<GeoquestSubmissions, Integer> {
    
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
