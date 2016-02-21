package nl.bransom.jdce.ndr;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link FormatLabelInteger}
 * 
 * @author Rob
 */
public class FormatLabelIntegerTest {

  @Test
  public void testParseInput() {
    final byte[] ref0 = null;
    final byte[] ref1 = { (byte) 0x00, (byte) 0x00 };
    final byte[] ref2 = { (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    try {
      FormatLabelInteger.parse(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelInteger.parse(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelInteger.parse(ref2, 0);
      Assert.fail("Incorrect input handling");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testParse() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // BIG_ENDIAN (ASCII)
    final byte[] ref1 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // LITTLE_ENDIAN (ASCII)
    final byte[] ref2 = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // BIG_ENDIAN (EBCDIC)
    final byte[] ref3 = new byte[] { (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // LITTLE_ENDIAN (EBCDIC)

    Assert.assertEquals("Incorrect FormatLabel", FormatLabelInteger.BIG_ENDIAN, FormatLabelInteger.parse(ref0, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelInteger.LITTLE_ENDIAN, FormatLabelInteger.parse(ref1, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelInteger.BIG_ENDIAN, FormatLabelInteger.parse(ref2, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelInteger.LITTLE_ENDIAN, FormatLabelInteger.parse(ref3, 0));
  }

  @Test
  public void testPatchFormatLabel() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // BIG_ENDIAN (ASCII)
    final byte[] ref1 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // LITTLE_ENDIAN (ASCII)
    final byte[] ref2 = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // BIG_ENDIAN (EBCDIC)
    final byte[] ref3 = new byte[] { (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // LITTLE_ENDIAN (EBCDIC)

    byte[] formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelCharacter.ASCII.patchFormatLabel(formatLabel, 0);
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref0.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref0[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelCharacter.ASCII.patchFormatLabel(formatLabel, 0);
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref1.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref1[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelCharacter.EBCDIC.patchFormatLabel(formatLabel, 0);
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref2.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref2[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelCharacter.EBCDIC.patchFormatLabel(formatLabel, 0);
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref3.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref3[i], formatLabel[i]);
    }
  }
}
