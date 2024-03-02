package ai.chat2db.server.web.api.controller.ai.DocParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CYY
 * @date March 11, 2023 3:23 pm
 * @description
 */
public class PdfParse extends AbstractParser {
    private static final int MAX_LENGTH = 200;

    @Override
    public List<String> parse(InputStream inputStream) throws IOException {
        // Open PDF file
        PDDocument document = PDDocument.load(inputStream);
        // Create a PDFTextStripper object
        PDFTextStripper stripper = new PDFTextStripper();
        // Get text content
        String text = stripper.getText(document);
        // Filter characters
        text = text.replaceAll("\\s", " ").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," ");
        String[] sentence = text.split("。");
        List<String> ans = new ArrayList<>();
        for (String s : sentence) {
            if (s.length() > MAX_LENGTH) {
                for (int index = 0; index < sentence.length; index = (index + 1) * MAX_LENGTH) {
                    String substring = s.substring(index, MAX_LENGTH);
                    if(substring.length() < 5) continue;
                    ans.add(substring);
                }
            } else {
                ans.add(s);
            }
        }
        // Close document
        document.close();
        return ans;
    }
}
