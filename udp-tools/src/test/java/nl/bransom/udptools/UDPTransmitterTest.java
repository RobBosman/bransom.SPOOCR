package nl.bransom.udptools;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.junit.Test;

public class UDPTransmitterTest {

  @Test
  public void testWOL() throws Exception {
    final String hostname = "10.0.0.255";
    final int port = WOLPacket.DEFAULT_PORT;
    final String macAddress = "6C:FD:B9:00:1C:FF";
    final String secureOnPassword = "DEADBEEFCAFE";

    final DatagramPacket packet = new WOLPacket(macAddress, secureOnPassword).getDatagramPacket();
    packet.setAddress(InetAddress.getByName(hostname));
    packet.setPort(port);
    new UDPTransmitter().transmit(packet);
  }
}
