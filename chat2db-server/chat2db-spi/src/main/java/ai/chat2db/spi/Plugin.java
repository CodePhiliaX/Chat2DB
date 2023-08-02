
package ai.chat2db.spi;

import ai.chat2db.spi.config.DBConfig;

/**
 * @author jipengfei
 * @version : Plugin.java
 */
public interface Plugin {

    /**
     * Get DB configuration information.
     *
     * @return
     */
    DBConfig getDBConfig();

    /**
     * Query db metadata information.
     *
     * @return
     */
    MetaData getMetaData();

    /**
     *
     * @return
     */
    DBManage getDBManage();

}