package nl.bransom.jdce.rpc;

import java.io.IOException;
import java.util.Arrays;

import nl.bransom.jdce.ndr.NDRConstants;
import nl.bransom.jdce.ndr.NDRReader;
import nl.bransom.jdce.ndr.NDRSerializable;
import nl.bransom.jdce.ndr.NDRWriter;

/**
 * See appendix A of <a href="http://pubs.opengroup.org/onlinepubs/9629399/toc.pdf">document C706 of the
 * "Open Group"</a>.
 * 
 * <pre>
 * typedef struct {
 *   unsigned32 time_low;
 *   unsigned16 time_mid;
 *   unsigned16 time_hi_and_version;
 *   unsigned8 clock_seq_high_and_reserved;
 *   unsigned8 clock_seq_low;
 *   byte node[6];
 * } uuid_t;
 * </pre>
 * 
 * @author Rob
 */
public class UUID implements NDRSerializable {

  /**
   * Number of node bytes in {@code node}.
   */
  public static final int NODE_LENGTH = 6;

  private static final int HEX_RADIX = 16;
  private static final int[] PART_LENGTHS = { 8, 4, 4, 4, 2 * NODE_LENGTH };
  private static final int UUID_STRING_LENGTH = 36;

  private int timeLow;
  private short timeMid;
  private short timeHighAndVersion;
  private byte clockSeqHighAndReserved;
  private byte clockSeqLow;
  private byte[] node;

  /**
   * Default constructor, used for deserialization.
   */
  public UUID() {
    node = new byte[NODE_LENGTH];
  }

  /**
   * Constructor to instantiate immutable UUID objects.
   * 
   * @param timeLow
   *          time_low
   * @param timeMid
   *          time_mid
   * @param timeHighAndVersion
   *          time_hi_and_version
   * @param clockSeqHighAndReserved
   *          clock_seq_high_and_reserved
   * @param clockSeqLow
   *          clock_seq_low
   * @param node
   *          array of 6 bytes
   */
  public UUID(final int timeLow, final short timeMid, final short timeHighAndVersion,
      final byte clockSeqHighAndReserved, final byte clockSeqLow, final byte[] node) {
    if (node == null) {
      throw new IllegalArgumentException("Byte array 'node' may not be null.");
    }
    if (node.length != NODE_LENGTH) {
      throw new IllegalArgumentException("Length of byte array 'node' must be " + NODE_LENGTH + " instead of "
          + node.length + ".");
    }

    this.timeLow = timeLow;
    this.timeMid = timeMid;
    this.timeHighAndVersion = timeHighAndVersion;
    this.clockSeqHighAndReserved = clockSeqHighAndReserved;
    this.clockSeqLow = clockSeqLow;
    this.node = Arrays.copyOf(node, node.length);
  }

  public int getTimeLow() {
    return timeLow;
  }

  public short getTimeMid() {
    return timeMid;
  }

  public short getTimeHighAndVersion() {
    return timeHighAndVersion;
  }

  public byte getClockSeqHighAndReserved() {
    return clockSeqHighAndReserved;
  }

  public byte getClockSeqLow() {
    return clockSeqLow;
  }

  /**
   * Getter for the {@code node} field.
   * 
   * @return a copy of the {@code node} byte array
   */
  public byte[] getNode() {
    return Arrays.copyOf(node, node.length);
  }

