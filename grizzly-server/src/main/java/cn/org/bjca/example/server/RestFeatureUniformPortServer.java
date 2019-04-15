package cn.org.bjca.example.server;

import cn.org.bjca.example.socket.SocketFucSetup;
import cn.org.bjca.example.webservice.WebServiceExampleService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Unified port server can use one port to provide various types of services such as restful
 * service,webservice service,socket service and so on
 * <p>
 *     此服务器支持 socket webservice 和 rest 同时在一个端口进行通讯
 * </p>
 */
public class RestFeatureUniformPortServer {

  public static void main(String[] args) throws IOException {
    // 需要扫描的注解了rest 相关注解的包路径
    final ResourceConfig rc = new ResourceConfig().packages("cn.org.bjca.example.restful");
    // 保证httpServer 没有启动，以便增加其他设置
    HttpServer httpServer =
        GrizzlyHttpServerFactory.createHttpServer(
            URI.create("http://localhost:8080/rest"), rc, false);
    // jersey 会注册一个名字为grizzly 的监听，监听的地址和端口通过工厂方法传入的baseurl 指定
    NetworkListener networkListener = httpServer.getListener("grizzly");
    // 增加功能，使httpServer 能够处理 socket通信
    networkListener.registerAddOn(new SocketFucSetup());
    // 控制连接最大活动时间，超过此时间释放
    networkListener.getKeepAlive().setIdleTimeoutInSeconds(30);
    //开启监控
    httpServer.getServerConfiguration().setJmxEnabled(true);
    // 处理http 协议相关 1 增加webservice 处理器
    httpServer
        .getServerConfiguration()
        .addHttpHandler(new JaxwsHandler(new WebServiceExampleService()), "/hello");
    httpServer.start();
    while (true) {
      byte[] bytes = new byte[1024];
      int i = System.in.read(bytes);
      String input = new String(bytes, 0, i);
      if (input.startsWith("c")) {
        break;
      }
    }
  }
}
