package com.pb.synth.cib.refdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.pb.synth.cib.refdata", "com.pb.synth.cib.infra"})
public class ReferenceDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReferenceDataApplication.class, args);
    }
}
