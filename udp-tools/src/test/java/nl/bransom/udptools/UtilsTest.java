package nl.bransom.udptools;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilsTest {

  @BeforeClass
  public static void logStatus() {
    // Log logback status info.
    // @@@ StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());
  }

  @Test
  public void testBytesToHex() throws Exception {
    final String refHex = "DEADBEEFCAFE";
    final byte[] refBytes = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF, (byte) 0xCA, (byte) 0xFE };
    Assert.assertEquals("Happy flow NOK", refHex, Utils.bytesToHex(refBytes, 0, refBytes.length).toUpperCase());

    Assert.assertEquals("Happy flow NOK", "BEEF", Utils.bytesToHex(refBytes, 2, 2).toUpperCase());

    Assert.assertNull("Null input NOK", Utils.bytesToHex(null, 0, 0));

    Assert.assertEquals("Empty input NOK", "", Utils.bytesToHex(new byte[0], 0, 0));
  }

  @Test
  public void testHexToBytes() throws Exception {
    final String refHex = "DEADBEEFCAFE";
    final byte[] refBytes = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF, (byte) 0xCA, (byte) 0xFE };
    Assert.assertArrayEquals("Happy flow NOK", refBytes, Utils.hexToBytes(refHex));

    Assert.assertArrayEquals("Happy flow NOK", new byte[] { (byte) 0x05 }, Utils.hexToBytes("5"));

    Assert.assertNull("Null input NOK", Utils.hexToBytes(null));

    Assert.assertArrayEquals("Empty input NOK", new byte[0], Utils.hexToBytes(""));

    try {
      Utils.hexToBytes("XYZ");
      Assert.fail("Invalid input NOK");
    } catch (IllegalArgumentException e) {
      // ignore; as expected
    }
  }

}
