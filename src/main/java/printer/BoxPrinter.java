package printer;

import crypto.PlaintextRaceSelection;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import tap.BallotImageHelper;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The new PDF generator using Apache PDFBox.
 */
public class BoxPrinter {
  public static PDDocument printCommittedBallot(List<PlaintextRaceSelection> ballot, String bid,
                                      List<List<String>> races, File ballotFile) throws IOException {

    final Map<String, Image> choiceToImage = BallotImageHelper.loadImagesForVVPAT(ballotFile);
    final List<String> choices = new ArrayList<>();
    final ArrayList<ChoicePair> reformedBallot = reformBallot(ballot, races);
    final ArrayList<RaceTitlePair> actualRaceNameImagePairs = getRaceNameImagePairs(choiceToImage);

    /* This for loop uses the corrected ballot, which accounts for No Selections. */
    for (ChoicePair currentItem : reformedBallot) {
      if (currentItem.getStatus() == 1) {
        choices.add(currentItem.getLabel());
      }
    }

    /* String paths for accessing ballot PNG files. */
    String fileChar = System.getProperty("file.separator");

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
      BufferedImage barcode = PrintImageUtils.getBarcode(bid);
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
      contents.showText(bid);
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

      int i = 0;
      ArrayList<ArrayList<PDImageXObject>> columnsToPrint = new ArrayList<>();
      ArrayList<PDImageXObject> currentColumn = new ArrayList<>();
      columnsToPrint.add(currentColumn);

      // Race separator image
      BufferedImage lineSeparator = new BufferedImage(200,3,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) lineSeparator.getGraphics();
      g.setColor(Color.BLACK);
      g.fillRect(0,0,200,3);

      // Add image files to columns to render later
      for (String selection : choices) {
        // Get race title
        String title = actualRaceNameImagePairs.get(i).getLabel();

        // Load images into BufferedImage
        BufferedImage racePic = ImageIO.read(
                new File("tmp/ballots/ballot/data/media/" + title + fileChar + title + "_printable_en.png"));
        BufferedImage selectPic = ImageIO.read(
                new File("tmp/ballots/ballot/data/media/" + selection + fileChar + selection + "_printable_en.png"));

        // Trim race PNG
        racePic = PrintImageUtils.trimImageHorizontally(racePic, true, Integer.MAX_VALUE);
        racePic = PrintImageUtils.trimImageHorizontally(racePic, false, Integer.MAX_VALUE);
        racePic = PrintImageUtils.trimImageVertically(racePic, true, Integer.MAX_VALUE);
        racePic = PrintImageUtils.trimImageVertically(racePic, false, Integer.MAX_VALUE);

        // Crop off front of selection PNG (that weird number in front of the printable IMG
        selectPic = selectPic.getSubimage(400, 0, selectPic.getWidth() - 400, selectPic.getHeight());

        // Crop selection PNG
        selectPic = PrintImageUtils.trimImageHorizontally(selectPic, true, Integer.MAX_VALUE);
        selectPic = PrintImageUtils.trimImageHorizontally(selectPic, false, Integer.MAX_VALUE);
        selectPic = PrintImageUtils.trimImageVertically(selectPic, true, Integer.MAX_VALUE);
        selectPic = PrintImageUtils.trimImageVertically(selectPic, false, Integer.MAX_VALUE);

        // Add PDImageXObject images to the column
        currentColumn.add(JPEGFactory.createFromImage(document, racePic));
        currentColumn.add(JPEGFactory.createFromImage(document, selectPic));
        currentColumn.add(JPEGFactory.createFromImage(document, lineSeparator));

        i++;

        if (i % 24 == 0) {
          currentColumn = new ArrayList<>();
          columnsToPrint.add(currentColumn);
        }
      }

      /* Render the columns */
      ArrayList<PDImageXObject> left = columnsToPrint.get(0);

      int leftJ = 0;
      while (left.size() >= 3) {
        PDImageXObject race = left.get(0);
        PDImageXObject selection = left.get(1);
        PDImageXObject sep = left.get(2);

        try {
          // Draw race
          contents.beginMarkedContent(COSName.IMAGE);
          contents.drawImage(race, 75, 660 - (80 * leftJ),
                  new Double(race.getWidth() * 0.25).intValue(),
                  new Double(race.getHeight() * 0.25).intValue());
          contents.endMarkedContent();

          // Draw selection
          contents.beginMarkedContent(COSName.IMAGE);
          contents.drawImage(selection, 75, 625 - (80 * leftJ),
                  new Double(selection.getWidth() * 0.25).intValue(),
                  new Double(selection.getHeight() * 0.25).intValue());
          contents.endMarkedContent();

          // Draw separator
          contents.beginMarkedContent(COSName.IMAGE);
          contents.drawImage(sep, 75, 610 - (80 * leftJ));
          contents.endMarkedContent();

          leftJ++;
        } catch (IOException e) {
          e.printStackTrace();
        }

        // Remove the printed images from the queue
        left.remove(0);
        left.remove(0);
        left.remove(0);
      }

      if (columnsToPrint.size() > 1) {
        ArrayList<PDImageXObject> right = columnsToPrint.get(1);
        int rightJ = 0;

        while (right.size() >= 3) {
          PDImageXObject race = right.get(0);
          PDImageXObject selection = right.get(1);
          PDImageXObject sep = right.get(2);

          try {
            // Draw race
            contents.beginMarkedContent(COSName.IMAGE);
            contents.drawImage(race, 350, 660 - (80 * rightJ),
                    new Double(race.getWidth() * 0.25).intValue(),
                    new Double(race.getHeight() * 0.25).intValue());
            contents.endMarkedContent();

            // Draw selection
            contents.beginMarkedContent(COSName.IMAGE);
            contents.drawImage(selection, 350, 625 - (80 * rightJ),
                    new Double(selection.getWidth() * 0.25).intValue(),
                    new Double(selection.getHeight() * 0.25).intValue());
            contents.endMarkedContent();

            // Draw separator
            contents.beginMarkedContent(COSName.IMAGE);
            contents.drawImage(sep, 350, 610 - (80 * rightJ));
            contents.endMarkedContent();

            rightJ++;
          } catch (IOException e) {
            e.printStackTrace();
          }

          // Remove the printed images from the queue
          right.remove(0);
          right.remove(1);
          right.remove(2);
        }
      }

      // QR image
      BufferedImage qrFile = ImageIO.read(
              new ByteArrayInputStream(QRCode.from("localhost:9000/".concat(bid)).stream().toByteArray()));

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
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document;
  }

