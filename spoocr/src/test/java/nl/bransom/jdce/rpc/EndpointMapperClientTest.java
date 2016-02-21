package nl.bransom.jdce.rpc;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link EndpointMapperClient}
 * 
 * @author Rob
 */
public class EndpointMapperClientTest {

  @Test
  public void testGo() throws Exception {
    final EndpointMapperClient endpointMapperClient = new EndpointMapperClient();

    endpointMapperClient.go("localhost", 135);
    try {
      endpointMapperClient.go(null, -1);
      Assert.fail("Invalid parameter not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

}
