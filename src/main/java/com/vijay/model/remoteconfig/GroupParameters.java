package com.vijay.model.remoteconfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GroupParameters {

    @SerializedName("parameters")
    @Expose
    private Map<String, ParameterValue> parameters;

    public Map<String, ParameterValue> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ParameterValue> parameters) {
        this.parameters = parameters;
    }
}
