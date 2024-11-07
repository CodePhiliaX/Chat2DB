
package ai.chat2db.server.web.api.controller.data.source.request;

/**
 * @author jipengfei
 * @version : DataSourceBaseRequestInfo.java
 */
public interface DataSourceBaseRequestInfo {

    /**
     * Get datasource id
     * @return
     */
    Long getDataSourceId();

    /**
     * get datasource name
     * @return
     */
    String getDatabaseName();
}