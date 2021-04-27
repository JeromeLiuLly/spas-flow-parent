package com.candao.spas.flow.core.model.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseFlowStatus {
	
	/**
	 * 1 操作成功
	 */
	SUCCESS(1, "操作成功"),
	
	/**
	 * 2 操作失败
	 */
	FAIL(2, "操作失败"),
	
	/**
	 * 3 参数错误
	 */
	PARAM_ERR(3, "参数错误"),

	SUCCESS_BREAK(9999 , "业务执行成功,并跳出链路"),

	ERROR_CONTINUE(10000 , "业务执行失败,继续执行链路"),

	BIZ_ERROR(10001 , "通用的业务逻辑错误"),

	FLOW_ERROR(10002 , "框架错误"),

	SYS_ERROR(10003 , "未知的系统错误" );
	;

	private final int status;
	private final String msg;
	public static ResponseFlowStatus getRspByStatus(int status) {
		for (ResponseFlowStatus rsp : values()) {
			if (rsp.status == status) {
				return rsp;
			}
		}
		return ResponseFlowStatus.FAIL;
	}
}