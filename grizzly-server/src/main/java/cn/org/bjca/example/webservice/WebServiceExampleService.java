package cn.org.bjca.example.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author lizhong
 * @create：2019-04-08 上午 10:44
 */
@WebService
public class WebServiceExampleService {
    @WebMethod
    public String hello(@WebParam String message){

        return "hello>"+message;
    }
}
