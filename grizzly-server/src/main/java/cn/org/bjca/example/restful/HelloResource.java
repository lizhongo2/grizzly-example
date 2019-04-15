package cn.org.bjca.example.restful;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/** Root resource (exposed at "hello" path) */
@Path("hello")
public class HelloResource {

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the client as
   * "application/json" media type.
   *
   * @return String that will be returned as a application/json response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, String> getIt() {
    Map<String, String> result = new HashMap<>();
    result.put("status", "success");
    result.put("data", "got it");
    return result;
  }
}
