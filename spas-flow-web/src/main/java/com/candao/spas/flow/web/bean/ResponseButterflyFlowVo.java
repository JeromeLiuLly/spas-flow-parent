package com.candao.spas.flow.web.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseButterflyFlowVo implements Serializable {

    private List<ButterflyFlow> nodes;

    private List<ButterflyEdges> edges;
}
