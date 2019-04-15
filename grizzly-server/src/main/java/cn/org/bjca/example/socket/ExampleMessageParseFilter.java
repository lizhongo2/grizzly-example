package cn.org.bjca.example.socket;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;

import java.io.IOException;

/**
 * @author lizhong
 * @create：2019-04-08 上午 10:07 负责 message 的解析和转换 exampleMessage<---->buffer
 */
public class ExampleMessageParseFilter extends BaseFilter {
  /**
   * {@inheritDoc}
   *
   * @param ctx
   */
  @Override
  public NextAction handleRead(FilterChainContext ctx) throws IOException {
    Buffer buffer = ctx.getMessage();
    // 当前缓冲区中读入的字节数
    int available = buffer.remaining();
    // 一个消息总的字节数
    int messageCount =
        SocketExampleFinder.magic.length
            + ExampleMessage.HEAD_LENGTH
            + ExampleMessage.CONTENT_LENGTH;
    if (available < messageCount) {
      // 缓冲区读入的不是一个完整的消息，保存消息并等待下一次调用
      return ctx.getStopAction(buffer);
    }
    Buffer remainder = null;
    // 缓冲区中的消息大于一个消息
    if (available > messageCount) {
      // 多余的消息
      remainder = buffer.split(messageCount);
    }
    ExampleMessage message = new ExampleMessage();
    // skip magic
    buffer.position(SocketExampleFinder.magic.length);
    byte[] head = new byte[ExampleMessage.HEAD_LENGTH];
    // 获取 head 内容 并填充消息
    buffer.get(head);
    message.setHead(head);
    // 获取 content 内容并填充
    byte[] content = new byte[ExampleMessage.CONTENT_LENGTH];
    buffer.get(content);
    message.setContent(content);
    ctx.setMessage(message);
    buffer.dispose();
    return ctx.getInvokeAction(remainder);
  }

  /**
   * {@inheritDoc} 将 exampleMessage 转化为buffer
   *
   * @param ctx
   */
  @Override
  public NextAction handleWrite(FilterChainContext ctx)  {
    ExampleMessage exampleMessage = ctx.getMessage();
    MemoryManager memoryManager = ctx.getConnection().getTransport().getMemoryManager();
    int messageCount =
        SocketExampleFinder.magic.length
            + ExampleMessage.HEAD_LENGTH
            + ExampleMessage.CONTENT_LENGTH;
    final Buffer output = memoryManager.allocate(messageCount);
    output.allowBufferDispose(true);
    output.put(SocketExampleFinder.magic);
    output.put(exampleMessage.getHead());
    output.put(exampleMessage.getContent());
    ctx.setMessage(output.flip());
    return ctx.getInvokeAction();
  }
}
