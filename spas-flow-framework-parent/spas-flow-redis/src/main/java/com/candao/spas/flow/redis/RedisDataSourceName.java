package com.candao.spas.flow.redis;

public enum RedisDataSourceName {
    /**
     * 默认
     */
    DeFault("default"),
    /**
     * 国际化
     */
    International("international");
    private String name;

    private RedisDataSourceName(String name) {
        this.name = name;
    }

    /**
     * 通过数据源名称获取数据源常量对象
     *
     * @param name 数据源名称
     * @return
     */
    public static RedisDataSourceName getRedisDataSourceName(String name) {
        for (RedisDataSourceName obj : RedisDataSourceName.values()) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}