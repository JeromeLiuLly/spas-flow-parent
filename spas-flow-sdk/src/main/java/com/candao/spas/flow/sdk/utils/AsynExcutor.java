package com.candao.spas.flow.sdk.utils;

import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.holder.ThreadHolder;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.parses.NodeParser;
import com.candao.spas.flow.core.utils.ThreadLocalHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


@Slf4j
@Getter
@Setter
public class AsynExcutor implements Callable<ResponseFlowDataVo>, Runnable {


	public AsynExcutor(String flowId,Node node, Object requestDataVo, ResponseFlowDataVo responseDataVo, NodeParser nodeInstance, int retryTime, int sleep, MethodParserEnum methodParserEnum, ThreadHolder threadHolder) {
		super();
		setParam(requestDataVo);
		setResponseDataVo(responseDataVo);
		setFlowId(flowId);
		setRetryTime(retryTime);
		setSleep(sleep);
		setThreadHolder(threadHolder);
		setNodeInstance(nodeInstance);
		setNode(node);
		setMethodParserEnum(methodParserEnum);
	}

	private Object requestDataVo;
	private ResponseFlowDataVo responseDataVo;
	private String methodName;
	private String flowId;
	private int retryTime;
	private int sleep;
	private ThreadHolder threadHolder;
	private NodeParser nodeInstance;
	private Node node;
	private MethodParserEnum methodParserEnum;

	public void setThreadHolder(ThreadHolder threadHolder) {
		ThreadHolder threadHolderClone = new ThreadHolder();
		BeanUtils.copyProperties(threadHolder, threadHolderClone);
		this.threadHolder = threadHolderClone;
	}


	public void setParam(Object param) {
		try {
			Class paramCloneClass = param.getClass();
			Object paramClone = paramCloneClass.newInstance();
			BeanUtils.copyProperties(param, paramClone);
			this.requestDataVo = paramClone;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}


	public void setResponseDataVo(ResponseFlowDataVo responseDataVo) {
		ResponseFlowDataVo responseDataVoClone = new ResponseFlowDataVo();
		BeanUtils.copyProperties(responseDataVo, responseDataVoClone);
		this.responseDataVo = responseDataVoClone;
	}

	private ResponseFlowDataVo work() {
		ThreadLocalHolder.setThreadHolder(threadHolder);
		for (int retryTimeindex = 0; retryTimeindex < retryTime; retryTimeindex++) {
			Integer oldResultCode = responseDataVo.getStatus();

			try {
				nodeInstance.parserNode(flowId,node, requestDataVo,responseDataVo, methodParserEnum);
				break;
			} catch (Exception e) {
				log.error(e.getMessage());
				if (responseDataVo.success()) {
					responseDataVo.setStatus(ResponseFlowStatus.SUCCESS_BREAK.getStatus());
				}
				responseDataVo.setMsg("system error occor:"+e.getMessage());

				if (retryTimeindex < retryTime - 1) {
					responseDataVo.setStatus(oldResultCode);
				}
				try {
					if (sleep > 0) {
						TimeUnit.MILLISECONDS.sleep(sleep);
					}
				} catch (InterruptedException e1) {
					log.error(e.getMessage());
				}
			}
		}
		return responseDataVo;
	}

	@Override
	public void run() {
		work();
	}

	@Override
	public ResponseFlowDataVo call() throws Exception {
		return work();
	}
}
