package com.candao.spas.flow.redis.config;

import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.redis.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 @DESC: TODO
            @EnableConfigurationProperties 注入RedisStatProperties
            @ConditionalOnProperty redis.properties必须存在spring.candao.redis.mode属性才注入
 @VERSION: 1.0
 ***/
@Configuration
@EnableConfigurationProperties({RedisProperties.class})
@Slf4j
@ConditionalOnClass(RedisClient.class)
public class RedisStatAutoConfigure {
    @Bean
    public RedisClient redisClient(RedisProperties properties) {
        if (properties.getSentinel() != null) {
            RedisInitParamForSentinel initParam = new RedisInitParamForSentinel();
            initParam.setServerA(properties.getSentinel().getNodes().get(0));
            initParam.setServerB(properties.getSentinel().getNodes().get(1));
            initParam.setServerC(properties.getSentinel().getNodes().get(2));
            initParam.setDatabase(properties.getDatabase());
            initParam.setMasterName(properties.getSentinel().getMaster());
            initParam.setPassword(properties.getPassword());
            RedisDataSourceFactory.getInstance().create(RedisDataSourceName.DeFault, initParam);
            log.info("RedisStatAutoConfigure create sentinel success initParam = {}", EasyJsonUtils.toJsonString(initParam));
        } else {
            RedisInitParam initParam = new RedisInitParam();
            initParam.setServer(properties.getHost());
            initParam.setPort(properties.getPort());
            initParam.setDatabase(properties.getDatabase());
            initParam.setPassword(properties.getPassword());
            RedisDataSourceFactory.getInstance().create(RedisDataSourceName.DeFault, initParam);
            log.info("RedisStatAutoConfigure create standalone success initParam = {}", EasyJsonUtils.toJsonString(initParam));
        }
        log.info("RedisStatAutoConfigure create redisClient success .....");
        return new RedisClient();
    }
}