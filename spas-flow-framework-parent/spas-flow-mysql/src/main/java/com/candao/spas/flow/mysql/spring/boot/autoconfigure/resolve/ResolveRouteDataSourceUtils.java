package com.candao.spas.flow.mysql.spring.boot.autoconfigure.resolve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
abstract class ResolveRouteDataSourceUtils {
    /**
     * @param enableDatasource 开启的数据源
     * @param prefix           前缀
     * @param dataSourceMap    数据源Map
     */
    static Map<String, DataSource> resolved(Set<String> enableDatasource,
                                            String prefix,
                                            Map<String, ? extends DataSource> dataSourceMap) {
        Map<String, DataSource> map = new ConcurrentHashMap<>();
        if (enableDatasource != null && !enableDatasource.isEmpty()) {
            if (dataSourceMap != null && !dataSourceMap.isEmpty()) {
                dataSourceMap.forEach((key, dataSource) -> {
                    String dataSourceName = prefix + "-" + key;
                    if (enableDatasource.contains(dataSourceName)) {
                        if (dataSource != null) {
                            map.put(dataSourceName, dataSource);
                            log.info(" [RouteDataSource][RouteDataSource-{}] dataSource -> {} 注入启用成功", prefix, dataSourceName);
                        } else {
                            log.warn(" [RouteDataSource][RouteDataSource-{}] dataSource -> {} 数据源为空,跳过配置", prefix, dataSourceName);
                        }
                    } else {
                        log.warn(" [RouteDataSource][RouteDataSource-{}] dataSource -> {} 数据源未启用,跳过配置", prefix, dataSourceName);
                    }
                });
            } else {
                log.warn(" [RouteDataSource][RouteDataSource-{}]} 数据源为空,跳过配置", prefix);
            }
        } else {
            throw new BeanInitializationException("[RouteDataSource][spring.datasource.route.enableDatasource=null] 未配置开启的数据源");
        }
        return map;
    }
}