package com.vkls.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vkls.apicommon.model.entity.UserInterfaceInfo;


/**
* @author zhao
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2023-05-01 11:19:22
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);
    boolean invokeCount(long interfaceInfoId, long userId);
}
