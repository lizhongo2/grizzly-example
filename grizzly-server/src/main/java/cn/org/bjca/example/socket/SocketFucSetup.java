package cn.org.bjca.example.socket;

import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChain;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.http.server.AddOn;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.portunif.PUFilter;
import org.glassfish.grizzly.portunif.PUProtocol;

/**
 * @author lizhong
 * @create：2019-04-08 上午 09:29
 * get httpServer to support socket protocol
 */
public class SocketFucSetup implements AddOn {

  @Override
  public void setup(NetworkListener networkListener, FilterChainBuilder builder) {
      // httpServer 只支持 http 协议，如果需要增加 socket协议的支持需要注册‘解析器<>协议处理链‘的键值对

      //此filter 需要注册到TransportFilter 后面，也就是说socket 的读操作先由此filter 根据注册的解析对子判断该由那种协议来处理
      final PUFilter puFilter = new PUFilter(false);
      SocketExampleFinder socketExampleFinder=new SocketExampleFinder();
      //exampleMessage 处理链
      FilterChain exampleMessageChain=puFilter.getPUFilterChainBuilder().add(new ExampleMessageParseFilter())
              .add(new ExampleMessageProcessFilter()).build();
      //构造键值对
      PUProtocol puProtocol=new PUProtocol(socketExampleFinder,exampleMessageChain);
      //注册
      puFilter.register(puProtocol);
      final int transportFilterIdx =
              builder.indexOfType(TransportFilter.class);

      assert transportFilterIdx != -1;

      builder.add(transportFilterIdx + 1, puFilter);

  }
}
