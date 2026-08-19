package org.n52.project.enforce.geoquest.api.impl.token;

import org.n52.project.enforce.geoquest.api.TokenApi;
import org.n52.project.enforce.geoquest.model.TokenPostRequest;
import org.n52.project.enforce.geoquest.utils.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Generated;
import jakarta.validation.Valid;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-06T08:45:51.461385900+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
@Controller
@RequestMapping("${openapi.eNFORCEDataAccess.base-path:}")
public class TokenApiController implements TokenApi {

    @Override
    public ResponseEntity<?> addGeoquestGeoQuestTokenAsBody(@Valid TokenPostRequest tokenPostRequest) {                       
        Token.setToken(tokenPostRequest.getToken());        
        return ResponseEntity.ok().build();
    }

}
