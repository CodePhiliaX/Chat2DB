package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * Delete mark enumeration
 * <p>
 * In order to be compatible with unique primary key + tombstone.
 * Use DeletedId to mark whether the current data is deleted.
 * If it is 0, it means it has not been deleted.
 * Anything else means it has been deleted.
 * When deleting, execute the statement: update set deleted_id = di where condition = condition;
 *
 * @author Shi Yi
 */
@Getter
public enum DeletedIdEnum implements BaseEnum<Long> {

    /**
     * Not deleted
     */
    NOT_DELETED(0L, "Not deleted"),

    ;

    final Long code;
    final String description;

    DeletedIdEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Determine whether the current data has been logically deleted
     *
     * @param deletedId deleted_id in table
     * @return Has it been deleted?
     */
    public static boolean isDeleted(Long deletedId) {
        return !NOT_DELETED.getCode().equals(deletedId);
    }
}
