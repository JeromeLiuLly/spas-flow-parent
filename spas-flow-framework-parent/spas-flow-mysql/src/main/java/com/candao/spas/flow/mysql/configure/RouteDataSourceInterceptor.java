package com.candao.spas.flow.mysql.configure;

import com.candao.spas.flow.mysql.annotation.RouteSelector;
import com.candao.spas.flow.mysql.bean.RouteDataSource;
import com.candao.spas.flow.mysql.bean.RouteDataSourceMatcher;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class RouteDataSourceInterceptor implements MethodInterceptor {
    private RouteDataSource routeDataSource;
    private RouteDataSourceMatcher routeDataSourceMatcher;

    public RouteDataSourceInterceptor(RouteDataSource routeDataSource, RouteDataSourceMatcher routeDataSourceMatcher) {
        this.routeDataSource = routeDataSource;
        this.routeDataSourceMatcher = routeDataSourceMatcher;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Object obj = invocation.getThis();
            Method method = invocation.getMethod();
            String methodPath;
            String methodName = method.getName();
            if (methodName.equals("toString") || methodName.equals("hashCode") || methodName.equals("equals")) {
                return invocation.proceed();
            }
            if (Proxy.isProxyClass(obj.getClass())) {
                methodPath = AopProxyUtils.proxiedUserInterfaces(obj)[0].getName() + "." + methodName;
            } else {
                methodPath = method.getDeclaringClass().getName() + "." + methodName;
            }
            String dataSourceName = null;
            // 类级别
            RouteSelector routeSelector = ClassUtils.getUserClass(method.getDeclaringClass())
                    .getAnnotation(RouteSelector.class);
            if (routeSelector != null) {
                dataSourceName = routeSelector.value();
            }
            // 方法级别
            RouteSelector methodRouteSelector = method.getAnnotation(RouteSelector.class);
            if (methodRouteSelector != null) {
                dataSourceName = methodRouteSelector.value();
            }
            // 配置文件级别
            String match = routeDataSourceMatcher.match(methodPath);
            if (!StringUtils.isEmpty(match)) {
                dataSourceName = match;
            }
            String defaultDatasourceName = routeDataSource.getDefaultDatasourceName();
            if (!StringUtils.isEmpty(dataSourceName)) {
                select(methodPath, dataSourceName, defaultDatasourceName);
            } else {
                log.debug(" [RouteDataSource][" + defaultDatasourceName + "] 未匹配数据源,使用默认数据源 " + defaultDatasourceName
                        + " -> " + methodPath);
            }
            return invocation.proceed();
        } finally {
            RouteDataSource.removeSwitchDatasource();
        }
    }

    private void select(String methodPath, String dataSourceName, String defaultDatasourceName) {
        if (routeDataSource.getDatasourceNames().contains(dataSourceName)) {
            if (dataSourceName.equals(defaultDatasourceName)) {
                log.debug(" [RouteDataSource][" + defaultDatasourceName + "] 使用默认数据源 " + defaultDatasourceName + " -> "
                        + methodPath);
            } else {
                RouteDataSource.switchDatasource(dataSourceName);
                log.debug(" [RouteDataSource][" + dataSourceName + "] 切换数据源 " + dataSourceName + " -> " + methodPath);
            }
        } else {
            log.debug(" [RouteDataSource][" + defaultDatasourceName + "] 无法找到数据源(" + dataSourceName + "),即将使用默认数据源 "
                    + defaultDatasourceName + " -> " + methodPath);
        }
    }
}