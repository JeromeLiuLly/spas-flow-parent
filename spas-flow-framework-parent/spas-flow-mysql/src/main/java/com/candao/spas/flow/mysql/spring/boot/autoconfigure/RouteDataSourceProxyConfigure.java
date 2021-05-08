package com.candao.spas.flow.mysql.spring.boot.autoconfigure;

import com.candao.spas.flow.mysql.bean.RouteDataSource;
import com.candao.spas.flow.mysql.bean.RouteDataSourceMatcher;
import com.candao.spas.flow.mysql.configure.DefaultRouteDataSourceMatcher;
import com.candao.spas.flow.mysql.configure.RouteDataSourceInterceptor;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为路由数据源创建自动代理实现动态数据源
 */
@ConditionalOnBean(RouteDataSource.class)
@ConditionalOnClass({MapperFactoryBean.class})
public class RouteDataSourceProxyConfigure {
    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator(ApplicationContext context) {
        List<String> proxyBeanNames = new LinkedList<>();
        // 支持原生的和 mybatis plus
        {
            Set<String> beanNamesForType = Arrays.stream(context.getBeanNamesForType(MapperFactoryBean.class))
                    .collect(Collectors.toSet());
            for (String beanName : beanNamesForType) {
                Object bean = context.getBean(beanName);
                proxyBeanNames.addAll(Arrays.asList(context.getBeanNamesForType(((MapperFactoryBean<?>) bean).getMapperInterface())));
            }
        }
        try {
            // 兼容 tk mybatis
            Class<?> tkMapperFactoryBeanClz = Class.forName("tk.mybatis.spring.mapper.MapperFactoryBean");
            Set<String> beanNamesForType = Arrays.stream(context.getBeanNamesForType(tkMapperFactoryBeanClz))
                    .collect(Collectors.toSet());
            for (String beanName : beanNamesForType) {
                Object bean = context.getBean(beanName);
                proxyBeanNames.addAll(Arrays.asList(
                        context.getBeanNamesForType(
                                ((tk.mybatis.spring.mapper.MapperFactoryBean<?>) bean).getMapperInterface())
                        )
                );
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        if (proxyBeanNames.size() > 0) {
            //设置要创建代理的那些Bean的名字
            beanNameAutoProxyCreator.setBeanNames(proxyBeanNames.toArray(new String[]{}));
        }
        //设置拦截链名字(这些拦截器是有先后顺序的)
        beanNameAutoProxyCreator.setInterceptorNames(context.getBeanNamesForType(RouteDataSourceInterceptor.class));
        return beanNameAutoProxyCreator;
    }

    /**
     * 当且仅当不存在 RouteDataSourceMatcher 的 bean 的时候才会配置默认的实现
     */
    @Bean
    @ConditionalOnMissingBean({RouteDataSourceMatcher.class})
    public RouteDataSourceMatcher defaultRouteDataSourceMatcher() {
        return new DefaultRouteDataSourceMatcher();
    }

    @Bean
    public RouteDataSourceInterceptor routeDataSourceInterceptor(RouteDataSource routeDataSource, RouteDataSourceMatcher routeDataSourceMatcher) {
        return new RouteDataSourceInterceptor(routeDataSource, routeDataSourceMatcher);
    }
}