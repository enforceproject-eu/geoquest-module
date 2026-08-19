package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.n52.project.enforce.geoquest.api.GeoquestApi;
import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-10T11:28:56.483012800+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
@Controller
@RequestMapping("${openapi.eNFORCEDataAccess.base-path:}")
public class GeoquestApiController implements GeoquestApi {

    private GeoquestUtils geoquestUtils;
    
    private GeoquestSubmissionsRepository geoquestDataRepository;
    
    @Autowired
    public GeoquestApiController(GeoquestSubmissionsRepository geoquestSubmissionRepository, GeoquestUtils geoquestUtils) {
        this.geoquestDataRepository = geoquestSubmissionRepository;
        this.geoquestUtils = geoquestUtils;
    }

    @Override
    public ResponseEntity<Serializable> addGeoquestGeoQuestDataAsBody(String body) {
//        try {
//            geoquestUtils.readCsvFile(new ByteArrayInputStream(body.getBytes()));
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Serializable> getGeoquestGeoQuestData() {
        try {
            return ResponseEntity.ok(geoquestDataRepository.getGeoJson());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
