package com.candao.spas.flow.redis.data;

import java.util.Set;

public interface SetDataGeter<T> {
    /**
     * 获取Set集合数据
     *
     * @return
     */
    Set<T> getSet();
}