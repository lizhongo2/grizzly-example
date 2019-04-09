package cn.org.bjca.example.client;

import cn.org.bjca.example.socket.ExampleMessageParseFilter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author lizhong
 * @create：2019-04-08 下午 05:30
 */
public class ExampleSocketClient {
    private static int THREAD_COUNT=1;
    private static CountDownLatch countDownLatch=new CountDownLatch(THREAD_COUNT);
  public static void main(String[] args) throws IOException, InterruptedException {
    final FilterChainBuilder puFilterChainBuilder = FilterChainBuilder.stateless();
    puFilterChainBuilder.add(new TransportFilter()).add(new ExampleMessageParseFilter());
    final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
    transport.setProcessor(puFilterChainBuilder.build());
    transport.start();
    for (int i = 0; i < THREAD_COUNT; i++) {
      new ThreadRequest("thread" + i, transport.connect("localhost", 8080),countDownLatch).start();
    }
      countDownLatch.await();
    transport.shutdownNow();
  }
}
