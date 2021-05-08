package com.candao.spas.flow.soa.impl;

import com.candao.spas.flow.core.exception.FlowException;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.core.utils.net.http.HttpResult;
import com.candao.spas.flow.core.utils.net.http.HttpSender;
import com.candao.spas.flow.soa.ISOAService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 实现基于HTTP协议方式的远程调用
 *
 * */
@Slf4j
@Service("http")
public class HttpSOAService implements ISOAService {

    @Override
    public Object handle(TransferEventModel transfer,Object o) {

        HttpResult httpResult;

        // 定义临时变量
        Object tempObj;

        // 断言是否存在入参定义
        if (transfer.getInputParamTypes() != null && transfer.getInputParamTypes().size() == 1){
            tempObj = transfer.getInputParamTypes().get(0);
        }else{
            tempObj = o;
        }

        // 断言是否Post请求
        if (HttpPost.METHOD_NAME.equals(transfer.getRequertType())){
            httpResult = HttpSender.postRequest(transfer.getFullLink(), EasyJsonUtils.toJsonString(tempObj), transfer.getTimeout());
        }else{
            Map param = EasyJsonUtils.toJavaObject(tempObj,Map.class);
            httpResult = HttpSender.getRequest(transfer.getUrl(),param, transfer.getTimeout());
        }

        // 断言请求是否成功,指代http code = 200
        if (httpResult != null && httpResult.isOk()) {
            Map<String,Object> responseData = EasyJsonUtils.toJavaObject(httpResult.content, Map.class);
            return getObject(responseData,transfer);

        }else{
            throw new FlowException("请求发起失败,请查看异常信息."+httpResult != null ? httpResult.errorMsg : "");
        }
    }
}
