package com.vijay;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.vijay.model.apigateway.ApiGatewayRequest;
import com.vijay.model.apigateway.ApiGatewayResponse;
import com.vijay.model.remoteconfig.ParameterValue;
import com.vijay.model.remoteconfig.Value;
import org.apache.log4j.Logger;

public class GetParameterHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayResponse>{

    private final Logger logger = Logger.getLogger(this.getClass());

    //Used for caching data if the lambda function is executed before its is teared down.
    private Map<String, ParameterValue> allParameters;
    private final RemoteConfigS3Client remoteConfigS3Client;

    //Static strings for Conditions defined in Remote Config.
    private static final String CONDITION_IOS = "iOS";
    private static final String CONDITION_ANDROID = "Android";

    public GetParameterHandler() {
        allParameters = new HashMap<String, ParameterValue>();
        remoteConfigS3Client = new RemoteConfigS3Client();
    }

    @Override
    public ApiGatewayResponse handleRequest(ApiGatewayRequest input, Context context) {

        logger.info("Input: " + input);
        try
        {
            String parameter = input.getPathParameters().get("parameter");
            String userAgent = input.getHeaders().get("User-Agent");

            if (allParameters.isEmpty()) {
                allParameters = remoteConfigS3Client.getRemoteConfigParametersFromS3();
            }

            if (allParameters.containsKey(parameter)) {
                ParameterValue parameterValue = allParameters.get(parameter);
                Value value = parameterValue.getDefaultValue();

                Map<String, Value> conditionalValue = parameterValue.getConditionalValues();
                if (conditionalValue != null) {
                    if (userAgent.contains(CONDITION_IOS) && conditionalValue.containsKey(CONDITION_IOS))
                        value = conditionalValue.get(CONDITION_IOS);
                    else if (userAgent.contains(CONDITION_ANDROID) && conditionalValue.containsKey(CONDITION_ANDROID))
                        value = conditionalValue.get(CONDITION_ANDROID);
                }

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(value)
                        .build();
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setRawBody("Parameter: '" + parameter + "' not found.")
                    .build();

        }catch(Exception ex) {
            //send the error response back
            logger.error("Error in retrieving parameter: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody("Server encountered error in retrieving parameter")
                    .build();
        }
    }

}
