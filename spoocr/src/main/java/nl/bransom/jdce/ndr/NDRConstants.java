package nl.bransom.jdce.ndr;

/**
 * This class defines a set of constants for the NDR data format.
 * 
 * @author Rob
 */
public final class NDRConstants {

  /** Maximum number of bytes to align the NDR data to. */
  public static final int MAX_NUM_ALIGNMENT_BYTES = 8;
  /** Number of bits in a byte. */
  public static final int NUM_BITS_PER_BYTE = 8;
  /** Mask for a single byte with all bits set to '1'. */
  public static final int BYTE_MASK = 0xff;
  /** Byte size of NDR format label in a RPC header. */
  public static final int CONNECTIONLESS_FORMAT_LABEL_LENGTH = 3;
  /** Byte size of NDR format label. */
  public static final int FORMAT_LABEL_LENGTH = 4;
  /** Minimal byte size of NDR format label. */
  public static final int MIN_FORMAT_LABEL_LENGTH = Math.min(CONNECTIONLESS_FORMAT_LABEL_LENGTH, FORMAT_LABEL_LENGTH);

  /**
   * Hidden constructor.
   */
  private NDRConstants() {
  }
}