  /**
   * Converts the raw ballot into an ArrayList of ChoicePairs for later printing
   *
   * @param rawBallot     the vote decisions as a ListExpression
   * @return              the vote decisions as an ArrayList of ChoicePairs
   */
  static private ArrayList<ChoicePair> reformBallot(List<PlaintextRaceSelection> rawBallot, List<List<String>> races) {

    ArrayList<ChoicePair> reformedBallot = new ArrayList<>();

        /* Cycle through each race in the List of all races */
    for (List<String> currentRaceCandidateList : races) {

      Boolean existingSelectedOption = false;

                /* Cycle through each PlaintextRaceSelection in the raw ballot until we find all of our candidate IDs */
      for (PlaintextRaceSelection currentRace : rawBallot) {

        System.out.println("Current RaceSelection Title: " + currentRace.getTitle());
        System.out.println("Current Initial Candidate: " + currentRaceCandidateList.get(0));

                    /* If the first candidate ID matches the title of the map */
        if (currentRace.getTitle().equals(currentRaceCandidateList.get(0))) {

                        /* Now cycle through each of the candidates */
          for (String currentCandidate : currentRaceCandidateList) {

            System.out.println("Current candidate from RaceSelection: " + currentCandidate);
                            /* Extract the vote value for this candidate */
            Integer currentVoteValue = currentRace.getRaceSelectionsMap().get(currentCandidate);

                            /* Write-in if they have a carat */
            String candidate = currentCandidate.contains("^") ? currentCandidate.substring(currentCandidate.indexOf("^")+1) :
                    currentCandidate;

                            /* Keep track if there has been at least one valid selection */
            existingSelectedOption = (currentVoteValue == 1) || existingSelectedOption;

                            /* Add this ChoicePair to the reformedBallot */
            reformedBallot.add(new ChoicePair(candidate, currentVoteValue));
          }

                        /* If there is a valid option selected, do nothing. Otherwise, add the "No Selection" option and select that one. */
          if (!existingSelectedOption)
            reformedBallot.add(new ChoicePair(currentRaceCandidateList.get(0), 1));
        }
      }
    }

    return reformedBallot;
  }

  /**
   * Converts the mapping of (Race Names:Images) to a sorted ArrayList of RaceTitlePairs
   *
   * @param imageMap      a mapping of images of names to text names for all races
   * @return              a sorted ArrayList of RaceTitlePairs
   */
  static private ArrayList<RaceTitlePair> getRaceNameImagePairs(Map<String, Image> imageMap) {

    /* This ArrayList will hold all the numeric IDs that correspond to race labels. */
    ArrayList<Integer> raceNumericIDs = new ArrayList<>();

    /* Go through the image mapping and whenever a UID starts with "L", add the following number to raceNumericIDs.
       If a race label's image has UID L50, then this ArrayList will hold 50 to represent that race label.
    */
    for (String UID:imageMap.keySet())
      if (UID.contains("L"))
        raceNumericIDs.add(new Integer(UID.substring(1)));

    /* Now sort them by number */
    ArrayList<RaceTitlePair> sortedRaceNameImagePairs = new ArrayList<>();
    Integer[] sortedRaceNumIDArray = raceNumericIDs.toArray(new Integer[raceNumericIDs.size()]);
    Arrays.sort(sortedRaceNumIDArray);

    /* Go through each integer in the sorted array */
    for (Integer ID:sortedRaceNumIDArray) {
      /* Add the "L" back */
      String currentKey = "L" + ID.toString();

      /* Add them back to the sorted ArrayList as RaceTitlePairs (mapping of keys to images */
      sortedRaceNameImagePairs.add(new RaceTitlePair(currentKey, imageMap.get(currentKey)));
    }

    /* Returne the ArrayList */
    return sortedRaceNameImagePairs;
  }
}
