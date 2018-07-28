/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;

/**
 * When invoke fails, log the initial error and retry other invokers (retry n times, which means at most n different invokers will be invoked)
 * Note that retry causes latency.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Failover">Failover</a>
 *
 * 以下摘自wiki:<br/>
 * 在计算机术语中，故障转移（英语：failover），即当活动的服务或应用意外终止时，快速启用冗余或备用的服务器、系统、硬件或者网络接替它们工作。 故障转移(failover)与交换转移操作基本相同，只是故障转移通常是自动完成的，没有警告提醒手动完成，而交换转移需要手动进行。
 *
 * 对于要求高可用和高稳定性的服务器、系统或者网络，系统设计者通常会设计故障转移功能。
 *
 * 在服务器级别，自动故障转移通常使用一个“心跳”线连接两台服务器。只要主服务器与备用服务器间脉冲或“心跳”没有中断，备用服务器就不会启用。为了热切换和防止服务中断，也可能会有第三台服务器运行备用组件待命。当检测到主服务器“心跳”报警后，备用服务器会接管服务。有些系统有发送故障转移通知的功能。
 *
 * 有些系统故意设计为不能进行完全自动故障转移，而是需要管理员介入。这种“人工确认的自动故障转移”配置，当管理员确认进行故障转移后，整个过程将自动完成。
 *
 * 故障恢复(failback)是将系统，组件，服务恢复到故障之前的组态。
 *
 * 使用虚拟化允许故障转移操作减少对硬件的依赖。更多信息请查看虚拟化
 */
public class FailoverClusterInvoker<T> extends AbstractClusterInvoker<T> {

  private static final Logger logger = LoggerFactory.getLogger(FailoverClusterInvoker.class);

  public FailoverClusterInvoker(Directory<T> directory) {
    super(directory);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers,
      LoadBalance loadbalance) throws RpcException {
    List<Invoker<T>> copyinvokers = invokers;
    checkInvokers(copyinvokers, invocation);
    int len = getUrl().getMethodParameter(invocation.getMethodName(), Constants.RETRIES_KEY,
        Constants.DEFAULT_RETRIES) + 1;
    if (len <= 0) {
      len = 1;
    }
    // retry loop.
    RpcException le = null; // last exception.
    List<Invoker<T>> invoked = new ArrayList<Invoker<T>>(copyinvokers.size()); // invoked invokers.
    Set<String> providers = new HashSet<String>(len);
    for (int i = 0; i < len; i++) {
      //Reselect before retry to avoid a change of candidate `invokers`.
      //NOTE: if `invokers` changed, then `invoked` also lose accuracy.
      if (i > 0) {
        checkWhetherDestroyed();
        copyinvokers = list(invocation);
        // check again
        checkInvokers(copyinvokers, invocation);
      }
      //4.进入负载均衡
      Invoker<T> invoker = select(loadbalance, invocation, copyinvokers, invoked);
      invoked.add(invoker);
      RpcContext.getContext().setInvokers((List) invoked);
      try {
        Result result = invoker.invoke(invocation);
        if (le != null && logger.isWarnEnabled()) {
          logger.warn("Although retry the method " + invocation.getMethodName()
              + " in the service " + getInterface().getName()
              + " was successful by the provider " + invoker.getUrl().getAddress()
              + ", but there have been failed providers " + providers
              + " (" + providers.size() + "/" + copyinvokers.size()
              + ") from the registry " + directory.getUrl().getAddress()
              + " on the consumer " + NetUtils.getLocalHost()
              + " using the dubbo version " + Version.getVersion() + ". Last error is: "
              + le.getMessage(), le);
        }
        return result;
      } catch (RpcException e) {
        if (e.isBiz()) { // biz exception.
          throw e;
        }
        le = e;
      } catch (Throwable e) {
        le = new RpcException(e.getMessage(), e);
      } finally {
        providers.add(invoker.getUrl().getAddress());
      }
    }
    throw new RpcException(le != null ? le.getCode() : 0, "Failed to invoke the method "
        + invocation.getMethodName() + " in the service " + getInterface().getName()
        + ". Tried " + len + " times of the providers " + providers
        + " (" + providers.size() + "/" + copyinvokers.size()
        + ") from the registry " + directory.getUrl().getAddress()
        + " on the consumer " + NetUtils.getLocalHost() + " using the dubbo version "
        + Version.getVersion() + ". Last error is: "
        + (le != null ? le.getMessage() : ""),
        le != null && le.getCause() != null ? le.getCause() : le);
  }

}
