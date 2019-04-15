package cn.org.bjca.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * @author lizhong
 * @create：2019-04-15 上午 10:54
 */
public class RestClient {
  public static void main(String[] args) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:8080/rest/");
    String response = target.path("hello").request().get(String.class);
    System.out.println("rest接口响应：" + response);
  }
}
