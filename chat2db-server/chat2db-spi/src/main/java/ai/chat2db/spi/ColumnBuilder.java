package ai.chat2db.spi;

import ai.chat2db.spi.model.TableColumn;

public interface ColumnBuilder {

    /**
     * Generate column sql
     * @param column
     * @return
     */
    String buildCreateColumnSql(TableColumn column);


    /**
     * Build modify column sql
     * @param tableColumn
     * @return
     */
    String buildModifyColumn(TableColumn tableColumn);
}
