package ai.chat2db.server.domain.core.cache;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import ai.chat2db.spi.model.LuceneField;
import ai.chat2db.spi.model.LuceneFieldType;

/**
 * 基于注解的 Lucene 文档构建器
 * 通过读取字段上的 @LuceneField 注解自动构建 Lucene Document
 * 使用 MethodHandle + 缓存机制优化性能，支持 AOT 编译
 */
public class AnnotationBasedDocumentBuilder {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 带注解的字段信息
     * 使用 MethodHandle 替代 Field 提升访问性能，AOT 友好
     */
    static class AnnotatedFieldInfo {
        final MethodHandle getter;
        final LuceneField annotation;

        AnnotatedFieldInfo(Field field, LuceneField annotation) {
            this.annotation = annotation;
            try {
                this.getter = LOOKUP.unreflectGetter(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to create MethodHandle for field: " + field.getName(), e);
            }
        }

        Object getValue(Object source) {
            try {
                return getter.invoke(source);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to get field value", e);
            }
        }
    }

    /**
     * 缓存类的字段信息，key 为类名，value 为带注解的字段列表
     */
    private final Map<String, List<AnnotatedFieldInfo>> fieldCache = new ConcurrentHashMap<>();

    /**
     * 缓存类的 STRING 类型字段（用于过滤），key 为类名，value 为 STRING 类型字段列表
     */
    private final Map<String, List<AnnotatedFieldInfo>> stringFieldCache = new ConcurrentHashMap<>();

    /**
     * 获取 STRING 类型的字段列表（用于构建查询过滤）
     * STRING 类型字段自动用于精确匹配过滤
     */
    public List<AnnotatedFieldInfo> getStringFields(Class<?> clazz) {
        return stringFieldCache.computeIfAbsent(clazz.getName(), k -> collectStringFields(clazz));
    }

    /**
     * 收集 STRING 类型的字段
     */
    private List<AnnotatedFieldInfo> collectStringFields(Class<?> clazz) {
        List<AnnotatedFieldInfo> stringFields = new ArrayList<>();
        collectStringFieldsRecursive(clazz, stringFields);
        return stringFields;
    }

    /**
     * 递归收集 STRING 类型的字段
     */
    private void collectStringFieldsRecursive(Class<?> clazz, List<AnnotatedFieldInfo> stringFields) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        collectStringFieldsRecursive(clazz.getSuperclass(), stringFields);

        ReflectionUtils.doWithLocalFields(clazz, field -> {
            LuceneField annotation = AnnotatedElementUtils.findMergedAnnotation(field, LuceneField.class);
            if (annotation != null && annotation.type() == LuceneFieldType.STRING) {
                ReflectionUtils.makeAccessible(field);
                stringFields.add(new AnnotatedFieldInfo(field, annotation));
            }
        });
    }

    /**
     * 基于注解构建 Lucene 文档
     *
     * @param source 源对象
     * @return Lucene 文档
     */
    public org.apache.lucene.document.Document buildDocument(Object source) {
        if (source == null) {
            return new org.apache.lucene.document.Document();
        }

        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        Class<?> clazz = source.getClass();

        List<AnnotatedFieldInfo> annotatedFields = fieldCache.computeIfAbsent(clazz.getName(), k -> collectAnnotatedFields(clazz));

        for (AnnotatedFieldInfo info : annotatedFields) {
            Object value = info.getValue(source);
            addFieldToDocument(doc, info.annotation, value);
        }

        return doc;
    }

    /**
     * 收集类及其父类中所有带 @LuceneField 注解的字段
     */
    private List<AnnotatedFieldInfo> collectAnnotatedFields(Class<?> clazz) {
        List<AnnotatedFieldInfo> fields = new ArrayList<>();
        collectAnnotatedFieldsRecursive(clazz, fields);
        return fields;
    }

    /**
     * 递归收集字段（从父类到子类）
     */
    private void collectAnnotatedFieldsRecursive(Class<?> clazz, List<AnnotatedFieldInfo> fields) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        collectAnnotatedFieldsRecursive(clazz.getSuperclass(), fields);

        ReflectionUtils.doWithLocalFields(clazz, field -> {
            LuceneField annotation = AnnotatedElementUtils.findMergedAnnotation(field, LuceneField.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                fields.add(new AnnotatedFieldInfo(field, annotation));
            }
        });
    }

    /**
     * 根据注解配置添加字段到文档
     */
    private void addFieldToDocument(org.apache.lucene.document.Document doc, LuceneField annotation, Object value) {
        if (value == null) {
            return;
        }

        String fieldName = annotation.name();
        LuceneFieldType type = annotation.type();
        boolean sort = annotation.sort();
        boolean store = annotation.store();

        switch (type) {
            case TEXT:
                addTextField(doc, fieldName, value.toString(), sort, store);
                break;
            case STRING:
                addStringField(doc, fieldName, value.toString(), sort, store);
                break;
            case LONG:
                addLongField(doc, fieldName, (Long) value, sort, store);
                break;
            case INTEGER:
                addIntegerField(doc, fieldName, (Integer) value, sort, store);
                break;
            case DOUBLE:
                addDoubleField(doc, fieldName, (Double) value, sort, store);
                break;
            default:
                throw new IllegalArgumentException("Unknown field type: " + type);
        }
    }

    /**
     * 添加文本字段
     */
    private void addTextField(org.apache.lucene.document.Document doc, String fieldName, String value, boolean sort, boolean store) {
        doc.add(new TextField(fieldName, value, store ? org.apache.lucene.document.Field.Store.YES : org.apache.lucene.document.Field.Store.NO));

        if (sort) {
            doc.add(new SortedDocValuesField(fieldName, new BytesRef(value)));
        }
    }

    /**
     * 添加字符串字段
     */
    private void addStringField(org.apache.lucene.document.Document doc, String fieldName, String value, boolean sort, boolean store) {
        doc.add(new StringField(fieldName, value, store ? org.apache.lucene.document.Field.Store.YES : org.apache.lucene.document.Field.Store.NO));

        if (sort) {
            doc.add(new SortedDocValuesField(fieldName, new BytesRef(value)));
        }
    }

    /**
     * 添加长整型字段
     */
    private void addLongField(org.apache.lucene.document.Document doc, String fieldName, Long value, boolean sort, boolean store) {
        doc.add(new LongPoint(fieldName, value));

        if (sort) {
            doc.add(new NumericDocValuesField(fieldName, value));
        }

        if (store) {
            doc.add(new StoredField(fieldName, value));
        }
    }

    /**
     * 添加整型字段
     */
    private void addIntegerField(org.apache.lucene.document.Document doc, String fieldName, Integer value, boolean sort, boolean store) {
        Long longValue = value.longValue();
        doc.add(new LongPoint(fieldName, longValue));

        if (sort) {
            doc.add(new NumericDocValuesField(fieldName, longValue));
        }

        if (store) {
            doc.add(new StoredField(fieldName, longValue));
        }
    }

    /**
     * 添加双精度浮点字段
     */
    private void addDoubleField(org.apache.lucene.document.Document doc, String fieldName, Double value, boolean sort, boolean store) {
        doc.add(new org.apache.lucene.document.DoublePoint(fieldName, value));

        if (sort) {
            long encoded = Double.doubleToLongBits(value);
            doc.add(new org.apache.lucene.document.SortedNumericDocValuesField(fieldName, encoded));
        }

        if (store) {
            doc.add(new StoredField(fieldName, value));
        }
    }
}
