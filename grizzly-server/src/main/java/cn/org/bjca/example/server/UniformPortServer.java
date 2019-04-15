package cn.org.bjca.example.server;

import cn.org.bjca.example.socket.SocketFucSetup;
import cn.org.bjca.example.webservice.WebServiceExampleService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;

import java.io.IOException;

/**
 * Unified port server can use one port to provide various types of services such as restful
 * service,webservice service,socket service and so on
 * <p>
 *     此服务器支持 socket 和 webservice 在同一个端口交互
 * </p>
 */
public class UniformPortServer {

  public static void main(String[] args) throws IOException {
      //此对象表示一个http 服务器
      HttpServer httpServer=new HttpServer();
      //networkListener 对象表示 服务器的一个监听端口，一个服务器可以监听
      //多个端口，例如 tomcat 默认会监听 8080、8005端口等，可以使用8080
      //端口提供各种服务
      NetworkListener networkListener = new NetworkListener(
              "uniformPortServer", "localhost", 8080);

      //增加功能，使httpServer 能够处理 socket通信
      networkListener.registerAddOn(new SocketFucSetup());
      //控制连接最大活动时间，超过此时间释放
      networkListener.getKeepAlive().setIdleTimeoutInSeconds(30);
      httpServer.addListener(networkListener);
      //处理http 协议相关 1 增加webservice 处理器
      httpServer.getServerConfiguration().
              addHttpHandler(new JaxwsHandler(new WebServiceExampleService()),"/hello");
      //开启监控
      httpServer.getServerConfiguration().setJmxEnabled(true);
      httpServer.start();
      while (true){
          byte[] bytes=new byte[1024];
          int i= System.in.read(bytes);
          String input=new String(bytes,0,i);
          if(input.startsWith("c")){
              break;
          }
      }
  }
}
