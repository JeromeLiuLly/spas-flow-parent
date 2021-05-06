package com.candao.spas.flow.sdk.service;

import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.ClassUtil;
import com.candao.spas.flow.core.utils.EasyJsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 根据节点情况,获取入参信息
     *
     * @param input 入参
     * @param output 出参
     *
     * */
    default T inputData(RequestFlowDataVo<T> input, ResponseFlowDataVo<R> output){
        // 断言是否是根节点(开始节点)
        if (NodeParserEnum.ROOT.getValue().equals(input.getNode().getFront())){
            TransferEventModel transfer = input.getNode().getTransfer();
            // 断言是否设置入参类型
            if (transfer != null && transfer.getInputParamTypesValues() != null){
                List list = new ArrayList();
                transfer.getInputParamTypesValues().forEach((value)->{
                    Class<?> cls =  ClassUtil.forName(value);

                });
            }
            return input.getData();
        }else{
            return (T) output.getData();
        }
    }

    default void transfer(TransferEventModel transfer, Class cls){
        if (cls.getClass().equals(Integer.class)){

        }
    }

}
