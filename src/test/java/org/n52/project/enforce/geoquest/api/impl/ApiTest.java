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
        
        client.setAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjQxODg4Q0IwRDA5OThGMjVBNTU5RUQ3RDg3REY0QUU1NjQ2MjZBMTIiLCJ4NXQiOiJRWWlNc05DWmp5V2xXZTE5aDk5SzVXUmlhaEkiLCJ0eXAiOiJhdCtqd3QifQ.eyJpc3MiOiJodHRwczovL2dlb3EtYXV0aC5paWFzYS5hYy5hdC8iLCJleHAiOjE3ODc3MzQ0ODksImlhdCI6MTc4NzczMDg4OSwiYXVkIjoiR2VvUXVlc3QiLCJzY29wZSI6Ikdlb1F1ZXN0IiwianRpIjoiZjJmY2Y1N2YtMDJiYi00ZWQwLWE2M2EtMWQ3ZjMxOWUxOTY1Iiwic3ViIjoiM2ExZmM5ZDAtYTg1My1mZjk4LTUwM2EtYzc2MDY3YjQ4OTAyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZW5mb3JjZWFkbWluIiwiZW1haWwiOiJlbmZvcmNlQGlpYXNhLmFjLmF0Iiwicm9sZSI6WyJyZXZpZXdlcjozYTFhNzZhZi1jMjZlLTVlYjktOGFmMC1mNzkzMDJhZjljY2MiLCJyZXZpZXdlcjozYTFjYWNmNy1jN2EwLWRlODYtZTkxNS03YzFlM2IyNWY1Y2YiLCJyZXZpZXdlcjozYTFlMzM0YS0zNzNhLWNkMjgtMjEyZC02OTlmN2FiMzIxNTMiXSwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjoiRmFsc2UiLCJlbWFpbF92ZXJpZmllZCI6IkZhbHNlIiwidW5pcXVlX25hbWUiOiJlbmZvcmNlYWRtaW4iLCJyZW1lbWJlcl9tZSI6IlRydWUiLCJvaV9wcnN0IjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX2F1X2lkIjoiM2ExZmM5ZjgtNzRjMS04OGNhLTU4MjUtNDE3MDk5OWRjMmM5IiwiY2xpZW50X2lkIjoiR2VvUXVlc3RfU3dhZ2dlciIsIm9pX3Rrbl9pZCI6IjNhMjM0ZjNkLTY0YTAtMmNkOC05MjFiLTFjMGYzMjc3YzkxZSJ9.FQ07G5kCpmPHiA0KGtMMBDdKpZeJmmULUADu_dWkmjrliZqjkoORA9wlMVNNYLltSavtosl8Motpg6HktkJku-gTcucgQqBCkx4Q3TWMxBTRP0TjPRB3Swwsb5oPgMdfgZqWNggQTAiS_MnGbIVVvvomuow-uvzxNSDuHDfS-t9yhvroYMRtG4T5i0SZftCz2kU9YX6wdWc0RK8r-gw3U2zvSMZeQwpj0KvmBVsNc_ArKx-LM0WACsNuTiAjbzcrtibKyMfXoTFmOn3L7UOyECVwP20q5jXvu-UtRGIZSKlxYmbyhAc3Qv60OtI8ALRvdM0yuNs5EalmmTA1bXtCrJYwboG_Y6x_xagR_06iLcOOVYweiliXdGrF5KOmw7CPmbMbTlUaj317pdOLAu8Sh-IfG0wgnOzbvTJM4R8AG4dwntzjYmifvZkoL2yJOKuPNEngZd5aARRZKGXJlTdHsgyU0Qyo1I-JdcsrVPK_6K-UneGfYrY6LU7mFOPKkpfUX-Q-NA3riwPSZ0RxYxmVVexa22JQ16Fn75yQE9wzv-HYSCB1gTT1EslbWMu6hhHD665Y3JXBU2tcSPgqBeWljC-BxKAbnxsTXffC_TtT0wpAUD8-i9x3KBDi1zJ9fFyP-4DtaI0SQ-FTfu8N9EgU0vWOzYnRtDSQNaZq9gHh1p0");
        
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
