package com.cloud.service.rpc.impl;

import com.cloud.service.rpc.service.HelloRPC;

/**
 * @author: HeYongLiu
 * @create: 08-19-2019
 * @description: Server(服务的提供方)
 **/
public class HelloRpcImpl implements HelloRPC {
    @Override
    public String hello(String name) {
        return "hello," + name;
    }
}
