package com.cloud.service.rpc.client;


import com.cloud.service.rpc.clientSub.NettyRpcProxy;

/**
 * @author: HeYongLiu
 * @create: 08-20-2019
 * @description:
 **/
public class TestNettyRpc {

    public static void main(String[] args) {
        //第一次远程调用
        HelloNetty helloNetty = (HelloNetty) NettyRpcProxy.create(HelloNetty.class);
        System.out.println(helloNetty.hello());

        //第二次远程调用
        HelloRPC helloRPC = (HelloRPC) NettyRpcProxy.create(HelloRPC.class);
        System.out.println(helloRPC.hello("RPC"));
    }

}
