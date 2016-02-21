package nl.bransom.jdce.ndr;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link NDRWriter}
 * 
 * @author Rob
 */
public class NDRWriterTest {

  @Test
  public void testWriteFormatLabel() throws Exception {
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect byte length", NDRConstants.FORMAT_LABEL_LENGTH, bos.size());
    try {
      ndrWriter = new NDRWriter(bos);
      ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
      Assert.fail("Method must fail if NDR Format Label is not specified.");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void testWriteBoolean() throws Exception {
    final boolean data0 = false;
    final boolean data1 = true;
    final byte ref0 = 0x00;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeBoolean(data0);
    ndrWriter.writeBoolean(data1);
    Assert.assertEquals("Wrong boolean data", ref0, bos.toByteArray()[0]);
    Assert.assertNotEquals("Wrong boolean data", ref0, bos.toByteArray()[1]);
  }

  @Test
  public void testWriteByte() throws Exception {
    final byte ref0 = 0x5e;
    final byte ref1 = 0x6f;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeByte(ref0);
    ndrWriter.writeByte(ref1);
    Assert.assertEquals("Wrong byte data", ref0, bos.toByteArray()[0]);
    Assert.assertEquals("Wrong byte data", ref1, bos.toByteArray()[1]);
  }

  @Test
  public void testWriteShort() throws Exception {
    final short data0 = (short) 0x1234;
    final short data1 = (short) 0x9abc;
    final byte data2 = (byte) 0x9a; // This will enforce re-alignment of
    // succeeding numbers.
    final short data3 = (short) 0x2143;
    final byte[] ref0 = new byte[] { (byte) 0x12, (byte) 0x34 };
    final byte[] ref1 = new byte[] { (byte) 0x9a, (byte) 0xbc };
    final byte[] ref2 = new byte[] { data2, (byte) 0x00 };
    final byte[] ref3 = new byte[] { (byte) 0x21, (byte) 0x43 };
    final int offset0 = 0;
    final int offset1 = offset0 + ref0.length;
    final int offset2 = offset1 + ref1.length;
    final int offset3 = offset2 + ref2.length;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeShort(data0);
    ndrWriter.writeShort(data1);
    ndrWriter.writeByte(data2);
    ndrWriter.writeShort(data3);
    Assert.assertArrayEquals("Wrong Short data", ref0,
        Arrays.copyOfRange(bos.toByteArray(), offset0, offset0 + ref0.length));
    Assert.assertArrayEquals("Wrong Short data", ref1,
        Arrays.copyOfRange(bos.toByteArray(), offset1, offset1 + ref1.length));
    Assert.assertArrayEquals("Incorrect alignment", ref2,
        Arrays.copyOfRange(bos.toByteArray(), offset2, offset2 + ref2.length));
    Assert.assertArrayEquals("Wrong Short data", ref3,
        Arrays.copyOfRange(bos.toByteArray(), offset3, offset3 + ref3.length));
  }

  @Test
  public void testWriteInteger() throws Exception {
    final int data0 = 0x12345678;
    final int data1 = 0x9abcdef0;
    final byte data2 = (byte) 0x9a; // This will enforce re-alignment of
    // succeeding numbers.
    final int data3 = 0x21436587;
    final byte[] ref0 = new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 };
    final byte[] ref1 = new byte[] { (byte) 0x9a, (byte) 0xbc, (byte) 0xde, (byte) 0xf0 };
    final byte[] ref2 = new byte[] { data2, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    final byte[] ref3 = new byte[] { (byte) 0x21, (byte) 0x43, (byte) 0x65, (byte) 0x87 };
    final int offset0 = 0;
    final int offset1 = offset0 + ref0.length;
    final int offset2 = offset1 + ref1.length;
    final int offset3 = offset2 + ref2.length;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeInt(data0);
    ndrWriter.writeInt(data1);
    ndrWriter.writeByte(data2);
    ndrWriter.writeInt(data3);
    Assert.assertArrayEquals("Wrong Integer data", ref0,
        Arrays.copyOfRange(bos.toByteArray(), offset0, offset0 + ref0.length));
    Assert.assertArrayEquals("Wrong Integer data", ref1,
        Arrays.copyOfRange(bos.toByteArray(), offset1, offset1 + ref1.length));
    Assert.assertArrayEquals("Incorrect alignment", ref2,
        Arrays.copyOfRange(bos.toByteArray(), offset2, offset2 + ref2.length));
    Assert.assertArrayEquals("Wrong Integer data", ref3,
        Arrays.copyOfRange(bos.toByteArray(), offset3, offset3 + ref3.length));
  }

  @Test
  public void testWriteLong() throws Exception {
    final long data0 = 0x1200340056007800L;
    final long data1 = 0x9a00bc00de00f000L;
    final byte data2 = (byte) 0xd6; // This will enforce re-alignment of
    // succeeding numbers.
    final long data3 = 0x2100430065008700L;
    final byte[] ref0 = new byte[] { (byte) 0x12, (byte) 0x00, (byte) 0x34, (byte) 0x00, (byte) 0x56, (byte) 0x00,
        (byte) 0x78, (byte) 0x00 };
    final byte[] ref1 = new byte[] { (byte) 0x9a, (byte) 0x00, (byte) 0xbc, (byte) 0x00, (byte) 0xde, (byte) 0x00,
        (byte) 0xf0, (byte) 0x00 };
    final byte[] ref2 = new byte[] { data2, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00 };
    final byte[] ref3 = new byte[] { (byte) 0x21, (byte) 0x00, (byte) 0x43, (byte) 0x00, (byte) 0x65, (byte) 0x00,
        (byte) 0x87, (byte) 0x00 };
    final int offset0 = 0;
    final int offset1 = offset0 + ref0.length;
    final int offset2 = offset1 + ref1.length;
    final int offset3 = offset2 + ref2.length;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeLong(data0);
    ndrWriter.writeLong(data1);
    ndrWriter.writeByte(data2);
    ndrWriter.writeLong(data3);
    Assert.assertArrayEquals("Wrong Long data", ref0,
        Arrays.copyOfRange(bos.toByteArray(), offset0, offset0 + ref0.length));
    Assert.assertArrayEquals("Wrong Long data", ref1,
        Arrays.copyOfRange(bos.toByteArray(), offset1, offset1 + ref1.length));
    Assert.assertArrayEquals("Incorrect alignment", ref2,
        Arrays.copyOfRange(bos.toByteArray(), offset2, offset2 + ref2.length));
    Assert.assertArrayEquals("Wrong Long data", ref3,
        Arrays.copyOfRange(bos.toByteArray(), offset3, offset3 + ref3.length));
  }

  @Test
  public void testWriteLittleBigEndian() throws Exception {
    final int data = -821773705;
    final byte[] refBE = new byte[] { (byte) 0xcf, (byte) 0x04, (byte) 0xba, (byte) 0x77 };
    final byte[] refLE = new byte[] { (byte) 0x77, (byte) 0xba, (byte) 0x04, (byte) 0xcf };
    final FormatLabel formatLabelBE = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelLE = new FormatLabel(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelBE);
    ndrWriter.writeInt(data);
    Assert.assertArrayEquals("Wrong Long data", refBE, Arrays.copyOfRange(bos.toByteArray(), 0, refBE.length));

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelLE);
    ndrWriter.writeInt(data);
    Assert.assertArrayEquals("Wrong Long data", refLE, Arrays.copyOfRange(bos.toByteArray(), 0, refLE.length));
  }

  @Test
  public void testWriteFloat() throws Exception {
    // IEEE: sign (1 bit) + exponent (8 bits) + mantissa (23 bits).
    final float data = -821.699F;
    // sign = 1 = 0b1 = 0x01
    // exponent = 136 = 0b10001000 = 0x88
    // mantissa = 5074108 = 0b1001101 01101100 10111100 = 0x4d 0x6c 0xbc
    // value: (sign) (1 + mantissa * 2^-23) * 2^(exponent - 127) = -(1 + 0,6048808) * 2^9 = -821,699
    final byte[] refIEEEBE = new byte[] { (byte) 0xc4, (byte) 0x4d, (byte) 0x6c, (byte) 0xbc };
    final byte[] refIEEELE = new byte[] { (byte) 0xbc, (byte) 0x6c, (byte) 0x4d, (byte) 0xc4 };
    final FormatLabel formatLabelBE = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelLE = new FormatLabel(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelBE);
    ndrWriter.writeFloat(data);
    Assert.assertArrayEquals("Wrong Float data IEEE big endian", refIEEEBE,
        Arrays.copyOfRange(bos.toByteArray(), 0, refIEEEBE.length));

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelLE);
    ndrWriter.writeFloat(data);
    Assert.assertArrayEquals("Wrong Float data IEEE little endian", refIEEELE,
        Arrays.copyOfRange(bos.toByteArray(), 0, refIEEELE.length));
  }

