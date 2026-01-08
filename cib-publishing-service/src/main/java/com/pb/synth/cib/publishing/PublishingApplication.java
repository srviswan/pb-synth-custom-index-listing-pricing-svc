package com.pb.synth.cib.publishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.pb.synth.cib.publishing", "com.pb.synth.cib.infra"})
public class PublishingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublishingApplication.class, args);
    }
}
