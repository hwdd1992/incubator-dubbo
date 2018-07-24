package org.apache.dubbo.rpc;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author: wendong.hu
 * @Date: 2018/7/16
 * @Time: 11:28
 * @Description:
 */
public class ProxyFactory$Adaptive implements org.apache.dubbo.rpc.ProxyFactory{

  public java.lang.Object getProxy(org.apache.dubbo.rpc.Invoker arg0)
      throws org.apache.dubbo.rpc.RpcException {
    if (arg0 == null) {
      throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument == null");
    }
    if (arg0.getUrl() == null) {
      throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument getUrl() == null");
    }
    org.apache.dubbo.common.URL url = arg0.getUrl();
    String extName = url.getParameter("proxy", "javassist");
    if (extName == null) {
      throw new IllegalStateException(
          "Fail to get extension(org.apache.dubbo.rpc.ProxyFactory) name from url(" + url.toString()
              + ") use keys([proxy])");
    }
    org.apache.dubbo.rpc.ProxyFactory extension = (org.apache.dubbo.rpc.ProxyFactory) ExtensionLoader
        .getExtensionLoader(org.apache.dubbo.rpc.ProxyFactory.class).getExtension(extName);
    return extension.getProxy(arg0);
  }

  public java.lang.Object getProxy(org.apache.dubbo.rpc.Invoker arg0, boolean arg1)
      throws org.apache.dubbo.rpc.RpcException {
    if (arg0 == null) {
      throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument == null");
    }
    if (arg0.getUrl() == null) {
      throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument getUrl() == null");
    }
    org.apache.dubbo.common.URL url = arg0.getUrl();
    String extName = url.getParameter("proxy", "javassist");
    if (extName == null) {
      throw new IllegalStateException(
          "Fail to get extension(org.apache.dubbo.rpc.ProxyFactory) name from url(" + url.toString()
              + ") use keys([proxy])");
    }
    org.apache.dubbo.rpc.ProxyFactory extension = (org.apache.dubbo.rpc.ProxyFactory) ExtensionLoader
        .getExtensionLoader(org.apache.dubbo.rpc.ProxyFactory.class).getExtension(extName);
    return extension.getProxy(arg0, arg1);
  }

  public org.apache.dubbo.rpc.Invoker getInvoker(java.lang.Object ref, java.lang.Class clazz,
      org.apache.dubbo.common.URL exportUrL) throws org.apache.dubbo.rpc.RpcException {
    if (exportUrL == null) {
      throw new IllegalArgumentException("url == null");
    }
    org.apache.dubbo.common.URL url = exportUrL;
    String extName = url.getParameter("proxy", "javassist");
    if (extName == null) {
      throw new IllegalStateException(
          "Fail to get extension(org.apache.dubbo.rpc.ProxyFactory) name from url(" + url.toString()
              + ") use keys([proxy])");
    }
    org.apache.dubbo.rpc.ProxyFactory extension = (org.apache.dubbo.rpc.ProxyFactory) ExtensionLoader
        .getExtensionLoader(org.apache.dubbo.rpc.ProxyFactory.class).getExtension(extName);
    return extension.getInvoker(ref, clazz, exportUrL);
  }
}