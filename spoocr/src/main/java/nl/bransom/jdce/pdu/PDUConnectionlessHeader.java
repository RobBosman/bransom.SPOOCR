package nl.bransom.jdce.pdu;

import java.io.IOException;

import nl.bransom.jdce.ndr.FormatLabel;
import nl.bransom.jdce.ndr.NDRConstants;
import nl.bransom.jdce.ndr.NDRReader;
import nl.bransom.jdce.ndr.NDRSerializable;
import nl.bransom.jdce.ndr.NDRWriter;
import nl.bransom.jdce.rpc.UUID;

/**
 * Implements NDR support for the PDU packet header for connectionless RPC PDU's.
 * 
 * <pre>
 * typedef struct {
 *   unsigned small rpc_vers = 4; // RPC protocol major version (4 LSB only)
 *   unsigned small ptype; // Packet type (5 LSB only)
 *   unsigned small flags1; // Packet flags
 *   unsigned small flags2; // Packet flags
 *   byte drep[3]; // Data representation format label
 *   unsigned small serial_hi; // High byte of serial number
 *   uuid_t object; // Object identifier
 *   uuid_t if_id; // Interface identifier
 *   uuid_t act_id; // Activity identifier
 *   unsigned long server_boot;// Server boot time
 *   unsigned long if_vers; // Interface version
 *   unsigned long seqnum; // Sequence number
 *   unsigned short opnum; // Operation number
 *   unsigned short ihint; // Interface hint
 *   unsigned short ahint; // Activity hint
 *   unsigned short len; // Length of packet body
 *   unsigned short fragnum; // Fragment number
 *   unsigned small auth_proto; // Authentication protocol identifier
 *   unsigned small serial_lo; // Low byte of serial number
 * } dc_rpc_cl_pkt_hdr_t;
 * </pre>
 * 
 * @author Rob
 */
public class PDUConnectionlessHeader implements NDRSerializable {

  /** Supported RPC main version. */
  public static final byte RPC_VERSION = 4;

  // RPC protocol major version (4 LSB only)
  private byte rpcVersion;
  // Packet type (5 LSB only)
  private PDUType pduType;
  // Packet flags
  private PDUFlags pduFlags;
  // Data representation format label
  private FormatLabel formatLabel;
  private short serialNumber;
  private UUID objectId;
  private UUID interfaceId;
  private UUID activityId;
  private int serverBootTime;
  private int interfaceVersion;
  private int sequenceNumber;
  private short operationNumber;
  private short interfaceHint;
  private short activityHint;
  // Length of packet body
  private short pduBodyLength;
  private short fragmentNumber;
  private AuthenticationProtocolId authenticationProtocolId;

  /**
   * Default constructor.
   */
  public PDUConnectionlessHeader() {
    rpcVersion = RPC_VERSION;
    pduFlags = new PDUFlags();
    formatLabel = new FormatLabel();
    authenticationProtocolId = AuthenticationProtocolId.NONE;
  }

  public byte getRpcVersion() {
    return rpcVersion;
  }

  public void setRpcVersion(final byte rpcVersion) {
    this.rpcVersion = rpcVersion;
  }

  public PDUType getPDUType() {
    return pduType;
  }

  public void setPDUType(final PDUType pduType) {
    this.pduType = pduType;
  }

  public PDUFlags getPDUFlags() {
    return pduFlags;
  }

  public void setPDUFlags(final PDUFlags pduFlags) {
    this.pduFlags = pduFlags;
  }

  public FormatLabel getFormatLabel() {
    return formatLabel;
  }

  public void setFormatLabel(final FormatLabel formatLabel) {
    this.formatLabel = formatLabel;
  }

  public short getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(final short serialNumber) {
    this.serialNumber = serialNumber;
  }

  public UUID getObjectId() {
    return objectId;
  }

  public void setObjectId(final UUID objectId) {
    this.objectId = objectId;
  }

  public UUID getInterfaceId() {
    return interfaceId;
  }

  public void setInterfaceId(final UUID interfaceId) {
    this.interfaceId = interfaceId;
  }

  public UUID getActivityId() {
    return activityId;
  }

