package nl.bransom.jdce.ndr;

/**
 * Enumeration to specify the NDR format label for floats.
 * 
 * @author Rob
 */
public enum FormatLabelFloat {
  /** format according to IEEE */
  IEEE(0x00),
  /** format according to VAX */
  VAX(0x01),
  /** format according to CRAY */
  CRAY(0x02),
  /** format according to IBM */
  IBM(0x03);

  private static final String INVALID_FORMAT_LABEL_ERROR = "NDR format label must be at least "
      + NDRConstants.MIN_FORMAT_LABEL_LENGTH + " bytes long.";

  private byte value;

  /**
   * Hidden constructor.
   * 
   * @param value
   *          NDR binary value
   */
  private FormatLabelFloat(final int value) {
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

    formatLabelBytes[byteOffset + 1] = value;
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
  public static FormatLabelFloat parse(final byte[] formatLabelBytes, final int byteOffset) {
    if (formatLabelBytes == null || formatLabelBytes.length < byteOffset + NDRConstants.MIN_FORMAT_LABEL_LENGTH) {
      throw new IllegalArgumentException(INVALID_FORMAT_LABEL_ERROR);
    }

    for (FormatLabelFloat enumValue : values()) {
      if (enumValue.value == formatLabelBytes[byteOffset + 1]) {
        return enumValue;
      }
    }
    throw new UnsupportedOperationException("Unsupported NDR format label code '" + formatLabelBytes[byteOffset + 1]
        + "'.");
  }
}
