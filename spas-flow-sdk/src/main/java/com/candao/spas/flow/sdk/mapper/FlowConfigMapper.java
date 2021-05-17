package com.candao.spas.flow.sdk.mapper;

import com.candao.spas.flow.core.model.db.FlowEventVo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface FlowConfigMapper extends Mapper<FlowEventVo>, MySqlMapper<FlowEventVo> {

    /**
     * 获取工作流列表
     *
     * */
    public List<FlowEventVo> findFlows();
}
