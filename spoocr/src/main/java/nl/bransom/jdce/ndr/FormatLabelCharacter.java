package nl.bransom.jdce.ndr;

import java.nio.charset.Charset;

/**
 * Enumeration to specify the NDR format label for characters.
 * 
 * @author Rob
 */
public enum FormatLabelCharacter {
  /** ASCII character set */
  ASCII(0x00, "ISO8859-15"),
  /** EBCDIC character set */
  EBCDIC(0x01, "CP1047");

  private static final int BYTE_MASK = 0x0f;
  private static final String INVALID_FORMAT_LABEL_ERROR = "NDR format label must be at least "
      + NDRConstants.MIN_FORMAT_LABEL_LENGTH + " bytes long.";

  private byte value;
  private Charset charset;

  /**
   * Hidden constructor.
   * 
   * @param value
   *          NDR binary value
   * @param charsetName
   *          character encoding to be applied
   */
  private FormatLabelCharacter(final int value, final String charsetName) {
    this.value = (byte) value;
    if (!Charset.isSupported(charsetName)) {
      throw new IllegalArgumentException("Unsupported characterset: " + charsetName);
    }
    this.charset = Charset.forName(charsetName);
  }

  public Charset getCharset() {
    return charset;
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

    formatLabelBytes[byteOffset] = (byte) ((formatLabelBytes[byteOffset] & ~BYTE_MASK) + value);
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
  public static FormatLabelCharacter parse(final byte[] formatLabelBytes, final int byteOffset) {
    if (formatLabelBytes == null || formatLabelBytes.length < byteOffset + NDRConstants.MIN_FORMAT_LABEL_LENGTH) {
      throw new IllegalArgumentException(INVALID_FORMAT_LABEL_ERROR);
    }

    final byte maskByte = (byte) (formatLabelBytes[byteOffset] & BYTE_MASK);
    for (FormatLabelCharacter enumValue : values()) {
      if (enumValue.value == maskByte) {
        return enumValue;
      }
    }
    throw new UnsupportedOperationException("Unsupported NDR format label code '" + formatLabelBytes[byteOffset] + "'.");
  }

}
