package cn.org.bjca.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lizhong
 * @create：2019-04-12 上午 09:10
 */
@Slf4j
public class NIOServer {
  public static void main(String[] args) throws IOException {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    SocketAddress socketAddress = new InetSocketAddress("localhost", 8081);
    serverSocketChannel.socket().bind(socketAddress);
    serverSocketChannel.configureBlocking(false);
    Selector selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
      int count = selector.select();
      if (count < 1) {
        continue;
      }
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selectionKeys.iterator();
      traverseKey:
      while (iterator.hasNext()) {
        SelectionKey selectionKey = iterator.next();
        if (selectionKey.isAcceptable()) {
          ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
          SocketChannel socketChannel = serverSocketChannel1.accept();
          if (socketChannel == null) {
            iterator.remove();
            continue traverseKey;
          }
          // 注册读写事件
          Selector selector1 = selectionKey.selector();
          ByteBuffer byteBuffer = ByteBuffer.allocate(10);
          socketChannel.configureBlocking(false);
          socketChannel.register(
              selector1, SelectionKey.OP_READ,byteBuffer);
        }
        if (selectionKey.isReadable()) {
            handleRead(selectionKey);
          }
        iterator.remove();
      }
    }
  }


  private static void responseMessage(SocketChannel sc) throws IOException{
    byte[] req ="服务器已接受".getBytes("utf-8");
    ByteBuffer byteBuffer = ByteBuffer.allocate(req.length+4);
    byteBuffer.putInt(req.length);
    byteBuffer.put(req);
    byteBuffer.flip();
    while(byteBuffer.hasRemaining()){
      sc.write(byteBuffer);
    }
    log.debug("响应消息发送成功！");
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
        responseMessage(socketChannel);

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
}
