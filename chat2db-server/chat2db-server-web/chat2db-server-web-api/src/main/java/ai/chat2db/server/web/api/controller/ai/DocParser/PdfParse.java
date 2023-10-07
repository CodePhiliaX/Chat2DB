package ai.chat2db.server.web.api.controller.ai.DocParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CYY
 * @date 2023年03月11日 下午3:23
 * @description
 */
public class PdfParse extends AbstractParser {
    private static final int MAX_LENGTH = 200;

    @Override
    public List<String> parse(InputStream inputStream) throws IOException {
        // 打开 PDF 文件
        PDDocument document = PDDocument.load(inputStream);
        // 创建 PDFTextStripper 对象
        PDFTextStripper stripper = new PDFTextStripper();
        // 获取文本内容
        String text = stripper.getText(document);
        //过滤字符
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
        // 关闭文档
        document.close();
        return ans;
    }
}
