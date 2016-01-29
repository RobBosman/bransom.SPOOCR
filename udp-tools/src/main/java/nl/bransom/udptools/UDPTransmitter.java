package nl.bransom.udptools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPTransmitter {

  private static final Logger LOG = LoggerFactory.getLogger(UDPTransmitter.class);

  public void transmit(final DatagramPacket packet) throws IOException {
    final DatagramSocket socket = new DatagramSocket();
    try {
      socket.setBroadcast(true);
      socket.send(packet);

      if (LOG.isDebugEnabled()) {
	LOG.debug(
	    "TRANSMITTED {} bytes to {}:{}\t{}",
	    new Object[] { packet.getLength(), packet.getAddress(), packet.getPort(),
	        Utils.bytesToHex(packet.getData(), packet.getOffset(), packet.getLength()) });
      }
    } finally {
      if (socket != null) {
	socket.close();
      }
    }
  }

}
