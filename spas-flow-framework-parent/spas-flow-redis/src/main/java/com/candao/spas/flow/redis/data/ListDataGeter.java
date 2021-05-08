package com.candao.spas.flow.redis.data;

import java.util.List;

public interface ListDataGeter<T> {
    /**
     * 获取列表数据接口
     *
     * @return
     */
    public List<T> getList();
}