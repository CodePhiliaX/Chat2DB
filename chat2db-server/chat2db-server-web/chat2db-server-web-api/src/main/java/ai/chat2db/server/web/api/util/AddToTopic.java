package ai.chat2db.server.web.api.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

import java.io.IOException;
import java.io.OutputStream;

/**
 * AddToTopic
 *
 * @author lzy
 **/
public class AddToTopic {

    public static void generateTOC(XWPFDocument document, OutputStream out) throws IOException {
        String findText = "目录哈哈";
        String replaceText = "";
        for (XWPFParagraph p : document.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                int pos = r.getTextPosition();
                String text = r.getText(pos);
                if (text != null && text.contains(findText)) {
                    text = text.replace(findText, replaceText);
                    r.setText(text, 0);
                    addField(p);
                    // addField(p, "TOC \\h");
                    break;
                }
            }
        }
        document.write(out);
    }

    private static void addField(XWPFParagraph paragraph) {
        CTSimpleField ctSimpleField = paragraph.getCTP().addNewFldSimple();
        ctSimpleField.setInstr("TOC \\o \"1-3\" \\h \\z \\u");
        ctSimpleField.setDirty(STOnOff.TRUE);
        ctSimpleField.addNewR().addNewT().setStringValue("<>");
    }

}
