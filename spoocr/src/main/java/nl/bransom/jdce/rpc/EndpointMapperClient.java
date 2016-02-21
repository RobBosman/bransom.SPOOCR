package nl.bransom.jdce.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to access a (remote) RPC endpoint mapper service.
 * 
 * @author Rob
 */
public final class EndpointMapperClient {

  private static final Logger LOG = LoggerFactory.getLogger(EndpointMapperClient.class);
  private static final int BUFFER_SIZE = 1024;
  private static final int TIMEOUT_MILLIS = 100;

  /**
   * Sends a TCP request to the RPC <i>endpoint mapper</i> located at the given host and logs the response.
   * 
   * @param hostname
   *          e.g. localhost
   * @param port
   *          e.g 8000
   * @throws IOException
   *           if network errors occur
   */
  public void go(final String hostname, final int port) throws IOException {
    Socket socket = null;
    try {
      socket = new Socket(hostname, port);
      socket.setSoTimeout(TIMEOUT_MILLIS);
      final InputStream is = socket.getInputStream();
      final byte[] b = new byte[BUFFER_SIZE];
      while (is.read(b) > 0) {
        LOG.info("{}", new String(b, "UTF8"));
      }
    } catch (SocketTimeoutException e) {
      LOG.error(e.getMessage(), e);
    } finally {
      if (socket != null) {
        socket.close();
      }
    }
  }
}
