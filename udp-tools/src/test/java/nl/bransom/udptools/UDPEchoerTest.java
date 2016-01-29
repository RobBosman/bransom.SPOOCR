package nl.bransom.udptools;

import org.junit.Test;

public class UDPEchoerTest {

  @Test
  public void test() throws Exception {
    final int port = WOLPacket.DEFAULT_PORT;

    DatagramPacketDispatcher dpDispatcher = new DatagramPacketDispatcher();
    dpDispatcher.registerProcessor(new EchoPacketProcessor());
    UDPListener.getInstance().startListening(port, dpDispatcher);

    Thread.yield();
    // Thread.sleep(10 * 1000);

    UDPListener.getInstance().stopListening(port);
  }

}
