package com.cloud.service.rpc.clientSub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: HeYongLiu
 * @create: 08-20-2019
 * @description: 作为客户端的业务处理类读取远程调用返回的数据
 **/
public class ResultHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse() {
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
        ctx.close();
    }

}
