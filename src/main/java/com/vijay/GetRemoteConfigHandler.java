package com.vijay;

import java.time.Instant;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.log4j.Logger;

public class GetRemoteConfigHandler implements RequestHandler<Void, Void> {

    private final RemoteConfigS3Client remoteConfigS3Client;
    private final RemoteConfigGoogleClient remoteConfigGoogleClient;
    private final Logger logger = Logger.getLogger(this.getClass());

    public GetRemoteConfigHandler() {
        remoteConfigS3Client = new RemoteConfigS3Client();
        remoteConfigGoogleClient = new RemoteConfigGoogleClient();
    }

    public Void handleRequest(Void input, Context context) {

        logger.info("Started invocation at: " + Instant.now().toEpochMilli());
        try {
            String jsonString = remoteConfigGoogleClient.getRemoteConfigFromFirebase();
            //remoteConfigS3Client.updateRemoteConfigInS3(jsonString);
            remoteConfigS3Client.uploadObjectWithSSEEncryption(jsonString);
        } catch (Exception ex) {
            logger.error("Encountered error in fetching remote config and updating the S3 bucket: " + ex);
        }
        logger.info("Completed invocation at: " + Instant.now().toEpochMilli());

        return null;
    }
}
