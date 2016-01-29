package nl.bransom.udptools;

import java.nio.ByteBuffer;

public class Utils {

  private static final String HEX_CHARS = "0123456789abcdef";

  private Utils() {
  }

  public static String bytesToHex(final byte[] bytes, final int offset, final int length) {
    if (bytes == null) {
      return null;
    }
    final StringBuilder sb = new StringBuilder();
    for (int i = offset; i < offset + length; i++) {
      sb.append(HEX_CHARS.charAt((bytes[i] & 0xF0) >> 4));
      sb.append(HEX_CHARS.charAt(bytes[i] & 0x0F));
    }
    return sb.toString();
  }

  public static String bytesToHex(final ByteBuffer byteBuffer) {
    if (byteBuffer == null) {
      return null;
    }
    return bytesToHex(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
  }

  public static byte[] hexToBytes(final String hex) {
    if (hex == null) {
      return null;
    }
    String evenHex = hex.toLowerCase();
    if (hex.length() % 2 != 0) {
      evenHex = "0" + evenHex;
    }
    final byte[] bytes = new byte[evenHex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      int msb = HEX_CHARS.indexOf(evenHex.charAt(2 * i));
      int lsb = HEX_CHARS.indexOf(evenHex.charAt(2 * i + 1));
      if (msb < 0 || lsb < 0) {
	final int errorPosition = 2 * i + (msb < 0 ? 0 : 1) - hex.length() % 2;
	throw new IllegalArgumentException("Hex string contains illegal character '" + hex.charAt(errorPosition)
	    + "' at position " + errorPosition + ".");
      }
      bytes[i] = (byte) (msb * 16 + lsb);
    }
    return bytes;
  }

}
