package nl.bransom.jdce.pdu;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class PDURejectionCodeTest {

  @Test
  public void testUniqueValues() {
    final Set<Integer> values = new HashSet<Integer>();
    for (PDURejectionCode pduRejectionCode : PDURejectionCode.values()) {
      Assert.assertTrue("Duplicate value;", !values.contains(pduRejectionCode.getValue()));
      values.add(pduRejectionCode.getValue());
    }
  }

  @Test
  public void testParse() {
    Assert.assertEquals("Incorrect value", PDURejectionCode.NCA_RPC_VERSION_MISMATCH,
        PDURejectionCode.parse(0x1c000008));
    Assert.assertEquals("Incorrect value", PDURejectionCode.NCA_PROTO_ERROR, PDURejectionCode.parse(0x1c01000b));
    Assert.assertEquals("Incorrect value", PDURejectionCode.NCA_INVALID_CRC, PDURejectionCode.parse(0x1c000020));
    Assert.assertEquals("Incorrect value", PDURejectionCode.NCA_S_BAD_ACTID, PDURejectionCode.parse(0x1c00000a));
    try {
      PDURejectionCode.parse(0);
      Assert.fail("Method must fail on invalid input.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

}
