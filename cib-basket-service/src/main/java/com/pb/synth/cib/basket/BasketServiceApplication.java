package com.pb.synth.cib.basket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.pb.synth.cib.basket", "com.pb.synth.cib.infra"})
public class BasketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BasketServiceApplication.class, args);
    }
}
