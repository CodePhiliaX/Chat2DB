package ai.chat2db.server.tools.base.wrapper.param;

/**
 * 排序条件
 *
 * @author 是仪
 */
public interface OrderCondition {

    /**
     * 返回列的名字
     *
     * @return
     */
    OrderBy getOrderBy();
}
