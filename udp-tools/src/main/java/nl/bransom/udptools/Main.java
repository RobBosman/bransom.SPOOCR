package nl.bransom.udptools;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Main {

  public static void main(final String[] args) throws Exception {
    String action = null;
    int port = WOLPacket.DEFAULT_PORT;
    String hostname = "10.0.0.255";
    String macAddresses = "6C:FD:B9:00:1C:FF, 00:13:8F:AD:C0:68";
    String secureOnPassword = "DEADBEEFCAFE";

    for (String arg : args) {
      final String uArg = arg.toUpperCase();
      if (uArg.equals("-TRANSMIT")) {
	action = "TRANSMIT";
      } else if (uArg.equals("-LISTEN")) {
	action = "LISTEN";
      } else if (uArg.equals("-ECHO")) {
	action = "ECHO";
      } else if (uArg.startsWith("PORT=")) {
	port = Integer.parseInt(arg.substring("PORT=".length()));
      } else if (uArg.startsWith("HOST=")) {
	hostname = arg.substring("HOST=".length());
      } else if (uArg.startsWith("MAC=")) {
	macAddresses = arg.substring("MAC=".length());
      } else if (uArg.startsWith("SECUREON=")) {
	secureOnPassword = arg.substring("SECUREON=".length());
      } else {
	System.err.println("Invalid argument '" + arg + "'");
	showUsageAndExit();
      }
    }
    if (action == null) {
      showUsageAndExit();
    }

    final DatagramPacketDispatcher dpDispatcher = new DatagramPacketDispatcher();
    switch (action) {
    case "LISTEN":
      dpDispatcher.registerProcessor(new WOLPacketProcessor());
      UDPListener.getInstance().startListening(port, dpDispatcher);
      break;
    case "TRANSMIT":
      for (String macAddress : macAddresses.split(",")) {
	final DatagramPacket packet = new WOLPacket(macAddress, secureOnPassword).getDatagramPacket();
	packet.setAddress(InetAddress.getByName(hostname));
	packet.setPort(port);
	new UDPTransmitter().transmit(packet);
      }
      break;
    case "ECHO":
      dpDispatcher.registerProcessor(new EchoPacketProcessor());
      UDPListener.getInstance().startListening(port, dpDispatcher);
      break;
    default:
      showUsageAndExit();
    }
  }

  private static void showUsageAndExit() {
    System.err.println("Usage: (-transmit | -listen | -echo) [options]"
	+ "\n\tport=9\n\thost=10.0.0.255\n\tmac=6C:FD:B9:00:1C:FF\n\tsecureOn=DEADBEEFCAFE");
    System.exit(1);
  }

}
