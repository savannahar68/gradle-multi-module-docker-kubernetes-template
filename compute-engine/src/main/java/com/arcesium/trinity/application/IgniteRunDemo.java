package com.example.org.application;

import com.example.org.configuration.IgniteClientConfiguration;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IgniteRunDemo {
    @Autowired
    public IgniteRunDemo(IgniteClientConfiguration igniteClientConfiguration) {
        Ignite igniteInstance = igniteClientConfiguration.getIgnite();
    }
}
