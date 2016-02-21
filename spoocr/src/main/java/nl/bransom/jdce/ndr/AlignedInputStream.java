package nl.bransom.jdce.ndr;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is a wrapper for class InputStream. It keeps track of the number of bytes that are read, so it can skip
 * alignment bytes if required.
 */
public class AlignedInputStream {

  private InputStream inputStream;
  private int totalBytesRead;

  /**
   * @param inputStream
   *          NDR byte stream
   */
  public AlignedInputStream(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

  /**
   * Delegates reading a single byte from the wrapped InputStream object.
   * 
   * @return a single byte that was read
   * @throws IOException
   *           when errors occur while reading data from the NDR byte stream
   */
  public byte read() throws IOException {
    final int dataRead = (byte) inputStream.read();
    if (dataRead == -1) {
      throw new IOException("No more data available on input stream.");
    }
    totalBytesRead++;
    return (byte) (dataRead & NDRConstants.BYTE_MASK);
  }

  /**
   * Delegates reading bytes from the wrapped InputStream object.
   * 
   * @param dataBytes
   *          byte array into which the data will be stored that is read from the NDR byte stream
   * @return the number of bytes that were read
   * @throws IOException
   *           when errors occur while reading data from the NDR byte stream
   */
  public int read(final byte[] dataBytes) throws IOException {
    return read(dataBytes, 0, dataBytes.length);
  }

  /**
   * Delegates reading bytes from the wrapped InputStream object.
   * 
   * @param dataBytes
   *          byte array into which the data will be stored that is read from the NDR byte stream
   * @param dataOffset
   *          storing data into {@code dataBytes} must start at this byte offset
   * @param dataLength
   *          number of bytes to read from {@code dataBytes}
   * @return number of bytes read into {@code dataBytes}. Note that this can be less than {@code dataLength} if EOF is
   *         reached.
   * @throws IOException
   *           when errors occur while reading data from the NDR byte stream
   */
  public int read(final byte[] dataBytes, final int dataOffset, final int dataLength) throws IOException {
    final int numBytesRead = inputStream.read(dataBytes, dataOffset, dataLength);
    totalBytesRead += numBytesRead;
    return numBytesRead;
  }

  /**
   * Reads bytes until the alignment of the given {@code fieldSizeInBytes} is reached.
   * 
   * @param fieldSizeInBytes
   *          size in bytes of the field that must be aligned
   * @throws IOException
   *           when errors occur while reading data from the NDR byte stream
   */
  public void skipAlignment(final int fieldSizeInBytes) throws IOException {
    final int alignError = totalBytesRead % fieldSizeInBytes;
    final int numAlignBytes = (alignError > 0) ? fieldSizeInBytes - alignError : 0;
    for (int i = 0; i < numAlignBytes; i++) {
      inputStream.read();
      totalBytesRead++;
    }
  }
}
