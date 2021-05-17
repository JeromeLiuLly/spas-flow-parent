package com.candao.spas.flow.web.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ButterflyEdges implements Serializable {

    private String flowId;
    private String nodeId;
    private String source;
    private String target;
    private String sourceNode;
    private String targetNode;
    private Boolean arrow = true;
    private String type = "endpoint";
    private Double arrowPosition = 0.5;
    @JsonProperty("Class")
    private String vueClass = "Edge";
}
