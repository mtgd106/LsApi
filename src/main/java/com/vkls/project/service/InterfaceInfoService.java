package com.vkls.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vkls.apicommon.model.entity.InterfaceInfo;

/**
* @author zhao
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-04-29 17:12:34
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
