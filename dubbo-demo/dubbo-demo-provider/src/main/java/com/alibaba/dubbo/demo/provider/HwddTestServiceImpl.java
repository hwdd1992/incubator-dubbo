package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.demo.HwddTestService;
import com.alibaba.dubbo.rpc.RpcContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hwdd1
 * @date 2019/5/5 7:30
 */
public class HwddTestServiceImpl implements HwddTestService {

  @Override
  public String sayHello(String name) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name
        + ", request from consumer: " + RpcContext
        .getContext().getRemoteAddress());
    return "Hello " + name + ", response from provider: " + RpcContext.getContext()
        .getLocalAddress();
  }
}
