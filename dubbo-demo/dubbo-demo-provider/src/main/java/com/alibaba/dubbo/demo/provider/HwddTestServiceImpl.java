package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.demo.HwddTestService;
import com.alibaba.dubbo.rpc.RpcContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hwdd
 * @date 19-4-23 下午11:39
 */
public class HwddTestServiceImpl implements HwddTestService {
    @Override
    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss")
                .format(new Date()) + "] Hello " + name + ", request from consumer: "
                + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

    @Override
    public void sayByeBye() {
        System.out.println("bye bye...");
    }
}
