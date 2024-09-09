package com.bytmasoft.dss.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {

    private Upload upload;

    @Getter
    @Setter
    public static class Upload{
        private String maxSize;
        private String location;

    }

}
