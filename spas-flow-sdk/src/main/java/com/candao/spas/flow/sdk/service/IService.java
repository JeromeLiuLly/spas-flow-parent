package com.candao.spas.flow.sdk.service;

import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;

import java.io.Serializable;

/***
 * 定义执行节点的方法
 *
 * */
public interface IService<T,R> extends Serializable {

    /**
     * success 成功服务
     *
     * @param input 入参
     * @param output 出参
     *
     * @throws Exception exception
     * */
    default void success(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output) throws Exception{};

    /**
     * handle 执行服务
     *
     * @param input 入参
     * @param output 出参
     *
     * @throws Exception exception
     * */
    void handle(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output) throws Exception;

    /**
     * complate 完成服务
     *
     * @param input 入参
     * @param output 出参
     *
     * @throws Exception exception
     * */
    default void complate(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output) throws Exception{};

    /**
     * fail 失败服务
     *
     * @param input 入参
     * @param output 出参
     *
     * @throws Exception exception
     * */
    default void fail(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output) throws Exception{};

    /**
     * rollback 回滚服务
     *
     * @param input 入参
     * @param output 出参
     *
     * @throws Exception exception
     * */
    default void rollback(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output) throws Exception{};

}
