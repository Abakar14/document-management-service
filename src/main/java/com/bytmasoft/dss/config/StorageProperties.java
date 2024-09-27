package com.bytmasoft.dss.config;



import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {

    private Upload upload;
    private Document document;

    @Getter
    @Setter
    public static class Upload{
        private String maxSize;
        private String location;

    }

    @Getter
    @Setter
    public static class Document{
        private List<String> supportedFileTypes;

    }

}
