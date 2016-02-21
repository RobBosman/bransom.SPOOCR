package nl.bransom.jdce.pdu;

/**
 * Enum to identify the authentication protocol used in PDU transmission.
 * 
 * @author Rob
 */
public enum AuthenticationProtocolId {
  /** No authentication. */
  NONE(0),
  /** OSF DCE private key authentication. */
  OSF_DCE_PRIVATE_KEY_AUTHENTICATION(1);

  private byte value;

  /**
   * Hidden constructor.
   * 
   * @param value
   */
  private AuthenticationProtocolId(final int value) {
    this.value = (byte) value;
  }

  public byte getValue() {
    return value;
  }

  /**
   * Parses the byte value to the corresponding AuthenticationProtocolId.
   * 
   * @param value
   *          byte value
   * @return the authentication protocol identifier
   */
  public static AuthenticationProtocolId parse(final byte value) {
    for (AuthenticationProtocolId authenticationProtocolId : AuthenticationProtocolId.values()) {
      if (authenticationProtocolId.getValue() == value) {
        return authenticationProtocolId;
      }
    }
    throw new IllegalArgumentException("Unknown PDU type binary value " + String.format("%02x", value));
  }
}
