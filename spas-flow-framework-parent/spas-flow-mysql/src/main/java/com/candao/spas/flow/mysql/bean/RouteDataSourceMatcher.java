package com.candao.spas.flow.mysql.bean;

import java.util.List;

public interface RouteDataSourceMatcher {
	void refresh(List<RouteDataSourceConfig> routeDataSourceConfigs);
	String match(String matchPath);
}