package printer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

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

      PDFont font = PDType1Font.HELVETICA_BOLD;

      PDPageContentStream contents = new PDPageContentStream(document, page);

      for (int i = 0; i < 66; i++) {
        contents.beginText();
        contents.setFont(font, 12);
        contents.newLineAtOffset(75, 725-10*i);
        contents.showText("###");
        contents.endText();
      }

      for (int i = 0; i < 66; i++) {
        contents.beginText();
        contents.setFont(font, 12);
        contents.newLineAtOffset(350, 725-10*i);
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

}
