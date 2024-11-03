package com.bytmasoft;

import com.bytmasoft.dss.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableDiscoveryClient
@EnableConfigurationProperties(StorageProperties.class)
@EnableJpaAuditing
@SpringBootApplication
//@SpringBootApplication(scanBasePackages = {"com.bytmasoft.common", "com.bytmasoft"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
