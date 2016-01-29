package nl.bransom.udptools;

import org.junit.Assert;
import org.junit.Test;

public class MACAddressTest {

  @Test
  public void testParse() throws Exception {
    final byte[] refBytes = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF, (byte) 0xCA, (byte) 0xFE };

    Assert.assertArrayEquals("Happy flow NOK", refBytes, MACAddress.parse("DE:AD:BE:EF:CA:FE").getBytes());

    Assert.assertNull("Null input NOK", MACAddress.parse(null));

    Assert.assertNull("Empty input NOK", MACAddress.parse(""));
  }

  @Test
  public void testToString() throws Exception {
    final byte[] refBytes = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF, (byte) 0xCA, (byte) 0xFE };

    Assert.assertEquals("Happy flow NOK", "DE:AD:BE:EF:CA:FE", new MACAddress(refBytes).toString().toUpperCase());
  }

}
