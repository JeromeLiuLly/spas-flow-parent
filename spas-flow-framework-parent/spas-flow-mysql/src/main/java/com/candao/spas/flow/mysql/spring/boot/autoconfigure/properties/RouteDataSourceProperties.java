package com.candao.spas.flow.mysql.spring.boot.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.Set;

@ToString
@Getter
@Setter
@PropertySource({"classpath:mysql.properties", "classpath:application.properties"})
@ConfigurationProperties("spring.datasource.route")
public class RouteDataSourceProperties {
    private String defaultDatasource;
    private Set<String> enableDatasource;
}