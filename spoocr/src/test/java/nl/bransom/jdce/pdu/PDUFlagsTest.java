package nl.bransom.jdce.pdu;

import org.junit.Assert;
import org.junit.Test;

public class PDUFlagsTest {

  @Test
  public void testSetUnset() {
    PDUFlags pduFlags = new PDUFlags();
    Assert.assertTrue("Incorrect value;", !pduFlags.isSet(PDUFlag1.LASTFRAG));
    Assert.assertTrue("Incorrect value;", !pduFlags.isSet(PDUFlag1.IDEMPOTENT));
    Assert.assertTrue("Incorrect value;", !pduFlags.isSet(PDUFlag2.CANCEL_PENDING));
    pduFlags.set(PDUFlag1.LASTFRAG);
    Assert.assertTrue("Incorrect value;", pduFlags.isSet(PDUFlag1.LASTFRAG));
    pduFlags.unset(PDUFlag1.LASTFRAG);
    Assert.assertTrue("Incorrect value;", !pduFlags.isSet(PDUFlag1.LASTFRAG));
    pduFlags.set(PDUFlag2.CANCEL_PENDING);
    Assert.assertTrue("Incorrect value;", pduFlags.isSet(PDUFlag2.CANCEL_PENDING));
    pduFlags.unset(PDUFlag2.CANCEL_PENDING);
    Assert.assertTrue("Incorrect value;", !pduFlags.isSet(PDUFlag2.CANCEL_PENDING));
  }

  @Test
  public void testEqualsHashCode() {
    PDUFlags pduFlags0 = new PDUFlags();
    pduFlags0.set(PDUFlag1.LASTFRAG);
    pduFlags0.set(PDUFlag1.NOFACK);
    PDUFlags pduFlags1 = new PDUFlags();
    pduFlags1.set(PDUFlag1.NOFACK);
    pduFlags1.set(PDUFlag2.CANCEL_PENDING);
    Assert.assertTrue("Incorrect value;", !pduFlags0.equals(null));
    Assert.assertTrue("Incorrect value;", pduFlags0.equals(pduFlags0));
    Assert.assertTrue("Incorrect value;", !pduFlags0.equals(pduFlags1));
    Assert.assertTrue("Incorrect value;", pduFlags0.hashCode() == pduFlags0.hashCode());
    Assert.assertTrue("Incorrect value;", pduFlags0.hashCode() != pduFlags1.hashCode());

    pduFlags1.unset(PDUFlag1.NOFACK);
    Assert.assertTrue("Incorrect value;", !pduFlags0.equals(pduFlags1));
    Assert.assertTrue("Incorrect value;", pduFlags0.hashCode() != pduFlags1.hashCode());
  }

}
