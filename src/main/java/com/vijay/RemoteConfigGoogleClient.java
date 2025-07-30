package com.vijay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vijay.model.secretsmanager.Secret;
import org.apache.log4j.Logger;

public class RemoteConfigGoogleClient {

    //Google Service Account Credentials
    private final static String GOOGLE_SERVICE_ACCOUNT = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"image-ocr-4a4e3\",\n" +
            "  \"private_key_id\": \"ca57eea0afb2dee492bbdc97c7a6a49373b17ade\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDLZcptNNkStpiV\\nyoOOLB0EUxlOYsiAqO8VRgy3KO0b3OmtfdSYYN6uXqRsGdZsUFCpwdtRAAGxERUo\\n5G0rR99tYIE844ZI7MIiGzAxfC9HLo/MqZaqtDbqprvUHBKonRoTuU91uAfd7d3v\\nWUKtv+csU3F2Ol8A1r01HuUQ/GcyKc9ANs0uB4eQPPqo4Q9Ck6483Ncq4dclJ3Wv\\nzqSrx8USjC05tECz3xmkiQ+Ani+T6zdfqRI/hx4q5DHr2Bh7CCrUAnC2CehVVkFi\\njZTMVMxp3rpZUMfmWH5Zwq+2cmawuasAM9d0EMe5JV7P9cWB1EjKDSXfj1fDFiKK\\nh43+0g1LAgMBAAECggEANPXpufrHROyfmW00SQoqegxLLBJjo8CZFEB7oht5dcZV\\nYDPB7fVXNi71gCJQJz7YagNMoHAwgJLyoAWueVULE7dLn3ec6RMgz3Gl4FS2GMJC\\ndid4UDWBkSLeCHJQ12+ksRsQ2hWTktFTdvWWe5ha0LtfcUnsn9LKTHEhcwVNt40d\\nXGLvjWTrIl96OvV2on2R9XVj2Zv2p5BEH/YxViiWzClyvVSIdYROchthI4PADb9l\\natSmpXhaI+TMTP6Os259tEQP/6GR7junxqpaQ0Wp34fQQDPeK8wICFlCN+scNQLQ\\n8micoewii/4KR3WAWKMyVYJi1vEJglbXtCgfXBBMxQKBgQDyBzNjvBZAKgEvsuf+\\nHPSskGZfp7w9GYb8972dvFwITJXPrSX0DDMMmK1cuplwB1R3il8WJXh842eJRd+V\\nWC+g8u0ZA+T8HrPHCVFs/brMYH4RBP+/tJGaOk2hUkZDaviG1JvMQ1PUkW+48Jv7\\nh2qPiQDbhqKYD+TVA0BPCjQ09wKBgQDXI7DwmYxHNRuv670cR3CZUevI3DGII+av\\nZcTQHO4kYVNuZFqqgZIc/LV6MBsuvF7lEIIMF41GQGJszxAm/p0GJMaEwjmpc00U\\nVpuTgPuZsNN3gBNHT/7Gl+sNfRAqM5gkR4i55QEkNZ1yH5eZiPIICJPZkrizUVhK\\nymRVmR4ZTQKBgQCAcUnYeAzm6GPQyIVN2lgO7GMoCIBhfJai0WcTus6sqNgoap9l\\nsMO2v5/hQjYDuiAdgJWNzzzuKlDcDPTm8PH13HXvfJ2dHz4RNuS7jrv/koXUym5f\\ncpiC3MYuAte/F5nlFBfKg0CN4tEDuM1O40KuAesulXjccjLiyOLsD2I9aQKBgQDJ\\nlPCy0Dznb7SspqdAexPfJSpXvzJODM2W5TV/hswAnI0PL8rgXp3ouUTV44NkzC3i\\nVhB0ghDmlWdj6rSIxSOeYRD9zfs3cgj0GJ6XnFCjVlMecowd0q+3pxsgPWHLGSLi\\nQ4CyJghmxDHtf/qNawrVSiw2vkqAAqVHFtLifaD9NQKBgF3Z/ctsoq+vFMcKEH55\\nOIK0vdDohq5mn3E5etVThPZbPys/qlhbEQUcdnkKPjU6bfrfNU06AoNyb8CpIUBm\\nZHQosULtG9B/2Qb83RGjzOg0q2gRcookTT+7sWRp30vd8VESMp/J7cZgxdiFUV30\\nL0EtlFB338vyZQ+plc1uufyk\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"firebase-adminsdk-r7mk0@image-ocr-4a4e3.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"117803934748855471946\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-r7mk0%40image-ocr-4a4e3.iam.gserviceaccount.com\"\n" +
            "}";

    private final static String PROJECT_ID = "image-ocr-4a4e3";
    private final static String BASE_URL = "https://firebaseremoteconfig.googleapis.com";
    private final static String REMOTE_CONFIG_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/remoteConfig";
    private final static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.remoteconfig" };

    private final Logger logger = Logger.getLogger(this.getClass());
    private final RemoteConfigSecretsManagerClient remoteConfigSecretsManagerClient;

    public RemoteConfigGoogleClient() {
        remoteConfigSecretsManagerClient = new RemoteConfigSecretsManagerClient();
    }

    private String getFirebaseProjectServiceAccount() throws Exception {
        String secretJson = remoteConfigSecretsManagerClient.getSecret();
        Gson gson = new Gson();
        Secret secret = gson.fromJson(secretJson, Secret.class);
        return secret.getFirebaseProjectServiceAccount();
    }

    private String getAccessToken(String firebaseProjectServiceAccount) throws IOException{
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(firebaseProjectServiceAccount.getBytes()))
                .createScoped(Arrays.asList(SCOPES));
        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();
        return token.getTokenValue();
    }

    private String inputStreamToString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        scanner.close();
        return stringBuilder.toString();
    }

    public String getRemoteConfigFromFirebase() throws Exception{

        String firebaseProjectServiceAccount = getFirebaseProjectServiceAccount();

        URL url = new URL(BASE_URL + REMOTE_CONFIG_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken(firebaseProjectServiceAccount));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
        String jsonStr = null;

        int code = httpURLConnection.getResponseCode();
        if (code == 200) {
            InputStream inputStream = new GZIPInputStream(httpURLConnection.getInputStream());
            String response = inputStreamToString(inputStream);

            JsonElement jsonElement = JsonParser.parseString(response);

            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            jsonStr = gson.toJson(jsonElement);
        } else {
            logger.error(inputStreamToString(httpURLConnection.getErrorStream()));
        }

        return jsonStr;
    }

}
