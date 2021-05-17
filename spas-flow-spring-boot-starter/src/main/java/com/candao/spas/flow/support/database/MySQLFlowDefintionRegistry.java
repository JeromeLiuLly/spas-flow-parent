package com.candao.spas.flow.support.database;

import com.candao.spas.flow.core.model.converter.FlowDefintionConverter;
import com.candao.spas.flow.core.model.db.TransferEventVo;
import com.candao.spas.flow.core.model.dto.YmlFlowDto;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.mapper.TransferConfigMapper;
import com.candao.spas.flow.support.FlowDefintionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Order(2)
@Component
public class MySQLFlowDefintionRegistry implements FlowDefintionRegistry {

    @Resource
    private TransferConfigMapper transferConfigMapper;

    @Override
    public Map<String, FlowDefintion> registry() throws Exception {
        return registryModel();
    }

    /**
     * 注册流程模型
     *
     * @return
     * @throws Exception
     */
    private Map<String, FlowDefintion> registryModel() throws Exception {

        List<String> list = transferConfigMapper.findTransfersGroupByFlowId();
        Map<String,FlowDefintion> flowMap = list.stream().map(value -> {
            List<TransferEventVo> transferEventVos =  transferConfigMapper.findTransferById(value);
            TransferEventVo root = transferEventVos.stream().filter(transferEventVo -> Objects.equals(transferEventVo.getFront(), NodeParserEnum.ROOT.getValue())).findFirst().get();
            Map<String,Node> param = transferEventVos.stream().map(transferEventVo -> {
                Node node = new Node();
                BeanUtils.copyProperties(transferEventVo,node);
                return node;
            }).collect(Collectors.toMap(w->w.getNodeId(), w->w));
            FlowDefintion defintition = new FlowDefintion();
            defintition.setFlowId(value);
            defintition.setDesc("可视化业务编排_"+value);
            defintition.setFlowName(value);
            defintition.setStartNodeId(root.getNodeId());
            defintition.setNodeMap(param);
            return defintition;
        }).collect(Collectors.toMap(w->w.getFlowId(),w->w));
        return flowMap;
    }
}
