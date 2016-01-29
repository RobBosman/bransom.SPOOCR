package nl.bransom.udptools;

import java.net.DatagramPacket;

public interface DatagramPacketProcessor {

  boolean processData(final DatagramPacket packet);

}
