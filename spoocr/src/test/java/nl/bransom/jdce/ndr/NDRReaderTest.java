package nl.bransom.jdce.ndr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link NDRReader}
 * 
 * @author Rob
 */
public class NDRReaderTest {

  @Test
  public void testReadFormatLabel() throws Exception {
    final byte[] bytes = { (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00 };

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytes));
    FormatLabel formatLabel = ndrReader.getFormatLabel();
    Assert.assertNull("Unexpected pre-defined value", formatLabel);

    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    formatLabel = ndrReader.getFormatLabel();
    Assert.assertNotNull("Incorrect format label", formatLabel);
    Assert.assertEquals("Incorrect integer format label", FormatLabelInteger.LITTLE_ENDIAN,
        formatLabel.getFormatLabelInteger());
    Assert.assertEquals("Incorrect character format label", FormatLabelCharacter.EBCDIC,
        formatLabel.getFormatLabelCharacter());
    Assert.assertEquals("Incorrect float format label", FormatLabelFloat.CRAY, formatLabel.getFormatLabelFloat());

    formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
    ndrReader = new NDRReader(new ByteArrayInputStream(bytes));
    ndrReader.setFormatLabel(formatLabel);
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    formatLabel = ndrReader.getFormatLabel();
    Assert.assertEquals("Incorrect integer format label", FormatLabelInteger.LITTLE_ENDIAN,
        formatLabel.getFormatLabelInteger());
    Assert.assertEquals("Incorrect character format label", FormatLabelCharacter.EBCDIC,
        formatLabel.getFormatLabelCharacter());
    Assert.assertEquals("Incorrect float format label", FormatLabelFloat.CRAY, formatLabel.getFormatLabelFloat());
  }

  @Test
  public void testReadBoolean() throws Exception {
    final byte[] bytes = { (byte) 0x01, (byte) 0x00 };

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytes));
    Assert.assertEquals("Incorrect value", true, ndrReader.readBoolean());
    Assert.assertEquals("Incorrect value", false, ndrReader.readBoolean());
  }

  @Test
  public void testReadByte() throws Exception {
    final byte[] bytes = { (byte) 0xab, (byte) 0xcd, (byte) 0xef };

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytes));
    Assert.assertEquals("Incorrect value", (byte) 0xab, (byte) ndrReader.readByte());
    Assert.assertEquals("Incorrect value", (byte) 0xcd, (byte) ndrReader.readByte());
    Assert.assertEquals("Incorrect value", (byte) 0xef, (byte) ndrReader.readByte());
    try {
      ndrReader.readByte();
      Assert.fail("Method readByte() must fail when reading beyond EOF.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadShort() throws Exception {
    final byte[] bytesBE = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x24, (byte) 0xc1 };
    final byte[] bytesLE = { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc1, (byte) 0x24 };
    final short ref = (short) 0x24c1;

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytesBE));
    try {
      ndrReader.readShort();
      Assert.fail("Method must fail when called before specifying NDR Format Label.");
    } catch (IllegalStateException e) {
      // expected
    }
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", ref, ndrReader.readShort());

    ndrReader = new NDRReader(new ByteArrayInputStream(bytesLE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", ref, ndrReader.readShort());
  }

  @Test
  public void testReadInt() throws Exception {
    final byte[] bytesBE = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56,
        (byte) 0x78 };
    final byte[] bytesLE = { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78, (byte) 0x56, (byte) 0x34,
        (byte) 0x12 };
    final int ref = 0x12345678;

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytesBE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", ref, ndrReader.readInt());

    ndrReader = new NDRReader(new ByteArrayInputStream(bytesLE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", ref, ndrReader.readInt());
  }

  @Test
  public void testReadLong() throws Exception {
    final byte[] bytes = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xdd, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd,
        (byte) 0xef };
    final byte ref0 = (byte) 0xdd;
    final long ref1 = 0x1234567890abcdefL;

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytes));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Incorrect value", ref0, ndrReader.readByte());
    Assert.assertEquals("Incorrect value", ref1, ndrReader.readLong());
  }

  @Test
  public void testReadFloat() throws Exception {
    // IEEE: sign (1 bit) + exponent (8 bits) + mantissa (23 bits).
    final float refData = -821.699F;
    // sign = 1 = 0b1 = 0x01
    // exponent = 136 = 0b10001000 = 0x88
    // mantissa = 5074108 = 0b1001101 01101100 10111100 = 0x4d 0x6c 0xbc
    // value: (sign) (1 + mantissa * 2^-23) * 2^(exponent - 127) = -(1 + 0,6048808) * 2^9 = -821,699
    final byte[] bytesBE = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc4, (byte) 0x4d,
        (byte) 0x6c, (byte) 0xbc };
    final byte[] bytesLE = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xbc, (byte) 0x6c,
        (byte) 0x4d, (byte) 0xc4 };

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytesBE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Wrong Float value IEEE big endian", refData, ndrReader.readFloat(), Float.MIN_VALUE);

    ndrReader = new NDRReader(new ByteArrayInputStream(bytesLE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Wrong Float value IEEE little endian", refData, ndrReader.readFloat(), Float.MIN_VALUE);
  }

  @Test
  public void testReadDouble() throws Exception {
    // IEEE: sign (1 bit) + exponent (11 bits) + mantissa (52 bits).
    final double refData = -7184.00951;
    // sign = 1 = 0b1 = 0x01
    // exponent = 12 - 1023 = 1035 = 0b10000001011 = 0x40b
    // mantissa = 3395302362927868 = 0b1100000100000000001001101111001111110101001011111100 = 0xC10026F3F52FC
    // value: (sign) (1 + mantissa * 2^-52) * 2^(exponent - 1023) = -(1 + 0,75390857177734375) * 2^12 = -7184.00951
    final byte[] bytesBE = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0xc0, (byte) 0xbc, (byte) 0x10, (byte) 0x02, (byte) 0x6f, (byte) 0x3f,
        (byte) 0x52, (byte) 0xfc };
    final byte[] bytesLE = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0xfc, (byte) 0x52, (byte) 0x3f, (byte) 0x6f, (byte) 0x02, (byte) 0x10,
        (byte) 0xbc, (byte) 0xc0 };

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(bytesBE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Wrong Double value IEEE big endian", refData, ndrReader.readDouble(), Double.MIN_VALUE);

    ndrReader = new NDRReader(new ByteArrayInputStream(bytesLE));
    ndrReader.readFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    Assert.assertEquals("Wrong Double value IEEE little endian", refData, ndrReader.readDouble(), Double.MIN_VALUE);
  }

  @Test
  public void testReadBytesFixed() throws Exception {
    final byte[] dataBytes = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final byte[] refBytes0 = dataBytes;
    final byte[] refBytes1 = { (byte) 0x11, (byte) 0x22, (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final int offset0 = 0;
    final int offset1 = 2;

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    byte[] testBytes = new byte[dataBytes.length];
    int numBytesRead = ndrReader.readBytesFixed(testBytes, offset0, refBytes0.length);
    Assert.assertEquals("Incorrect number of bytes read", refBytes0.length, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes0, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    testBytes = new byte[5];
    testBytes[0] = 0x11;
    testBytes[1] = 0x22;
    numBytesRead = ndrReader.readBytesFixed(testBytes, offset1, refBytes0.length);
    Assert.assertEquals("Incorrect number of bytes read", refBytes0.length, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes1, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    testBytes = new byte[2];
    try {
      ndrReader.readBytesFixed(testBytes, offset0, refBytes0.length);
      Assert.fail("Method must fail if buffer size is insifficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    testBytes = new byte[3];
    try {
      ndrReader.readBytesFixed(testBytes, offset0 + 1, refBytes0.length);
      Assert.fail("Method must fail if buffer size is insifficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    testBytes = new byte[4];
    try {
      ndrReader.readBytesFixed(testBytes, offset0, refBytes0.length + 1);
      Assert.fail("Method must fail when reading beyond EOF.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadBytesConformant() throws Exception {
    final byte[] dataBytes = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x0a, (byte) 0xb0,
        (byte) 0x0c, (byte) 0x00, (byte) 0x00 };
    final byte[] refBytes = { (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    ndrReader.setFormatLabel(formatLabel);
    byte[] testBytes = new byte[refBytes.length];
    int numBytesRead = ndrReader.readBytesConformant(testBytes, 0);
    Assert.assertEquals("Incorrect number of bytes read", refBytes.length, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes.length];
    numBytesRead = ndrReader.readBytesConformant(testBytes, 0);
    Assert.assertEquals("Incorrect number of bytes read", refBytes.length, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes.length];
    try {
      ndrReader.readBytesConformant(testBytes, 1);
      Assert.fail("Method must fail if buffer size is insifficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes.length - 1];
    try {
      ndrReader.readBytesConformant(testBytes, 0);
      Assert.fail("Method must fail if buffer size is insifficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes.length];
    try {
      ndrReader.readInt();
      ndrReader.readBytesConformant(testBytes, 0);
      Assert.fail("Method must fail when reading beyond EOF.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testReadBytesVarying() throws Exception {
    final int ndrOffset = 0x04;
    final int ndrActualLength = 0x03;
    final byte[] dataBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x03, (byte) 0x0a, (byte) 0xb0, (byte) 0x0c, (byte) 0x00, (byte) 0x00 };
    final byte[] dataBytes1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x03, (byte) 0x0a, (byte) 0xb0 };
    final int storeOffset = 0x03;
    final byte[] refBytes0 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0x07, (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final byte[] refBytes1 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0x07, (byte) 0x0a, (byte) 0xb0, (byte) 0x0c };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes0));
    ndrReader.setFormatLabel(formatLabel);
    byte[] testBytes = new byte[refBytes0.length];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    int numBytesRead = ndrReader.readBytesVarying(testBytes, storeOffset);
    Assert.assertEquals("Incorrect number of bytes read", ndrOffset + ndrActualLength, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes0, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes0));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes0.length - 1];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    try {
      ndrReader.readBytesVarying(testBytes, storeOffset);
      Assert.fail("Method must fail if buffer size is insifficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes1));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[refBytes1.length];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    try {
      ndrReader.readBytesVarying(testBytes, storeOffset);
      Assert.fail("Method must fail when reading beyond EOF.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadBytesConformantVarying() throws Exception {
    final int ndrMaxLength = 0x07;
    final int ndrOffset = 0x04;
    final int ndrActualLength = 0x03;
    final byte[] refBytes0 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0xa0, (byte) 0xb0, (byte) 0xc0 };
    final byte[] refBytes1 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0xa0, (byte) 0xb0, (byte) 0xc0, (byte) 0x0a, (byte) 0x0b };
    final int storeOffset = 0x02;
    final byte[] dataBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0xa0, (byte) 0xb0,
        (byte) 0xc0 };
    final byte[] dataBytes1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0xa0, (byte) 0xb0,
        (byte) 0xc0 };
    final byte[] dataBytes2 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0xa0, (byte) 0xb0,
        (byte) 0xc0 };
    final byte[] dataBytes3 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0xa0, (byte) 0xb0 };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes0));
    ndrReader.setFormatLabel(formatLabel);
    byte[] testBytes = new byte[storeOffset + ndrMaxLength];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    int numBytesRead = ndrReader.readBytesConformantVarying(testBytes, storeOffset);
    Assert.assertEquals("Incorrect number of bytes read", ndrOffset + ndrActualLength, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes0, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes1));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[storeOffset + ndrMaxLength + 2];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    numBytesRead = ndrReader.readBytesConformantVarying(testBytes, storeOffset);
    Assert.assertEquals("Incorrect number of bytes read", ndrOffset + ndrActualLength, numBytesRead);
    Assert.assertArrayEquals("Incorrect bytes", refBytes1, testBytes);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes2));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[storeOffset + ndrMaxLength];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    try {
      ndrReader.readBytesConformantVarying(testBytes, storeOffset);
      Assert.fail("Method must fail when reading conflicting NDR data.");
    } catch (IOException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes2));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[storeOffset + ndrMaxLength];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    try {
      ndrReader.readBytesConformantVarying(testBytes, storeOffset + 1);
      Assert.fail("Method must fail when buffer size is insufficient.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes3));
    ndrReader.setFormatLabel(formatLabel);
    testBytes = new byte[storeOffset + ndrMaxLength];
    for (int i = 0; i < testBytes.length; i++) {
      testBytes[i] = (byte) (i + 1);
    }
    try {
      ndrReader.readBytesConformantVarying(testBytes, storeOffset);
      Assert.fail("Method must fail when reading beyond EOF.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadStringVarying() throws Exception {
    final String ref = "Hello world!";
    final byte[] asciiBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x0d, (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20,
        (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21, (byte) 0x00 };
    final byte[] asciiBytes1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x0d, (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20,
        (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21, (byte) 0x00 };
    final byte[] asciiBytes2 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x0c, (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20,
        (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21 };
    final byte[] ebcdicBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x0d, (byte) 0xc8, (byte) 0x85, (byte) 0x93, (byte) 0x93, (byte) 0x96, (byte) 0x40,
        (byte) 0xa6, (byte) 0x96, (byte) 0x99, (byte) 0x93, (byte) 0x84, (byte) 0x5a, (byte) 0x00 };
    final FormatLabel formatLabel0 = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabel1 = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes0));
    ndrReader.setFormatLabel(formatLabel0);
    Assert.assertEquals("Incorrect text", ref, ndrReader.readStringVarying());

    ndrReader = new NDRReader(new ByteArrayInputStream(ebcdicBytes0));
    ndrReader.setFormatLabel(formatLabel1);
    Assert.assertEquals("Incorrect text", ref, ndrReader.readStringVarying());

    ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes0));
    try {
      ndrReader.readStringVarying();
      Assert.fail("Method must fail when NDR Format Label is unspecified.");
    } catch (IllegalStateException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes1));
    ndrReader.setFormatLabel(formatLabel0);
    try {
      ndrReader.readStringVarying();
      Assert.fail("Detect non-zero NDR string offsets.");
    } catch (UnsupportedOperationException e) {
      // expected
    }

    ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes2));
    ndrReader.setFormatLabel(formatLabel0);
    try {
      ndrReader.readStringVarying();
      Assert.fail("Method must fail if the string terminator is missing.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadStringConformantVarying() throws Exception {
    final String ref = "Hello world!";
    final byte[] asciiBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0d, (byte) 0x48, (byte) 0x65,
        (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20, (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c,
        (byte) 0x64, (byte) 0x21, (byte) 0x00 };
    final byte[] asciiBytes1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0d, (byte) 0x48, (byte) 0x65,
        (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x20, (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c,
        (byte) 0x64, (byte) 0x21, (byte) 0x00 };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes0));
    ndrReader.setFormatLabel(formatLabel);
    Assert.assertEquals("Incorrect text", ref, ndrReader.readStringConformantVarying());

    ndrReader = new NDRReader(new ByteArrayInputStream(asciiBytes1));
    ndrReader.setFormatLabel(formatLabel);
    try {
      ndrReader.readStringConformantVarying();
      Assert.fail("Method must fail if the NDR data contains conflicting values.");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testReadReference() throws Exception {
    final byte[] dataBytes0 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    final byte[] dataBytes1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
    final byte[] dataBytes2 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x0d, (byte) 0x0e,
        (byte) 0x0f };
    final byte[] refBytes0 = {};
    final byte[] refBytes1 = { (byte) 0x0a, (byte) 0x0b, (byte) 0x0c };
    final byte[] refBytes2 = { (byte) 0x0d, (byte) 0x0e, (byte) 0x0f };
    final Map<Integer, byte[]> referentMap = new HashMap<Integer, byte[]>();
    referentMap.put(1, refBytes1);
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes0));
    ndrReader.setFormatLabel(formatLabel);
    byte[] testReferent = ndrReader.readReference(referentMap, refBytes0.length);
    Assert.assertArrayEquals("Incorrect referent", refBytes0, testReferent);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes1));
    ndrReader.setFormatLabel(formatLabel);
    testReferent = ndrReader.readReference(referentMap, refBytes1.length);
    Assert.assertArrayEquals("Incorrect referent", refBytes1, testReferent);

    ndrReader = new NDRReader(new ByteArrayInputStream(dataBytes2));
    ndrReader.setFormatLabel(formatLabel);
    testReferent = ndrReader.readReference(referentMap, refBytes2.length);
    Assert.assertArrayEquals("Incorrect referent", refBytes2, testReferent);
  }
}
