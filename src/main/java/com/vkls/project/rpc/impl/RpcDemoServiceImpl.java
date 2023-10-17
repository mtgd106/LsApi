package com.vkls.project.rpc.impl;

import com.vkls.project.rpc.RpcDemoService;
import org.apache.dubbo.config.annotation.DubboService;
import com.alibaba.dubbo.rpc.RpcContext;

@DubboService
public class RpcDemoServiceImpl implements RpcDemoService {
    @Override
    public String sayHello(String name) {
        System.out.println("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name;
    }
}