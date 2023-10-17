package com.vkls.project.rpc;

//在消费者这一方，该文件的路径要和生产者中该文件的路径保持一致
//在生产者中，该文件在com.vkls.project.rpc包下，所以在该项目中，就在vkls下新建了一个project.rpc包
public interface RpcDemoService {
    String sayHello(String name);
}