  @Test
  public void testWriteDouble() throws Exception {
    // IEEE: sign (1 bit) + exponent (11 bits) + mantissa (52 bits).
    final double data = -7184.00951;
    // sign = 1 = 0b1 = 0x01
    // exponent = 12 - 1023 = 1035 = 0b10000001011 = 0x40b
    // mantissa = 3395302362927868 = 0b1100000100000000001001101111001111110101001011111100 = 0xC10026F3F52FC
    // value: (sign) (1 + mantissa * 2^-52) * 2^(exponent - 1023) = -(1 + 0,75390857177734375) * 2^12 = -7184.00951
    final byte[] refIEEEBE = new byte[] { (byte) 0xc0, (byte) 0xbc, (byte) 0x10, (byte) 0x02, (byte) 0x6f, (byte) 0x3f,
        (byte) 0x52, (byte) 0xfc };
    final byte[] refIEEELE = new byte[] { (byte) 0xfc, (byte) 0x52, (byte) 0x3f, (byte) 0x6f, (byte) 0x02, (byte) 0x10,
        (byte) 0xbc, (byte) 0xc0 };
    final FormatLabel formatLabelBE = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelLE = new FormatLabel(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelBE);
    ndrWriter.writeDouble(data);
    Assert.assertArrayEquals("Wrong Double data IEEE big endian", refIEEEBE,
        Arrays.copyOfRange(bos.toByteArray(), 0, refIEEEBE.length));

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelLE);
    ndrWriter.writeDouble(data);
    Assert.assertArrayEquals("Wrong Double data IEEE little endian", refIEEELE,
        Arrays.copyOfRange(bos.toByteArray(), 0, refIEEELE.length));
  }

  @Test
  public void testWriteBytesFixed() throws Exception {
    final byte[] data = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final String ref = "" //
        + "0000  0a b0 0c                                           .\u00b0.";
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeBytesFixed(data, 0, data.length);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabel.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect output bytes[fixed]", ref, hexView);
  }

  @Test
  public void testWriteBytesConformant() throws Exception {
    final byte[] data = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final int ndrMaxLength = 0x05;
    final String ref = "" //
        + "0000  00 00 00 05 0a b0 0c 00  00                        .....\u00b0.. .";
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeBytesConformant(data, 0, data.length, ndrMaxLength);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabel.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect output bytes[conformant]", ref, hexView);
    try {
      ndrWriter.writeBytesConformant(null, 0, data.length, ndrMaxLength);
      Assert.fail("Method must fail on illegal input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      ndrWriter.writeBytesConformant(data, 1, data.length, ndrMaxLength);
      Assert.fail("Method must fail on illegal input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      ndrWriter.writeBytesConformant(data, 0, data.length, data.length - 1);
      Assert.fail("Method must fail on illegal input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testWriteBytesVarying() throws Exception {
    final byte[] data = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final int ndrOffset = 0x07;
    final String ref = "" //
        + "0000  00 00 00 07 00 00 00 03  0a b0 0c                  ........ .\u00b0.";
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeBytesVarying(data, 0, data.length, ndrOffset);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabel.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect output bytes[varying]", ref, hexView);
  }

  @Test
  public void testWriteBytesConformantVarying() throws Exception {
    final byte[] data = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final int ndrMaxLength = 0x0e;
    final int ndrOffset = 0x07;
    final String ref = "" //
        + "0000  00 00 00 0e 00 00 00 07  00 00 00 03 0a b0 0c      ........ .....\u00b0.";
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeBytesConformantVarying(data, 0, data.length, ndrMaxLength, ndrOffset);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabel.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect output bytes[conformant-varying]", ref, hexView);
  }

  @Test
  public void testWriteStringVarying() throws Exception {
    final String data = "Hello world!";
    final int offset = 0x07;
    final String refASCII = "" //
        + "0000  00 00 00 00 00 00 00 07  00 00 00 0d 48 65 6c 6c   ........ ....Hell\n"
        + "0010  6f 20 77 6f 72 6c 64 21  00                        o world! .";
    final String refEBDIC = "" //
        + "0000  01 00 00 00 00 00 00 07  00 00 00 0d c8 85 93 93   ........ ....Hell\n"
        + "0010  96 40 a6 96 99 93 84 5a  00                        o world! .";
    final FormatLabel formatLabelASCII = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelEBCDIC = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelASCII);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeStringVarying(data, offset);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabelASCII
        .getFormatLabelCharacter().getCharset());
    Assert.assertEquals("Incorrect ASCII output bytes[varying]", refASCII, hexView);

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelEBCDIC);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeStringVarying(data, offset);
    hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabelEBCDIC.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect EBCDIC output bytes[varying]", refEBDIC, hexView);
  }

  @Test
  public void testWriteStringConformantVarying() throws Exception {
    final String data = "Hello world!";
    final int ndrMaxLength = 0xde;
    final int offset = 0x07;
    final String ref = "" //
        + "0000  00 00 00 00 00 00 00 de  00 00 00 07 00 00 00 0d   .......\u00de ........\n"
        + "0010  48 65 6c 6c 6f 20 77 6f  72 6c 64 21 00            Hello wo rld!.";
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeStringConformantVarying(data, ndrMaxLength, offset);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabel.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect ASCII output bytes[conformant-varying]", ref, hexView);
  }

  @Test
  public void testWriteReference() throws Exception {
    final byte[] data0 = new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 };
    final byte[] data1 = new byte[] { (byte) 0x9a, (byte) 0xbc, (byte) 0xde, (byte) 0xf0, (byte) 0x17 };
    final byte[] data2 = new byte[] { (byte) 0xf0 };
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    final byte[] ref1 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
    final byte[] ref2 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x02 };
    final byte[] ref3 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
    final int offset = NDRConstants.FORMAT_LABEL_LENGTH;
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    Map<Integer, byte[]> referentMap = new HashMap<Integer, byte[]>();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeReference(referentMap, null);
    Assert.assertArrayEquals("Incorrect null reference", ref0,
        Arrays.copyOfRange(bos.toByteArray(), offset, offset + ref0.length));

    referentMap = new HashMap<Integer, byte[]>();
    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeReference(referentMap, data0);
    Assert.assertArrayEquals("Incorrect reference", ref1,
        Arrays.copyOfRange(bos.toByteArray(), offset, offset + ref1.length));
    Assert.assertEquals("Incorrect content of referentMap", 1, referentMap.size());
    Assert.assertArrayEquals("Incorrect content of referentMap", data0, referentMap.get(1));
    ndrWriter.writeReference(referentMap, data1);
    Assert.assertArrayEquals("Incorrect reference", ref2,
        Arrays.copyOfRange(bos.toByteArray(), offset, offset + ref2.length));
    Assert.assertEquals("Incorrect content of referentMap", 2, referentMap.size());
    Assert.assertArrayEquals("Incorrect content of referentMap", data1, referentMap.get(2));
    ndrWriter.writeReference(referentMap, data0);
    Assert.assertArrayEquals("Incorrect reference", ref3,
        Arrays.copyOfRange(bos.toByteArray(), offset, offset + ref3.length));
    Assert.assertEquals("Incorrect content of referentMap", 2, referentMap.size());
    Assert.assertArrayEquals("Incorrect content of referentMap", data0, referentMap.get(1));
    Assert.assertArrayEquals("Incorrect content of referentMap", data1, referentMap.get(2));

    referentMap.put(referentMap.size() + 2, data0);
    try {
      ndrWriter.writeReference(referentMap, data2);
      Assert.fail("Method must fail if referentMap contains an Integer key that is larger than the size of the map.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
}
