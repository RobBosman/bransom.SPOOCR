package nl.bransom.jdce.ndr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to marshal data to NDR format, for instance to compose DCE/RPC messages.
 * 
 * The implementation is based on chapter 14 of <a href="https://www2.opengroup.org/ogsys/catalog/c706.">document C706
 * of the "Open Group"</a>.
 * 
 * @author Rob
 */
public class NDRWriter {

  private static final Logger LOG = LoggerFactory.getLogger(NDRWriter.class);

  private AlignedOutputStream alignedOutputStream;
  private FormatLabel formatLabel;

  /**
   * Constructor.
   * 
   * @param outputStream
   *          NDR byte stream to be used for writing
   */
  public NDRWriter(final OutputStream outputStream) {
    this.alignedOutputStream = new AlignedOutputStream(outputStream);
  }

  public void setFormatLabel(final FormatLabel formatLabel) {
    this.formatLabel = formatLabel;
  }

  /**
   * Writes the {@code numBytes} bytes long NDR Format Label based on the values passed in the constructor.
   * 
   * @param numBytes
   *          number of bytes to be written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeFormatLabel(final int numBytes) throws IOException {
    verifyFormatLabel();
    alignedOutputStream.write(formatLabel.toByteArray(numBytes), 0, numBytes);
  }

  /**
   * Delegates writing to the given {@code ndrSerializable}.
   * 
   * @param ndrSerializable
   *          object to which serialization of the NDR data is delegated.
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void write(final NDRSerializable ndrSerializable) throws IOException {
    ndrSerializable.ndrSerialize(this);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeBoolean(final boolean value) throws IOException {
    alignedOutputStream.write((byte) (value ? 0x01 : 0x00));
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeByte(final byte value) throws IOException {
    alignedOutputStream.write(value);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeShort(final short value) throws IOException {
    writeNumber(value, Short.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeInt(final int value) throws IOException {
    writeNumber(value, Integer.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeLong(final long value) throws IOException {
    writeNumber(value, Long.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeFloat(final float value) throws IOException {
    writeNumber(Float.floatToRawIntBits(value), Float.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Converts {@code value} to the appropriate NDR byte format and writes it to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param value
   *          data to be serialized to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeDouble(final double value) throws IOException {
    writeNumber(Double.doubleToRawLongBits(value), Double.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Writes {@code fixedBytes} to the internal NDR packet buffer.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param fixedBytes
   *          byte buffer from which the data will be copied to the NDR byte stream, starting at position
   *          {@code dataOffset}. The length of this buffer must be at least {@code dataOffset + dataLength}.
   * @param dataOffset
   *          byte offset to be taken into account when copying data from {@code fixedBytes}
   * @param dataLength
   *          number of bytes to be written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeBytesFixed(final byte[] fixedBytes, final int dataOffset, final int dataLength) throws IOException {
    alignedOutputStream.write(fixedBytes, dataOffset, dataLength);
  }

  /**
   * Writes {@code ndrMaxLength} bytes in a <i>conformant</i> format to the internal NDR packet buffer. The full content
   * of {@code conformantBytes} will be written. Any remaining space is filled with null-bytes up to
   * {@code ndrMaxLength}.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer or if the length of
   * {@code conformantBytes} exceeds {@code ndrMaxLength}.
   * 
   * @param conformantBytes
   *          byte buffer from which the data will be copied to the NDR byte stream, starting at position
   *          {@code dataOffset}. The length of this buffer must be at least {@code dataOffset + dataLength}.
   * @param dataOffset
   *          byte offset to be taken into account when copying data from {@code conformantBytes}
   * @param dataLength
   *          number of bytes to be written to the NDR byte stream
   * @param ndrMaxLength
   *          maximum number of bytes of the content
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeBytesConformant(final byte[] conformantBytes, final int dataOffset, final int dataLength,
      final int ndrMaxLength) throws IOException {
    verifyDataLength(conformantBytes, dataOffset, dataLength, ndrMaxLength, null);

    // Write the maximum length as an integer primitive. Alignment bytes are skipped automatically.
    writeInt(ndrMaxLength);

    // Now write the data bytes.
    alignedOutputStream.write(conformantBytes, dataOffset, dataLength);

    // Fill the remaining (unused) positions.
    for (int i = conformantBytes.length; i < ndrMaxLength; i++) {
      alignedOutputStream.write((byte) 0x00);
    }
  }

  /**
   * Writes the full content of {@code varyingBytes} in a <i>varying</i> format to the internal NDR packet buffer. That
   * is, the {@code offset} is written first, followed by the length of {@code rawBytes}, and then all content bytes.
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param varyingBytes
   *          byte buffer from which the data will be copied to the NDR byte stream, starting at position
   *          {@code dataOffset}.
   * @param dataOffset
   *          byte offset to be taken into account when copying data from {@code varyingBytes}
   * @param dataLength
   *          number of bytes to be written to the NDR byte stream
   * @param ndrOffset
   *          byte offset of the content written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeBytesVarying(final byte[] varyingBytes, final int dataOffset, final int dataLength,
      final int ndrOffset) throws IOException {
    // Write the offset and the actual length as integer primitives. Alignment bytes are skipped automatically.
    writeInt(ndrOffset);
    writeInt(dataLength);

    // Now write the data bytes.
    alignedOutputStream.write(varyingBytes, dataOffset, dataLength);
  }

  /**
   * Writes the full content of {@code conformantVaryingBytes} in a <i>conformant-varying</i> format to the internal NDR
   * packet buffer. That is, the {@code ndrMaxLength} is written first, followed by {@code ndrOffset} and the length of
   * {@code conformantVaryingBytes}, and then all content bytes.
   * 
   * An IllegalArgumentException is thrown if the length of {@code conformantVaryingBytes} exceeds {@code ndrMaxLength}.
   * 
   * @param conformantVaryingBytes
   *          byte buffer from which the data will be copied to the NDR byte stream, starting at position
   *          {@code dataOffset}. The length of this buffer must be at least {@code dataOffset + dataLength}.
   * @param dataOffset
   *          byte offset to be taken into account when copying data from {@code conformantVaryingBytes}
   * @param dataLength
   *          number of bytes to be written to the NDR byte stream
   * @param ndrMaxLength
   *          maximum number of bytes of the content
   * @param ndrOffset
   *          byte offset of the content written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeBytesConformantVarying(final byte[] conformantVaryingBytes, final int dataOffset,
      final int dataLength, final int ndrMaxLength, final int ndrOffset) throws IOException {
    verifyDataLength(conformantVaryingBytes, dataOffset, dataLength, ndrMaxLength, "including offset");

    // Write the maximum length, the offset and the actual length as integer primitives. Alignment bytes are skipped
    // automatically.
    writeInt(ndrMaxLength);
    writeInt(ndrOffset);
    writeInt(dataLength);

    // Now write the data bytes.
    alignedOutputStream.write(conformantVaryingBytes, dataOffset, dataLength);
  }

  /**
   * Converts {@code text} to the appropriate NDR <i>varying</i> byte format, adds a terminator byte and writes
   * everything to the NDR output stream.
   * 
   * Note that the String is converted into a single-byte array using the character encoding that is specified in
   * {@link #formatLabelCharacter} (ASCII or EBCDIC).
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param text
   *          textual data to be serialized
   * @param ndrOffset
   *          byte offset of the content written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeStringVarying(final String text, final int ndrOffset) throws IOException {
    verifyFormatLabel();
    final byte[] textBytes = text.getBytes(formatLabel.getFormatLabelCharacter().getCharset());
    final int dataLength = textBytes.length + 1;
    writeBytesVarying(Arrays.copyOf(textBytes, dataLength), 0, dataLength, ndrOffset);
  }

  /**
   * Converts {@code text} to the appropriate NDR <i>conformant-varying</i> byte format, adds a terminator byte and
   * writes everything to the NDR output stream.
   * 
   * Note that the String is converted into a single-byte array using the character encoding that is specified in
   * {@link #formatLabelCharacter} (ASCII or EBCDIC).
   * 
   * An IllegalArgumentException is thrown if there is insufficient space left in the buffer.
   * 
   * @param text
   *          textual data to be serialized
   * @param ndrMaxLength
   *          maximum number of bytes of the content
   * @param ndrOffset
   *          byte offset of the content written to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeStringConformantVarying(final String text, final int ndrMaxLength, final int ndrOffset)
      throws IOException {
    verifyFormatLabel();
    final byte[] textBytes = text.getBytes(formatLabel.getFormatLabelCharacter().getCharset());
    final byte[] ndrBytes = Arrays.copyOf(textBytes, textBytes.length + 1);
    verifyDataLength(ndrBytes, 0, ndrBytes.length, ndrMaxLength, "including offset and terminator");
    writeBytesConformantVarying(ndrBytes, 0, ndrBytes.length, ndrMaxLength, ndrOffset);
  }

  /**
   * Writes a pointer to {@code referent} to the internal NDR packet buffer. If it's a <i>top-level pointer</i>, then
   * the content of {@code referent} will be written to the buffer too and {@code referent} will be added to
   * {@code referentMap}.
   * 
   * The content of {@code referentMap} is used to determine the type of pointer:
   * <ul>
   * <li>a null pointer if {@code referent} is null</li>
   * <li>a top-level pointer if {@code referentMap} does not contain {@code referent}</li>
   * <li>a link pointer if {@code referentMap} contains {@code referent}</li>
   * </ul>
   * 
   * @param referentMap
   *          maps referenceIds to referents.
   * @param referent
   *          (NDR serialized) referent
   * @throws IOException
   *           if errors occur while writing data to the NDR byte stream
   */
  public void writeReference(final Map<Integer, byte[]> referentMap, final byte[] referent) throws IOException {
    if (referent == null) {
      LOG.debug("Writing NULL pointer.");
      writeInt(0);
    } else {
      // Find the referent and determine the reference ID.
      int referenceID = 0;
      for (Map.Entry<Integer, byte[]> entry : referentMap.entrySet()) {
        if (entry.getValue() == referent) {
          referenceID = entry.getKey();
          break;
        }
      }
      // If the referent is not yet present in the map...
      if (referenceID == 0) {
        // ...then invent a new reference for it and add it to the map.
        referenceID = referentMap.size() + 1;
        final byte[] replacedValue = referentMap.put(referenceID, referent);
        if (replacedValue != null) {
          throw new IllegalArgumentException(
              "The map with referents is supposed to contain Integer keys that are less than or equal to the size of the map."
                  + " The map contains " + referentMap.size() + " elements, one with a key of '" + referenceID + "'.");
        }
      }
      // Write the reference.
      LOG.debug("Writing pointer with reference ID {}.", referenceID);
      writeInt(referenceID);
    }
  }

