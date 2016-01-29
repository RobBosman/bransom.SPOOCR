package nl.bransom.udptools;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public class WOLPacket {

  public static final int DEFAULT_PORT = 9;

  // WOL packet: 6 bytes FF + 16 times the MAC address plus (optionally) 6 password bytes.
  public static final byte[] PREFIX = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
  public static final int NUM_MAC_ADDRESSES = 16;
  public static final int NUM_SECURE_ON_BYTES = 6;
  public static final int MIN_PACKET_LENGTH = PREFIX.length + NUM_MAC_ADDRESSES * MACAddress.NUM_BYTES;
  public static final int MAX_PACKET_LENGTH = MIN_PACKET_LENGTH + NUM_SECURE_ON_BYTES;

  public static WOLPacket parse(final DatagramPacket packet) {
    final ByteBuffer packetBuffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());

    // WOL packet: 6 bytes FF + 16 times the MAC address plus (optionally) 6 password bytes.
    if ((packet.getLength() != MIN_PACKET_LENGTH) && (packet.getLength() != MAX_PACKET_LENGTH)) {
      return null;
    }
    // Check the prefix.
    if (!ByteBuffer.wrap(packetBuffer.array(), packetBuffer.arrayOffset(), PREFIX.length).equals(
	ByteBuffer.wrap(PREFIX))) {
      return null;
    }
    // Get the MAC address.
    final ByteBuffer macAddressByteBuffer = ByteBuffer.wrap(packetBuffer.array(), packetBuffer.arrayOffset()
	+ PREFIX.length, MACAddress.NUM_BYTES);
    // Check if the MAC address is repeated 16 times.
    for (int i = 0; i < NUM_MAC_ADDRESSES; i++) {
      if (!ByteBuffer.wrap(packetBuffer.array(), packetBuffer.arrayOffset() + PREFIX.length + i * MACAddress.NUM_BYTES,
	  MACAddress.NUM_BYTES).equals(macAddressByteBuffer)) {
	return null;
      }
    }

    return new WOLPacket(new DatagramPacket(packetBuffer.array(), packetBuffer.arrayOffset(), packetBuffer.remaining()));
  }

  private DatagramPacket datagramPacket;

  public WOLPacket(final String macAddressHex, final String secureOnPasswordHex) {
    final MACAddress macAddress = MACAddress.parse(macAddressHex);
    byte[] secureOnPassword = Utils.hexToBytes(secureOnPasswordHex);
    if (secureOnPassword != null) {
      if (secureOnPassword.length == 0) {
	secureOnPassword = null;
      } else if (secureOnPassword.length != NUM_SECURE_ON_BYTES) {
	throw new IllegalArgumentException("Illegan SecureOn password '" + secureOnPasswordHex + "'; must be "
	    + NUM_SECURE_ON_BYTES + " bytes long.");
      }
    }

    final byte[] bytes = new byte[MIN_PACKET_LENGTH + (secureOnPassword == null ? 0 : NUM_SECURE_ON_BYTES)];
    datagramPacket = new DatagramPacket(bytes, bytes.length);
    final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    // Fill the prefix...
    byteBuffer.put(PREFIX);
    // ...repeatedly add the MAC address...
    for (int i = 0; i < NUM_MAC_ADDRESSES; i++) {
      byteBuffer.put(macAddress.getBytes());
    }
    // ...and append the password.
    if (secureOnPassword != null) {
      byteBuffer.put(secureOnPassword);
    }
  }

  protected WOLPacket(final DatagramPacket datagramPacket) {
    this.datagramPacket = datagramPacket;
  }

  public DatagramPacket getDatagramPacket() {
    return datagramPacket;
  }

  public MACAddress getMACAddress() {
    return new MACAddress(datagramPacket.getData(), PREFIX.length);
  }

  public ByteBuffer getSecureOnPassword() {
    if (datagramPacket.getLength() == MAX_PACKET_LENGTH) {
      return ByteBuffer.wrap(datagramPacket.getData(), MIN_PACKET_LENGTH, NUM_SECURE_ON_BYTES);
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    final ByteBuffer secureOnPassword = getSecureOnPassword();
    return getMACAddress().toString()
	+ (secureOnPassword == null ? "" : " [" + Utils.bytesToHex(secureOnPassword) + "]");
  }

}
