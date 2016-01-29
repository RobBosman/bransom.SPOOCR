package nl.bransom.udptools;

import java.util.Arrays;

public class MACAddress {

  public static final int NUM_BYTES = 6;

  public static MACAddress parse(final String macAddressString) {
    if (macAddressString == null) {
      return null;
    }
    final byte[] bytes = Utils.hexToBytes(macAddressString.replaceAll("[^0-9a-fA-F]", ""));
    if (bytes == null || bytes.length < NUM_BYTES) {
      return null;
    }
    return new MACAddress(bytes, 0);
  }

  private byte[] bytes;

  public MACAddress(final byte[] bytes) {
    this(bytes, 0);
  }

  public MACAddress(final byte[] bytes, final int offset) {
    if (bytes == null || bytes.length < offset + NUM_BYTES) {
      throw new IllegalArgumentException("MAC address requires " + NUM_BYTES + " bytes");
    }
    this.bytes = Arrays.copyOfRange(bytes, offset, offset + NUM_BYTES);
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    String hex = Utils.bytesToHex(bytes, 0, NUM_BYTES);
    if (hex.length() > 2) {
      hex = hex.replaceAll(".{2}", ":$0").substring(1);
    }
    return hex.toUpperCase();
  }

}
