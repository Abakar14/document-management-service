package com.bytmasoft;

import com.bytmasoft.dss.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@EnableConfigurationProperties(StorageProperties.class)
@SpringBootApplication(scanBasePackages = {"com.bytmasoft.common", "com.bytmasoft"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
