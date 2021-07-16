package com.martin.zlatkov.configurations;

import com.martin.zlatkov.helpers.StorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigApp {

    @Bean
    public StorageProperties StorageProperties() {
        return new StorageProperties();
    }
}
