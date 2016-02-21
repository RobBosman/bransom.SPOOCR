package nl.bransom.jdce.pdu;

/**
 * Enum type for PDU flag1.
 * 
 * @author Rob
 */
public enum PDUFlag1 {
  /** Reserved for use by implementations. */
  RESERVED_01(0x01),
  /** Meaningful in either direction. If set, the PDU is the last fragment of a multi-PDU transmission. */
  LASTFRAG(0x02),
  /** Meaningful in either direction. If set, the PDU is a fragment of a multi-PDU transmission. */
  FRAG(0x04),
  /**
   * Meaningful for fragments sent in either direction. If set, the receiver is not requested to send a <b>fack</b> PDU
   * for the fragment. Otherwise, if not set, the receiver acknowledges the received PDU with a <b>fack</b> PDU. Note
   * that both client and server may send <b>fack</b> PDUs independent of the status of this ﬂag.
   */
  NOFACK(0x08),
  /** Meaningful only from client to server. If set, the PDU is for a maybe request. */
  MAYBE(0x10),
  /** Meaningful only from client to server. If set, the PDU is for an idempotent request. */
  IDEMPOTENT(0x20),
  /** Meaningful only from client to server. If set, the PDU is for a broadcast request. */
  BROADCAST(0x40),
  /** Reserved for use by implementations. */
  RESERVED_80(0x80);

  private byte mask;

  /**
   * Hidden constructor.
   * 
   * @param mask
   *          byte mask value
   */
  private PDUFlag1(final int mask) {
    this.mask = (byte) mask;
  }

  public byte getMask() {
    return mask;
  }
}
