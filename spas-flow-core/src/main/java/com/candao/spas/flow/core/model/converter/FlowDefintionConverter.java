package com.candao.spas.flow.core.model.converter;

import com.candao.spas.flow.core.model.dto.YmlFlowDto;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.model.vo.Node;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface FlowDefintionConverter {

    FlowDefintion domain2dto(YmlFlowDto ymlFlowDto);

    HashMap<String,Node> domain2Map(Node node);

    default Map<String, Node> domain2Map(List<YmlFlowDto.NodeDto> list){
        Map<String, Node> nodeMap = new HashMap<>();
        list.forEach(tempNode -> {
            Node node = tempNode.getNode();
            nodeMap.put(node.getNodeId(),node);
        });
        return nodeMap;
    }

}
