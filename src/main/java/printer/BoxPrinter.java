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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The new PDF generator using Apache PDFBox
 */
public class BoxPrinter {
  public static boolean printCommittedBallot(List<PlaintextRaceSelection> ballot, String bid,
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
    String ballotPath = ballotFile.getAbsolutePath();
    String cleanFilePath = ballotPath.substring(0, ballotPath.lastIndexOf(".")) + fileChar;
    String path = cleanFilePath + "media" + fileChar;
    String altPath = cleanFilePath + "data" + fileChar + "media" + fileChar;
    String lineSeparatorFileNameAlt = altPath + "LineSeparator.png";
    String lineSeparatorFileName = path + "LineSeparator.png";
    String PDFFileName = cleanFilePath + "ballot.pdf";

    File file = new File(PDFFileName);

    try (PDDocument document = new PDDocument()) {
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

      int i = 0;
      ArrayList<ArrayList<PDImageXObject>> columnsToPrint = new ArrayList<>();
      ArrayList<PDImageXObject> currentColumn = new ArrayList<>();
      columnsToPrint.add(currentColumn);

      // Add image files to columns to render later
      for (String selection : choices) {
        String title = actualRaceNameImagePairs.get(i).getLabel();
        System.out.println(title + fileChar + title + "_printable_en.png");
        currentColumn.add(PDImageXObject.createFromFileByContent(
                new File("tmp/ballots/ballot/data/media/" + title + fileChar + title + "_printable_en.png"), document));
        currentColumn.add(PDImageXObject.createFromFileByContent(
                new File("tmp/ballots/ballot/data/media/" + selection + fileChar + selection + "_printable_en.png"), document));
        i++;

        if (i % 46 == 0) {
          currentColumn = new ArrayList<>();
          columnsToPrint.add(currentColumn);
        }
      }

      /* Render the columns */
      ArrayList<PDImageXObject> left = columnsToPrint.get(0);

      int leftJ = 0;
      for (PDImageXObject image : left) {
        try {
          contents.beginMarkedContent(COSName.IMAGE);
          contents.drawImage(image, 75, 665 - 15 * leftJ);
          contents.endMarkedContent();
          leftJ++;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (columnsToPrint.size() > 1) {
        ArrayList<PDImageXObject> right = columnsToPrint.get(1);
        int rightJ = 0;
        for (PDImageXObject image : right) {
          try {
            contents.beginMarkedContent(COSName.IMAGE);
            contents.drawImage(image, 350, 665 - 15 * rightJ);
            contents.endMarkedContent();
            rightJ++;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      contents.close();

      document.save(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return true;
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
