package com.vijay.model.remoteconfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Parameters {

    @SerializedName("parameters")
    @Expose
    private Map<String, ParameterValue> parameters;

    @SerializedName("parameterGroups")
    @Expose
    private Map<String, GroupParameters> parameterGroups;

    public Map<String, ParameterValue> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ParameterValue> parameters) {
        this.parameters = parameters;
    }

    public Map<String, GroupParameters> getParameterGroups() {
        return parameterGroups;
    }

    public void setParameterGroups(Map<String, GroupParameters> parameterGroups) {
        this.parameterGroups = parameterGroups;
    }
}