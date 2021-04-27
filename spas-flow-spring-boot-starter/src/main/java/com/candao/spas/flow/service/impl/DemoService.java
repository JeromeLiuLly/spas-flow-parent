package com.candao.spas.flow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DemoService {

    public void sayHello(){
       log.info("伟大的HelloWorld！！！");
    }
}
