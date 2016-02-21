package nl.bransom.jdce.ndr;

import java.io.IOException;

/**
 * Classes should implement this interface to (de)serialize their data to and from NDR byte streams.
 * 
 * @author Rob
 */
public interface NDRSerializable {

  /**
   * Uses {@code ndrWriter} to write the current object state to an NDR byte stream.
   * 
   * @param ndrWriter
   *          helper to write data to the NDR byte stream
   * @throws IOException
   *           if errors occur while writing the NDR byte stream
   */
  void ndrSerialize(final NDRWriter ndrWriter) throws IOException;

  /**
   * Uses {@code ndrReader} to read data from an NDR byte stream and set the current object state accordingly.
   * 
   * @param ndrReader
   *          helper to read data from the NDR byte stream
   * @throws IOException
   *           if errors occur while reading the NDR byte stream
   */
  void ndrDeserialize(final NDRReader ndrReader) throws IOException;
}
