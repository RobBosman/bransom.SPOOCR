package nl.bransom.udptools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPListenerThreadWorker implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(UDPListenerThreadWorker.class);

  private int port;
  private DatagramPacketDispatcher dpDispatcher;
  private DatagramSocket socket;

  public UDPListenerThreadWorker(final int port, final DatagramPacketDispatcher dpDispatcher) throws SocketException {
    this.port = port;
    this.dpDispatcher = dpDispatcher;
    socket = new DatagramSocket(port);
  }

  public void run() {
    try {
      LOG.debug("Started listening for UDP on port {}", port);

      final byte[] receiveBuffer = new byte[256];
      final DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      while (true) {
	socket.receive(packet);
	dpDispatcher.dispatch(packet);
      }
    } catch (SocketException e) {
      // Ignore the exception if the socket is already closed.
      if (socket != null) {
	e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socket != null) {
	socket.close();
	socket = null;
      }

      LOG.debug("Stopped listening for UDP on port {}", port);
    }
  }

  public void stop() {
    if (socket != null) {
      // First set the socket to null, indicating that it is closed deliberately...
      DatagramSocket socketToClose = socket;
      socket = null;
      // ...and then actually close it.
      socketToClose.close();
    }
  }

}
