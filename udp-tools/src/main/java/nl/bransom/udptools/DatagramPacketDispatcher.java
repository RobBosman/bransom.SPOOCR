package nl.bransom.udptools;

import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatagramPacketDispatcher {

  private static final Logger LOG = LoggerFactory.getLogger(DatagramPacketDispatcher.class);

  private Set<DatagramPacketProcessor> dpProcessors;

  public DatagramPacketDispatcher() {
    dpProcessors = new HashSet<>();
  }

  public void registerProcessor(final DatagramPacketProcessor dpProcessor) {
    dpProcessors.add(dpProcessor);
  }

  public void dispatch(final DatagramPacket packet) {
    for (DatagramPacketProcessor dpProcessor : dpProcessors) {
      if (dpProcessor.processData(packet)) {
	return;
      }
    }

    if (LOG.isInfoEnabled()) {
      LOG.info("RECEIVED {} bytes that could not be processed:\n\t{}", packet.getLength(),
	  Utils.bytesToHex(packet.getData(), 0, packet.getLength()));
    }
  }

}
