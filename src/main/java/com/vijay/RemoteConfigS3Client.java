package com.vijay;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.SSEResultBase;
import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.vijay.model.remoteconfig.GroupParameters;
import com.vijay.model.remoteconfig.ParameterValue;
import com.vijay.model.remoteconfig.Parameters;
import org.apache.log4j.Logger;

public class RemoteConfigS3Client {

    //S3 Region, Bucket and Key (get Region and Bucket from the environment variables that were set on the lambda function)
    //private final static String REGION = "us-west-2";
    private final static String REGION = System.getenv("REGION");
    //private final static String S3_BUCKET_NAME = "feature-flags-test-produs";
    private final static String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    private final static String S3_FILE_KEY = "remote-config.json";
    private final Logger logger = Logger.getLogger(this.getClass());

    protected Map<String, ParameterValue> getRemoteConfigParametersFromS3() throws SdkClientException, AmazonServiceException{

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION)
                .build();

        S3Object s3object = s3client.getObject(S3_BUCKET_NAME, S3_FILE_KEY);
        S3ObjectInputStream inputStream = s3object.getObjectContent();

        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        Gson gson = new Gson();
        Parameters parameters = gson.fromJson(br, Parameters.class);

        Map<String, ParameterValue> map = new HashMap<>(parameters.getParameters());

        Map<String, GroupParameters> parameterGroups = parameters.getParameterGroups();
        if (parameterGroups != null){
            for(String group : parameterGroups.keySet())
                map.putAll(parameterGroups.get(group).getParameters());
        }

        return map;
    }

    protected boolean updateRemoteConfigInS3(String jsonString) throws AmazonServiceException, SdkClientException{
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION)
                .build();

        PutObjectResult putObjectResult = s3client
                .putObject(S3_BUCKET_NAME, S3_FILE_KEY, jsonString);
        return putObjectResult.getETag() != null;
    }

    protected void uploadObjectWithSSEEncryption(String jsonString) {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION)
                .build();

        byte[] objectBytes = jsonString.getBytes();

        // Specify server-side encryption.
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(objectBytes.length);
        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        PutObjectRequest putRequest = new PutObjectRequest(S3_BUCKET_NAME,
                S3_FILE_KEY,
                new ByteArrayInputStream(objectBytes),
                objectMetadata);

        // Upload the object and check its encryption status.
        PutObjectResult putResult = s3Client.putObject(putRequest);
        String encryptionStatus = putResult.getSSEAlgorithm();
        logger.info("Object \"" + S3_FILE_KEY + "\" encryption status is: " + encryptionStatus);
    }

    private static void printEncryptionStatus(SSEResultBase response) {
        String encryptionStatus = response.getSSEAlgorithm();
        if (encryptionStatus == null) {
            encryptionStatus = "Not encrypted with SSE";
        }
        System.out.println("Object encryption status is: " + encryptionStatus);
    }

}

