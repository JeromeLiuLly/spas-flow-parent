package com.candao.spas.flow.soa;

import com.candao.spas.flow.sdk.exception.FlowException;
import com.candao.spas.flow.core.model.vo.TransferEventModel;

import java.util.Map;

public interface ISOAService<T,R> {

    /**
     * SOA 调用细节
     *
     * ⚠️ 调用方法不允许单独处理异常错误,需要抛出上层处理❗️❗️❗️
     *
     * @param transfer 事件模型
     * @param t 请求参数
     *
     * @return R 返回参数
     * */
     R handle(TransferEventModel transfer, T t);

    /**
     * SOA 通用化 针对返回结果数据结构
     *
     * */
    default Object getObject(Map<String,Object> responseData,TransferEventModel transfer){
        // 获取第三方返回状态码值
        Object returnCode = responseData.get(transfer.getOutput().getCode());

        // 断言状态码是否成功
        if (returnCode != null){
            // 断言状态码是否成功
            if (returnCode.toString().equals(transfer.getOutput().getValue().toString())){
                return responseData.get(transfer.getOutput().getData());
            }else {
                throw new FlowException(responseData.get(transfer.getOutput().getMsg()).toString());
            }
        }else {
            throw new FlowException("请求状态码配置有误,无法获取状态码信息.");
        }

    }

}
