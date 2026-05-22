package ai.chat2db.server.domain.core.cache;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Sets;

import ai.chat2db.spi.model.BaseModel;
import ai.chat2db.spi.model.IndexModel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * 类LuceneIndexManager用于管理Lucene全文索引的创建、更新和查询
 * 它实现了AutoCloseable接口，支持使用try-with-resources语句自动关闭资源
 */
public class LuceneIndexManager<T extends IndexModel> implements AutoCloseable {
    /**
     * 索引、分析器、写入器、读者和搜索者实例变量
     */
    private Directory index;
    private Analyzer analyzer;
    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Getter
    private Integer lastDocId;


    /**
     * 读写锁
     */
    @Getter
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final String[] TEXT_FIELDS = {"name", "comment", "aiComment"};

    /**
     * 基于注解的文档构建器
     */
    private final AnnotationBasedDocumentBuilder documentBuilder = new AnnotationBasedDocumentBuilder();

    /**
     * 构造函数，根据给定的ID初始化Lucene索引管理器
     *
     * @param id 用于确定索引文件路径的ID
     */
    @SneakyThrows
    public LuceneIndexManager(@NotNull Long id) {
        String indexPath = getIndexPath(id);
        this.index = FSDirectory.open(Paths.get(indexPath));
        this.analyzer = new MixedAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.writer = new IndexWriter(index, config);
        this.reader = DirectoryReader.open(writer);
        this.searcher = new IndexSearcher(reader);
    }

    @SneakyThrows
    public <E extends BaseModel> Long getMaxVersion(E queryModel) {
        // 创建查询条件
        BooleanQuery query = buildBooleanQuery(queryModel).build();

        // 创建按版本号降序排序的排序规则
        // true表示降序
        Sort sort = new Sort(new SortField("version", SortField.Type.LONG, true));

        // 执行查询，按版本号降序排序，只取第一个文档
        TopDocs topDocs = searcher.search(query, 1, sort);

        if (topDocs.totalHits.value == 0) {
            return null;
        }

        // 获取匹配的最高版本号文档
        Document document = searcher.doc(topDocs.scoreDocs[0].doc, Collections.singleton("version"));
        IndexableField versionField = document.getField("version");

        return versionField != null ? (Long) versionField.numericValue() : null;
    }

    /**
     * 根据ID和环境获取索引的文件路径
     *
     * @param id 用于确定索引文件路径的ID
     * @return 索引的文件路径
     */
    private String getIndexPath(Long id) {
        String environment = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
        String basePath = System.getProperty("user.home") + "/.chat2db/index/";
        switch (environment.toLowerCase()) {
            case "test":
                return basePath + id + "_test";
            case "dev":
                return basePath + id + "_dev";
            default:
                return basePath + id;
        }
    }

    /**
     * 释放资源，关闭索引读者、写入器和目录
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (index != null) {
            index.close();
        }
    }

    /**
     * 重建Reader和Searcher
     */
    @SneakyThrows
    private void reload() {
        writer.commit();
        if (this.reader != null) {
            this.reader.close();
        }
        this.reader = DirectoryReader.open(writer);
        this.searcher = new IndexSearcher(reader);
    }

