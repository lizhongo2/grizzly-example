package cn.org.bjca.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lizhong
 * @create：2019-04-12 上午 09:10
 */
@Slf4j
public class NIOClient {
  public static void main(String[] args) throws IOException {
    Selector selector = Selector.open();
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress("localhost", 8081));
    socketChannel.register(selector, SelectionKey.OP_CONNECT);
    while (true) {
      selector.select();
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selectionKeys.iterator();
      while (iterator.hasNext()) {
        SelectionKey selectionKey = iterator.next();
        if (selectionKey.isConnectable()) {
          SocketChannel socketChannel1 = (SocketChannel) selectionKey.channel();
          if (socketChannel1.finishConnect()) {
            socketChannel1.register(
                selectionKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(10));
            sendRequestMessage(selectionKey);
          } else {
            log.error("连接异常！");
          }
        }
        if (selectionKey.isReadable()) {
          handleRead(selectionKey);
        }
        iterator.remove();
      }
    }
  }

  private static void handleRead(SelectionKey selectionKey) throws IOException {
    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
    ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
    int readCount = socketChannel.read(byteBuffer);
    if (byteBuffer.position() > 4) {
      int messageLength = ((ByteBuffer)(byteBuffer.duplicate().flip())).getInt();
      int count = byteBuffer.capacity();
      if (messageLength+4 > count) {
        // 消息长度不够
        ByteBuffer newByteBuffer = copyAndExpandBuffer(byteBuffer, messageLength+4, count);
        selectionKey.attach(newByteBuffer);
        return;
      }
      //检查是否已经包含一个消息
      if(messageLength+4<=byteBuffer.position()){
        byte[] bytes=new byte[messageLength];
        byteBuffer.position(4);
        byteBuffer.get(bytes);
        byteBuffer.compact();
        selectionKey.attach(byteBuffer);
        log.debug("读入数据：" + new String(bytes, "utf-8"));

      }
    } else {
      if (readCount == -1) {
        socketChannel.close();
        log.info("客户端连接断开" + socketChannel);
      }
    }
  }

  private static ByteBuffer copyAndExpandBuffer(ByteBuffer byteBuffer, int capacity, int count) {
    ByteBuffer newBuffer = ByteBuffer.allocate(capacity);
    byte[] bytes = new byte[count];
    byteBuffer.flip();
    byteBuffer.get(bytes);
    newBuffer.put(bytes);
    return newBuffer;
  }

  private static void sendRequestMessage(SelectionKey selectionKey) throws IOException {
    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
    byte[] message = "client message".getBytes("utf-8");
    ByteBuffer buffer = ByteBuffer.allocate(message.length+4);
    buffer.putInt(message.length);
    buffer.put(message);
    buffer.flip();
    while (buffer.hasRemaining()) {
      socketChannel.write(buffer);
    }
  }
}
