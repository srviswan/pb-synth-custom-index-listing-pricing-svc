package com.pb.synth.cib.listing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.pb.synth.cib.listing", "com.pb.synth.cib.infra"})
public class ListingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListingApplication.class, args);
    }
}
