package nl.bransom.jdce.ndr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test class for {@link WireSharkViewer}
 * 
 * @author Rob
 */
public class WireSharkViewerTest {

  @Test
  public void testGetHexView() throws IOException {
    final int data0 = 0x12345678;
    final String data1 = "Hello world!";
    final String refASCII = "" //
        + "0000  00 00 00 00 12 34 56 78  00 00 00 00 00 00 00 0d   ....\u00124Vx ........\n"
        + "0010  48 65 6c 6c 6f 20 77 6f  72 6c 64 21 00            Hello wo rld!.";
    final String refEBCDIC = "" //
        + "0000  01 00 00 00 12 34 56 78  00 00 00 00 00 00 00 0d   ....\u0012\u00ee\u00cc ........\n"
        + "0010  c8 85 93 93 96 40 a6 96  99 93 84 5a 00            Hello wo rld!.";
    final FormatLabel formatLabelASCII = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelEBCDIC = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelASCII);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeInt(data0);
    ndrWriter.writeStringVarying(data1, 0);
    String hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabelASCII
        .getFormatLabelCharacter().getCharset());
    Assert.assertEquals("Incorrect hex view", refASCII, hexView);

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelEBCDIC);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeInt(data0);
    ndrWriter.writeStringVarying(data1, 0);
    hexView = WireSharkViewer.getHexView(bos.toByteArray(), 0, bos.size(), formatLabelEBCDIC.getFormatLabelCharacter()
        .getCharset());
    Assert.assertEquals("Incorrect hex view", refEBCDIC, hexView);
  }

  @Test
  public void testGetBitsView() throws IOException {
    final int data0 = 0x12345678;
    final String data1 = "Hello world!";
    final String refASCII = "" //
        + "0000  00000000 00000000 00000000 00000000 00010010 00110100 01010110 01111000   ....\u00124Vx\n"
        + "0008  00000000 00000000 00000000 00000000 00000000 00000000 00000000 00001101   ........\n"
        + "0010  01001000 01100101 01101100 01101100 01101111 00100000 01110111 01101111   Hello wo\n"
        + "0018  01110010 01101100 01100100 00100001 00000000                              rld!.";
    final String refEBCDIC = "" //
        + "0000  00000001 00000000 00000000 00000000 00010010 00110100 01010110 01111000   ....\u0012\u00ee\u00cc\n"
        + "0008  00000000 00000000 00000000 00000000 00000000 00000000 00000000 00001101   ........\n"
        + "0010  11001000 10000101 10010011 10010011 10010110 01000000 10100110 10010110   Hello wo\n"
        + "0018  10011001 10010011 10000100 01011010 00000000                              rld!.";
    final FormatLabel formatLabelASCII = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.ASCII,
        FormatLabelFloat.IEEE);
    final FormatLabel formatLabelEBCDIC = new FormatLabel(FormatLabelInteger.BIG_ENDIAN, FormatLabelCharacter.EBCDIC,
        FormatLabelFloat.IEEE);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    NDRWriter ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelASCII);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeInt(data0);
    ndrWriter.writeStringVarying(data1, 0);
    String bitsView = WireSharkViewer.getBitsView(bos.toByteArray(), 0, bos.size(), formatLabelASCII
        .getFormatLabelCharacter().getCharset());
    Assert.assertEquals("Incorrect bits view", refASCII, bitsView);

    bos = new ByteArrayOutputStream();
    ndrWriter = new NDRWriter(bos);
    ndrWriter.setFormatLabel(formatLabelEBCDIC);
    ndrWriter.writeFormatLabel(NDRConstants.FORMAT_LABEL_LENGTH);
    ndrWriter.writeInt(data0);
    ndrWriter.writeStringVarying(data1, 0);
    bitsView = WireSharkViewer.getBitsView(bos.toByteArray(), 0, bos.size(), formatLabelEBCDIC
        .getFormatLabelCharacter().getCharset());
    Assert.assertEquals("Incorrect bits view", refEBCDIC, bitsView);
  }
}
