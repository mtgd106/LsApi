package com.vkls.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vkls.project.common.ErrorCode;
import com.vkls.project.exception.BusinessException;
import com.vkls.apicommon.model.entity.UserInterfaceInfo;
import com.vkls.project.mapper.UserInterfaceInfoMapper;
import com.vkls.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
* @author zhao
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
* @createDate 2023-05-01 11:19:22
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空  这里仅校验了接口的Id和用户的Id
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId()<=0||userInterfaceInfo.getUserId()<=0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口剩余调用次数不能小于0！");
        }

    }

    //调用计数
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId<=0||userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId)
                .eq("userId",userId)
                .setSql("leftNum=leftNum-1,totalNum=totalNum+1");
        return this.update(updateWrapper);
    }
}




