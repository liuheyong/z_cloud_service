package com.cloud.service.rpc.impl;

import com.cloud.service.rpc.service.HelloNetty;

/**
 * @author: HeYongLiu
 * @create: 08-19-2019
 * @description: Server(服务的提供方)
 **/
public class HelloNettyImpl implements HelloNetty {
    @Override
    public String hello() {
        return "----> hello,netty <---";
    }
}
