package com.vkls.apicommon.service;

import com.vkls.apicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author vkls
 */
public interface InnerUserService {

    /**
     * 根据accessKey查询用户
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
