package nl.bransom.jdce.ndr;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link FormatLabelCharacter}
 * 
 * @author Rob
 */
public class FormatLabelCharacterTest {

  @Test
  public void testParseInput() {
    final byte[] ref0 = null;
    final byte[] ref1 = { (byte) 0x00, (byte) 0x00 };
    final byte[] ref2 = { (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    try {
      FormatLabelCharacter.parse(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelCharacter.ASCII.patchFormatLabel(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelCharacter.parse(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelCharacter.ASCII.patchFormatLabel(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelCharacter.parse(ref2, 0);
      Assert.fail("Incorrect input handling");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testParseFormatLabel() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // ASCII (BIG)
    final byte[] ref1 = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // EBCDIC (BIG)
    final byte[] ref2 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // ASCII (LITTLE)
    final byte[] ref3 = new byte[] { (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // EBCDIC (LITTLE)

    Assert.assertEquals("Incorrect FormatLabel", FormatLabelCharacter.ASCII, FormatLabelCharacter.parse(ref0, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelCharacter.EBCDIC, FormatLabelCharacter.parse(ref1, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelCharacter.ASCII, FormatLabelCharacter.parse(ref2, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelCharacter.EBCDIC, FormatLabelCharacter.parse(ref3, 0));
  }

  @Test
  public void testPatchFormatLabel() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // ASCII (BIG)
    final byte[] ref1 = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // EBCDIC (BIG)
    final byte[] ref2 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // ASCII (LITTLE)
    final byte[] ref3 = new byte[] { (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // EBCDIC (LITTLE)

    byte[] formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelCharacter.ASCII.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref0.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref0[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelCharacter.EBCDIC.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref1.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref1[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelCharacter.ASCII.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref2.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref2[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelCharacter.EBCDIC.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref3.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref3[i], formatLabel[i]);
    }
  }
}
