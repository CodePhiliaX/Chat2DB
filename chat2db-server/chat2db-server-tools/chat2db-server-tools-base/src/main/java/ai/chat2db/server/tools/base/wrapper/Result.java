package ai.chat2db.server.tools.base.wrapper;

/**
 * @author qiuyuyu
 * @date 2022/01/20
 */
public interface Result<T> extends Traceable{
    /**
     * whether succeed
     *
     * @return
     * @mock true
     */
    boolean success();

    /**
     * Is the setting successful?
     *
     * @return
     */
    void success(boolean success);

    /**
     * error coding
     *
     * @return
     * @mock 000000
     */
    String errorCode();

    /**
     * Set error encoding
     *
     * @param errorCode
     */
    void errorCode(String errorCode);

    /**
     * error message
     *
     * @return
     */
    String errorMessage();


    /**
     * Set error message
     *
     * @param errorMessage
     */
    void errorMessage(String errorMessage);

    /**
     * error detail stack info
     */
    void errorDetail(String errorDetail);

    /**
     * error detail
     *
     * @return
     */
    String errorDetail();

    /**
     * solution link
     */
    void solutionLink(String solutionLink);

    /**
     * solution link
     *
     * @return
     */
    String solutionLink();
}