  public void setActivityId(final UUID activityId) {
    this.activityId = activityId;
  }

  public int getServerBootTime() {
    return serverBootTime;
  }

  public void setServerBootTime(final int serverBootTime) {
    this.serverBootTime = serverBootTime;
  }

  public int getInterfaceVersion() {
    return interfaceVersion;
  }

  public void setInterfaceVersion(final int interfaceVersion) {
    this.interfaceVersion = interfaceVersion;
  }

  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(final int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public short getOperationNumber() {
    return operationNumber;
  }

  public void setOperationNumber(final short operationNumber) {
    this.operationNumber = operationNumber;
  }

  public short getInterfaceHint() {
    return interfaceHint;
  }

  public void setInterfaceHint(final short interfaceHint) {
    this.interfaceHint = interfaceHint;
  }

  public short getActivityHint() {
    return activityHint;
  }

  public void setActivityHint(final short activityHint) {
    this.activityHint = activityHint;
  }

  public short getPDUBodyLength() {
    return pduBodyLength;
  }

  public void setPDUBodyLength(final short pduBodyLength) {
    this.pduBodyLength = pduBodyLength;
  }

  public short getFragmentNumber() {
    return fragmentNumber;
  }

  public void setFragmentNumber(final short fragmentNumber) {
    this.fragmentNumber = fragmentNumber;
  }

  public AuthenticationProtocolId getAuthenticationProtocolId() {
    return authenticationProtocolId;
  }

  public void setAuthenticationProtocolId(final AuthenticationProtocolId authProtocolId) {
    this.authenticationProtocolId = authProtocolId;
  }

  @Override
  public void ndrSerialize(final NDRWriter ndrWriter) throws IOException {
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeByte(RPC_VERSION);
    ndrWriter.writeByte(pduType.getValue());
    ndrWriter.write(pduFlags);
    ndrWriter.writeFormatLabel(NDRConstants.CONNECTIONLESS_FORMAT_LABEL_LENGTH);
    ndrWriter.writeByte((byte) (serialNumber >> NDRConstants.NUM_BITS_PER_BYTE));
    ndrWriter.write(objectId);
    ndrWriter.write(interfaceId);
    ndrWriter.write(activityId);
    ndrWriter.writeInt(serverBootTime);
    ndrWriter.writeInt(interfaceVersion);
    ndrWriter.writeInt(sequenceNumber);
    ndrWriter.writeShort(operationNumber);
    ndrWriter.writeShort(interfaceHint);
    ndrWriter.writeShort(activityHint);
    ndrWriter.writeShort(pduBodyLength);
    ndrWriter.writeShort(fragmentNumber);
    ndrWriter.writeByte(authenticationProtocolId.getValue());
    ndrWriter.writeByte((byte) (serialNumber & NDRConstants.BYTE_MASK));
  }

  @Override
  public void ndrDeserialize(final NDRReader ndrReader) throws IOException {
    rpcVersion = ndrReader.readByte();
    pduType = PDUType.parse(ndrReader.readByte());
    pduFlags = ndrReader.read(PDUFlags.class);
    // Read the NDR format label and apply it to the ndrReader.
    ndrReader.readFormatLabel(NDRConstants.CONNECTIONLESS_FORMAT_LABEL_LENGTH);
    // Copy the NDR format label of the ndrReader to the formatLabel byte array.
    formatLabel = ndrReader.getFormatLabel();
    serialNumber = (short) (ndrReader.readByte() << NDRConstants.NUM_BITS_PER_BYTE);
    objectId = ndrReader.read(UUID.class);
    interfaceId = ndrReader.read(UUID.class);
    activityId = ndrReader.read(UUID.class);
    serverBootTime = ndrReader.readInt();
    interfaceVersion = ndrReader.readInt();
    sequenceNumber = ndrReader.readInt();
    operationNumber = ndrReader.readShort();
    interfaceHint = ndrReader.readShort();
    activityHint = ndrReader.readShort();
    pduBodyLength = ndrReader.readShort();
    fragmentNumber = ndrReader.readShort();
    authenticationProtocolId = AuthenticationProtocolId.parse(ndrReader.readByte());
    serialNumber |= ndrReader.readByte();
  }
}
