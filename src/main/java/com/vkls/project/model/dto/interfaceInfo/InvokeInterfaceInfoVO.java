package com.vkls.project.model.dto.interfaceInfo;

import com.vkls.apicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvokeInterfaceInfoVO extends InterfaceInfo {

    /**
     * 统计接口调用次数
     */
    private Integer invokeNum;

    private static final long serialVersionUID = 1L;

}