package nl.bransom.udptools;

import java.io.IOException;
import java.net.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoPacketProcessor implements DatagramPacketProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(EchoPacketProcessor.class);

  private UDPTransmitter udpTransmitter;

  public EchoPacketProcessor() {
    udpTransmitter = new UDPTransmitter();
  }

  public boolean processData(final DatagramPacket packet) {
    try {
      LOG.debug("RECEIVED {} bytes from {}:{}",
	  new Object[] { packet.getLength(), packet.getAddress(), packet.getPort() });

      udpTransmitter.transmit(packet);
      return true;
    } catch (IOException e) {
      LOG.error("Error echoing packet", e);
      return false;
    }
  }

}
