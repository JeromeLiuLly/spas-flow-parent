package com.candao.spas.flow.sdk.service;

import com.candao.spas.flow.core.constants.ChainConstants;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.ClassUtil;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.googlecode.aviator.AviatorEvaluator;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        TransferEventModel transfer = input.getNode().getTransfer();
        if (NodeParserEnum.ROOT.getValue().equals(input.getNode().getFront())){
            convert(transfer,input.getData());
            return input.getData();
        }else{
            convert(transfer,output.getData());
            return (T) output.getData();
        }
    }


    default void convert(TransferEventModel transfer,Object objData){
        // 断言是否设置入参类型
        if (transfer != null && transfer.getInputParamTypesValues() != null){
            List list = new ArrayList();
            List listParam = new ArrayList();
            transfer.getInputParamTypesValues().forEach((value)->{
                String[] split = value.split(ChainConstants.COLON);
                Class<?> cls;
                if (split.length > 1){
                    cls = ClassUtil.forName(split[0]);
                    Object o = transfer(objData,cls,split[1]);
                    list.add(o);
                    listParam.add(split[0]);
                }else {
                    cls = ClassUtil.forName(value);
                    Object o = transfer(objData,cls,null);
                    list.add(o);
                    listParam.add(value);
                }
            });
            transfer.setInputParamTypes(list);
            transfer.setInputParamTypesValues(listParam);
        }
    }

    // 执行表达式
    default Object transfer(Object objData,Class cls,String method) {
        try {
            if (method == null) {
                Object newObject = cls.newInstance();
                if (objData.getClass().isAssignableFrom(String.class)){
                    objData = EasyJsonUtils.toJavaObject(objData,Map.class);
                }
                newObject = EasyJsonUtils.toJavaObject(objData,newObject.getClass());
                return newObject;
            } else {
                Map<String,Object> param = EasyJsonUtils.toJavaObject(objData,Map.class);
                Object eval = AviatorEvaluator.execute(method,param,true);
                return eval;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
