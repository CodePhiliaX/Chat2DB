package ai.chat2db.server.tools.base.enums;

/**
 * 排序方向的枚举
 *
 * @author 是仪
 */
public enum OrderByDirectionEnum implements BaseEnum<String> {

    /**
     * 升序
     */
    ASC,
    /**
     * 降序
     */
    DESC;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.name();
    }
}
