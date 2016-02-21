package nl.bransom.jdce.ndr;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link NDRWriter}
 * 
 * @author Rob
 */
public class FormatLabelTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    FormatLabel formatLabel = new FormatLabel();
    Assert.assertNull("Incorrect integer format label", formatLabel.getFormatLabelInteger());
    Assert.assertNull("Incorrect character format label", formatLabel.getFormatLabelCharacter());
    Assert.assertNull("Incorrect float format label", formatLabel.getFormatLabelFloat());
  }

  @Test
  public void testGetFormatLabelInteger() throws Exception {
    FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    Assert.assertEquals("Incorrect integer format label", FormatLabelInteger.BIG_ENDIAN,
        formatLabel.getFormatLabelInteger());

    formatLabel.set(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
    Assert.assertEquals("Incorrect integer format label", FormatLabelInteger.LITTLE_ENDIAN,
        formatLabel.getFormatLabelInteger());
  }

  @Test
  public void testGetFormatLabelCharacter() throws Exception {
    FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    Assert.assertEquals("Incorrect character format label", FormatLabelCharacter.ASCII,
        formatLabel.getFormatLabelCharacter());

    formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC, FormatLabelFloat.IEEE);
    Assert.assertEquals("Incorrect character format label", FormatLabelCharacter.EBCDIC,
        formatLabel.getFormatLabelCharacter());
  }

  @Test
  public void testGetFormatLabelFloat() throws Exception {
    FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    Assert.assertEquals("Incorrect float format label", FormatLabelFloat.IEEE, formatLabel.getFormatLabelFloat());

    try {
      formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.VAX);
      Assert.assertEquals("Incorrect float format label", FormatLabelFloat.VAX, formatLabel.getFormatLabelFloat());
    } catch (UnsupportedOperationException e) {
      // expected
    }

    try {
      formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.CRAY);
      Assert.assertEquals("Incorrect float format label", FormatLabelFloat.CRAY, formatLabel.getFormatLabelFloat());
    } catch (UnsupportedOperationException e) {
      // expected
    }

    try {
      formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IBM);
      Assert.assertEquals("Incorrect float format label", FormatLabelFloat.IBM, formatLabel.getFormatLabelFloat());
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testIsInitialized() throws Exception {
    FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect state", formatLabel.isInitialized());

    formatLabel.set(null, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect state", !formatLabel.isInitialized());
    formatLabel.set(FormatLabelInteger.LITTLE_ENDIAN, null, FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect state", !formatLabel.isInitialized());
    formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC, null);
    Assert.assertTrue("Incorrect state", !formatLabel.isInitialized());
    formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC, FormatLabelFloat.CRAY);
    Assert.assertTrue("Incorrect state", formatLabel.isInitialized());
  }

  @Test
  public void testPatchFormatLabel() throws Exception {
    FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    byte[] formatLabelBytes = new byte[NDRConstants.MIN_FORMAT_LABEL_LENGTH];
    formatLabel.patch(formatLabelBytes, 0);
    Assert.assertArrayEquals("Incorrect data;", formatLabelBytes,
        formatLabel.toByteArray(NDRConstants.MIN_FORMAT_LABEL_LENGTH));
    try {
      formatLabel = new FormatLabel();
      formatLabel.patch(formatLabelBytes, 0);
      Assert.fail("Method must fail if NDR Format Label is not specified.");
    } catch (IllegalStateException e) {
      // expected
    }
    try {
      formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
      formatLabel.patch(null, 0);
      Assert.fail("Method must fail on illegal input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      formatLabel.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
      formatLabel.patch(formatLabelBytes, 1);
      Assert.fail("Method must fail on illegal input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testEqualsHashCode() {
    FormatLabel formatLabel0 = new FormatLabel();
    FormatLabel formatLabel1 = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    FormatLabel formatLabel2 = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect result;", !formatLabel0.equals(null));
    Assert.assertTrue("Incorrect result;", formatLabel0.equals(formatLabel0));
    Assert.assertTrue("Incorrect result;", !formatLabel0.equals(formatLabel1));
    Assert.assertTrue("Incorrect result;", formatLabel1.equals(formatLabel2));
    Assert.assertTrue("Incorrect result;", formatLabel2.equals(formatLabel1));
    Assert.assertTrue("Incorrect result;", formatLabel0.hashCode() == formatLabel0.hashCode());
    Assert.assertTrue("Incorrect result;", formatLabel0.hashCode() != formatLabel1.hashCode());
    Assert.assertTrue("Incorrect result;", formatLabel1.hashCode() == formatLabel2.hashCode());

    formatLabel1.set(FormatLabelInteger.LITTLE_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect result;", !formatLabel1.equals(formatLabel2));
    Assert.assertTrue("Incorrect result;", formatLabel1.hashCode() != formatLabel2.hashCode());

    formatLabel1.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC, FormatLabelFloat.IEEE);
    Assert.assertTrue("Incorrect result;", !formatLabel1.equals(formatLabel2));
    Assert.assertTrue("Incorrect result;", formatLabel1.hashCode() != formatLabel2.hashCode());

    formatLabel1.set(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII, FormatLabelFloat.VAX);
    Assert.assertTrue("Incorrect result;", !formatLabel1.equals(formatLabel2));
    Assert.assertTrue("Incorrect result;", formatLabel1.hashCode() != formatLabel2.hashCode());
  }
}
