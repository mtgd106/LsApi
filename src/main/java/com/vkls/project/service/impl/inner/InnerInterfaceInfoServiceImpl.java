package com.vkls.project.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vkls.apicommon.model.entity.InterfaceInfo;
import com.vkls.apicommon.service.InnerInterfaceInfoService;
import com.vkls.project.common.ErrorCode;
import com.vkls.project.exception.BusinessException;
import com.vkls.project.mapper.InterfaceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StrUtil.hasBlank(path, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getUrl, path).eq(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(lambdaQueryWrapper);
    }
}