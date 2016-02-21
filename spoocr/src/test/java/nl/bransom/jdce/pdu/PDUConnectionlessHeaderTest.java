package nl.bransom.jdce.pdu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.bransom.jdce.ndr.FormatLabel;
import nl.bransom.jdce.ndr.FormatLabelCharacter;
import nl.bransom.jdce.ndr.FormatLabelFloat;
import nl.bransom.jdce.ndr.FormatLabelInteger;
import nl.bransom.jdce.ndr.NDRReader;
import nl.bransom.jdce.ndr.NDRWriter;
import nl.bransom.jdce.rpc.UUID;

import org.junit.Assert;
import org.junit.Test;

public class PDUConnectionlessHeaderTest {

  @Test
  public void testNDRSerialize() throws IOException {
    final String packetString = "04 0B 08 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B8 4A 9F 4D 1C"
        + "7D CF 11 86 1E 00 20 AF 6E 7C 57 86 C2 37 67 F7 1E D1 11 BC D9 00 60 97 92 D2 6C 79 BE 01 34 00 00 00 00"
        + "00 00 00 00 00 00 FF FF FF FF 68 00 00 00 01 00";
    final byte rpcVersion = (byte) 0x04;
    final PDUType pduType = PDUType.BIND;
    final PDUFlags pduFlags = new PDUFlags();
    pduFlags.set(PDUFlag1.NOFACK);
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final short serialNumber = (short) 0x0000;
    final UUID objectId = UUID.parse("00000000-0000-0000-0000-000000000000");
    final UUID interfaceId = UUID.parse("4d9f4ab8-7d1c-11cf-861e-0020af6e7c57");
    final UUID activityId = UUID.parse("6737c286-1ef7-11d1-bcd9-00609792d26c");
    final int serverBootTime = 0x3401be79;
    final int interfaceVersion = 0x00;
    final int sequenceNumber = 0x00;
    final short operationNumber = (short) 0x00;
    final short interfaceHint = (short) 0xffff;
    final short activityHint = (short) 0xffff;
    final short pduBodyLenght = (short) 0x0068;
    final short fragmentNumber = (short) 0x00;
    final AuthenticationProtocolId authProtocolId = AuthenticationProtocolId.OSF_DCE_PRIVATE_KEY_AUTHENTICATION;

    final String refHex = packetString.replace(" ", "");
    final byte[] refBytes = new byte[refHex.length() / 2];
    for (int i = 0; i < refBytes.length; i++) {
      refBytes[i] = (byte) Short.parseShort(refHex.substring(2 * i, 2 * i + 2), 16);
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    PDUConnectionlessHeader pduPacketHeader = new PDUConnectionlessHeader();
    pduPacketHeader.setRpcVersion(rpcVersion);
    pduPacketHeader.setPDUType(pduType);
    pduPacketHeader.setPDUFlags(pduFlags);
    pduPacketHeader.setFormatLabel(formatLabel);
    pduPacketHeader.setSerialNumber(serialNumber);
    pduPacketHeader.setObjectId(objectId);
    pduPacketHeader.setInterfaceId(interfaceId);
    pduPacketHeader.setActivityId(activityId);
    pduPacketHeader.setServerBootTime(serverBootTime);
    pduPacketHeader.setInterfaceVersion(interfaceVersion);
    pduPacketHeader.setSequenceNumber(sequenceNumber);
    pduPacketHeader.setOperationNumber(operationNumber);
    pduPacketHeader.setInterfaceHint(interfaceHint);
    pduPacketHeader.setActivityHint(activityHint);
    pduPacketHeader.setPDUBodyLength(pduBodyLenght);
    pduPacketHeader.setFragmentNumber(fragmentNumber);
    pduPacketHeader.setAuthenticationProtocolId(authProtocolId);
    pduPacketHeader.ndrSerialize(ndrWriter);
    Assert.assertArrayEquals("Incorrect NDR data;", refBytes, bos.toByteArray());
  }

  @Test
  public void testNDRDeserialize() throws IOException {
    final String packetString = "04 0B 08 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 B8 4A 9F 4D 1C"
        + "7D CF 11 86 1E 00 20 AF 6E 7C 57 86 C2 37 67 F7 1E D1 11 BC D9 00 60 97 92 D2 6C 79 BE 01 34 00 00 00 00"
        + "00 00 00 00 00 00 FF FF FF FF 68 00 00 00 01 00";
    final byte rpcVersion = (byte) 0x04;
    final PDUType pduType = PDUType.BIND;
    final PDUFlags pduFlags = new PDUFlags();
    pduFlags.set(PDUFlag1.NOFACK);
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final short serialNumber = (short) 0x0000;
    final UUID objectId = UUID.parse("00000000-0000-0000-0000-000000000000");
    final UUID interfaceId = UUID.parse("4d9f4ab8-7d1c-11cf-861e-0020af6e7c57");
    final UUID activityId = UUID.parse("6737c286-1ef7-11d1-bcd9-00609792d26c");
    final int serverBootTime = 0x3401be79;
    final int interfaceVersion = 0x00;
    final int sequenceNumber = 0x00;
    final short operationNumber = (short) 0x00;
    final short interfaceHint = (short) 0xffff;
    final short activityHint = (short) 0xffff;
    final short pduBodyLenght = (short) 0x0068;
    final short fragmentNumber = (short) 0x00;
    final AuthenticationProtocolId authProtocolId = AuthenticationProtocolId.OSF_DCE_PRIVATE_KEY_AUTHENTICATION;

    final String refHex = packetString.replace(" ", "");
    final byte[] refBytes = new byte[refHex.length() / 2];
    for (int i = 0; i < refBytes.length; i++) {
      refBytes[i] = (byte) Short.parseShort(refHex.substring(2 * i, 2 * i + 2), 16);
    }

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(refBytes));
    PDUConnectionlessHeader pduPacketHeader = new PDUConnectionlessHeader();
    pduPacketHeader.ndrDeserialize(ndrReader);
    Assert.assertEquals("Incorrect field value;", rpcVersion, pduPacketHeader.getRpcVersion());
    Assert.assertEquals("Incorrect field value;", pduType, pduPacketHeader.getPDUType());
    Assert.assertEquals("Incorrect field value;", pduFlags, pduPacketHeader.getPDUFlags());
    Assert.assertEquals("Incorrect field value;", formatLabel, pduPacketHeader.getFormatLabel());
    Assert.assertEquals("Incorrect field value;", serialNumber, pduPacketHeader.getSerialNumber());
    Assert.assertEquals("Incorrect field value;", objectId, pduPacketHeader.getObjectId());
    Assert.assertEquals("Incorrect field value;", interfaceId, pduPacketHeader.getInterfaceId());
    Assert.assertEquals("Incorrect field value;", activityId, pduPacketHeader.getActivityId());
    Assert.assertEquals("Incorrect field value;", serverBootTime, pduPacketHeader.getServerBootTime());
    Assert.assertEquals("Incorrect field value;", interfaceVersion, pduPacketHeader.getInterfaceVersion());
    Assert.assertEquals("Incorrect field value;", sequenceNumber, pduPacketHeader.getSequenceNumber());
    Assert.assertEquals("Incorrect field value;", operationNumber, pduPacketHeader.getOperationNumber());
    Assert.assertEquals("Incorrect field value;", interfaceHint, pduPacketHeader.getInterfaceHint());
    Assert.assertEquals("Incorrect field value;", activityHint, pduPacketHeader.getActivityHint());
    Assert.assertEquals("Incorrect field value;", pduBodyLenght, pduPacketHeader.getPDUBodyLength());
    Assert.assertEquals("Incorrect field value;", fragmentNumber, pduPacketHeader.getFragmentNumber());
    Assert.assertEquals("Incorrect field value;", authProtocolId, pduPacketHeader.getAuthenticationProtocolId());
  }
}