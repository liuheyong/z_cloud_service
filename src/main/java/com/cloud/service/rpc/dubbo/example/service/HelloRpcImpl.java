package com.cloud.service.rpc.dubbo.example.service;

/**
 * @author: HeYongLiu
 * @create: 08-19-2019
 * @description: Server(服务的提供方)
 **/
public class HelloRpcImpl implements HelloRPC {
    @Override
    public String hello(String name) {
        return "hello," + name + "(服务端)";
    }
}
