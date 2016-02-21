package nl.bransom.jdce.pdu;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class PDUTypeTest {

  @Test
  public void testUniqueValues() {
    final Set<Byte> values = new HashSet<Byte>();
    for (PDUType pduType : PDUType.values()) {
      Assert.assertTrue("Duplicate value;", !values.contains(pduType.getValue()));
      values.add(pduType.getValue());
    }
  }

  @Test
  public void testParse() {
    Assert.assertEquals("Incorrect value", PDUType.REQUEST, PDUType.parse((byte) 0));
    Assert.assertEquals("Incorrect value", PDUType.WORKING, PDUType.parse((byte) 4));
    Assert.assertEquals("Incorrect value", PDUType.NOCALL, PDUType.parse((byte) 5));
    Assert.assertEquals("Incorrect value", PDUType.CO_CANCEL, PDUType.parse((byte) 18));
    try {
      PDUType.parse((byte) 16);
      Assert.fail("Method must fail on invalid input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

}
