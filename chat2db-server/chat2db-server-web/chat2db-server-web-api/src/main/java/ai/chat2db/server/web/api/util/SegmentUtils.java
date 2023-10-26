package ai.chat2db.server.web.api.util;

import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SegmentUtils {

    /**
     * BaseAnalysis
     *
     * @param content
     */
    public static String baseAnalysis(String content) {
        Result result = BaseAnalysis.parse(delHTMLTag(content).replace("\n", "").replace(" ", "").replace("\t", ""));
        log.info("base analysis result:" + result);
        return convertResToString(result);
    }

    /**
     * ToAnalysis
     *
     * @param content
     */
    public static String toAnalysis(String content) {
        Result result = ToAnalysis.parse(content);
        log.info("to analysis result:" + result);
        return convertResToString(result);
    }

    /**
     * NlpAnalysis
     *
     * @param content
     */
    public static String nlpAnalysis(String content) {
        Result result = NlpAnalysis.parse(delHTMLTag(content).replace("\n", "").replace(" ", "").replace("\t", ""));
        log.info("nlp analysis result:" + result);
        return convertResToString(result);
    }

    /**
     * convert result to string
     *
     * @param result
     * @return
     */
    private static String convertResToString(Result result) {
        List<Term> terms = result.getTerms();
        StringBuilder sb = new StringBuilder();
        for (Term term : terms) {
            String name = term.getName();
            String nature = term.getNatureStr();
            if (nature.equals("nt") || nature.equals("nr") || nature.equals("n")) {
                sb.append(name).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * delete html tag
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        String regEx_html = "<[^>]+>";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");

        return htmlStr.trim();
    }
}
