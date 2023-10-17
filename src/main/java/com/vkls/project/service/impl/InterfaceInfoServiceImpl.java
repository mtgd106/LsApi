package com.vkls.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.vkls.project.common.ErrorCode;
import com.vkls.project.exception.BusinessException;
import com.vkls.project.mapper.InterfaceInfoMapper;
import com.vkls.apicommon.model.entity.InterfaceInfo;
import com.vkls.project.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author zhao
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-04-29 17:12:34
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url=interfaceInfo.getUrl();
        String description=interfaceInfo.getDescription();
        String method=interfaceInfo.getMethod();
        //add为true时，代表是创建新的接口;创建时，可以校验令所有参数必须非空，这里仅校验了四个参数
        if (add) {
            if (StringUtils.isAnyBlank(name,url,description,method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        //如果add为false，代表是更新接口
        if (StringUtils.isAnyBlank(name)&&name.length()>50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"名字太长！");
        }
    }
}




