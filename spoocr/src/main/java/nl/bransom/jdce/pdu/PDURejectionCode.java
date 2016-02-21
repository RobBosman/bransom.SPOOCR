package nl.bransom.jdce.pdu;

/**
 * Enumeration for all supported PDU rejection codes.
 * 
 * @author Rob
 */
public enum PDURejectionCode {
  /** The server does not support the RPC protocol version specified in the request PDU. */
  NCA_RPC_VERSION_MISMATCH(0x1c000008),
  /** The request is being rejected for unspecified reasons. */
  NCA_UNSPEC_REJECT(0x1c000009),
  /** The server has no state corresponding to the activity identifier in the message. */
  NCA_S_BAD_ACTID(0x1c00000a),
  /** The Conversation Manager callback failed */
  NCA_WHO_ARE_YOU_FAILED(0x1c00000b),
  /** The server manager routine has not been entered and executed. */
  NCA_MANAGER_NOT_ENTERED(0x1c00000c),
  /**
   * The operation number passed in the request PDU is greater than or equal to the number of operations in the
   * interface.
   */
  NCA_OP_RNG_ERROR(0x1c010002),
  /** The server does not export the requested interface. */
  NCA_UNK_IF(0x1c010003),
  /** The server boot time passed in the request PDU does not match the actual server boot time. */
  NCA_WRONG_BOOT_TIME(0x1c010006),
  /** A restarted server called back a client */
  NCA_S_YOU_CRASHED(0x1c010009),
  /** The RPC client or server protocol has been violated. */
  NCA_PROTO_ERROR(0x1c01000b),
  /** The output parameters of the operation exceed their declared maximum size. */
  NCA_OUT_ARGS_TOO_BIG(0x1c010013),
  /** The server is too busy to handle the call */
  NCA_SERVER_TOO_BUSY(0x1c010014),
  /** The server does not implement the requested operation for the type of the requested object. */
  NCA_UNSUPPORTED_TYPE(0x1c010017),
  /** Invalid presentation context ID */
  NCA_INVALID_PRES_CONTEXT_ID(0x1c00001c),
  /** The server did not support the requested authentication level */
  NCA_UNSUPPORTED_AUTHN_LEVEL(0x1c00001d),
  /** Invalid checksum. */
  NCA_INVALID_CHECKSUM(0x1c00001f),
  /** Invalid CRC. */
  NCA_INVALID_CRC(0x1c000020);

  private int value;

  /**
   * Hidden constructor.
   * 
   * @param value
   *          binary value of rejection code
   */
  private PDURejectionCode(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  /**
   * Parses the byte value to the corresponding PDU RejectionCode.
   * 
   * @param value
   *          NDR byte value
   * @return PDURejectionCode enum
   */
  public static PDURejectionCode parse(final int value) {
    for (PDURejectionCode pduRejectionCode : PDURejectionCode.values()) {
      if (pduRejectionCode.getValue() == value) {
        return pduRejectionCode;
      }
    }
    throw new IllegalArgumentException("Unknown PDU Rejection Code binary value " + String.format("%08x", value));
  }
}
