package cn.org.bjca.example.client;

import cn.org.bjca.example.socket.ExampleMessage;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.ReadResult;
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.impl.FutureImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author lizhong
 * @create：2019-04-09 下午 03:01
 */
public class ThreadRequest extends Thread {
  Future<Connection> connectFuture;
  private CountDownLatch countDownLatch;

  public ThreadRequest(
      String name, Future<Connection> connectFuture, CountDownLatch countDownLatch) {
    super(name);
    this.connectFuture = connectFuture;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    Connection connection = null;
    try {
      while (true) {
        connection = connectFuture.get();
        connection.configureBlocking(false);
        ExampleMessage exampleMessage = new ExampleMessage();
        exampleMessage.setHead(getName().getBytes("utf-8"));
        exampleMessage.setContent(getName().getBytes("utf-8"));
        GrizzlyFuture<WriteResult> writeResultGrizzlyFuture = connection.write(exampleMessage);
        WriteResult writeResult = writeResultGrizzlyFuture.get();
        int writeSize = (int) writeResult.getWrittenSize();
        System.out.println("writerSize:" + writeSize);
        GrizzlyFuture<ReadResult> readResultGrizzlyFuture = connection.read();
        ReadResult readResult = readResultGrizzlyFuture.get();
        ExampleMessage readResultMessage = (ExampleMessage) readResult.getMessage();
        System.out.println("服务端返回内容：");
        System.out.println(
            "head:"
                + readResultMessage.getHeadByUtf8()
                + " content:"
                + readResultMessage.getContentByUtf8());
        Thread.sleep(2000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.close();
      }
      countDownLatch.countDown();
    }
  }
}
