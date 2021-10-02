package com.example.org.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ClientConfiguration {
    private final String clientId;

    @Autowired
    public ClientConfiguration(Environment env) {
        clientId = env.getProperty("properties.driver.clientId");
        assert clientId != null;
    }

    public String getClientId() {
        return clientId;
    }
}
