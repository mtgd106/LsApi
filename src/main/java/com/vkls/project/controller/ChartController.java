package com.vkls.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vkls.apicommon.model.entity.InterfaceInfo;
import com.vkls.project.annotation.AuthCheck;
import com.vkls.project.common.ErrorCode;
import com.vkls.project.exception.BusinessException;
import com.vkls.project.mapper.UserInterfaceInfoMapper;
import com.vkls.project.model.dto.interfaceInfo.InvokeInterfaceInfoVO;
import com.vkls.project.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @GetMapping("/interface/analysis")
    @AuthCheck(mustRole="admin")
    public List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo(int limit) {
        List<InvokeInterfaceInfoVO> vos = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(limit);
        if (vos == null || vos.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 根据id查询接口名称
        LinkedHashMap<Long, InvokeInterfaceInfoVO> voHashMap = new LinkedHashMap<>(vos.size());
        //将VO类放入到一个Map中，键为VO类的ID，值为VO对象本身
        for (InvokeInterfaceInfoVO vo : vos) {
            voHashMap.put(vo.getId(), vo);
        }
        //根据Map中的VO的ID查询出所有的InterfaceInfo对象
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(InterfaceInfo::getId, voHashMap.keySet());
        List<InterfaceInfo> infoList = interfaceInfoService.list(queryWrapper);

        //将InterfaceInfo对象中的接口名称封装到VO类中
        for (InterfaceInfo interfaceInfo : infoList) {
            voHashMap.get(interfaceInfo.getId()).setName(interfaceInfo.getName());
        }

        return new ArrayList<>(voHashMap.values());
    }

}