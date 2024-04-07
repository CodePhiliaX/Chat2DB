package ai.chat2db.server.tools.common.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import ai.chat2db.server.tools.common.util.EasySqlUtils;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.collections4.CollectionUtils;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.EQ;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ORDER_BY;

/**
 * Custom query wrapper
 *
 * @author Jiaju Zhuang
 */
public class EasyLambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, EasyLambdaQueryWrapper<T>>
    implements Query<EasyLambdaQueryWrapper<T>, T, SFunction<T, ?>> {

    public void orderBy(List<OrderBy> orderByList) {
        if (CollectionUtils.isEmpty(orderByList)) {
            return;
        }
        for (OrderBy orderBy : orderByList) {
            appendSqlSegments(ORDER_BY, EasySqlUtils.columnToSqlSegment(orderBy.getOrderConditionName()),
                EasySqlUtils.parseOrderBy(orderBy.getDirection()));
        }
    }

    public EasyLambdaQueryWrapper<T> eqWhenPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            addCondition(true, column, EQ, val);
        }
        return typedThis;
    }

    public EasyLambdaQueryWrapper<T> likeWhenPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return like(true, column, val);
        }
        return typedThis;
    }

    public EasyLambdaQueryWrapper<T> inWhenPresent(SFunction<T, ?> column, Collection<?> coll) {
        if (coll != null) {
            return in(true, column, coll);
        }
        return typedThis;
    }

    // The following are the methods that come with the system
    /**
     * Query field
     */
    private SharedString sqlSelect = new SharedString();

    public EasyLambdaQueryWrapper() {
        this((T)null);
    }

    public EasyLambdaQueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public EasyLambdaQueryWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    EasyLambdaQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
        Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
        SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    /**
     * SELECT some SQL settings
     *
     * @param columns query fields
     */
    @SafeVarargs
    @Override
    public final EasyLambdaQueryWrapper<T> select(SFunction<T, ?>... columns) {
        return select(Arrays.asList(columns));
    }

    public EasyLambdaQueryWrapper<T> select(List<SFunction<T, ?>> columns) {
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(columnsToString(false, columns));
        }
        return typedThis;
    }

    /**
     * Filter the field information of the query (except primary key!)
     * <p>Example 1: As long as the java field name starts with "test" -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * <p>Example 2: As long as the java field attribute is of type CharSequence -> select(TableFieldInfo::isCharSequence)</p>
     * <p>Example 3: As long as the java field does not have a filling strategy -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)</p>
     * <p>Example 4: Want all fields -> select(i -&gt; true)</p>
     * <p>Example 5: As long as the primary key field -> select(i -&gt; false)</p>
     *
     * @param predicate filtering method
     * @return this
     */
    @Override
    public EasyLambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        if (entityClass == null) {
            entityClass = getEntityClass();
        } else {
            setEntityClass(entityClass);
        }
        Assert.notNull(entityClass, "entityClass can not be null");
        this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    /**
     * Used to generate nested sql
     * <p>Therefore sqlSelect does not pass down</p>
     */
    @Override
    protected EasyLambdaQueryWrapper<T> instance() {
        return new EasyLambdaQueryWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
            new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(),
            SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
    }

}
