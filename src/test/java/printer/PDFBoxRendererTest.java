package printer;

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
import java.awt.image.BufferedImage;
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

    PDDocument document = new PDDocument();

    try {
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

      for (int i = 0; i < 39; i++) {
        contents.beginText();
        contents.setFont(font, 12);
        contents.newLineAtOffset(75, 665-15*i);
        contents.showText("###");
        contents.endText();
      }

      for (int i = 0; i < 39; i++) {
        contents.beginText();
        contents.setFont(font, 12);
        contents.newLineAtOffset(350, 665-15*i);
        contents.showText("###");
        contents.endText();
      }

      contents.close();

      document.save(file);
    }

    catch (Exception e) {
      e.printStackTrace();
      fail();
    }

    finally {
      document.close();
    }
  }

  @Test
  public void barcodeRender() throws Exception {
    BufferedImage barcode = PrintImageUtils.getBarcode("12345");

    File barcodeFile = new File("src/test/java/printer/barcode.png");
    ImageIO.write(barcode, "png", barcodeFile);
  }
}