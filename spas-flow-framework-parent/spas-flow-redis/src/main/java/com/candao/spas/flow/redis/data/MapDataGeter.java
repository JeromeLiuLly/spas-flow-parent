package com.candao.spas.flow.redis.data;

import java.util.Map;

public interface MapDataGeter<T> {

    /**
     * 获取集合数据接口
     *
     * @return
     */
    public Map<String, T> getMap();
}