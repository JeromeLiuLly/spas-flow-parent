package com.candao.spas.flow.mysql.configure;

import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.mysql.bean.RouteDataSourceConfig;
import com.candao.spas.flow.mysql.bean.RouteDataSourceMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DefaultRouteDataSourceMatcher implements RouteDataSourceMatcher {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final Map<String, RouteDataSourceConfig> expressionMap = new ConcurrentHashMap<>();
    private final Map<String, String> matchPathCache = new ConcurrentHashMap<>();
    @Value("${spring.datasource.route.config:}")
    private String config;
    private String oldConfig;

    @Override
    public void refresh(List<RouteDataSourceConfig> routeDataSourceConfigs) {
        expressionMap.clear();
        matchPathCache.clear();
        this.oldConfig = this.config;
        if (routeDataSourceConfigs != null && !routeDataSourceConfigs.isEmpty()) {
            expressionMap.putAll(routeDataSourceConfigs.stream()
                    .collect(
                            Collectors.toMap(
                                    RouteDataSourceConfig::getExpression,
                                    routeDataSourceConfig -> routeDataSourceConfig,
                                    (routeDataSourceConfig, routeDataSourceConfig2) -> {
                                        log.warn(" [RouteDataSourceMatch] 路由配置重复,已经存在 expression {}", routeDataSourceConfig.getExpression());
                                        return routeDataSourceConfig;
                                    })));
        }
    }

    @Override
    public String match(String matchPath) {
        // 为空不切换
        if (StringUtils.isEmpty(config)) {
            return null;
        }
        try {
            // 刷新配置
            if (!config.equals(oldConfig)) {
                this.refresh(EasyJsonUtils.toJavaList(config, RouteDataSourceConfig.class));
            }
            String expressionCache = matchPathCache.get(matchPath);
            if (expressionCache != null) {
                return expressionCache;
            }
            for (String expression : expressionMap.keySet()) {
                if (antPathMatcher.match(expression, matchPath)) {
                    RouteDataSourceConfig routeDataSourceConfig = expressionMap.get(expression);
                    String dataSourceName = routeDataSourceConfig.getDataSourceName();
                    matchPathCache.put(matchPath, dataSourceName);
                    return dataSourceName;
                }
            }
        } catch (Throwable e) {
            log.error(" [RouteDataSourceMatch] 数据源切换失败,即将使用默认数据源", e);
        }
        return null;
    }
}