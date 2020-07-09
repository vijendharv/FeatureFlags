package com.vijay.model.remoteconfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ParameterValue {

    @SerializedName("defaultValue")
    @Expose
    private Value defaultValue;

    @SerializedName("conditionalValues")
    @Expose
    private Map<String, Value> conditionalValues;

    public Value getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, Value> getConditionalValues() {
        return conditionalValues;
    }

    public void setConditionalValues(Map<String, Value> conditionalValues) {
        this.conditionalValues = conditionalValues;
    }
}