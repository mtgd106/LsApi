package com.vkls.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vkls.apicommon.model.entity.UserInterfaceInfo;
import com.vkls.project.model.dto.interfaceInfo.InvokeInterfaceInfoVO;

import java.util.List;

/**
* @author zhao
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2023-05-01 11:19:22
* @Entity generator.pojo.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}




