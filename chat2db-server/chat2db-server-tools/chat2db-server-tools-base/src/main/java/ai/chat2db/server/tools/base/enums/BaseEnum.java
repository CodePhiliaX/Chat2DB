package ai.chat2db.server.tools.base.enums;

/**
 * Basic enumeration
 *
 * Due to the limitations of Java enumeration inheritance, the enumeration base class can only be designed as an interface.
 * Please ensure that the subclass must be an enumeration type.
 *
 * @author Jiaju Zhuang
 **/
public interface BaseEnum<T> {

    /**
     * Returns the enumeration code.
     * It is generally recommended to directly return the name of the enumeration
     *
     * @return code
     */
    T getCode();

    /**
     * Returns the description of the enumeration.
     * Return the enumerated Chinese to facilitate front-end drop-down
     *
     * @return description
     */
    String getDescription();

}
