package com.vijay.model.secretsmanager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Secret {

    @SerializedName("firebaseProjectServiceAccount")
    @Expose
    private String firebaseProjectServiceAccount;

    public String getFirebaseProjectServiceAccount() {
        return firebaseProjectServiceAccount;
    }

    public void setFirebaseProjectServiceAccount(String firebaseProjectServiceAccount) {
        this.firebaseProjectServiceAccount = firebaseProjectServiceAccount;
    }
}
