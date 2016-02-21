package nl.bransom.jdce.ndr;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a wrapper for class OutputStream. It keeps track of the number of bytes that are written, so it can add
 * alignment bytes if required.
 */
public class AlignedOutputStream {

  private OutputStream outputStream;
  private int numBytesWritten;

  /**
   * @param outputStream
   *          NDR byte stream
   */
  public AlignedOutputStream(final OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * Delegates writing A byte to the wrapped OutputStream object.
   * 
   * @param aByte
   *          single byte to write to the NDR stream
   * @throws IOException
   *           when errors occur while writing data to the NDR byte stream
   */
  public void write(final byte aByte) throws IOException {
    outputStream.write(aByte);
    numBytesWritten++;
  }

  /**
   * Delegates writing bytes to the wrapped OutputStream object.
   * 
   * @param dataBytes
   *          byte array containing the data that must be written to the NDR byte stream
   * @param dataOffset
   *          copying data from {@code dataBytes} must start at this byte offset
   * @param dataLength
   *          number of bytes to write
   * @throws IOException
   *           when errors occur while writing data to the NDR byte stream
   */
  public void write(final byte[] dataBytes, final int dataOffset, final int dataLength) throws IOException {
    outputStream.write(dataBytes, dataOffset, dataLength);
    numBytesWritten += dataLength;
  }

  /**
   * Writes null-bytes until the given {@code fieldSizeInBytes} is aligned.
   * 
   * @param fieldSizeInBytes
   *          size in bytes of the field that must be aligned
   * @throws IOException
   *           when errors occur while writing data to the NDR byte stream
   */
  public void writeAlignment(final int fieldSizeInBytes) throws IOException {
    final int alignError = numBytesWritten % fieldSizeInBytes;
    final int numAlignBytes = (alignError > 0) ? fieldSizeInBytes - alignError : 0;
    for (int i = 0; i < numAlignBytes; i++) {
      outputStream.write((byte) 0x00);
      numBytesWritten++;
    }
  }
}
