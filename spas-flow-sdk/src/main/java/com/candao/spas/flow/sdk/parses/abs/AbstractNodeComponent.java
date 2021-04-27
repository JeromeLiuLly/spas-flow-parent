package com.candao.spas.flow.sdk.parses.abs;

import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.parses.NodeParser;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public abstract class AbstractNodeComponent<T,R> implements NodeParser<T,R> {

    public Map<String, Node> nodeMap;

}