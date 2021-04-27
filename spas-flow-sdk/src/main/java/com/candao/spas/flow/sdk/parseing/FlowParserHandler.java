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
     * @param node 节点信息
     * @param requestDataVo 输出参数
     * @param nodeMap 节点集合
     * @param responseDataVo 输出参数
     * @return 输出参数
     */
    public void execNode(Node node, Object requestDataVo, Map<String, Node> nodeMap, ResponseFlowDataVo responseDataVo){
        // 获取节点类型
        String type = node.getNodeType();

        //  设置工作流链路
        NodeParser nodeInstance = NodeComponentFactory.getNodeInstance(type);
        nodeInstance.setNodeMap(nodeMap);

        try {
            // 执行 handle函数
            doHandle(node, requestDataVo, nodeMap, responseDataVo, nodeInstance);

            Boolean isSuccess = status(responseDataVo);

            if (isSuccess) {
                doSuccess(node, requestDataVo, responseDataVo, nodeInstance);
            } else {
                doFail(node, requestDataVo, responseDataVo, nodeInstance);
                doRollback(node,requestDataVo,responseDataVo,nodeInstance);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                doComplate(node, requestDataVo, nodeMap, responseDataVo, nodeInstance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doHandle(Node node,Object requestDataVo,Map<String, Node> nodeMap,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        log.info("doHandle:[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doHandleNum.addAndGet(1)+"次执行");

        doTask(node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.HANDLE);
        Boolean isSuccess = status(responseDataVo);

        if (isSuccess) {
            // TODO output ==> input
            requestDataVo = responseDataVo.getData();
            String nextNode = node.getNext();
            // 断言下一个节点不为空
            if (!StringUtils.isEmpty(nextNode)) {
                Node nodeNext = nodeMap.get(nextNode);
                execNode(nodeNext, requestDataVo, nodeMap, responseDataVo);
            }
        }
    }

    // 同步执行
    private void execSyn(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,int retryTime, int sleep,MethodParserEnum methodParserEnum,boolean isSetResultCode) {

        // 断言,执行节点是否为子流程工作流
        if (node.getNodeType().equals(NodeParserEnum.SUBFLOW.getValue())){
            nodeInstance.parserNode(node, requestDataVo,responseDataVo, methodParserEnum);
        }else{
            Integer oldCode = responseDataVo.getStatus();
            int doNum = 1;
            for (int retryTimeindex  = 0; retryTimeindex <= retryTime; retryTimeindex++) {
                try{
                    nodeInstance.parserNode(node, requestDataVo,responseDataVo, methodParserEnum);
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    if (retryTimeindex == 0) {
                        log.error(e.getMessage());
                    }else{
                        log.error("异常重试：[第" + doNum++ + "次数]," + e.getMessage());
                    }
                    if (isSetResultCode){
                        if (responseDataVo.success()){
                            responseDataVo.setStatus(ResponseFlowStatus.SUCCESS_BREAK.getStatus());
                        }
                    }
                    responseDataVo.setMsg("system error occor:"+e.getMessage());
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
    private void execASyn(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,int retryTime, int sleep,MethodParserEnum methodParserEnum,boolean isSetResultCode) {

        ThreadHolder threadHolder = ThreadLocalHolder.getThreadHolder();
        try {
            AsynExcutor asynExcutor;
            if (node.getNodeType().equals(NodeParserEnum.SUBFLOW.getValue())){
                asynExcutor = new AsynExcutor(node,requestDataVo, responseDataVo, nodeInstance, 0, sleep, methodParserEnum, threadHolder);
            }else{
                asynExcutor = new AsynExcutor(node,requestDataVo, responseDataVo, nodeInstance, retryTime, sleep, methodParserEnum, threadHolder);
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

    private void doSuccess(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doSuccess:[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doSuccessNum.addAndGet(1)+"次执行");
        doTask(node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.SUCCESS);
    }

    private void doComplate(Node node,Object requestDataVo,Map<String, Node> nodeMap,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doComplate:[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doComplateNum.addAndGet(1)+"次执行");
        doTask(node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.COMPLATE);
    }

    private void doFail(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doFail:[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],第"+doFailNum.addAndGet(1)+"次执行");
        doTask(node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.FAIL);
    }

    private void doRollback(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance){
        if (NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            return;
        }
        log.info("doRollback:[节点名称:"+node.getNodeId()+"]_[节点描述:"+node.getDesc()+"]_[节点类型:"+node.getNodeType()+"],,第"+doRollBackkNum.addAndGet(1)+"次执行");
        doTask(node,requestDataVo,responseDataVo,nodeInstance,MethodParserEnum.ROLLBACK);
    }

    private void doTask(Node node,Object requestDataVo,ResponseFlowDataVo responseDataVo,NodeParser nodeInstance,MethodParserEnum methodParserEnum){
        Boolean isAysn = node.getAsyn();
        if (isAysn){
            execASyn(node, requestDataVo, responseDataVo, nodeInstance, node.getRetryTime(), node.getSleep(), methodParserEnum, false);
        } else {
            execSyn(node, requestDataVo, responseDataVo, nodeInstance, node.getRetryTime(), node.getSleep(), methodParserEnum, false);
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