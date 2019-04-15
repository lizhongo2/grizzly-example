package cn.org.bjca.example.client;

import cn.org.bjca.example.socket.ExampleMessage;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;

import java.util.concurrent.CountDownLatch;

/**
 * @author lizhong
 * @create：2019-04-09 下午 03:01
 */
@Slf4j
public class ThreadRequest extends Thread {
  TCPNIOTransport tcpnioTransport;
  private CountDownLatch countDownLatch;

  public ThreadRequest(
      String name, TCPNIOTransport tcpnioTransport, CountDownLatch countDownLatch) {
    super(name);
    this.tcpnioTransport = tcpnioTransport;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    Connection connection = null;
    while (true) {
      try {
        connection = tcpnioTransport.connect("localhost", 8080).get();
        ExampleMessage exampleMessage = new ExampleMessage();
        exampleMessage.setHead(getName().getBytes("utf-8"));
        exampleMessage.setContent(getName().getBytes("utf-8"));
        connection.write(exampleMessage);
        Thread.sleep(1000);
      } catch (Exception e) {
        e.printStackTrace();
        countDownLatch.countDown();
        break;
      } finally {
        if (connection != null) {
          connection.close();
        }
      }
    }
  }
}
