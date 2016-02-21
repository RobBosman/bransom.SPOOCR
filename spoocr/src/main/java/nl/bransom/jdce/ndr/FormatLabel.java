package nl.bransom.jdce.ndr;

/**
 * This class deals with the NDR Format Label. Use it to serialize/deserialize Format Label data in NDR byte streams.
 * 
 * @author Rob
 */
public class FormatLabel {

  private static final int HASH_PRIME = 31;

  private FormatLabelInteger formatLabelInteger;
  private FormatLabelCharacter formatLabelCharacter;
  private FormatLabelFloat formatLabelFloat;

  /**
   * Default constructor for uninitialized instances.
   */
  public FormatLabel() {
  }

  /**
   * Constructor to create an initialized instance.
   * 
   * @param formatLabelInteger
   *          describes the FormatLabel for integer numbers
   * @param formatLabelCharacter
   *          describes the FormatLabel for character strings
   * @param formatLabelFloat
   *          describes the FormatLabel for floating point numbers
   */
  public FormatLabel(final FormatLabelInteger formatLabelInteger, final FormatLabelCharacter formatLabelCharacter,
      final FormatLabelFloat formatLabelFloat) {
    this.formatLabelInteger = formatLabelInteger;
    this.formatLabelCharacter = formatLabelCharacter;
    this.formatLabelFloat = formatLabelFloat;
  }

  public FormatLabelInteger getFormatLabelInteger() {
    return formatLabelInteger;
  }

  public FormatLabelCharacter getFormatLabelCharacter() {
    return formatLabelCharacter;
  }

  public FormatLabelFloat getFormatLabelFloat() {
    return formatLabelFloat;
  }

  /**
   * Setter for NDR Format Label.
   * 
   * @param formatLabelInteger
   *          describes the FormatLabel for integer numbers
   * @param formatLabelCharacter
   *          describes the FormatLabel for character strings
   * @param formatLabelFloat
   *          describes the FormatLabel for floating point numbers
   */
  public void set(final FormatLabelInteger formatLabelInteger, final FormatLabelCharacter formatLabelCharacter,
      final FormatLabelFloat formatLabelFloat) {
    this.formatLabelInteger = formatLabelInteger;
    this.formatLabelCharacter = formatLabelCharacter;
    this.formatLabelFloat = formatLabelFloat;
  }

  /**
   * Determines if all FormatLabel properties are set. Returns {@code false} if one or more internal fields is null.
   * 
   * @return {@code true} if all fields are non-null.
   */
  public boolean isInitialized() {
    return formatLabelInteger != null && formatLabelCharacter != null && formatLabelFloat != null;
  }

  /**
   * Serialized its state to an NDR Format Label byte stream.
   * 
   * @param numBytes
   *          the number of bytes to use for serializing the Forat Label data
   * @return an array of {@code numBytes} bytes
   */
  public byte[] toByteArray(final int numBytes) {
    final byte[] formatLabelBytes = new byte[numBytes];
    patch(formatLabelBytes, 0);
    return formatLabelBytes;
  }

  /**
   * Writes the NDR Format Label as a byte sequence to {@code formatLabelBytes}.
   * 
   * @param formatLabelBytes
   *          byte array to which NDR serialized Format Label data will be written
   * @param byteOffset
   *          byte offset to take into account when writing data to {@code formatLabelBytes}
   * @throws IllegalStateException
   *           if not initialized
   */
  public void patch(final byte[] formatLabelBytes, final int byteOffset) {
    if (!isInitialized()) {
      throw new IllegalStateException("The NDR Format Label has not been specified.");
    }

    formatLabelInteger.patchFormatLabel(formatLabelBytes, byteOffset);
    formatLabelCharacter.patchFormatLabel(formatLabelBytes, byteOffset);
    formatLabelFloat.patchFormatLabel(formatLabelBytes, byteOffset);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof FormatLabel) {
      final FormatLabel other = (FormatLabel) obj;
      if (this.formatLabelCharacter == other.formatLabelCharacter && this.formatLabelFloat == other.formatLabelFloat
          && this.formatLabelInteger == other.formatLabelInteger) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = HASH_PRIME;
    if (formatLabelInteger != null) {
      hash += formatLabelInteger.hashCode();
    }
    hash *= HASH_PRIME;
    if (formatLabelCharacter != null) {
      hash += formatLabelCharacter.hashCode();
    }
    hash *= HASH_PRIME;
    if (formatLabelFloat != null) {
      hash += formatLabelFloat.hashCode();
    }
    return hash;
  }

  /**
   * Parses the byte sequence to {@code formatLabelBytes} to obtain the NDR Format Label.
   * 
   * @param formatLabelBytes
   *          byte array containing NDR serialized Format Label data
   * @param byteOffset
   *          byte offset to take into account when reading data from {@code formatLabelBytes}
   * @return newly created FormatLabel instance
   */
  public static FormatLabel parse(final byte[] formatLabelBytes, final int byteOffset) {
    return new FormatLabel(FormatLabelInteger.parse(formatLabelBytes, byteOffset), FormatLabelCharacter.parse(
        formatLabelBytes, byteOffset), FormatLabelFloat.parse(formatLabelBytes, byteOffset));
  }
}
