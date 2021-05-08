package com.candao.spas.flow.mysql.bean;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
public class RouteDataSource extends AbstractRoutingDataSource {
	private static final ThreadLocal<String> ROUTE_DATASOURCE_IN_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);
	private final Set<String> datasourceNames = new TreeSet<>();
	private String defaultDatasourceName;
	private static Map<Object, Object> dataSources;
	private RouteDataSource() {
	}
	public static RouteDataSource initRouteDataSource(Map<String, ? extends DataSource> targetDataSources,
			String defaultDatasourceName, DataSource defaultTargetDataSource) {
		RouteDataSource routeDataSource = new RouteDataSource();
		dataSources = new HashMap<>(targetDataSources.size());
		targetDataSources.forEach((dataSourceName, dataSource) -> {
			routeDataSource.getDatasourceNames().add(dataSourceName);
			dataSources.put(dataSourceName, dataSource);
		});
		routeDataSource.setTargetDataSources(dataSources);
		routeDataSource.setDefaultTargetDataSource(defaultTargetDataSource);
		routeDataSource.setDefaultDatasourceName(defaultDatasourceName);
		return routeDataSource;
	}
	public static void switchDatasource(String datasource) {
		RouteDataSource.ROUTE_DATASOURCE_IN_THREAD_LOCAL.set(datasource);
	}
	public static String getSwitchDatasource() {
		return RouteDataSource.ROUTE_DATASOURCE_IN_THREAD_LOCAL.get();
	}
	public static void removeSwitchDatasource() {
		RouteDataSource.ROUTE_DATASOURCE_IN_THREAD_LOCAL.remove();
	}
	public static Map<Object, Object> getDataSource(){return dataSources;}
	@Override
	protected Object determineCurrentLookupKey() {
		return RouteDataSource.getSwitchDatasource();
	}
}