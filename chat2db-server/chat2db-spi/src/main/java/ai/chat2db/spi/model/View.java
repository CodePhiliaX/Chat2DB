package ai.chat2db.spi.model;

import org.springframework.beans.BeanUtils;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View metadata uses the same fields as Table, but must have an independent Lucene type.
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class View extends Table {

    public static View from(Table table) {
        if (table == null) {
            return null;
        }
        View view = new View();
        BeanUtils.copyProperties(table, view);
        return view;
    }

    @Override
    public Class<? extends IndexModel> getClassType() {
        return View.class;
    }
}
