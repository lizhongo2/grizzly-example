package cn.org.bjca.example.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author lizhong
 * @create：2019-04-08 下午 04:38
 */
public class ExampleWebserviceClient {
  public static void main(String[] args) throws MalformedURLException {
    //首先生成客户端代码。。。使用相应工具
    WebServiceExampleServiceService webServiceExampleServiceService=
            new WebServiceExampleServiceService(new URL("http://127.0.0.1:8080/hello?wsdl"));
    WebServiceExampleService webServiceExampleService= webServiceExampleServiceService.getWebServiceExampleServicePort();
    String helloResponse= webServiceExampleService.hello("grizzly webservice");
    System.out.println("hello response:"+helloResponse);
  }
}
