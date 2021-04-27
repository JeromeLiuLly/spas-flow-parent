package com.candao.spas.flow.core.model.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.LinkedHashMap;

@Data
@AllArgsConstructor
public class ResponseFlowDataVo<T> implements Serializable {

    /**
     * 响应状态
     */
    @JsonProperty(index = 1)
    private Integer status;

    /**
     * 响应状态描述
     */
    @JsonProperty(index = 2)
    private String msg;

    /**
     * 日志id
     */
    @JsonProperty(index = 3)
    private String logId;

    /**
     * 服务器当前时间
     */
    @JsonProperty(index = 4)
    private Long serverTime;

    /**
     * 入参数据
     */
    @JsonProperty(index = 100)
    private T input;

    /**
     * 响应具体数据
     */
    @JsonProperty(index = 100)
    private T data;

    public ResponseFlowDataVo() {
        this.serverTime = System.currentTimeMillis();
    }


    /**
     * 设置响应状态（使用默认描述）
     *
     * @param responseStatus-{link RspStatus}
     */
    public void setResponseStatus(ResponseFlowStatus responseStatus) {
        status = responseStatus.getStatus();
        msg = responseStatus.getMsg();
    }
    /**
     * 设置响应状态（使用自定义描述）
     *
     * @param responseStatus-{link RspStatus}
     * @param msg-自定义描述
     */
    public void setResponseStatus(ResponseFlowStatus responseStatus, String msg) {
        status = responseStatus.getStatus();
        this.msg = msg;
    }

    // **********************************http常用数据返回方法***************************************
    public static ResponseFlowDataVo generateSuccess(Object data) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.SUCCESS);
        responseData.data = data;
        return responseData;
    }
    /**
     * 返回一个无具体数据的成功的RspData<br/>
     *
     * @return
     */
    public static ResponseFlowDataVo generateSuccess() {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.SUCCESS);
        return responseData;
    }
    /**
     * 返回一个无具体数据的成功的RspData<br/>
     *
     * @return
     */
    public static ResponseFlowDataVo generateSuccess(String msg) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.SUCCESS, msg);
        return responseData;
    }
    /**
     * 返回一个失败的RspData<br/>
     * 默认为RspStatus.FAIL失败类型
     *
     * @param msg-自定义失败信息
     * @return
     */
    public static ResponseFlowDataVo generateFail(String msg) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.FAIL, msg);
        return responseData;
    }

    /**
     * 返回一个失败的RspData<br/>
     *
     * @param status-自定义状态
     * @param msg-自定义信息
     * @return
     */
    public static ResponseFlowDataVo generateFail(int status, String msg) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.status = status;
        responseData.msg = msg;
        return responseData;
    }
    /**
     * 返回一个参数错误的RspData<br/>
     *
     * @param paramName 错误的参数名
     * @return
     */
    public static ResponseFlowDataVo generateParamFail(String paramName) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.PARAM_ERR);
        responseData.msg += "，参数名：" + paramName;
        return responseData;
    }

    /**
     * 返回一个失败的RspData<br/>
     *
     * @param responseStatus-{link RspStatus}
     * @return
     */
    public static ResponseFlowDataVo generateTokenFail(ResponseFlowStatus responseStatus) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(responseStatus);
        return responseData;
    }

    /**
     * 返回一个失败的RspData<br/>
     *
     * @param responseStatus-{link RspStatus}
     * @return
     */
    public static ResponseFlowDataVo generateFail(ResponseFlowStatus responseStatus) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(responseStatus);
        return responseData;
    }
    /**
     * 返回一个自定义的RspData
     *
     * @param status-响应状态
     * @param msg-响应信息
     * @param data-具体响应数据
     * @return
     */
    public static ResponseFlowDataVo generate(int status, String msg, Object data) {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.status = status;
        responseData.msg = msg;
        responseData.data = data;
        return responseData;
    }
    /**
     * 返回一个失败的RspData<br/>
     *
     * @return
     */
    public static ResponseFlowDataVo generateFailException() {
        ResponseFlowDataVo responseData = new ResponseFlowDataVo();
        responseData.setResponseStatus(ResponseFlowStatus.FAIL);
        responseData.setMsg("服务器异常，请将logId反馈给技术人员");
        return responseData;
    }

    /**
     * 用于简单构建返回对象
     */
    public static class SimpleResponseData extends LinkedHashMap<String, Object> implements Serializable {
        public static SimpleResponseData newInstance() {
            return new SimpleResponseData();
        }
        public static SimpleResponseData newInstance(String key, Object value) {
            SimpleResponseData simpleResponseData = newInstance();
            simpleResponseData.put(key, value);
            return simpleResponseData;
        }
        public SimpleResponseData result(String key, Object value) {
            this.put(key, value);
            return this;
        }
    }

    public boolean success() {
        if (status == ResponseFlowStatus.SUCCESS.getStatus() || status == ResponseFlowStatus.SUCCESS_BREAK.getStatus()) {
            return true;
        }
        return false;
    }
}