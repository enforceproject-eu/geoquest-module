package org.n52.project.enforce.geoquest.api.impl;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.n52.project.enforce.geoquest.api.impl.geoquest.SubmissionsMapper;
import org.n52.project.enforce.geoquest.remote.ApiClient;
import org.n52.project.enforce.geoquest.remote.ApiException;
import org.n52.project.enforce.geoquest.remote.api.QuestApi;
import org.n52.project.enforce.geoquest.remote.api.QuestSurveySubmissionApi;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissionDto;
import org.n52.project.enforce.geoquest.remote.model.IIASAGeoQuestQuestQuestSurveySubmissions;
import org.n52.project.enforce.geoquest.utils.ProvenanceResponseFilter;
import org.n52.project.enforce.geoquest.utils.ProvenanceService;
import org.n52.project.enforce.geoquest.utils.ResponseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiTest {

   
    @Autowired
    ProvenanceService provenanceService;
    
    @Test
    public void testIntercept() {
        
   ApiClient client = new ApiClient();
//           .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
//        client.getHttpClient().register(ProvenanceResponseFilter.class);
        client.getHttpClient().register(ResponseInterceptor.class);
        
        client.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        client.setBasePath("https://geoqapi.main.geo-wiki.org");
        
//        client.getJSON().getContext(null).configOverride(OffsetDateTime.class)
//        .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"));
        
//        client.setUsername("enforceadmin");
//        client.setPassword("Qwertyu1!");
        
        client.setAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjQxODg4Q0IwRDA5OThGMjVBNTU5RUQ3RDg3REY0QUU1NjQ2MjZBMTIiLCJ4NXQiOiJRWWlNc05DWmp5V2xXZTE5aDk5SzVXUmlhaEkiLCJ0eXAiOiJhdCtqd3QifQ.eyJpc3MiOiJodHRwczovL2dlb3EtYXV0aC5paWFzYS5hYy5hdC8iLCJleHAiOjE3ODcxMjk2NTQsImlhdCI6MTc4NzEyNjA1NCwiYXVkIjoiR2VvUXVlc3QiLCJzY29wZSI6Ikdlb1F1ZXN0IiwianRpIjoiODE0MDNhYzAtMzlmNS00ZTBmLWJhOTAtNTFlMDQ4NDlkODRiIiwic3ViIjoiM2ExZmM5ZDAtYTg1My1mZjk4LTUwM2EtYzc2MDY3YjQ4OTAyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZW5mb3JjZWFkbWluIiwiZW1haWwiOiJlbmZvcmNlQGlpYXNhLmFjLmF0Iiwicm9sZSI6WyJyZXZpZXdlcjozYTFhNzZhZi1jMjZlLTVlYjktOGFmMC1mNzkzMDJhZjljY2MiLCJyZXZpZXdlcjozYTFjYWNmNy1jN2EwLWRlODYtZTkxNS03YzFlM2IyNWY1Y2YiLCJyZXZpZXdlcjozYTFlMzM0YS0zNzNhLWNkMjgtMjEyZC02OTlmN2FiMzIxNTMiXSwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjoiRmFsc2UiLCJlbWFpbF92ZXJpZmllZCI6IkZhbHNlIiwidW5pcXVlX25hbWUiOiJlbmZvcmNlYWRtaW4iLCJyZW1lbWJlcl9tZSI6IlRydWUiLCJvaV9wcnN0IjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX2F1X2lkIjoiM2ExZmM5ZjgtNzRjMS04OGNhLTU4MjUtNDE3MDk5OWRjMmM5IiwiY2xpZW50X2lkIjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX3Rrbl9pZCI6IjNhMjMyYjMwLTU3OGItYzNjYS0zYTU2LTg2OGVhYmY0YTZkOCJ9.CUX9yzjfz_7PiygOIl5dqIpeiF84ZzULRF01BzGeHUHI7xe9Jrb8MosedFMEoS4CgbU6J-ywPkD6I9Av0kP5dFJDtYjGSV8vqeT9e5RYcX4pqcbMnifhosVFkyPzBkvkPzHIZ0cgr0s2HP9bgSFBWHglS_eIWVOdNqd0s52Z0Od5tsixO2fClhaU7JYPJ553T2s0J6NsyibG0vAR0lfpk3h0eS1ke0jKAJok88P_2kEMr5Ejk-O5zUbWqbpXUeD_3kSFDAFGKDr4smxC2bLrOz4ziLIqnOvUDhnxlWCZkFXPQpxOrVr1ym78v0WqGV2b-X0BG162jtp8MQPMmkU_IswgkQp381ALumXEunz3xuYXJB_xyv7ZDHTvKo7LDMEmVa-shFt9CTBIS71ns3Tvg0UYYxVF-oQUiBJKfgjNXgh9raYxKFJByBN07etwHUv24hRqlEV6JfH2ULeX-i0lTQ4mzgXti_21ZK9yl45hmRLH6gSJAdS1NDmG_fAc4lU64FurPhKQSjaVbyQNt3bPfX2it2Fq-eFNY5lLSlUtSr7VeFd4EHtvzr0IJbhRkBJliHxXqgGoFDlt23KG43BRFtVN6SAr_QX3mCK4ohKnKTmqnggR9kGLze56PF66blQV_XmvELdlwgDIW0ha9Q3i46LHwBrpYVW5Et864z_plAQ");
        
//        
////        OkHttpClient client2 = new OkHttpClient.Builder().addNetworkInterceptor(new GzipRequestInterceptor()).addNetworkInterceptor(new GzipRequestInterceptor()).build();
//        OkHttpClient client2 = new OkHttpClient.Builder().build();
//        client.setHttpClient(client2 );
//        
        QuestApi api = new QuestApi(client);
        
        QuestSurveySubmissionApi questSurveySubmissionApi = new QuestSurveySubmissionApi(client);
        
        try {
            IIASAGeoQuestQuestQuestSurveySubmissions subs = questSurveySubmissionApi.apiQuestsQuestIdSubmissionGet(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"), null, null, null);
            
            List<IIASAGeoQuestQuestQuestSurveySubmissionDto> subslist = subs.getSubmissions();
            
            for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : subslist) {
                SubmissionsMapper.INSTANCE.toDb(iiasaGeoQuestQuestQuestSurveySubmissionDto);
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getImageCount());
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getUserName());
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getSubmissionData());
            }
            
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static void main(String[] args) {             
                
//        try {
//            System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2026-06-22T11:47:23.45047"));
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
       
        ApiClient client = new ApiClient().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        client.getHttpClient().register(ProvenanceResponseFilter.class);
        client.getHttpClient().register(ResponseInterceptor.class);
        
        client.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        client.setBasePath("https://geoqapi.main.geo-wiki.org");
        
        client.getJSON().getContext(null).configOverride(OffsetDateTime.class)
        .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"));   
        
//        client.setUsername("enforceadmin");
//        client.setPassword("Qwertyu1!");
        
        client.setAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjQxODg4Q0IwRDA5OThGMjVBNTU5RUQ3RDg3REY0QUU1NjQ2MjZBMTIiLCJ4NXQiOiJRWWlNc05DWmp5V2xXZTE5aDk5SzVXUmlhaEkiLCJ0eXAiOiJhdCtqd3QifQ.eyJpc3MiOiJodHRwczovL2dlb3EtYXV0aC5paWFzYS5hYy5hdC8iLCJleHAiOjE3ODcwNTM1MDksImlhdCI6MTc4NzA0OTkwOSwiYXVkIjoiR2VvUXVlc3QiLCJzY29wZSI6Ikdlb1F1ZXN0IiwianRpIjoiYjY2YmQ3NzMtN2FiMi00Yjk0LThmMzctZjgyZjE4MmRiYTIxIiwic3ViIjoiM2ExZmM5ZDAtYTg1My1mZjk4LTUwM2EtYzc2MDY3YjQ4OTAyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZW5mb3JjZWFkbWluIiwiZW1haWwiOiJlbmZvcmNlQGlpYXNhLmFjLmF0Iiwicm9sZSI6WyJyZXZpZXdlcjozYTFhNzZhZi1jMjZlLTVlYjktOGFmMC1mNzkzMDJhZjljY2MiLCJyZXZpZXdlcjozYTFjYWNmNy1jN2EwLWRlODYtZTkxNS03YzFlM2IyNWY1Y2YiLCJyZXZpZXdlcjozYTFlMzM0YS0zNzNhLWNkMjgtMjEyZC02OTlmN2FiMzIxNTMiXSwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjoiRmFsc2UiLCJlbWFpbF92ZXJpZmllZCI6IkZhbHNlIiwidW5pcXVlX25hbWUiOiJlbmZvcmNlYWRtaW4iLCJyZW1lbWJlcl9tZSI6IlRydWUiLCJvaV9wcnN0IjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX2F1X2lkIjoiM2ExZmM5ZjgtNzRjMS04OGNhLTU4MjUtNDE3MDk5OWRjMmM5IiwiY2xpZW50X2lkIjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX3Rrbl9pZCI6IjNhMjMyNmE2LTc1YTUtNWI5ZC00OThkLTA0NWQ5N2U2YWE3NyJ9.a-URK7l8BMCron3tSHlCRmIQAvccALieAJp8Dy1KJELZ3-abo851jVRgg_Z1z3pPVIXLBFCuS6jgb7KmbMcXiDkPdcr3sVN_KXYT7lUqEAUulAkirxXz0VL2JaxuJba5l0RTxUCYinRgcYqJhY18LfmZpN1xGHKHOfyTvPo1gKSLc9rAYYNlYLO0p4wAnGBTthIIKWECwHf866jRbi0ZSODUN2N075HQQkASq7SxHC_t1_yh2u56LLU0vzU8ufgbeor2JJXBrxaHkuIR_dcdxLK7tsmyFp4OfAse2njWve5N_i2KnVmwJ2MMcTOwo9qD-xWrqzrrlZiVri7xV_w2brN7kLMDuevfi0kSDJizUzODEE7iEnzHrnJ3E3B7ikqlUhwwyTA_HwGBTWWydGZftZYo1lg4F7AYjQFXqKWrQWk9PGJkADaVv3bf0e4BOLCd2M6XaB8ZEd_ZYqO_0dQlZea7iCFU0Ya9DU5JXjjtHQu1hSu6YLoKoe6DUZvnkzQZWP6_31uVs-wL4YZ-V-ZEAJVzpKTuoknwDK7X4D7lzzAopBfhc5R9Oq4SRcBSbxXrQDe-CvcZxfFHwpJHhkWgJiSj8Zd3g04TkWJje7kiW5b4VRmNxJhqnt2FudhHKtZ6AhlMjw0tfMgmy9ZfPW1tsgU1yNBPHJCdFH7xitux7Kk");
        
//        
////        OkHttpClient client2 = new OkHttpClient.Builder().addNetworkInterceptor(new GzipRequestInterceptor()).addNetworkInterceptor(new GzipRequestInterceptor()).build();
//        OkHttpClient client2 = new OkHttpClient.Builder().build();
//        client.setHttpClient(client2 );
//        
        QuestApi api = new QuestApi(client);
        
        QuestSurveySubmissionApi questSurveySubmissionApi = new QuestSurveySubmissionApi(client);
        
        try {
            IIASAGeoQuestQuestQuestSurveySubmissions subs = questSurveySubmissionApi.apiQuestsQuestIdSubmissionGet(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"), null, null, null);
            
            List<IIASAGeoQuestQuestQuestSurveySubmissionDto> subslist = subs.getSubmissions();
            
            for (IIASAGeoQuestQuestQuestSurveySubmissionDto iiasaGeoQuestQuestQuestSurveySubmissionDto : subslist) {
//                SubmissionsMapper.INSTANCE.toDb(iiasaGeoQuestQuestQuestSurveySubmissionDto);
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getImageCount());
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getUserName());
                System.out.println(iiasaGeoQuestQuestQuestSurveySubmissionDto.getSubmissionData());
            }
            
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        
//        try {
//            
//           ApiCallback<Void> callback;
//            
//            questSurveySubmissionApi.apiQuestsQuestIdSubmissionDownloadGetAsync(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"), new ApiCallback<Void>() {
//                
//                @Override
//                public void onUploadProgress(long bytesWritten,
//                        long contentLength,
//                        boolean done) {
//                    // TODO Auto-generated method stub
//                    
//                }
//                
//                @Override
//                public void onSuccess(Void result,
//                        int statusCode,
//                        Map<String, List<String>> responseHeaders) {
//                    for (Entry<String, List<String>> string : responseHeaders.entrySet()) {
//                        System.out.println(string.getKey());
//                    }
//                    
//                }
//                
//                @Override
//                public void onFailure(ApiException e,
//                        int statusCode,
//                        Map<String, List<String>> responseHeaders) {
//                    // TODO Auto-generated method stub
//                    
//                }
//                
//                @Override
//                public void onDownloadProgress(long bytesRead,
//                        long contentLength,
//                        boolean done) {
//                    // TODO Auto-generated method stub
//                    
//                }
//            });
//            
//            
////            questSurveySubmissionApi.apiQuestsQuestIdSubmissionDownloadGet(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"));
//            
//            
//        } catch (ApiException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
////        try {
////            IIASAGeoQuestQuestQuestInfoDto quest = api.apiQuestsQuestIdGet(UUID.fromString("3a1cacf7-c7a0-de86-e915-7c1e3b25f5cf"));
////            
////            System.out.println(quest.getName());
////            
//////            List<IIASAGeoQuestQuestQuestInfoNamesDto> apis = api.apiQuestsAllGet();
//////            
//////            for (IIASAGeoQuestQuestQuestInfoNamesDto iiasaGeoQuestQuestQuestInfoNamesDto : apis) {
//////                System.out.println(iiasaGeoQuestQuestQuestInfoNamesDto.getName());
//////            }
////        } catch (ApiException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }

    }

}