  private void writeNumber(long value, final int sizeInBytes) throws IOException {
    verifyFormatLabel();

    // Fill any alignment bytes.
    alignedOutputStream.writeAlignment(Math.min(NDRConstants.MAX_NUM_ALIGNMENT_BYTES, sizeInBytes));

    // Now write the primitive data bytes.
    for (int i = 0; i < sizeInBytes; i++) {
      switch (formatLabel.getFormatLabelInteger()) {
      case BIG_ENDIAN:
        alignedOutputStream.write((byte) (value >>> ((sizeInBytes - i - 1) * NDRConstants.NUM_BITS_PER_BYTE)));
        break;
      case LITTLE_ENDIAN:
        alignedOutputStream.write((byte) (value >>> (i * NDRConstants.NUM_BITS_PER_BYTE)));
        break;
      default:
        throw new UnsupportedOperationException("Unsupported NDR format label '" + formatLabel.getFormatLabelInteger()
            + "'.");
      }
    }
  }

  private void verifyDataLength(final byte[] dataBuffer, final int dataOffset, final int dataLength,
      final int ndrMaxLength, final String remark) {
    if (dataBuffer == null) {
      throw new IllegalArgumentException("Illegal input: dataBuffer is null.");
    }
    if (dataLength > ndrMaxLength) {
      throw new IllegalArgumentException("Length of data (" + dataLength + (remark == null ? "" : ", " + remark)
          + ") is more than the given maximum (" + ndrMaxLength + ").");
    }
    if (dataOffset + dataLength > dataBuffer.length) {
      throw new IllegalArgumentException("The specified position and length of data (" + (dataOffset + dataLength)
          + (remark == null ? "" : ", " + remark) + ") exceeds the size of the buffer (" + dataBuffer.length + ").");
    }
  }

  private void verifyFormatLabel() {
    if (formatLabel == null || !formatLabel.isInitialized()) {
      throw new IllegalStateException("NDR Format Label has not been defined.");
    }
  }
}
