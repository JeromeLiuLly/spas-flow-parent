package com.candao;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.candao.spas.flow"})
public class SampleHttpServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleHttpServiceApplication.class, args);
    }
}
