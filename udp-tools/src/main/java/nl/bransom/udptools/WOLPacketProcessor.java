package nl.bransom.udptools;

import java.net.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WOLPacketProcessor implements DatagramPacketProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(WOLPacketProcessor.class);

  public boolean processData(final DatagramPacket packet) {
    WOLPacket wolPacket = WOLPacket.parse(packet);
    if (wolPacket == null) {
      return false;
    }

    LOG.debug("RECEIVED WOL packet for {}", wolPacket);

    return true;
  }
}
