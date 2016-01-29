package nl.bransom.udptools;

import org.junit.Test;

public class UDPListenerTest {

  @Test
  public void test() throws Exception {
    final int port = WOLPacket.DEFAULT_PORT;

    DatagramPacketDispatcher dpDispatcher = new DatagramPacketDispatcher();
    dpDispatcher.registerProcessor(new WOLPacketProcessor());
    UDPListener.getInstance().startListening(port, dpDispatcher);

    Thread.yield();
    // Thread.sleep(10 * 1000);

    UDPListener.getInstance().stopListening(port);
  }

}
