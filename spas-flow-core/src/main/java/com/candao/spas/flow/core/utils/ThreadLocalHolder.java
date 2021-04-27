package com.candao.spas.flow.core.utils;

import com.candao.spas.flow.core.model.holder.ThreadHolder;

import java.util.UUID;

public class ThreadLocalHolder {

	private static ThreadLocal<ThreadHolder> contextHolder = new ThreadLocal<ThreadHolder>(); // 线程本地环境

	public static void initRunningChain() {
		if (null == contextHolder.get()) {
			ThreadHolder th = new ThreadHolder();
			contextHolder.set(th);
		}
		ThreadHolder th = contextHolder.get();
		String runningChainId = UUID.randomUUID().toString();
		th.setRunningChainId(runningChainId);
	}

	public static String getRunningChainId() {
		if (null == contextHolder.get()) {
			initRunningChain();
		}
		return contextHolder.get().getRunningChainId();
	}

	public static Integer getRunningChainFlag() {
		if (null == contextHolder.get()) {
			initRunningChain();
		}
		return contextHolder.get().getRunningChainFlag();
	}

	public static void setRunningChainFlag(Integer runningChainFlag) {
		if (null == contextHolder.get()) {
			initRunningChain();
		}
		contextHolder.get().setRunningChainFlag(runningChainFlag);
	}

	public static Integer getRequestLogFlag() {
		if (null == contextHolder.get()) {
			initRunningChain();
		}
		return contextHolder.get().getRequestLogFlag();
	}

	public static void setRequestLogFlag(Integer requestLogFlag) {
		if (null == contextHolder.get()) {
			initRunningChain();
		}
		contextHolder.get().setRequestLogFlag(requestLogFlag);
	}

	public static ThreadHolder getThreadHolder() {
		return contextHolder.get();
	}

	public static void setThreadHolder(ThreadHolder threadHolder) {
		contextHolder.set(threadHolder);
	}

}
