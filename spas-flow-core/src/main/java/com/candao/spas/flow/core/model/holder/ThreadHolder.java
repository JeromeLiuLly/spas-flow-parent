package com.candao.spas.flow.core.model.holder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ThreadHolder {

	private String runningChainId;

	private Integer runningChainFlag;

	private Integer requestLogFlag;

	@Override
	public String toString() {
		return "ThreadHolder [runningAccountId=" + runningChainId + ", runningAccountFlag=" + runningChainFlag + ", requestLogFlag=" + requestLogFlag + "]";
	}
}
