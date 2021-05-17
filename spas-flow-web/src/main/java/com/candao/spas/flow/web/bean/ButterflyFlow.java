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
public class ButterflyFlow implements Serializable {

    private String id;
    private String flowId;
    private String nodeId;
    private String nodeName;
    private String eventName;
    private String component;
    private String description;
    private String label;
    private String className;
    private String iconType;
    private String nodeType;
    private String shape;
    private String text;
    private String color;
    private String source;
    private String target;
    private String sourceNode;
    private String targetNode;
    private Integer top;
    private Integer left;
    @JsonProperty("Class")
    private String vueClass;
    private Object endpoints;
    private String front;
    private String next;
    private String output;
    private String url;
    private String requertType;
    private String serverPort;
    private String methodName;
    private String inputParamTypesValues;
    private String convertRule;
    private Integer timeout;
    private String eventType;

}
