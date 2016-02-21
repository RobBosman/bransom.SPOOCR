package nl.bransom.jdce.ndr;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link FormatLabelFloat}
 * 
 * @author Rob
 */
public class FormatLabelFloatTest {

  @Test
  public void testParseInput() {
    final byte[] ref0 = null;
    final byte[] ref1 = { (byte) 0x00, (byte) 0x00 };
    final byte[] ref2 = { (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00 };

    try {
      FormatLabelFloat.parse(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelFloat.IEEE.patchFormatLabel(ref0, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelFloat.parse(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      FormatLabelFloat.IEEE.patchFormatLabel(ref1, 0);
      Assert.fail("Incorrect input handling");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      FormatLabelFloat.parse(ref2, 0);
      Assert.fail("Incorrect input handling");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testParseFormatLabel() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // IEEE (BIG)
    final byte[] ref1 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // IEEE (LITTLE)
    final byte[] ref2 = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00 }; // VAX (BIG)
    final byte[] ref3 = new byte[] { (byte) 0x10, (byte) 0x01, (byte) 0x00, (byte) 0x00 }; // VAX (LITTLE)
    final byte[] ref4 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00 }; // CRAY (BIG)
    final byte[] ref5 = new byte[] { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00 }; // CRAY (LITTLE)
    final byte[] ref6 = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00 }; // IBM (BIG)
    final byte[] ref7 = new byte[] { (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00 }; // IBM (LITTLE)

    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.IEEE, FormatLabelFloat.parse(ref0, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.IEEE, FormatLabelFloat.parse(ref1, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.VAX, FormatLabelFloat.parse(ref2, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.VAX, FormatLabelFloat.parse(ref3, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.CRAY, FormatLabelFloat.parse(ref4, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.CRAY, FormatLabelFloat.parse(ref5, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.IBM, FormatLabelFloat.parse(ref6, 0));
    Assert.assertEquals("Incorrect FormatLabel", FormatLabelFloat.IBM, FormatLabelFloat.parse(ref7, 0));
  }

  @Test
  public void testPatchFormatLabel() {
    final byte[] ref0 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // IEEE (BIG)
    final byte[] ref1 = new byte[] { (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // IEEE (LITTLE)
    final byte[] ref2 = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00 }; // VAX (BIG)
    final byte[] ref3 = new byte[] { (byte) 0x10, (byte) 0x01, (byte) 0x00, (byte) 0x00 }; // VAX (LITTLE)
    final byte[] ref4 = new byte[] { (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00 }; // CRAY (BIG)
    final byte[] ref5 = new byte[] { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00 }; // CRAY (LITTLE)
    final byte[] ref6 = new byte[] { (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00 }; // IBM (BIG)
    final byte[] ref7 = new byte[] { (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00 }; // IBM (LITTLE)

    byte[] formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.IEEE.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref0.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref0[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.IEEE.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref1.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref1[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.VAX.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref2.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref2[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.VAX.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref3.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref3[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.CRAY.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref4.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref4[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.CRAY.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref5.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref5[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.BIG_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.IBM.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref6.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref6[i], formatLabel[i]);
    }

    formatLabel = new byte[NDRConstants.FORMAT_LABEL_LENGTH];
    FormatLabelInteger.LITTLE_ENDIAN.patchFormatLabel(formatLabel, 0);
    FormatLabelFloat.IBM.patchFormatLabel(formatLabel, 0);
    for (int i = 0; i < ref7.length; i++) {
      Assert.assertEquals("Incorrect FormatLabel[" + i + "]", ref7[i], formatLabel[i]);
    }
  }
}
