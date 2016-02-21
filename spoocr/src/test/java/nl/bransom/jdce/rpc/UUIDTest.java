package nl.bransom.jdce.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.bransom.jdce.ndr.FormatLabel;
import nl.bransom.jdce.ndr.FormatLabelCharacter;
import nl.bransom.jdce.ndr.FormatLabelFloat;
import nl.bransom.jdce.ndr.FormatLabelInteger;
import nl.bransom.jdce.ndr.NDRReader;
import nl.bransom.jdce.ndr.NDRWriter;

import org.junit.Assert;
import org.junit.Test;

public class UUIDTest {

  @Test
  public void testConstructor() {
    final int timeLow = 0x2fac1234;
    final short timeMid = 0x31f8;
    final short timeHighAndVersion = 0x11b4;
    final byte clockSeqHighAndReserved = (byte) 0xa2;
    final byte clockSeqLow = (byte) 0x22;
    final byte[] node = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };

    UUID uuid = new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow, node);
    Assert.assertEquals("Incorrect field value;", 0x2fac1234, uuid.getTimeLow());
    Assert.assertEquals("Incorrect field value;", (short) 0x31f8, uuid.getTimeMid());
    Assert.assertEquals("Incorrect field value;", (short) 0x11b4, uuid.getTimeHighAndVersion());
    Assert.assertEquals("Incorrect field value;", (byte) 0xa2, uuid.getClockSeqHighAndReserved());
    Assert.assertEquals("Incorrect field value;", (byte) 0x22, uuid.getClockSeqLow());
    Assert.assertArrayEquals("Incorrect field value;", node, uuid.getNode());
    try {
      new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow, null);
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow,
          new byte[UUID.NODE_LENGTH - 1]);
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow,
          new byte[UUID.NODE_LENGTH + 1]);
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testNDRSerialize() throws IOException {
    final byte[] refBytes = { (byte) 0x2f, (byte) 0xac, (byte) 0x12, (byte) 0x34, (byte) 0x31, (byte) 0xf8,
        (byte) 0x11, (byte) 0xb4, (byte) 0xa2, (byte) 0x22, (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34,
        (byte) 0xc0, (byte) 0x03 };
    final int timeLow = 0x2fac1234;
    final short timeMid = 0x31f8;
    final short timeHighAndVersion = 0x11b4;
    final byte clockSeqHighAndReserved = (byte) 0xa2;
    final byte clockSeqLow = (byte) 0x22;
    final byte[] node = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    UUID uuid = new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow, node);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabel);
    uuid.ndrSerialize(ndrWriter);
    Assert.assertArrayEquals("Incorrect NDR data;", refBytes, bos.toByteArray());
  }

  @Test
  public void testNDRDeserialize() throws IOException {
    final byte[] refBytes = { (byte) 0x2f, (byte) 0xac, (byte) 0x12, (byte) 0x34, (byte) 0x31, (byte) 0xf8,
        (byte) 0x11, (byte) 0xb4, (byte) 0xa2, (byte) 0x22, (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34,
        (byte) 0xc0, (byte) 0x03 };
    final int timeLow = 0x2fac1234;
    final short timeMid = 0x31f8;
    final short timeHighAndVersion = 0x11b4;
    final byte clockSeqHighAndReserved = (byte) 0xa2;
    final byte clockSeqLow = (byte) 0x22;
    final byte[] node = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final FormatLabel formatLabel = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);

    NDRReader ndrReader = new NDRReader(new ByteArrayInputStream(refBytes));
    ndrReader.setFormatLabel(formatLabel);
    UUID uuid = ndrReader.read(UUID.class);
    Assert.assertEquals("Incorrect field value;", timeLow, uuid.getTimeLow());
    Assert.assertEquals("Incorrect field value;", timeMid, uuid.getTimeMid());
    Assert.assertEquals("Incorrect field value;", timeHighAndVersion, uuid.getTimeHighAndVersion());
    Assert.assertEquals("Incorrect field value;", clockSeqHighAndReserved, uuid.getClockSeqHighAndReserved());
    Assert.assertEquals("Incorrect field value;", clockSeqLow, uuid.getClockSeqLow());
    Assert.assertArrayEquals("Incorrect field value;", node, uuid.getNode());
  }

  @Test
  public void testParse() {
    final String ref = "2fac1234-31f8-11b4-a222-08002b34c003";

    UUID uuid = UUID.parse(ref);
    Assert.assertEquals("Incorrect value;", ref, uuid.toString());
    try {
      UUID.parse(null);
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("2fac1234--31f8-11b4-a222-08002b34c003");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("2fac1234-0000-31f8-11b4-a222-08002b34c003");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("-2fac1234-31f8-11b4-a222-08002b34c003");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("2fac1234-31f8-11b4-a222-08002b34c003-");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("2fac123-431f8-11b4-a222-08002b34c003");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      UUID.parse("2fac1234-31f8-11b4-a222-08002b34c0039");
      Assert.fail("Illegal argument not detected.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testToString() {
    final String ref = "2fac1234-31f8-11b4-a222-08002b34c003";
    final int timeLow = 0x2fac1234;
    final short timeMid = 0x31f8;
    final short timeHighAndVersion = 0x11b4;
    final byte clockSeqHighAndReserved = (byte) 0xa2;
    final byte clockSeqLow = (byte) 0x22;
    final byte[] node = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };

    UUID uuid = new UUID(timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow, node);
    Assert.assertEquals("Incorrect value;", ref, uuid.toString());
  }

  @Test
  public void testEquals() {
    final int timeLow0 = 0x2fac1234;
    final int timeLow1 = 0x2fad1234;
    final short timeMid0 = 0x31f8;
    final short timeMid1 = 0x41f8;
    final short timeHighAndVersion0 = 0x11b4;
    final short timeHighAndVersion1 = 0x12b4;
    final byte clockSeqHighAndReserved0 = (byte) 0xa2;
    final byte clockSeqHighAndReserved1 = (byte) 0xa3;
    final byte clockSeqLow0 = (byte) 0x22;
    final byte clockSeqLow1 = (byte) 0x32;
    final byte[] node0 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node1 = new byte[] { (byte) 0x09, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node2 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2c, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node3 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc1, (byte) 0x03 };
    final UUID ref = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);

    Assert.assertTrue("Incorrect value;", ref.equals(ref));
    Assert.assertTrue("Incorrect value;", !ref.equals(null));
    Assert.assertTrue("Incorrect value;", !ref.equals(new Object()));
    UUID other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertTrue("Incorrect value;", other.equals(ref));
    other = new UUID(timeLow1, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid1, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion1, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved1, clockSeqLow0, node0);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow1, node0);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node1);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node2);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node3);
    Assert.assertTrue("Incorrect value;", !other.equals(ref));
  }

  @Test
  public void testHashCode() {
    final int timeLow0 = 0x2fac1234;
    final int timeLow1 = 0x2fad1234;
    final short timeMid0 = 0x31f8;
    final short timeMid1 = 0x41f8;
    final short timeHighAndVersion0 = 0x11b4;
    final short timeHighAndVersion1 = 0x12b4;
    final byte clockSeqHighAndReserved0 = (byte) 0xa2;
    final byte clockSeqHighAndReserved1 = (byte) 0xa3;
    final byte clockSeqLow0 = (byte) 0x22;
    final byte clockSeqLow1 = (byte) 0x32;
    final byte[] node0 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node1 = new byte[] { (byte) 0x09, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node2 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2c, (byte) 0x34, (byte) 0xc0, (byte) 0x03 };
    final byte[] node3 = new byte[] { (byte) 0x08, (byte) 0x00, (byte) 0x2b, (byte) 0x34, (byte) 0xc1, (byte) 0x03 };
    final UUID ref = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);

    Assert.assertEquals("Incorrect value;", ref.hashCode(), ref.hashCode());
    UUID other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow1, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid1, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion1, clockSeqHighAndReserved0, clockSeqLow0, node0);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved1, clockSeqLow0, node0);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow1, node0);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node1);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node2);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
    other = new UUID(timeLow0, timeMid0, timeHighAndVersion0, clockSeqHighAndReserved0, clockSeqLow0, node3);
    Assert.assertNotEquals("Incorrect value;", ref.hashCode(), other.hashCode());
  }
}
