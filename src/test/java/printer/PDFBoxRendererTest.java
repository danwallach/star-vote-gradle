package printer;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Basic tests to figure out how PDFBox works for the new printer
 * Created by Alex Addy on 10/12/16.
 */

public class PDFBoxRendererTest {

  @Test
  public void pageHandling() throws Exception {
    File file = new File("src/test/java/printer/test.pdf");

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);

      PDFont font = PDType1Font.HELVETICA;
      PDFont fontBold = PDType1Font.HELVETICA_BOLD;
      PDFont ocrFont = PDType0Font.load(document, new File("src/test/java/printer/OCRAEXT.TTF"));

      PDPageContentStream contents = new PDPageContentStream(document, page);

      // Barcode image
      contents.beginMarkedContent(COSName.IMAGE);
      BufferedImage barcode = PrintImageUtils.getBarcode("1122334455");
      barcode = PrintImageUtils.trimImageHorizontally(barcode, true, Integer.MAX_VALUE);
      barcode = PrintImageUtils.trimImageHorizontally(barcode, false, Integer.MAX_VALUE);
      barcode = PrintImageUtils.trimImageVertically(barcode, true, Integer.MAX_VALUE);
      barcode = PrintImageUtils.trimImageVertically(barcode, false, Integer.MAX_VALUE);
      PDImageXObject ximage = JPEGFactory.createFromImage(document, barcode);
      contents.drawImage(ximage, 75, 705);
      contents.endMarkedContent();

      // OCR text
      contents.beginText();
      contents.setFont(ocrFont, 12);
      contents.newLineAtOffset(75, 690);
      contents.showText("12345");
      contents.endText();

      // Header text
      contents.beginText();
      contents.setFont(fontBold, 12);
      contents.newLineAtOffset(350, 745);
      contents.showText("Official Ballot");
      contents.endText();

      contents.beginText();
      contents.setFont(font, 12);
      contents.newLineAtOffset(350, 730);
      contents.showText("November 8, 2016 General Election");
      contents.endText();

      contents.beginText();
      contents.setFont(font, 12);
      contents.newLineAtOffset(350, 715);
      contents.showText("Harris County, TX - Precinct 101A");
      contents.endText();

      //
      // A line is 15 whatever-units...? personal choice
      //

      // Race separator image
      BufferedImage lineSeparator = new BufferedImage(200,3,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) lineSeparator.getGraphics();
      g.setColor(Color.BLACK);
      g.fillRect(0,0,200,3);

      // Get race PNG
      BufferedImage race = ImageIO.read(new File("src/main/java/test-ballots/testballot/media/L61/L61_printable_en.png"));

      // Trim race PNG
      race = PrintImageUtils.trimImageHorizontally(race, true, Integer.MAX_VALUE);
      race = PrintImageUtils.trimImageHorizontally(race, false, Integer.MAX_VALUE);
      race = PrintImageUtils.trimImageVertically(race, true, Integer.MAX_VALUE);
      race = PrintImageUtils.trimImageVertically(race, false, Integer.MAX_VALUE);

      // Get selection PNG
      BufferedImage selection = ImageIO.read(new File("src/main/java/test-ballots/testballot/media/B6/B6_printable_en.png"));

      // Crop off front of selection PNG
      selection = selection.getSubimage(400, 0, selection.getWidth() - 400, selection.getHeight());

      // Crop selection PNG
      selection = PrintImageUtils.trimImageHorizontally(selection, true, Integer.MAX_VALUE);
      selection = PrintImageUtils.trimImageHorizontally(selection, false, Integer.MAX_VALUE);
      selection = PrintImageUtils.trimImageVertically(selection, true, Integer.MAX_VALUE);
      selection = PrintImageUtils.trimImageVertically(selection, false, Integer.MAX_VALUE);

      // Left column
      for (int i = 0; i < 8; i++) {
        // Draw race
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, race), 75, 660 - (80 * i),
                new Double(race.getWidth() * 0.25).intValue(),
                new Double(race.getHeight() * 0.25).intValue());
        contents.endMarkedContent();

        // Draw selection
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, selection), 75, 625 - (80 * i),
                new Double(selection.getWidth() * 0.25).intValue(),
                new Double(selection.getHeight() * 0.25).intValue());
        contents.endMarkedContent();

        // Draw separator
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, lineSeparator), 75, 610 - (80 * i));
        contents.endMarkedContent();
      }

      // Right column
      for (int i = 0; i < 8; i++) {
        // Draw race
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, race), 350, 660 - (80 * i),
                new Double(race.getWidth() * 0.25).intValue(),
                new Double(race.getHeight() * 0.25).intValue());
        contents.endMarkedContent();

        // Draw selection
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, selection), 350, 625 - (80 * i),
                new Double(selection.getWidth() * 0.25).intValue(),
                new Double(selection.getHeight() * 0.25).intValue());
        contents.endMarkedContent();

        // Draw separator
        contents.beginMarkedContent(COSName.IMAGE);
        contents.drawImage(JPEGFactory.createFromImage(document, lineSeparator), 350, 610 - (80 * i));
        contents.endMarkedContent();
      }
//      for (int i = 0; i < 39; i++) {
//        contents.beginText();
//        contents.setFont(font, 12);
//        contents.newLineAtOffset(75, 665 - 15 * i);
//        contents.showText("###");
//        contents.endText();
//      }
//
//      for (int i = 0; i < 39; i++) {
//        contents.beginText();
//        contents.setFont(font, 12);
//        contents.newLineAtOffset(350, 665 - 15 * i);
//        contents.showText("###");
//        contents.endText();
//      }
      // QR image
      BufferedImage qrFile = ImageIO.read(
              new ByteArrayInputStream(QRCode.from("localhost:9000/".concat("12345")).stream().toByteArray()));

      PDPage instructionPage = new PDPage();
      document.addPage(instructionPage);
      PDPageContentStream instructionContents = new PDPageContentStream(document, instructionPage);

      //TODO: Make this not ugly

      instructionContents.beginText();
      instructionContents.setFont(fontBold, 12);
      instructionContents.newLineAtOffset(75, 630);
      instructionContents.showText("KEEP THIS BALLOT TRACKER");
      instructionContents.endText();

      instructionContents.beginText();
      instructionContents.setFont(font, 12);
      instructionContents.newLineAtOffset(75, 615);
      instructionContents.showText("To cast your ballot, place it in the ballot box.");
      instructionContents.endText();

      instructionContents.beginText();
      instructionContents.setFont(font, 12);
      instructionContents.newLineAtOffset(75, 600);
      instructionContents.showText("Keep this ballot tracker in case you want to check on your ballot after the polls close.");
      instructionContents.endText();

      instructionContents.beginText();
      instructionContents.setFont(font, 12);
      instructionContents.newLineAtOffset(75, 575);
      instructionContents.showText("You can check your vote by going to checkmyvote.com and entering your ballot ID");
      instructionContents.endText();

      instructionContents.beginText();
      instructionContents.setFont(font, 12);
      instructionContents.newLineAtOffset(75, 560);
      instructionContents.showText("or scanning the QR code printed above.");
      instructionContents.endText();

      instructionContents.beginMarkedContent(COSName.IMAGE);
      instructionContents.drawImage(JPEGFactory.createFromImage(document, qrFile), 75, 650);
      instructionContents.endMarkedContent();

      contents.close();
      instructionContents.close();

      document.save(file);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void barcodeRender() throws Exception {
    BufferedImage barcode = PrintImageUtils.getBarcode("12345");

    File barcodeFile = new File("src/test/java/printer/barcode.png");
    ImageIO.write(barcode, "png", barcodeFile);
  }
}