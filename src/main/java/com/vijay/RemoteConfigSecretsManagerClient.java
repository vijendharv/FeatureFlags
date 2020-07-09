package com.vijay;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import org.apache.log4j.Logger;

import java.util.Base64;

public class RemoteConfigSecretsManagerClient {

    //Secrets Manager Region (get Region from the environment variable set on the lambda function)
    //private final static String REGION = "us-west-2";
    private final static String REGION = System.getenv("REGION");
    private final Logger logger = Logger.getLogger(this.getClass());

    public String getSecret() throws Exception{

        String secretName = "Firebase-Service-Account";

        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(REGION)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.

        String secret = null, decodedBinarySecret = null;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            logger.error("Secrets Manager can't decrypt the protected secret text using the provided KMS key.");
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InternalServiceErrorException e) {
            logger.error("An error occurred on secrets manager server side.");
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidParameterException e) {
            logger.error("An invalid value for a parameter is provided.");
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidRequestException e) {
            logger.error("You Provided a parameter value that is not valid for the current state of the resource.");
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (ResourceNotFoundException e) {
            logger.error("Can't find the resource that you asked for.");
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (Exception e){
            logger.error("Encountered an exception while retrieving the secret from Secrets Manager.");
            throw e;
        }

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
        }
        else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }

        return secret != null? secret: decodedBinarySecret;

    }

}
