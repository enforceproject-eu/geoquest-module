package org.n52.project.enforce.geoquest.api.impl.geoquest;

import org.mapstruct.factory.Mappers;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;


public interface SubmissionsMapper {

    SubmissionsMapper INSTANCE = Mappers.getMapper(SubmissionsMapper.class);
    
    default GeoquestSubmissions toDb(IIASAGeoQuestQuestQuestSurveySubmissionDto geoQuestQuestQuestSurveySubmissionDto) {
        
        GeoquestSubmissions result = new GeoquestSubmissions();
//        
        result.setQuestSurveySubmissionId(geoQuestQuestQuestSurveySubmissionDto.getId());
        
//        result.setSubmissionData();
        return result;
    }
    
}
