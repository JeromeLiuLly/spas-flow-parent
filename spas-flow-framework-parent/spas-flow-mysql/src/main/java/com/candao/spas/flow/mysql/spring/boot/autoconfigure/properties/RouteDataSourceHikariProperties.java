package com.candao.spas.flow.mysql.spring.boot.autoconfigure.properties;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@Getter
@Setter
@Slf4j
@PropertySource({"classpath:mysql.properties", "classpath:application.properties"})
@ConfigurationProperties(value = "spring.datasource.route", ignoreInvalidFields = true)
public class RouteDataSourceHikariProperties {
    @Autowired
    private RouteDataSourceProperties routeDataSourceProperties;
    private Map<String, HikariDataSource> hikari;

    public void setHikari(Map<String, HikariDataSource> hikari) {
        Map<String, HikariDataSource> map = new ConcurrentHashMap<>();
        Set<String> enableDatasource = routeDataSourceProperties.getEnableDatasource();
        if (enableDatasource != null && !enableDatasource.isEmpty()
                && hikari != null && !hikari.isEmpty()) {
            hikari.forEach((key, dataSource) -> {
                if (enableDatasource.contains("hikari" + "-" + key)) {
                    map.put(key, dataSource);
                    log.info(" [RouteDataSource][RouteDataSource-{}] dataSource -> {} 数据源已经启用,读取配置成功", "hikari", key);
                } else {
                    log.warn(" [RouteDataSource][RouteDataSource-{}] dataSource -> {} 数据源未启用,跳过配置", "hikari", key);
                }
            });
        }
        this.hikari = map;
    }

    public Map<String,HikariDataSource> getHikari(){return hikari;}
}