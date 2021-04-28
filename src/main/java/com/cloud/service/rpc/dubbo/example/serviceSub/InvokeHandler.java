package com.cloud.service.rpc.dubbo.example.serviceSub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author: HeYongLiu
 * @create: 08-20-2019
 * @description: 服务器端业务处理类
 **/
public class InvokeHandler extends ChannelInboundHandlerAdapter {

    /**
     * @date: 2019/8/20
     * @description: 得到某接口下某个实现类的名字
     */
    private String getImplClassName(ClassInfo classInfo) throws ClassNotFoundException {
        Class<?> superClass = Class.forName(classInfo.getClassName());
        Reflections reflections = new Reflections(classInfo.getClassName().substring(0, classInfo.getClassName().lastIndexOf(".")));
        //得到某接口下的所有实现类
        Set<Class<?>> ImplClassSet = reflections.getSubTypesOf((Class<Object>) superClass);
        if (ImplClassSet.size() == 0) {
            System.out.println("未找到实现类");
            return null;
        } else if (ImplClassSet.size() > 1) {
            System.out.println("找到多个实现类，未明确使用哪一个");
            return null;
        } else {
            //把集合转换为数组
            Class[] classes = ImplClassSet.toArray(new Class[0]);
            return classes[0].getName();//得到实现类的名字
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo classInfo = (ClassInfo) msg;
        Object clazz = Class.forName(getImplClassName(classInfo)).newInstance();
        Method method = clazz.getClass().getMethod(classInfo.getMethodName(), classInfo.getTypes());
        //通过反射调用实现类的方法
        Object result = method.invoke(clazz, classInfo.getObjects());
        ctx.writeAndFlush(result);
    }

}
