package com.candao.spas.flow.sdk.mapper;

import com.candao.spas.flow.core.model.db.TransferEventVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface TransferConfigMapper extends Mapper<TransferEventVo>, MySqlMapper<TransferEventVo>{

    /**
     * 根据工作流ID 、 节点ID 获取事件模型
     *
     * @param flowId 工作流id
     * @param nodeId 节点id
     *
     * */
    public TransferEventVo getTransferById(@Param("flowId") String flowId,@Param("nodeId") String nodeId);

    /**
     * 根据工作流ID 获取事件模型列表
     *
     * @param flowId 工作流id
     *
     * */
    public List<TransferEventVo> findTransferById(@Param("flowId") String flowId);

    /**
     * 获取事件模型列表
     *
     *
     * */
    public List<TransferEventVo> findTransfers();

    /**
     * 获取事件模型列表
     *
     *
     * */
    public List<String> findTransfersGroupByFlowId();

    public Integer saveTransfer(@Param("vo") TransferEventVo transferEventVo);

    public Integer saveTransfers(@Param("vos") List<TransferEventVo> transferEventVos);

    public Integer updateTransferVersionAndStatus(@Param("flowId")String flowId, @Param("version")String version, @Param("status")Integer status);
}
