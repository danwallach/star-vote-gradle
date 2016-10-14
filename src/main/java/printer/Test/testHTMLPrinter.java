package printer.Test;


import printer.HTMLPrinter;
import votebox.AuditoriumParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arghyac on 6/25/14.
 */
public class testHTMLPrinter {

    public static void main(String args[])throws IOException{

        AuditoriumParams params = new AuditoriumParams("vb.conf");
        List<ArrayList<String>> outerList = new ArrayList<>();
        ArrayList<String> innerList = new ArrayList<>();

        innerList.add("images.png");
        innerList.add("bigst.png");
        outerList.add(innerList);

        HTMLPrinter.generateHTMLFile("printer/test.html", false, "/Users/Alex/rice/comp435/star-vote-gradle/src/main/java/printer/Test/imageHTMLP/", params,
                outerList);

        HTMLPrinter.generateHTMLFile("printer/test2.html", true, "/Users/Alex/rice/comp435/star-vote-gradle/src/main/java/printer/Test/imageHTMLP/", params,
                outerList);
    }

}
