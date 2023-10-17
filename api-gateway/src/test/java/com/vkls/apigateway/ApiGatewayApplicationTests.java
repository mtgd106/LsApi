package com.vkls.apigateway;


import com.vkls.project.rpc.RpcDemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

    @DubboReference
    private RpcDemoService rpcDemoService;

    @Test
    void contextLoads() {
        System.out.println(rpcDemoService.sayHello("world"));

    }

}
