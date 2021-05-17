package com.candao.spas.flow.sdk.mapper;

import com.candao.spas.flow.core.model.db.EdgeEventVo;
import com.candao.spas.flow.core.model.db.NodeEventVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface NodeConfigMapper extends Mapper<NodeEventVo>, MySqlMapper<NodeEventVo> {

    /**
     * 根据工作流ID 获取节点列表
     *
     * @param flowId 工作流id
     *
     * */
    public List<NodeEventVo> findNodesById(@Param("flowId") String flowId);
}
