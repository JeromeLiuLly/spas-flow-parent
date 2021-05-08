package com.candao.spas.flow.sdk.parseing;

import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.holder.ThreadHolder;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.parses.NodeParser;
import com.candao.spas.flow.sdk.parses.factory.NodeComponentFactory;
import com.candao.spas.flow.core.utils.ThreadLocalHolder;
import com.candao.spas.flow.sdk.utils.AsynExcutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FlowParserHandler {

    // handle函数执行次数计数
    AtomicInteger doHandleNum = new AtomicInteger(0);

    // success函数执行次数计数
    AtomicInteger doSuccessNum = new AtomicInteger(0);

    // complate函数执行次数计数
    AtomicInteger doComplateNum = new AtomicInteger(0);

    // fail函数执行次数计数
    AtomicInteger doFailNum = new AtomicInteger(0);

    // 函数执行次数计数
    AtomicInteger doRollBackkNum = new AtomicInteger(0);

    /**
     * 执行流程节点
     *
     * @param flowId 工作流Id
     * @param node 节点信息
     * @param requestDataVo 输出参数
     * @param nodeMap 节点集合
     * @param responseDataVo 输出参数
     * @return 输出参数
     */
    public void execNode(String flowId, Node node, Object requestDataVo, Map<String, Node> nodeMap, ResponseFlowDataVo responseDataVo){
        // 获取节点类型
        String type = node.getNodeType();

        //  设置工作流链路
        NodeParser nodeInstance = NodeComponentFactory.getNodeInstance(type);
        nodeInstance.setNodeMap(nodeMap);

        try {
            // 执行 handle函数
            doHandle(flowId,node, requestDataVo, nodeMap, responseDataVo, nodeInstance);

            Boolean isSuccess = status(responseDataVo);

            if (isSuccess) {
                doSuccess(flowId,node, requestDataVo, responseDataVo, nodeInstance);
            } else {
                doFail(flowId,node, requestDataVo, responseDataVo, nodeInstance);
                doRollback(flowId,node,requestDataVo,responseDataVo,nodeInstance);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                doComplate(flowId,node, requestDataVo, nodeMap, responseDataVo, nodeInstance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doHandle(String flowId, Node node,Object requestDataVo,Map<String, Node> nodeMap,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        log.info("doHandle:[工作流节点名称:"+flowId+"]_[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doHandleNum.addAndGet(1)+"次执行");

        doTask(flowId,node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.HANDLE);
        Boolean isSuccess = status(responseDataVo);

        if (isSuccess) {
            // TODO output ==> input
            //requestDataVo = responseDataVo.getData();
            String nextNode = node.getNext();
            // 断言下一个节点不为空
            if (!StringUtils.isEmpty(nextNode)) {
                Node nodeNext = nodeMap.get(nextNode);
                execNode(flowId,nodeNext, requestDataVo, nodeMap, responseDataVo);
            }
        }
    }

    // 同步执行
    private void execSyn(String flowId,Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,int retryTime, int sleep,MethodParserEnum methodParserEnum,boolean isSetResultCode) {

        // 断言,执行节点是否为子流程工作流
        if (node.getNodeType().equals(NodeParserEnum.SUBFLOW.getValue())){
            nodeInstance.parserNode(flowId,node, requestDataVo,responseDataVo, methodParserEnum);
        }else{
            Integer oldCode = responseDataVo.getStatus();
            int doNum = 1;
            for (int retryTimeindex  = 0; retryTimeindex <= retryTime; retryTimeindex++) {
                try{
                    nodeInstance.parserNode(flowId,node, requestDataVo,responseDataVo, methodParserEnum);
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    String errorMsg;

                    // 断言异常的类型,获取异常信息内容
                    if (e instanceof UndeclaredThrowableException){
                        errorMsg = ((UndeclaredThrowableException) e).getUndeclaredThrowable().getMessage();
                        errorMsg = errorMsg != null ? errorMsg :((UndeclaredThrowableException)(((UndeclaredThrowableException) e).getUndeclaredThrowable().getCause())).getUndeclaredThrowable().getMessage();
                    } else {
                        errorMsg = e.getMessage();
                    }

                    if (retryTimeindex == 0) {
                        log.error(errorMsg);
                    }else{
                        log.error("异常重试：[第" + doNum++ + "次数]," + errorMsg);
                    }
                    if (isSetResultCode){
                        if (responseDataVo.success()){
                            responseDataVo.setStatus(ResponseFlowStatus.SUCCESS_BREAK.getStatus());
                        }
                    }
                    responseDataVo.setMsg(errorMsg);
                    if (retryTimeindex <= retryTime - 1) {
                        responseDataVo.setStatus(oldCode);
                    }
                    try {
                        if (sleep > 0){
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

    // 异步执行
    private void execASyn(String flowId,Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,int retryTime, int sleep,MethodParserEnum methodParserEnum,boolean isSetResultCode) {

        ThreadHolder threadHolder = ThreadLocalHolder.getThreadHolder();
        try {
            AsynExcutor asynExcutor;
            if (node.getNodeType().equals(NodeParserEnum.SUBFLOW.getValue())){
                asynExcutor = new AsynExcutor(flowId,node,requestDataVo, responseDataVo, nodeInstance, 0, sleep, methodParserEnum, threadHolder);
            }else{
                asynExcutor = new AsynExcutor(flowId,node,requestDataVo, responseDataVo, nodeInstance, retryTime, sleep, methodParserEnum, threadHolder);
            }
            Thread thread = new Thread(asynExcutor);
            thread.start();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (isSetResultCode) {
                if (responseDataVo.success()) {
                    responseDataVo.setStatus(ResponseFlowStatus.SUCCESS_BREAK.getStatus());
                }
            }
            responseDataVo.setMsg("system error occor:"+e.getMessage());
        }
    }

    private void doSuccess(String flowId, Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doSuccess:[工作流节点名称:"+flowId+"]_[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doSuccessNum.addAndGet(1)+"次执行");
        doTask(flowId,node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.SUCCESS);
    }

    private void doComplate(String flowId, Node node,Object requestDataVo,Map<String, Node> nodeMap,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doComplate:[工作流节点名称:"+flowId+"]_[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doComplateNum.addAndGet(1)+"次执行");
        doTask(flowId,node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.COMPLATE);
    }

    private void doFail(String flowId, Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doFail:[工作流节点名称:"+flowId+"]_[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doFailNum.addAndGet(1)+"次执行");
        doTask(flowId,node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.FAIL);
    }

    private void doRollback(String flowId, Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doRollback:[工作流节点名称:"+flowId+"]_[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],,第"+doRollBackkNum.addAndGet(1)+"次执行");
        doTask(flowId, node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.ROLLBACK);
    }

    private void doTask(String flowId, Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,MethodParserEnum methodParserEnum){
        Boolean isAysn = node.getAsyn();
        if (isAysn){
            execASyn(flowId,node, requestDataVo, responseDataVo, nodeInstance, node.getRetryTime(), node.getSleep(), methodParserEnum, false);
        } else {
            execSyn(flowId,node, requestDataVo, responseDataVo, nodeInstance, node.getRetryTime(), node.getSleep(), methodParserEnum, false);
        }
    }

    private Boolean status(ResponseFlowDataVo responseDataVo){
        Boolean isSuccess = true;
        if (responseDataVo != null && responseDataVo.getStatus() != null){
            if (ResponseFlowStatus.SUCCESS.getStatus() == responseDataVo.getStatus()){
                if (ResponseFlowStatus.SUCCESS_BREAK.getStatus() == responseDataVo.getStatus()) { // successAndBreak是成功且但不继续往下走
                    isSuccess = false; // 其它情况均为错误
                }
                if (ResponseFlowStatus.ERROR_CONTINUE.getStatus() == responseDataVo.getStatus()) { // errorButContinue是错误但是继续往下执行
                    isSuccess = true;
                }
            }else{
                isSuccess = false;
            }
        }else{
            isSuccess = false;
        }
        return isSuccess;
    }
}