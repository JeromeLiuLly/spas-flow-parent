package com.candao.spas.flow.mysql.bean;

import com.candao.spas.flow.mysql.spring.boot.autoconfigure.properties.RouteDataSourceHikariProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteDataSourcePoolMetadataProvider {
    @Autowired
    RouteDataSourceHikariProperties routeDataSourceHikariProperties;


    @Bean("dataSourcePoolMetadataProvider")
    public List<HikariDataSourcePoolMetadata> hikarPoolDataSourceMetadataProvider() {
        List<HikariDataSourcePoolMetadata> list = new ArrayList<>();
        for(String key : routeDataSourceHikariProperties.getHikari().keySet()){
            list.add(new HikariDataSourcePoolMetadata(routeDataSourceHikariProperties.getHikari().get(key)));
        }
        return list;
    }


}
