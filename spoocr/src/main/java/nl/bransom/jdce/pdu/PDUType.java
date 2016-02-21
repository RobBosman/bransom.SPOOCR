package nl.bransom.jdce.pdu;

/**
 * Class to handle NDR PDU types.
 * 
 * @author Rob
 */
public enum PDUType {
  /** request */
  REQUEST(0),
  /** ping */
  PING(1),
  /** response */
  RESPONSE(2),
  /** fault */
  FAULT(3),
  /** working */
  WORKING(4),
  /** nocall */
  NOCALL(5),
  /** reject */
  REJECT(6),
  /** ack */
  ACK(7),
  /** cl_cancel */
  CL_CANCEL(8),
  /** fack */
  FACK(9),
  /** cancel_ack */
  CANCEL_ACK(10),
  /** bind */
  BIND(11),
  /** bind_ack */
  BIND_ACK(12),
  /** bind_nack */
  BIND_NACK(13),
  /** alter_context */
  ALTER_CONTEXT(14),
  /** alter_context_resp */
  ALTER_CONTEXT_RESP(15),
  /** shutdown */
  SHUTDOWN(17),
  /** co_cancel */
  CO_CANCEL(18),
  /** orphaned */
  ORPHANED(19);

  private byte value;

  /**
   * Hidden constructor.
   * 
   * @param value
   *          NDR byte value
   */
  private PDUType(final int value) {
    this.value = (byte) value;
  }

  /**
   * @return NDR byte value representing the PDU type.
   */
  public byte getValue() {
    return value;
  }

  /**
   * Parses the byte value to the corresponding PDUType.
   * 
   * @param value
   *          NDR byte value
   * @return PDUType enum
   */
  public static PDUType parse(final byte value) {
    for (PDUType pduType : PDUType.values()) {
      if (pduType.getValue() == value) {
        return pduType;
      }
    }
    throw new IllegalArgumentException("Unknown PDU type binary value " + String.format("%02x", value));
  }
}
