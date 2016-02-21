package nl.bransom.jdce.ndr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to unmarshal NDR formatted binary data, for instance to parse DCE/RPC messages. Note that method
 * {@link #readFormatLabel()} must be called before any number String or (int, float, etc.) can be read.
 * 
 * The implementation is based on chapter 14 of <a href="https://www2.opengroup.org/ogsys/catalog/c706.">document C706
 * of the "Open Group"</a>.
 * 
 * @author Rob
 */
public class NDRReader {

  private static final Logger LOG = LoggerFactory.getLogger(NDRReader.class);

  private AlignedInputStream alignedInputStream;
  private FormatLabel formatLabel;

  /**
   * Constructor.
   * 
   * @param inputStream
   *          NDR byte stream to be used for reading
   */
  public NDRReader(final InputStream inputStream) {
    this.alignedInputStream = new AlignedInputStream(inputStream);
  }

  public FormatLabel getFormatLabel() {
    return formatLabel;
  }

  public void setFormatLabel(final FormatLabel formatLabel) {
    this.formatLabel = formatLabel;
  }

  /**
   * Reads {@code numBytes} bytes from the NDR binary stream and parses them to obtain the NDR format label.
   * 
   * @param numBytes
   *          number of bytes to be read for the Format Label
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public void readFormatLabel(final int numBytes) throws IOException {
    final byte[] formatLabelBytes = new byte[numBytes];
    alignedInputStream.read(formatLabelBytes);
    formatLabel = FormatLabel.parse(formatLabelBytes, 0);
  }

  /**
   * Instantiates {@code ndrSerializableClass} and delegates reading to the new instance.
   * 
   * @param <T>
   *          subclass of NDRSerializable
   * @param ndrSerializableClass
   *          class to which the reading and parsing of NDR data is delegated
   * @return newly created instance of given {@code ndrSerializableClass}
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public <T extends NDRSerializable> T read(final Class<T> ndrSerializableClass) throws IOException {
    final T ndrSerializable;
    // Instantiate the object...
    try {
      ndrSerializable = (T) ndrSerializableClass.newInstance();
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Error instantiating class " + ndrSerializableClass.getName(), e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Insufficient privileges to instantiate class "
          + ndrSerializableClass.getName(), e);
    }
    // ...and delegate the reading to it.
    read(ndrSerializable);
    return ndrSerializable;
  }

  /**
   * Delegates reading to the given {@code ndrSerializable}.
   * 
   * @param <T>
   *          subclass of NDRSerializable
   * @param ndrSerializable
   *          object to which the reading and parsing of NDR data is delegated
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public <T extends NDRSerializable> void read(final T ndrSerializable) throws IOException {
    ndrSerializable.ndrDeserialize(this);
  }

  /**
   * Reads a single byte from the NDR binary stream and converts it to a boolean.
   * 
   * @return boolean value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public boolean readBoolean() throws IOException {
    return alignedInputStream.read() != 0x00;
  }

  /**
   * Reads a single byte from the NDR binary stream.
   * 
   * @return byte value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public byte readByte() throws IOException {
    return (byte) alignedInputStream.read();
  }

  /**
   * Reads bytes from the NDR binary stream and converts them to a number using the specified NDR format label.
   * 
   * @return short value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public short readShort() throws IOException {
    return (short) readNumber(Short.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Reads bytes from the NDR binary stream and converts them to a number using the specified NDR format label.
   * 
   * @return int value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public int readInt() throws IOException {
    return (int) readNumber(Integer.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Reads bytes from the NDR binary stream and converts them to a number using the specified NDR format label.
   * 
   * @return long value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public long readLong() throws IOException {
    return readNumber(Long.SIZE / NDRConstants.NUM_BITS_PER_BYTE);
  }

  /**
   * Reads bytes from the NDR binary stream and converts them to a number using the specified NDR format label.
   * 
   * @return float value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public float readFloat() throws IOException {
    verifyFormatLabelFloat();
    return Float.intBitsToFloat((int) readNumber(Float.SIZE / NDRConstants.NUM_BITS_PER_BYTE));
  }

  /**
   * Reads bytes from the NDR binary stream and converts them to a number using the specified NDR format label.
   * 
   * @return double value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public double readDouble() throws IOException {
    verifyFormatLabelFloat();
    return Double.longBitsToDouble(readNumber(Double.SIZE / NDRConstants.NUM_BITS_PER_BYTE));
  }

  /**
   * Reads a fixed number of bytes ({@code length}) from the NDR binary stream into {@code fixedBytes}, starting at
   * position {@code storeOffset}.
   * 
   * @param fixedBytes
   *          byte buffer into which the read data will be copied, starting at position {@code storeOffset}. The length
   *          of this buffer must be at least {@code storeOffset + expectedLength}.
   * @param storeOffset
   *          byte offset to take into account when storing data in {@code fixedBytes}
   * @param expectedLength
   *          number of bytes to read
   * @return number of bytes read
   * @throws IllegalArgumentException
   *           if the length of {@code fixedBytes} is insufficient to store the bytes that will be read from the NDR
   *           byte stream
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public int readBytesFixed(final byte[] fixedBytes, final int storeOffset, final int expectedLength)
      throws IOException {
    verifyBufferLength(fixedBytes.length, storeOffset + expectedLength);

    final int numBytesRead = alignedInputStream.read(fixedBytes, storeOffset, expectedLength);
    if (numBytesRead < expectedLength) {
      throw new IOException("Expected to read " + expectedLength + " bytes, but only " + numBytesRead
          + " bytes have been read.");
    }
    return numBytesRead;
  }

  /**
   * Reads bytes in the NDR <i>conformant</i> format. First, the {@code ndrMaxLength} is read from the NDR buffer and
   * then that many bytes are read and stored in {@code conformantBytes}, starting at position {@code storeOffset}.
   * 
   * @param conformantBytes
   *          byte buffer into which the read data will be copied, starting at position {@code storeOffset}. The length
   *          of this buffer must be at least {@code storeOffset + ndrMaxLength} .
   * @param storeOffset
   *          byte offset to be taken into account when writing data to {@code conformBytes}
   * @return number of bytes read
   * @throws IllegalArgumentException
   *           if the length of {@code conformantBytes} is insufficient to store the bytes that will be read from the
   *           NDR byte stream
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public int readBytesConformant(final byte[] conformantBytes, final int storeOffset) throws IOException {
    // Read the maximum length as an integer primitive. Alignment bytes are skipped automatically.
    final int ndrMaxLength = readInt();
    verifyBufferLength(conformantBytes.length, storeOffset + ndrMaxLength);

    // Read the data bytes.
    readBytesFixed(conformantBytes, storeOffset, ndrMaxLength);
    return ndrMaxLength;
  }

  /**
   * Reads bytes in the NDR <i>varying</i> format. First, the ndrOffset is read from the NDR buffer, then the
   * actualLength of the byte array. Then that many bytes are read and stored in {@code varyingBytes}, starting at
   * position {@code storeOffset}.
   * 
   * @param varyingBytes
   *          byte buffer into which the read data will be copied, starting at position {@code storeOffset}. The length
   *          of this buffer must be at least {@code storeOffset + ndrOffset + ndrActualLength}.
   * @param storeOffset
   *          byte offset to be taken into account when writing data to {@code varyingBytes}
   * @return number of bytes read (ndrOffset + ndrActualLength)
   * @throws IllegalArgumentException
   *           if the length of {@code varyingBytes} is insufficient to store the bytes that will be read from the NDR
   *           byte stream
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public int readBytesVarying(final byte[] varyingBytes, final int storeOffset) throws IOException {
    // Read the offset and the actual length as integer primitives (alignment bytes are skipped automatically).
    final int ndrOffset = readInt();
    final int ndrActualLength = readInt();
    verifyBufferLength(varyingBytes.length, storeOffset + ndrOffset + ndrActualLength);

    // Read the data bytes.
    final int numBytesRead = alignedInputStream.read(varyingBytes, storeOffset + ndrOffset, ndrActualLength);
    if (numBytesRead < ndrActualLength) {
      throw new IOException("Read only " + numBytesRead + " bytes, but expected to read " + ndrActualLength + ".");
    }
    return ndrOffset + ndrActualLength;
  }

  /**
   * Reads bytes in a NDR <i>conformant and varying</i> format. That is, the {@code ndrMaxLength} is read first,
   * followed by the offset and the actual length of the content. Then the content bytes are read and stored in the
   * buffer, starting at the given {@code ndrOffset}.
   * 
   * An IllegalArgumentException is thrown if the size of {@code conformantVaryingBytes} is insufficient.
   * 
   * @param conformantVaryingBytes
   *          byte buffer into which the read data will be copied, starting at position {@code storeOffset}. The length
   *          of this buffer must be at least {@code storeOffset + ndrOffset + ndrActualLength}.
   * @param storeOffset
   *          byte offset to be taken into account when writing data to {@code conformVaryingBytes}
   * @return number of bytes read
   * @throws IllegalArgumentException
   *           if the length of {@code varyingBytes} is insufficient to store the bytes that will be read from the NDR
   *           byte stream
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public int readBytesConformantVarying(final byte[] conformantVaryingBytes, final int storeOffset) throws IOException {
    // Read the ndrMaxLength, the ndrOffset and the ndrActualLength as integer primitives. Alignment bytes are skipped
    // automatically.
    final int ndrMaxLength = readInt();
    verifyBufferLength(conformantVaryingBytes.length, storeOffset + ndrMaxLength);
    final int numBytesRead = readBytesVarying(conformantVaryingBytes, storeOffset);
    if (numBytesRead > ndrMaxLength) {
      throw new IOException("The NDR offset and actualLength exceed the specified ndrMaxLength.");
    }
    return numBytesRead;
  }

  /**
   * Reads a byte array in <i>varying</i> NDR format and converts it to a String.
   * 
   * Note that the resulting String will contain single-byte characters (ASCII or EBCDIC) that are encoded with the
   * {@link #formatLabelCharacter} specified in the constructor or in {@link #readFormatLabel()}.
   * 
   * @return String value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public String readStringVarying() throws IOException {
    verifyFormatLabel();

    // Read the offset and the actual byte length.
    final int ndrOffset = readInt();
    final int ndrActualLength = readInt();
    if (ndrOffset > 0) {
      throw new UnsupportedOperationException("Non-zero NDR String offsets are not supported.");
    }

    // Read the data bytes, including the terminator.
    final byte[] buffer = new byte[ndrOffset + ndrActualLength];
    final int numBytesRead = alignedInputStream.read(buffer);
    if ((numBytesRead == 0) || (buffer[numBytesRead - 1] != 0x00)) {
      throw new IOException("A string terminator was expected but has not been read.");
    }
    return new String(buffer, 0, ndrOffset + ndrActualLength - 1, formatLabel.getFormatLabelCharacter().getCharset());
  }

  /**
   * Reads a byte array in <i>conformant-varying</i> NDR format and converts it to a String.
   * 
   * Note that the resulting String will contain single-byte characters (ASCII or EBCDIC) that are encoded with the
   * {@link #formatLabelCharacter} specified in the constructor or in {@link #readFormatLabel()}.
   * 
   * @return String value
   * @throws IOException
   *           if errors occur while reading the NDR stream
   */
  public String readStringConformantVarying() throws IOException {
    // Read the ndrMaxLength and delegate the rest of the work to readStringVarying().
    final int ndrMaxLength = readInt();
    final String result = readStringVarying();
    if (result.length() > ndrMaxLength) {
      throw new IOException("Too many bytes (" + result.length() + ") have been read; maximum is " + ndrMaxLength + ".");
    }
    return result;
  }

  /**
   * Reads a reference value (pointer) from the NDR buffer. If it's a null-pointer, this method will return an empty
   * byte array. Otherwise the reference that was read is looked-up in {@code referentMap}. If found, the method returns
   * the {@code referent}. Otherwise the {@code referent} data will be read from the NDR data stream, added to
   * {@code referentMap} and returned.
   * 
   * @param referentMap
   *          maps referenceIds to referents.
   * @param expectedReferentSize
   *          size in bytes of the (NDR serialized) referent
   * @return referent
   * @throws IOException
   *           if errors occur while reading NDR bytes
   */
  public byte[] readReference(final Map<Integer, byte[]> referentMap, final int expectedReferentSize)
      throws IOException {
    // Read the reference.
    final int referenceID = readInt();
    if (referenceID == 0x00) {
      LOG.debug("Read null-pointer.");
      return new byte[0];
    }
    // If the referenceID refers to a known referent...
    if (referentMap.containsKey(referenceID)) {
      // ...then return that referent.
      LOG.debug("Read pointer to known referent; referenceID = {}.", referenceID);
      return referentMap.get(referenceID);
    }
    // Read the referent data and add it to the Map.
    final byte[] referent = new byte[expectedReferentSize];
    readBytesFixed(referent, 0, expectedReferentSize);
    referentMap.put(referenceID, referent);
    LOG.debug("Read pointer to new referent; referenceID = {}.", referenceID);
    return referent;
  }

  private long readNumber(final int sizeInBytes) throws IOException {
    verifyFormatLabel();

    // Skip any alignment bytes.
    alignedInputStream.skipAlignment(Math.min(NDRConstants.MAX_NUM_ALIGNMENT_BYTES, sizeInBytes));

    final byte[] buffer = new byte[sizeInBytes];
    final int numBytesread = alignedInputStream.read(buffer);
    if (numBytesread < sizeInBytes) {
      throw new IOException("Could not read the required " + sizeInBytes + " bytes from the NDR byte stream.");
    }

    long value = 0;
    // Now write the primitive data bytes.
    for (int i = 0; i < sizeInBytes; i++) {
      switch (formatLabel.getFormatLabelInteger()) {
      case BIG_ENDIAN:
        value |= (((long) buffer[i] & NDRConstants.BYTE_MASK) << ((sizeInBytes - i - 1) * NDRConstants.NUM_BITS_PER_BYTE));
        break;
      case LITTLE_ENDIAN:
        value |= (((long) buffer[i] & NDRConstants.BYTE_MASK) << (i * NDRConstants.NUM_BITS_PER_BYTE));
        break;
      default:
        throw new UnsupportedOperationException("Unsupported NDR format label '" + formatLabel.getFormatLabelInteger()
            + "'.");
      }
    }
    return value;
  }

  private void verifyBufferLength(final int bufferLength, final int dataLength) {
    if (bufferLength < dataLength) {
      throw new IllegalArgumentException("Insufficient buffer length (" + bufferLength + "); it must be at least "
          + dataLength + ".");
    }
  }

  private void verifyFormatLabel() {
    if (formatLabel == null || !formatLabel.isInitialized()) {
      throw new IllegalStateException("NDR Format Label not defined; please specify the FormatLabelCharacter.");
    }
  }

  private void verifyFormatLabelFloat() {
    verifyFormatLabel();
    if (formatLabel.getFormatLabelFloat() != FormatLabelFloat.IEEE) {
      throw new UnsupportedOperationException("Unsupported float format label: "
          + formatLabel.getFormatLabelFloat().name());
    }
  }
}
