package nl.bransom.jdce.ndr;

/**
 * Enumeration to specify the NDR format label for integers.
 * 
 * @author Rob
 */
public enum FormatLabelInteger {
  /** Big-endian format */
  BIG_ENDIAN(0x00),
  /** Little-endian format */
  LITTLE_ENDIAN(0x10);

  private static final int SIGNIFICANT_BITS_MASK = 0xf0;
  private static final String INVALID_FORMAT_LABEL_ERROR = "NDR format label must be at least "
      + NDRConstants.MIN_FORMAT_LABEL_LENGTH + " bytes long.";

  private byte value;

  /**
   * Hidden constructor.
   * 
   * @param value
   *          NDR binary value
   */
  private FormatLabelInteger(final int value) {
    this.value = (byte) value;
  }

  /**
   * Apply the NDR format label to the given byte array.
   * 
   * @param formatLabelBytes
   *          byte array to which NDR serialized Format Label data will be written
   * @param byteOffset
   *          byte offset to take into account when writing data to {@code formatLabelBytes}
   */
  public void patchFormatLabel(final byte[] formatLabelBytes, final int byteOffset) {
    if (formatLabelBytes == null || formatLabelBytes.length < byteOffset + NDRConstants.MIN_FORMAT_LABEL_LENGTH) {
      throw new IllegalArgumentException(INVALID_FORMAT_LABEL_ERROR);
    }

    formatLabelBytes[byteOffset] = (byte) (value + (formatLabelBytes[byteOffset] & ~SIGNIFICANT_BITS_MASK));
  }

  /**
   * Parse the given NDR byte array ({@code formatLabel}) to obtain the format label enum.
   * 
   * @param formatLabelBytes
   *          byte array containing NDR serialized Format Label data
   * @param byteOffset
   *          byte offset to take into account when reading data from {@code formatLabelBytes}
   * @return format label enum. An UnsupportedOperationException is thrown if no match was found.
   */
  public static FormatLabelInteger parse(final byte[] formatLabelBytes, final int byteOffset) {
    if (formatLabelBytes == null || formatLabelBytes.length < byteOffset + NDRConstants.MIN_FORMAT_LABEL_LENGTH) {
      throw new IllegalArgumentException(INVALID_FORMAT_LABEL_ERROR);
    }

    final byte maskByte = (byte) (formatLabelBytes[byteOffset] & SIGNIFICANT_BITS_MASK);
    for (FormatLabelInteger enumValue : values()) {
      if (enumValue.value == maskByte) {
        return enumValue;
      }
    }
    throw new UnsupportedOperationException("Unsupported NDR format label code '" + formatLabelBytes[byteOffset] + "'.");
  }
}
