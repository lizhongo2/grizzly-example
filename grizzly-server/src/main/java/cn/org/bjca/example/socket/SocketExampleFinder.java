package cn.org.bjca.example.socket;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.portunif.PUContext;
import org.glassfish.grizzly.portunif.ProtocolFinder;

import java.nio.charset.Charset;

/**
 * @author lizhong
 * @create：2019-04-08 上午 09:41 假设我们的私有协议，最前面几个字符是 socket_example,如果是 这些字符则认为是需要socket处理的报文
 */
public class SocketExampleFinder implements ProtocolFinder {
    private static final Charset charset=Charset.forName("utf-8");
    public static final byte[] magic="socket_example".getBytes(charset);
  @Override
  public Result find(PUContext puContext, FilterChainContext filterChainContext) {
      final Buffer inputBuffer = filterChainContext.getMessage();
      int lessIndex=Math.min(magic.length,inputBuffer.remaining());
      //比较当前读入的数据是否和magic 匹配，由于读入的数据可能小于 magic 所以需要将比较的截至点设置为 magic 和 buffer 较小值
      int postion=inputBuffer.position();
      for(int i=0;i<lessIndex;i++){
          if(magic[i]!=inputBuffer.get(postion+i)){
              //如果有一个不匹配则返回此协议不支持的信息，get(index) 为绝对地址读，不会导致 postion 移动
              return Result.NOT_FOUND;
          }
      }
      //如果发现部分匹配则要求继续读入，如果全部匹配则由此socket协议处理
      return lessIndex == magic.length ?
              Result.FOUND : Result.NEED_MORE_DATA;

  }
}
