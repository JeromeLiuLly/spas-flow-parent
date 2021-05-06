package com.candao.spas.flow.soa;


import com.candao.spas.flow.core.constants.ChainConstants;
import com.candao.spas.flow.core.exception.FlowException;
import com.candao.spas.flow.core.model.vo.TransferEventModel;

import java.util.Map;

public interface ISOAService<T,R> {

    /**
     * SOA 调用细节
     *
     * @param transfer 事件模型
     * @param t 请求参数
     *
     * @return R 返回参数
     * */
    public R handle(TransferEventModel transfer, T... t);

    /**
     * SOA 通用化 返回结果数据结构
     *
     * */
    default Object getObject(Map<String,Object> responseData,TransferEventModel transfer){
        // 获取第三方返回状态码值
        Object returnCode = responseData.get(transfer.getOutput().get(ChainConstants.CODEKEY));

        // 断言状态码是否成功
        if (returnCode != null){
            // 断言状态码是否成功
            if (returnCode.toString().equals(transfer.getOutput().get(ChainConstants.CODEVALUE).toString())){
                return responseData.get(transfer.getOutput().get(ChainConstants.DATAKEY));
            }else{
                throw new FlowException(responseData.get(transfer.getOutput().get(ChainConstants.MSGKEY)).toString());
            }
        }else {
            throw new FlowException("请求状态码配置有误,无法获取状态码信息.");
        }
    }

}
