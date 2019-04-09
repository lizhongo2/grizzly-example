package cn.org.bjca.example.socket;

import java.nio.charset.Charset;

/**
 * @author lizhong
 * @create：2019-04-08 上午 11:04 假设消息头固定为10字节,不足使用空字符补充
 *     <p>消息体100字节 不足使用空格补充
 */
public class ExampleMessage {
  private static final Charset charset = Charset.forName("utf-8");
  private static final byte padding = ' ';
  private byte[] content;

  public byte[] getHead() {
    return head;
  }

  public void setHead(byte[] head) {
    fillHead(head);
  }

  private byte[] head;
  public static final int HEAD_LENGTH = 20;
  public static final int CONTENT_LENGTH = 100;

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    fillContent(content);
  }

  public String getHeadByUtf8() {
    return new String(head, charset).trim();
  }

  public String getContentByUtf8() {
    return new String(content, charset).trim();
  }

  /** 如果head 内容不够10个字节，则使用空格填充 */
  private void fillHead(byte[] head) {
    byte[] bytes = fill(head, HEAD_LENGTH);
    this.head = bytes;
  }

  /** 如果 content 不够100字节 则使用空格填充 */
  private void fillContent(byte[] content) {
    byte[] bytes = fill(content, CONTENT_LENGTH);
    this.content = bytes;
  }

  private byte[] fill(byte[] src, int length) {
    byte[] bytes = new byte[length];
    System.arraycopy(src, 0, bytes, 0, src.length);
    for (int i = src.length; i < length; i++) {
      bytes[i] = padding;
    }
    return bytes;
  }
}
