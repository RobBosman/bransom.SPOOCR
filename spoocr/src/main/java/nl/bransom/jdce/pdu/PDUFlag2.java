package nl.bransom.jdce.pdu;

/**
 * Enum type for PDU flag2.
 * 
 * @author Rob
 */
public enum PDUFlag2 {
  /** Reserved for use by implementations. */
  RESERVED_01(0x01),
  /** Cancel pending at the call end. */
  CANCEL_PENDING(0x02),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_04(0x04),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_08(0x08),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_10(0x10),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_20(0x20),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_40(0x40),
  /** Reserved for future use. Must be set to 0. */
  RESERVED_80(0x80);

  private byte mask;

  /**
   * Hidden constructor.
   * 
   * @param mask
   *          byte mask value
   */
  private PDUFlag2(final int mask) {
    this.mask = (byte) mask;
  }

  public byte getMask() {
    return mask;
  }
}
