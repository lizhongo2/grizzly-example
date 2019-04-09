package cn.org.bjca.example.socket;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

/**
 * @author lizhong
 * @create：2019-04-08 上午 10:10 对 消息处理的message 真正的业务逻辑部分
 */
public class ExampleMessageProcessFilter extends BaseFilter {
  /**
   * {@inheritDoc}
   *
   * @param ctx
   */
  @Override
  public NextAction handleRead(FilterChainContext ctx) throws IOException {
    ExampleMessage exampleMessage = ctx.getMessage();
    ExampleMessage responseMessage = new ExampleMessage();
    responseMessage.setHead(("hello>" + exampleMessage.getHeadByUtf8()).getBytes("utf-8"));
    responseMessage.setContent(("hello>" + exampleMessage.getContentByUtf8()).getBytes("utf-8"));
    System.out.println("服务端返回内容：");
    System.out.println(
        "head:"
            + responseMessage.getHeadByUtf8()
            + " content:"
            + responseMessage.getContentByUtf8());
    Object peerAddress = ctx.getAddress();
    ctx.write(
        peerAddress,
        responseMessage,
        new CompletionHandler<WriteResult>() {
          @Override
          public void cancelled() {
            System.out.println("ExampleMessageProcessFilter.cancelled");
          }

          @Override
          public void failed(Throwable throwable) {
            System.out.println("ExampleMessageProcessFilter.failed");
          }

          @Override
          public void completed(WriteResult result) {
            System.out.println("ExampleMessageProcessFilter.completed");
            result.getConnection().close();
          }

          @Override
          public void updated(WriteResult result) {
            System.out.println("ExampleMessageProcessFilter.updated");
          }
        });
    return ctx.getStopAction();
  }
}
