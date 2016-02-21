package nl.bransom.jdce.ndr;

import java.nio.charset.Charset;

/**
 * Use this utillity class to format and display the content of a byte buffer as the WireShark <i>bits view</i> or
 * <i>hex view</i>.
 * 
 * <p>
 * This is an example of the <i>hex view</i>
 * 
 * <pre>
 * 0000  00 00 00 00 12 34 56 78  48 65 6c 6c 6f 20 77 6f   .....4Vx Hello wo
 * 0010  72 6c 64 21                                        rld!
 * </pre>
 * 
 * and here is an example of the <i>bits view</i>
 * 
 * <pre>
 * 0000  00000000 00000000 00000000 00000000 00010010 00110100 01010110 01111000   .....4Vx
 * 0008  01001000 01100101 01101100 01101100 01101111 00100000 01110111 01101111   Hello wo
 * 0010  01110010 01101100 01100100 00100001                                       rld!
 * </pre>
 * 
 * @author Rob
 */
public final class WireSharkViewer {

  private static final int FIRST_PRINTABLE_CHAR = 0x10;
  private static final int NUM_BYTES_PER_HEXVIEW_LINE = 16;
  private static final int NUM_BITS_PER_BITSVIEW_LINE = 8;

  /**
   * Hidden constructor.
   */
  private WireSharkViewer() {
  }

  /**
   * Mimics the <i>hex view</i> output of WireShark.
   * 
   * <pre>
   * 0000  00 00 00 00 12 34 56 78  48 65 6c 6c 6f 20 77 6f   .....4Vx Hello wo
   * 0010  72 6c 64 21                                        rld!
   * </pre>
   * 
   * @param bytes
   *          content bytes
   * @param offset
   *          byte offset in {@code bytes}
   * @param numBytes
   *          number of content bytes in {@code bytes}
   * @param charset
   *          character set to be used for converting bytes to characters
   * @return String representation of {@code bytes} in WireShark <i>hex view</i> style.
   */
  public static String getHexView(final byte[] bytes, final int offset, final int numBytes, final Charset charset) {
    final StringBuilder hexView = new StringBuilder();
    for (int line = 0; line < (numBytes + NUM_BYTES_PER_HEXVIEW_LINE - 1) / NUM_BYTES_PER_HEXVIEW_LINE; line++) {
      final int numBytesOnThisLine = Math.min(numBytes - line * NUM_BYTES_PER_HEXVIEW_LINE, NUM_BYTES_PER_HEXVIEW_LINE);
      if (line > 0) {
        hexView.append("\n");
      }
      final StringBuilder readableText = new StringBuilder();
      hexView.append(String.format("%04x", line * NUM_BYTES_PER_HEXVIEW_LINE));
      for (int i = 0; i < NUM_BYTES_PER_HEXVIEW_LINE; i++) {
        if (i % (NUM_BYTES_PER_HEXVIEW_LINE / 2) == 0) {
          hexView.append(" ");
          readableText.append(" ");
        }
        if (i < numBytesOnThisLine) {
          final byte aByte = bytes[offset + line * NUM_BYTES_PER_HEXVIEW_LINE + i];
          hexView.append(String.format(" %02x", aByte));
          readableText.append(byteToChar(aByte, charset));
        } else {
          hexView.append("   ");
        }
      }
      hexView.append("   ");
      hexView.append(readableText.toString().trim());
    }
    return hexView.toString();
  }

  /**
   * Mimics the <i>bits view</i> output of WireShark.
   * 
   * <pre>
   * 0000  00000000 00000000 00000000 00000000 00010010 00110100 01010110 01111000   .....4Vx
   * 0008  01001000 01100101 01101100 01101100 01101111 00100000 01110111 01101111   Hello wo
   * 0010  01110010 01101100 01100100 00100001                                       rld!
   * </pre>
   * 
   * @param bytes
   *          content bytes
   * @param offset
   *          byte offset in {@code bytes}
   * @param numBytes
   *          number of content bytes in {@code bytes}
   * @param charset
   *          character set to be used for converting bytes to characters
   * @return String representation of {@code bytes} in WireShark <i>bits view</i> style.
   */
  public static String getBitsView(final byte[] bytes, final int offset, final int numBytes, final Charset charset) {
    final StringBuilder bitsView = new StringBuilder();
    for (int line = 0; line < (numBytes + NUM_BITS_PER_BITSVIEW_LINE - 1) / NUM_BITS_PER_BITSVIEW_LINE; line++) {
      final int numBytesOnThisLine = Math.min(numBytes - line * NUM_BITS_PER_BITSVIEW_LINE, NUM_BITS_PER_BITSVIEW_LINE);
      if (line > 0) {
        bitsView.append("\n");
      }
      final StringBuilder readableText = new StringBuilder();
      bitsView.append(String.format("%04x ", line * NUM_BITS_PER_BITSVIEW_LINE));
      for (int i = 0; i < NUM_BITS_PER_BITSVIEW_LINE; i++) {
        if (i < numBytesOnThisLine) {
          final byte aByte = bytes[offset + line * NUM_BITS_PER_BITSVIEW_LINE + i];
          bitsView.append(" ");
          appendBitsOfByte(aByte, bitsView);
          readableText.append(byteToChar(aByte, charset));
        } else {
          bitsView.append("         ");
        }
      }
      bitsView.append("   ");
      bitsView.append(readableText.toString().trim());
    }
    return bitsView.toString();
  }

  private static void appendBitsOfByte(final byte aByte, final StringBuilder bitsView) {
    for (int b = NDRConstants.NUM_BITS_PER_BYTE - 1; b >= 0; b--) {
      if (((aByte >>> b) & 0x01) == 0x00) {
        bitsView.append("0");
      } else {
        bitsView.append("1");
      }
    }
  }

  private static String byteToChar(final byte aByte, final Charset charset) {
    if ((aByte & NDRConstants.BYTE_MASK) < FIRST_PRINTABLE_CHAR) {
      return ".";
    } else {
      return new String(new byte[] { aByte }, charset);
    }
  }
}
