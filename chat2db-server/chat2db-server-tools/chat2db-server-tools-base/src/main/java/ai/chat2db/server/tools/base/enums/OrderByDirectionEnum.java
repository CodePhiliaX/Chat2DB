package ai.chat2db.server.tools.base.enums;

/**
 * Enumeration of sorting directions
 *
 * @author Shi Yi
 */
public enum OrderByDirectionEnum implements BaseEnum<String> {

    /**
     * asc
     */
    ASC,
    /**
     * desc
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