    /**
     * 构建旧数据映射表
     *
     * @param query   查询条件
     * @param maxHits 最大命中数
     * @return 以"name"为Key的旧数据映射表
     */
    @SneakyThrows
    private Map<String, JSONObject> buildSourceMap(BooleanQuery query, int maxHits) {
        TopDocs topDocs = searcher.search(query, maxHits);
        long total = topDocs.totalHits.value;
        if (total == 0) {
            return Collections.emptyMap();
        }
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> getSource(scoreDoc.doc))
                .map(JSONObject::parseObject)
                .collect(Collectors.toMap(
                        obj -> obj.getString("name"),
                        obj -> obj,
                        // 重复时保留最新值
                        (oldVal, newVal) -> newVal
                ));
    }

    /**
     * 批量更新文档到Lucene索引,version留空则初始化
     *
     * @param sources
     * @param version
     */
    public void updateDocuments(List<? extends T> sources, Long version) {
        this.updateDocuments(sources, version == null ? 1L : version, true);
    }

    /**
     * 批量更新文档到Lucene索引,version留空则忽略版本控制
     * 逻辑说明：
     * 1. 参数校验
     * 2. 类型一致性检查
     * 3. 准备搜索环境
     * 4. 构建旧数据映射表
     * 5. 批量创建文档并更新
     */
    @SneakyThrows
    public void updateDocuments(List<? extends T> sources, Long version, boolean all) {
        if (CollectionUtils.isEmpty(sources)) {
            return;
        }
        T model = sources.get(0);
        lock.writeLock().lock();
        try {
            // 获取全部相关旧数据
            String fld = StringUtils.uncapitalize(model.getClassType().getSimpleName() + "Name");
            BooleanQuery.Builder booleanQuery = buildBooleanQuery(model);
            if (!all) {
                BooleanQuery.Builder nameQuery = new BooleanQuery.Builder();
                sources.forEach(source -> nameQuery.add(new TermQuery(new Term(fld, source.getName())), BooleanClause.Occur.SHOULD));
                booleanQuery.add(nameQuery.build(), BooleanClause.Occur.MUST);
            }
            BooleanQuery query = booleanQuery.build();
            Map<String, JSONObject> sourceMap = buildSourceMap(query, 1000);
            List<Document> docs = sources.stream()
                    .peek(source -> source.setVersion(version))
                    .map(source -> createDocument(source, sourceMap))
                    .collect(Collectors.toList());

            writer.updateDocuments(query, docs);
            reload();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 更新单个文档到Lucene索引
     * 逻辑说明：
     * 1. 参数校验
     * 2. 准备搜索环境
     * 3. 构建旧数据映射表
     * 4. 创建新文档并更新
     */
    @SneakyThrows
    public void updateDocument(T source) {
        if (source == null) {
            throw new IllegalArgumentException("Source must not be null");
        }
        lock.writeLock().lock();
        try {
            String fld = StringUtils.uncapitalize(source.getClassType().getSimpleName() + "Name");
            TermQuery termQuery = new TermQuery(new Term(fld, source.getName()));
            BooleanQuery query = buildBooleanQuery(source)
                    .add(termQuery, BooleanClause.Occur.FILTER)
                    .build();
            Map<String, JSONObject> sourceMap = buildSourceMap(query, 1);
            Document document = createDocument(source, sourceMap);
            writer.updateDocuments(query, Collections.singletonList(document));
            reload();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 构建更新文档时使用的布尔查询
     *
     * @return 构建的布尔查询对象
     */
    private <E extends BaseModel<?>> BooleanQuery.Builder buildBooleanQuery(E model) {
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        // 添加类型标识条件
        addTermQuery(booleanQuery, "type", model.getClassType().getSimpleName(), BooleanClause.Occur.FILTER);

        // 使用 STRING 类型的字段自动构建过滤条件
        List<AnnotationBasedDocumentBuilder.AnnotatedFieldInfo> stringFields = documentBuilder.getStringFields(model.getClass());
        for (AnnotationBasedDocumentBuilder.AnnotatedFieldInfo info : stringFields) {
            try {
                Object value = info.field.get(model);
                if (value instanceof String) {
                    addTermQuery(booleanQuery, info.annotation.name(), (String) value, BooleanClause.Occur.FILTER);
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }

        return booleanQuery;
    }

    /**
     * 向布尔查询构建器中添加术语查询
     *
     * @param booleanQuery 布尔查询构建器
     * @param field        字段名
     * @param value        字段值
     * @param occur        查询条款的发生关系
     */
    private void addTermQuery(BooleanQuery.Builder booleanQuery, String field, String value,
                              BooleanClause.Occur occur) {
        if (StringUtils.isNotBlank(value)) {
            Query query = new TermQuery(new Term(field, value));
            booleanQuery.add(query, occur);
        }
    }

    /**
     * 根据实体对象创建Lucene文档
     * 逻辑说明：
     * 1. 添加类型标识字段
     * 2. 处理版本号
     * 3. AI注释继承逻辑
     * 4. 使用注解自动构建字段
     * 5. 存储原始数据快照
     *
     * @param source    源实体对象
     * @param sourceMap 旧数据映射表（用于字段继承）
     * @return 构建完成的Lucene文档
     */
    private Document createDocument(T source, Map<String, JSONObject> sourceMap) {
        Document doc = new Document();

        // 1. 添加类型标识字段
        String typeName = source.getClassType().getSimpleName();
        addStringField(doc, "type", typeName);

        // 2. 处理版本冲突检测和版本号设置
        Long incomingVersion = source.getVersion();
        Long storedVersion = getStoredVersion(source, sourceMap);

        if (storedVersion != null) {
            if (incomingVersion != null && incomingVersion < storedVersion) {
                throw new ConcurrentModificationException(
                        String.format("Data version conflict detected incomingVersion:%s storedVersion:%s",
                                incomingVersion, storedVersion));
            }
            long newVersion = storedVersion + 1;
            doc.add(new StoredField("version", newVersion));
            doc.add(new NumericDocValuesField("version", newVersion));
            source.setVersion(newVersion);
        } else {
            doc.add(new StoredField("version", 1L));
            doc.add(new NumericDocValuesField("version", 1L));
            source.setVersion(1L);
        }

        // 3. AI注释继承逻辑（新数据为空时从旧数据获取）
        handleAiCommentInheritance(source, sourceMap);

        // 4. 使用注解自动构建字段（替代 instanceof 判断）
        Document autoDoc = documentBuilder.buildDocument(source);
        for (IndexableField field : autoDoc) {
            doc.add(field);
        }

        // 5. 添加名称字段别名（typeName + "Name"）
        Optional.ofNullable(source.getName())
                .ifPresent(name -> addStringField(doc, StringUtils.uncapitalize(typeName + "Name"), name));

        // 6. 存储原始数据快照
        doc.add(new StoredField("source", JSONObject.toJSONString(source)));
        return doc;
    }

    /**
     * 新增版本获取方法
     */
    private Long getStoredVersion(T source, Map<String, JSONObject> sourceMap) {
        String nameValue = source.getName();
        if (nameValue == null || sourceMap == null) {
            return null;
        }

        JSONObject oldData = sourceMap.get(nameValue);
        return oldData != null ? oldData.getLong("version") : null;
    }

    // 辅助方法：处理AI注释继承
    private void handleAiCommentInheritance(T source, Map<String, JSONObject> sourceMap) {
        String nameValue = source.getName();
        if (nameValue == null) {
            return;
        }

        String aiComment = source.getAiComment();
        if (aiComment == null && sourceMap != null) {
            JSONObject oldData = sourceMap.get(nameValue);
            if (oldData != null) {
                source.setAiComment(oldData.getString("aiComment"));
            }
        }
    }

    /**
     * 向文档中添加字符串字段
     *
     * @param doc       文档对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    private void addStringField(Document doc, String fieldName, String value) {
        if (value != null) {
            doc.add(new StringField(fieldName, value, Field.Store.NO));
        }
    }

    /**
     * 搜索文档
     *
     * @param lastDocId 上一次搜索结果中的最后一个文档ID，用于分页搜索
     * @param queryStr  搜索查询字符串
     * @return 搜索结果的TopDocs对象
     */
    @SneakyThrows
    public <E extends BaseModel> List<T> search(E queryModel, Integer lastDocId, String queryStr) {
        return search(queryModel, lastDocId, queryStr, null, false);
    }

    /**
     * 搜索文档（支持排序）
     *
     * @param queryModel 查询模型
     * @param lastDocId  上一次搜索结果中的最后一个文档ID，用于分页搜索
     * @param queryStr   搜索查询字符串
     * @param sortField  排序字段名（如 "name", "rowCount"，对应 @LuceneField 注解的 name 属性）
     * @param reverse    是否降序
     * @return 搜索结果的TopDocs对象
     */
    @SneakyThrows
    public <E extends BaseModel> List<T> search(E queryModel, Integer lastDocId, String queryStr, String sortField, boolean reverse) {
        lock.readLock().lock();
        try {
            BooleanQuery booleanQuery = buildSearchQuery(queryModel, queryStr);
            ScoreDoc lastScoreDoc = null;
            if (lastDocId != null) {
                lastScoreDoc = new ScoreDoc(lastDocId, 1);
            }

            TopDocs topDocs;
            if (StringUtils.isNotBlank(sortField)) {
                // 使用排序搜索（字段名直接对应 @LuceneField 注解的 name 属性）
                Sort sort = createSort(sortField, reverse);
                if (sort != null) {
                    topDocs = searcher.searchAfter(lastScoreDoc, booleanQuery, 1000, sort);
                } else {
                    topDocs = searcher.searchAfter(lastScoreDoc, booleanQuery, 1000);
                }
            } else {
                // 不使用排序
                topDocs = searcher.searchAfter(lastScoreDoc, booleanQuery, 1000);
            }
            long total = topDocs.totalHits.value;
            if (total >= 1000) {
                this.lastDocId = topDocs.scoreDocs[topDocs.scoreDocs.length - 1].doc;
            }
            return Arrays.stream(topDocs.scoreDocs)
                    .map(scoreDoc -> {
                        T doc = (T) getDocument(queryModel.getClassType(), scoreDoc.doc);
                        return doc;
                    })
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 创建排序对象
     *
     * @param sortField 排序字段名（对应 @LuceneField 注解的 name 属性）
     * @param reverse   是否降序
     * @return 排序对象，如果字段不支持排序则返回 null
     */
    private Sort createSort(String sortField, boolean reverse) {
        // 根据字段名确定排序类型（与 @LuceneField 注解定义保持一致）
        if ("name".equals(sortField)) {
            return new Sort(new SortField("name", SortField.Type.STRING, reverse));
        } else if ("rowCount".equals(sortField)) {
            return new Sort(new SortField("rowCount", SortField.Type.LONG, reverse));
        }
        return null;
    }

    /**
     * 构建搜索查询
     *
     * @param queryStr 搜索查询字符串
     * @return 构建的布尔查询对象
     */
    @SneakyThrows
    private <E extends BaseModel<T>> BooleanQuery buildSearchQuery(E queryModel, String queryStr) {
        BooleanQuery.Builder booleanQuery = buildBooleanQuery(queryModel);
        if (StringUtils.isBlank(queryStr)) {
            booleanQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
        } else {
            MultiFieldQueryParser multiParser = new MultiFieldQueryParser(TEXT_FIELDS, analyzer);
            multiParser.setDefaultOperator(QueryParser.Operator.AND);
            Query query = multiParser.parse(queryStr);
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
        return booleanQuery.build();
    }

    @SneakyThrows
    private String getSource(int docId) {
        StoredFields storedFields = searcher.storedFields();
        Document document = storedFields.document(docId, Sets.newHashSet("source"));
        return document.get("source");
    }

    /**
     * 获取指定ID的文档，可指定需要加载的字段
     *
     * @param docId 文档ID
     * @return 加载的文档对象
     */

    private <E extends IndexModel> E getDocument(Class<E> clz, int docId) {
        return JSONObject.parseObject(getSource(docId), clz);
    }

}
