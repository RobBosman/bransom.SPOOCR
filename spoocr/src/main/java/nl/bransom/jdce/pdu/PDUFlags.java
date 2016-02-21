package nl.bransom.jdce.pdu;

import java.io.IOException;

import nl.bransom.jdce.ndr.NDRReader;
import nl.bransom.jdce.ndr.NDRSerializable;
import nl.bransom.jdce.ndr.NDRWriter;

/**
 * Convenience class to handle PDU flags.
 * 
 * @author Rob
 */
public class PDUFlags implements NDRSerializable {

  private byte flags1;
  private byte flags2;

  /**
   * Default constructor.
   */
  public PDUFlags() {
  }

  /**
   * Tests if {@code pduFlag1} is set.
   * 
   * @param pduFlag1
   *          binary flag to test
   * @return {@code true} if set, {@code false} if not.
   */
  public boolean isSet(final PDUFlag1 pduFlag1) {
    return (flags1 & pduFlag1.getMask()) != 0x00;
  }

  /**
   * Tests if {@code pduFlag2} is set.
   * 
   * @param pduFlag2
   *          binary flag to test
   * @return {@code true} if set, {@code false} if not.
   */
  public boolean isSet(final PDUFlag2 pduFlag2) {
    return (flags2 & pduFlag2.getMask()) != 0x00;
  }

  /**
   * Sets the given {@code pduFlag1}.
   * 
   * @param pduFlag1
   *          binary flag to set
   */
  public void set(final PDUFlag1 pduFlag1) {
    flags1 |= pduFlag1.getMask();
  }

  /**
   * Resets the given {@code pduFlag1}.
   * 
   * @param pduFlag1
   *          binary flag to reset
   */
  public void unset(final PDUFlag1 pduFlag1) {
    flags1 &= ~pduFlag1.getMask();
  }

  /**
   * Sets the given {@code pduFlag2}.
   * 
   * @param pduFlag2
   *          binary flag to set
   */
  public void set(final PDUFlag2 pduFlag2) {
    flags2 |= pduFlag2.getMask();
  }

  /**
   * Resets the given {@code pduFlag2}.
   * 
   * @param pduFlag2
   *          binary flag to reset
   */
  public void unset(final PDUFlag2 pduFlag2) {
    flags2 &= ~pduFlag2.getMask();
  }

  @Override
  public void ndrSerialize(NDRWriter ndrWriter) throws IOException {
    ndrWriter.writeByte(flags1);
    ndrWriter.writeByte(flags2);
  }

  @Override
  public void ndrDeserialize(NDRReader ndrReader) throws IOException {
    flags1 = ndrReader.readByte();
    flags2 = ndrReader.readByte();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof PDUFlags) {
      final PDUFlags other = (PDUFlags) obj;
      return this.flags1 == other.flags1 && this.flags2 == other.flags2;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 256 * flags1 + flags2;
  }
}
