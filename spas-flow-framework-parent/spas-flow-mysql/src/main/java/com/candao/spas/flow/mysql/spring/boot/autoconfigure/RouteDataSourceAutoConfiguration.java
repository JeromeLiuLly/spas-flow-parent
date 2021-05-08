package com.candao.spas.flow.mysql.spring.boot.autoconfigure;

import com.candao.spas.flow.mysql.bean.RouteDataSource;
import com.candao.spas.flow.mysql.spring.boot.autoconfigure.properties.RouteDataSourceProperties;
import com.candao.spas.flow.mysql.spring.boot.autoconfigure.resolve.HikariResolver;
import com.candao.spas.flow.mysql.spring.boot.autoconfigure.resolve.RouteDatasourceResolve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由数据源自动配置实现相关
 * <code>
 * // 默认情况不需要指定 spring.datasource.type 类型，会自动匹配 com.candao.spas.flow.mysql.bean.RouteDataSource
 * spring.datasource.type=com.candao.spas.flow.mysql.bean.RouteDataSource
 * </code>
 * <p>
 * 自动配置顺序先于 {@see org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class}
 */
@Slf4j
@Configuration
@ConditionalOnClass({DataSource.class, RouteDataSource.class})
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.candao.spas.flow.mysql.bean.RouteDataSource", matchIfMissing = true)
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(value = {RouteDataSourceProperties.class})
@Import(RouteDataSourceProxyConfigure.class)
public class RouteDataSourceAutoConfiguration {
    @Import({HikariResolver.class})
    static class RouteDataSourceMetadataProviderConfiguration implements ApplicationContextAware {
        ApplicationContext applicationContext;
        @Primary
        @Bean
        public RouteDataSource routeDataSource(RouteDataSourceProperties routeDataSourceProperties) {
            // 获取所有的数据源解析器
            Map<String, RouteDatasourceResolve> datasourceResolveMap = applicationContext.getBeansOfType(RouteDatasourceResolve.class);
            // 参与初始化的数据源集合
            final Map<String, DataSource> initDataSourceMap = new ConcurrentHashMap<>();
            // 迭代解析数据源并且把开启的数据源加载到初始化数据源集合
            datasourceResolveMap.forEach((beanName, routeDatasourceResolve) -> {
                Map<String, ? extends DataSource> resolve = routeDatasourceResolve.resolve();
                if (!resolve.isEmpty()) {
                    initDataSourceMap.putAll(resolve);
                    log.info(" [RouteDataSource] 获取到数据源解析器 beanName:{}, 解析的数据源[{}]", beanName, StringUtils.collectionToDelimitedString(resolve.keySet(), "],["));
                } else {
                    log.warn(" [RouteDataSource] 获取到数据源解析器 beanName:{}, 可是没有解析的数据源", beanName);
                }
            });
            if (initDataSourceMap.isEmpty()) {
                throw new BeanInitializationException(" [RouteDataSource] 找不到配置的多数据源");
            }
            String defaultDatasource = routeDataSourceProperties.getDefaultDatasource();
            String defaultDatasourceName;
            DataSource defaultTargetDataSource;
            if (initDataSourceMap.size() == 1) {
                // 假如参与初始化的数据源集合中的数据源只有一个
                // 1.假如默认数据源没有填写,默认使用当前唯一的数据源作为默认数据源
                // 2.假如填写的默认数据源的名称不是当前唯一的数据,触发容错机制自动使用当前唯一的数据源作为默认数据源
                String onlyOneDataSource = new ArrayList<>(initDataSourceMap.keySet()).get(0);
                if (StringUtils.isEmpty(defaultDatasource)) {
                    log.warn(" [RouteDataSource] 当前没有配置默认数据源,而且只存在一个数据源 {},所以默认数据源设置为 {}", onlyOneDataSource, onlyOneDataSource);
                } else {
                    if (!onlyOneDataSource.equals(defaultDatasource)) {
                        log.warn(" [RouteDataSource] 找不到 {} 这个配置的默认数据源,不过当前环境只存在一个数据源 {},所以默认数据源设置为 {}", defaultDatasource, onlyOneDataSource, onlyOneDataSource);
                    } else {
                        log.info(" [RouteDataSource] 默认数据源设置为 {}", onlyOneDataSource);
                    }
                }
                defaultDatasourceName = onlyOneDataSource;
                defaultTargetDataSource = initDataSourceMap.get(onlyOneDataSource);
            } else {
                // 假如参与初始化的数据源集合中的数据源存在多个数据源
                // 1.假如默认数据源没有填写,抛出 BeanInitializationException 停止当前初始化
                // 2.假如填写的默认数据源的名称在初始化的数据源集合中无法找到对应的数据源,抛出 BeanInitializationException 停止当前初始化
                if (StringUtils.isEmpty(defaultDatasource)) {
                    throw new BeanInitializationException(" [RouteDataSource] 默认数据源没有配置 [defaultDatasource= null]");
                }
                DataSource dataSource = initDataSourceMap.get(defaultDatasource);
                if (dataSource == null) {
                    throw new BeanInitializationException(" [RouteDataSource] 配置的默认数据源[defaultDatasource=" + defaultDatasource + "]找不到配置的数据源");
                }
                defaultDatasourceName = defaultDatasource;
                defaultTargetDataSource = dataSource;
            }
            return RouteDataSource.initRouteDataSource(initDataSourceMap, defaultDatasourceName, defaultTargetDataSource);
        }
        @Override
        public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }
}