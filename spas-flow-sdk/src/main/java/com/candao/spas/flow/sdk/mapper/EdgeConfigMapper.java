package com.candao.spas.flow.sdk.mapper;

import com.candao.spas.flow.core.model.db.EdgeEventVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface EdgeConfigMapper extends Mapper<EdgeEventVo>, MySqlMapper<EdgeEventVo> {

    /**
     * 根据工作流ID 获取连线节点列表
     *
     * @param flowId 工作流id
     *
     * */
    public List<EdgeEventVo> findEdgesById(@Param("flowId") String flowId);
}
