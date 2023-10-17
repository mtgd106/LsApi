package com.vkls.apiinterface;

import com.vkls.apiclientsdk.client.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {

    @Resource
    private ApiClient apiClient;
//    @Test
//    public void test() throws UnsupportedEncodingException {
//        String result1 = apiClient.getNameByGet("槿泽");
//        String result2 = apiClient.getNameByPost("槿泽");
//        String result3 = apiClient.getNameByPostWithJson(new User("槿泽"));
////        System.out.println(result1);
////        System.out.println(result2);
//        System.out.println(result3);
//    }
    @Test
    void contextLoads() {
    }

}
