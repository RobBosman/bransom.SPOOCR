package nl.bransom.jdce.ndr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit class to test the consistency of reading and writing NDR data. Each primitive type (boolean, byte, int, String,
 * etc.) is marshaled to an NDR byte stream and then unmarshaled again. This class tests if the resulting primitive is
 * equal.
 * 
 * @author Rob
 */
public class NDRReadAndWriteTest {

  @Test
  public void testReadAndWrite() throws Exception {
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final boolean boolean0 = true;
    final boolean boolean1 = false;
    final byte byte0 = 0x3a;
    final short short0 = 0x6f12;
    final int int0 = 0x82f0991d;
    final long long0 = 0x82f0991da7a1fe69L;
    final long long1 = -0x8f91aaf287a5f107L;
    final float float0 = -17.028F;
    final double double0 = 777640186.2219;
    final byte[] fixedBytes = { (byte) 0x38, (byte) 0xd3, (byte) 0x77, (byte) 0x07, (byte) 0xda };
    final byte[] conformantBytes = { (byte) 0xbb, (byte) 0x3f, (byte) 0x92 };
    final byte[] varyingBytes = { (byte) 0x5b, (byte) 0x72, (byte) 0x21, (byte) 0xc8 };
    final byte[] conformantVaryingBytes = { (byte) 0x2b, (byte) 0xf4, (byte) 0x86, (byte) 0x16, (byte) 0x06,
        (byte) 0xd4 };
    final int ndrOffset0 = 2;
    final int ndrOffset1 = 3;
    final int ndrMaxLength0 = 8;
    final int ndrMaxLength1 = 13;
    final int ndrMaxLength2 = 21;
    final String text0 = "Test text number one.";
    final String text1 = "Second test text.";
    final byte[] referent0 = { (byte) 0xfe, (byte) 0x57, (byte) 0x2e, (byte) 0x09, (byte) 0xaa, };
    final byte[] referent1 = { (byte) 0x04, (byte) 0x1e, (byte) 0xa2 };

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeBoolean(boolean0);
    ndrWriter.writeBoolean(boolean1);
    ndrWriter.writeByte(byte0);
    ndrWriter.writeShort(short0);
    ndrWriter.writeInt(int0);
    ndrWriter.writeLong(long0);
    ndrWriter.writeFloat(float0);
    ndrWriter.writeDouble(double0);
    ndrWriter.writeBytesFixed(fixedBytes, 0, fixedBytes.length);
    ndrWriter.writeBytesConformant(conformantBytes, 0, conformantBytes.length, ndrMaxLength0);
    ndrWriter.writeBytesVarying(varyingBytes, 0, varyingBytes.length, ndrOffset0);
    ndrWriter.writeBytesConformantVarying(conformantVaryingBytes, 0, conformantVaryingBytes.length, ndrMaxLength1,
        ndrOffset1);
    ndrWriter.writeStringVarying(text0, 0);
    ndrWriter.writeStringConformantVarying(text1, ndrMaxLength2, 0);
    final Map<Integer, byte[]> referentMap0 = new HashMap<Integer, byte[]>();
    ndrWriter.writeReference(referentMap0, referent0);
    ndrWriter.writeBytesFixed(referent0, 0, referent0.length);
    ndrWriter.writeReference(referentMap0, referent1);
    ndrWriter.writeBytesFixed(referent1, 0, referent1.length);
    ndrWriter.writeReference(referentMap0, referent0);
    ndrWriter.writeLong(long1);

    byte[] transmittedBytes = bos.toByteArray();
    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(transmittedBytes));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", formatLabel, ndrReader.getFormatLabel());
    Assert.assertEquals("Incorrect value", boolean0, ndrReader.readBoolean());
    Assert.assertEquals("Incorrect value", boolean1, ndrReader.readBoolean());
    Assert.assertEquals("Incorrect value", byte0, ndrReader.readByte());
    Assert.assertEquals("Incorrect value", short0, ndrReader.readShort());
    Assert.assertEquals("Incorrect value", int0, ndrReader.readInt());
    Assert.assertEquals("Incorrect value", long0, ndrReader.readLong());
    Assert.assertEquals("Incorrect value", float0, ndrReader.readFloat(), Float.MIN_VALUE);
    Assert.assertEquals("Incorrect value", double0, ndrReader.readDouble(), Double.MIN_VALUE);
    byte[] testFixedBytes = new byte[fixedBytes.length];
    ndrReader.readBytesFixed(testFixedBytes, 0, fixedBytes.length);
    Assert.assertArrayEquals("Incorrect value", fixedBytes, testFixedBytes);
    byte[] testConformantBytes = new byte[ndrMaxLength0];
    ndrReader.readBytesConformant(testConformantBytes, 0);
    Assert.assertArrayEquals("Incorrect value", conformantBytes,
        Arrays.copyOfRange(testConformantBytes, 0, conformantBytes.length));
    byte[] testVaryingBytes = new byte[ndrOffset0 + varyingBytes.length];
    ndrReader.readBytesVarying(testVaryingBytes, 0);
    Assert.assertArrayEquals("Incorrect value", varyingBytes,
        Arrays.copyOfRange(testVaryingBytes, ndrOffset0, ndrOffset0 + varyingBytes.length));
    byte[] testConformantVaryingBytes = new byte[ndrMaxLength1];
    ndrReader.readBytesConformantVarying(testConformantVaryingBytes, 0);
    Assert.assertArrayEquals("Incorrect value", conformantVaryingBytes,
        Arrays.copyOfRange(testConformantVaryingBytes, ndrOffset1, ndrOffset1 + conformantVaryingBytes.length));
    Assert.assertEquals("Incorrect value", text0, ndrReader.readStringVarying());
    Assert.assertEquals("Incorrect value", text1, ndrReader.readStringConformantVarying());
    final Map<Integer, byte[]> referentMap1 = new HashMap<Integer, byte[]>();
    Assert.assertArrayEquals("Incorrect value", referent0, ndrReader.readReference(referentMap1, referent0.length));
    Assert.assertArrayEquals("Incorrect value", referent1, ndrReader.readReference(referentMap1, referent1.length));
    Assert.assertArrayEquals("Incorrect value", referent0, ndrReader.readReference(referentMap1, referent0.length));
    Assert.assertEquals("Incorrect referentMap", referentMap0.keySet(), referentMap1.keySet());
    for (Integer referenceID : referentMap0.keySet()) {
      Assert.assertArrayEquals("Incorrect referent", referentMap0.get(referenceID), referentMap1.get(referenceID));
    }
    Assert.assertEquals("Incorrect value", long1, ndrReader.readLong());
  }
}