  /**
   * The formal definition of the UUID string representation is provided by the following extended BNF (Backus-Naur
   * Form):
   * 
   * <pre>
   * UUID                    = &lt;timeLow&gt; &lt;hyphen&gt; &lt;timeMid&gt; &lt;hyphen&gt; &lt;timeHighAndVersion&gt;
   *                           &lt;hyphen&gt; &lt;clockSeqHighAndReserved&gt;
   *                           &lt;clockSeqLow&gt; &lt;hyphen&gt; &lt;node&gt;
   * timeLow                 = &lt;hexOctet&gt; &lt;hexOctet&gt; &lt;hexOctet&gt; &lt;hexOctet&gt;
   * timeMid                 = &lt;hexOctet&gt; &lt;hexOctet&gt;
   * timeHighAndVersion      = &lt;hexOctet&gt; &lt;hexOctet&gt;
   * clockSeqHighAndReserved = &lt;hexOctet&gt;
   * clockSeqLow             = &lt;hexOctet&gt;
   * node                    = &lt;hexOctet&gt;&lt;hexOctet&gt;&lt;hexOctet&gt;
   *                           &lt;hexOctet&gt;&lt;hexOctet&gt;&lt;hexOctet&gt;
   * hexOctet                = &lt;hexDigit&gt; &lt;hexDigit&gt;
   * hexDigit                = &lt;digit&gt; | &lt;a&gt; | &lt;b&gt; | &lt;c&gt; | &lt;d&gt; | &lt;e&gt; | &lt;f&gt;
   * digit                   = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
   * hyphen                  = "-"
   * a                       = "a" | "A"
   * b                       = "b" | "B"
   * c                       = "c" | "C"
   * d                       = "d" | "D"
   * e                       = "e" | "E"
   * f                       = "f" | "F"
   * </pre>
   * 
   * The following is an example of the string representation of a UUID:
   * 
   * <pre>
   * 2fac1234-31f8-11b4-a222-08002b34c003
   * </pre>
   * 
   * @param uuidString
   *          String representation of UUID.
   * @return an instance of class UUID
   */
  public static UUID parse(final String uuidString) {
    if (uuidString == null || uuidString.trim().length() != UUID_STRING_LENGTH) {
      throw new IllegalArgumentException("Input must not be null.");
    }
    final String[] uuidParts = uuidString.trim().split("-");
    if (uuidParts.length != PART_LENGTHS.length) {
      throw new IllegalArgumentException("Incorrect or unsupported UUID string length.");
    }
    for (int i = 0; i < PART_LENGTHS.length; i++) {
      if (uuidParts[i].length() != PART_LENGTHS[i]) {
        throw new IllegalArgumentException("Incorrect or unsupported UUID format.");
      }
    }

    final int timeLow = (int) Long.parseLong(uuidParts[0], HEX_RADIX);
    final short timeMid = (short) Integer.parseInt(uuidParts[1], HEX_RADIX);
    final short timeHighAndVersion = (short) Integer.parseInt(uuidParts[2], HEX_RADIX);
    final short clockSeq = (short) Integer.parseInt(uuidParts[3], HEX_RADIX);
    final byte[] node = new byte[NODE_LENGTH];
    for (int i = 0; i < node.length; i++) {
      node[i] = (byte) Short.parseShort(uuidParts[4].substring(2 * i, 2 * i + 2), HEX_RADIX);
    }
    return new UUID(timeLow, timeMid, timeHighAndVersion, (byte) (clockSeq >>> NDRConstants.NUM_BITS_PER_BYTE),
        (byte) clockSeq, node);
  }

  @Override
  public void ndrSerialize(final NDRWriter ndrWriter) throws IOException {
    ndrWriter.writeInt(timeLow);
    ndrWriter.writeShort(timeMid);
    ndrWriter.writeShort(timeHighAndVersion);
    ndrWriter.writeByte(clockSeqHighAndReserved);
    ndrWriter.writeByte(clockSeqLow);
    ndrWriter.writeBytesFixed(node, 0, NODE_LENGTH);
  }

  @Override
  public void ndrDeserialize(final NDRReader ndrReader) throws IOException {
    timeLow = ndrReader.readInt();
    timeMid = ndrReader.readShort();
    timeHighAndVersion = ndrReader.readShort();
    clockSeqHighAndReserved = ndrReader.readByte();
    clockSeqLow = ndrReader.readByte();
    ndrReader.readBytesFixed(node, 0, NODE_LENGTH);
  }

  @Override
  public String toString() {
    return String.format("%08x-%04x-%04x-%02x%02x-%02x%02x%02x%02x%02x%02x", timeLow, timeMid, timeHighAndVersion,
        clockSeqHighAndReserved, clockSeqLow, node[0], node[1], node[2], node[3], node[4], node[5]);
  }

  @Override
  public boolean equals(final Object obj) {
    boolean isEqual = false;
    if (obj instanceof UUID) {
      final UUID that = (UUID) obj;
      isEqual = true;
      isEqual &= this.timeLow == that.timeLow;
      isEqual &= this.timeMid == that.timeMid;
      isEqual &= this.timeHighAndVersion == that.timeHighAndVersion;
      isEqual &= this.clockSeqHighAndReserved == that.clockSeqHighAndReserved;
      isEqual &= this.clockSeqLow == that.clockSeqLow;
      isEqual &= Arrays.equals(this.node, that.node);
    }
    return isEqual;
  }

  @Override
  public int hashCode() {
    return timeLow + timeMid + timeHighAndVersion + clockSeqHighAndReserved + clockSeqLow + clockSeqLow
        + Arrays.hashCode(node);
  }
}
