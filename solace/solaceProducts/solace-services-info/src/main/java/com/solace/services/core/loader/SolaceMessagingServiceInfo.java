package com.solace.services.core.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solace.services.core.model.SolaceServiceCredentialsImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
class SolaceMessagingServiceInfo {
    private SolaceServiceCredentialsImpl credentials;
    private String name;

    public SolaceServiceCredentialsImpl getCredentials() {
        return credentials;
    }

    public void setCredentials(SolaceServiceCredentialsImpl credentials) {
        this.credentials = credentials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
