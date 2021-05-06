package com.candao.spas.flow.core.utils.net.http;

import org.apache.http.HttpStatus;

public class HttpResult {
	/**
	 * 请求代码，如200、500、503等
	 */
	public int statusCode;
	/**
	 * 结果内容
	 */
	public String content;
	
	/**执行出现异常的异常信息*/
	public String errorMsg;
	/**
	 * 请求是否正常
	 * @return
	 */
	public boolean isOk() {
		return statusCode == HttpStatus.SC_OK;
	}
}