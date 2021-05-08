package com.candao.spas.flow.sample;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@DubboComponentScan
@SpringBootApplication(scanBasePackages={"com.candao.spas.flow"})
@MapperScan(basePackages = {"com.candao.spas.flow.sdk.mapper"})
public class SampleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleServiceApplication.class, args);
    }

}
