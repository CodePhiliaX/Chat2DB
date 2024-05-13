package ai.chat2db.server.tools.base.wrapper.param;

/**
 * Sorting conditions
 *
 * @author Shi Yi
 */
public interface OrderCondition {

    /**
     * Return column name
     *
     * @return
     */
    OrderBy getOrderBy();
}
