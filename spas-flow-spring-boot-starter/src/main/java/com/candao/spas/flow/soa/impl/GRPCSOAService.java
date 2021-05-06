package com.candao.spas.flow.soa.impl;

import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.soa.ISOAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现基于gRPC协议方式的远程调用
 *
 * */
@Slf4j
@Service("gRPC")
public class GRPCSOAService implements ISOAService{

    @Override
    public Object handle(TransferEventModel transfer, Object... t) {
        return null;
    }
}
