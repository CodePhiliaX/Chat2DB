# 生成数据和导入数据猜一猜功能设计

## 一、功能概述

参考 `NL_2_COMMENT` 的实现模式，为两个场景新增 prompt 类型：

| 场景 | 新增 PromptType | 功能描述 |
|------|-----------------|----------|
| **导入数据** - 字段映射 | `NL_2_FIELD_MAPPING` | AI 智能推荐源文件字段到目标表字段的映射关系 |
| **生成数据** - 表达式 | `NL_2_DATA_EXPRESSION` | AI 智能推荐各字段的数据生成表达式（datafaker） |

## 二、整体架构设计

两个功能都复用现有的 `/api/ai/chat` SSE 接口，通过新增 `promptType` 和对应的状态机动作来实现。

**核心复用组件：**
- `/api/ai/chat` SSE 接口（ChatController）
- Spring State Machine 状态机
- PromptTemplateRegistry 模板管理
- EventSource 前端 SSE 连接
- AiChat 组件交互流程
- pendingAiChat 状态传递机制

## 三、流程图

### 3.1 导入数据 - 字段映射猜一猜

```
用户上传文件
    ↓
步骤1: 选择文件
    ↓
点击下一步
    ↓
调用 preview_headers 获取源字段和目标列
    ↓
步骤2: 字段映射页面
    ↓
显示源字段和目标字段映射表
    ↓
[用户点击猜一猜按钮] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
    ↓                                            ↓
构建 pendingAiChat                          切换到 AI Chat 面板
    ↓                                            ↓
promptType = NL_2_FIELD_MAPPING              AiChat 检测到 pendingAiChat
    ↓                                            ↓
传入: 源字段列表 + 表名                       调用 /api/ai/chat SSE接口
    ↓                                            ↓
                                             后端状态机执行:
                                             IDLE → FETCHING_TABLE_SCHEMA
                                             → FetchSchemaAction 自动获取目标表 DDL
                                             → BUILDING_PROMPT
                                             → 组装 prompt (源字段+目标表DDL+映射要求)
                                             → STREAMING
                                             → 调用 AI 生成映射建议
                                             → COMPLETED
    ↓                                            ↓
                                        前端解析 JSON 结果
    ↓                                            ↓
                                    自动填充映射下拉框 ←━━━━━━━━┛
    ↓
用户确认/修改映射
    ↓
继续导入流程
```

### 3.2 生成数据 - 表达式猜一猜

```
打开生成数据弹窗
    ↓
加载表列信息和模板
    ↓
显示列配置表格 (列名, 类型, 注释, 表达式)
    ↓
[用户点击猜一猜按钮] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
    ↓                                            ↓
构建 pendingAiChat                          切换到 AI Chat 面板
    ↓                                            ↓
promptType = NL_2_DATA_EXPRESSION            AiChat 检测到 pendingAiChat
    ↓                                            ↓
传入: 表名                                   调用 /api/ai/chat SSE接口
    ↓                                            ↓
                                             后端状态机执行:
                                             IDLE → FETCHING_TABLE_SCHEMA
                                             → FetchSchemaAction 自动获取目标表 DDL
                                             → BUILDING_PROMPT
                                             → 组装 prompt (目标表DDL+datafaker要求)
                                             → STREAMING
                                             → 调用 AI 生成表达式建议
                                             → COMPLETED
    ↓                                            ↓
                                        前端解析 JSON 结果
    ↓                                            ↓
                                    自动填充表达式列 ←━━━━━━━━┛
    ↓
用户预览/修改表达式
    ↓
执行数据生成
```

## 四、时序图

### 4.1 导入数据 - 字段映射猜一猜

```
用户                ImportDataModal        WorkspaceStore        AiChat组件          EventSource        ChatController        状态机              AI模型
 |                        |                      |                    |                    |                    |                    |                    |
 |-- 上传文件，进入步骤2 -->|                      |                    |                    |                    |                    |                    |
 |                        |-- 显示字段映射表 ---->|                    |                    |                    |                    |                    |
 |                        |  (源字段 vs 目标字段)  |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |--- 点击"猜一猜" ------>|                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- setPendingAiChat({ |                    |                    |                    |                    |                    |
 |                        |   promptType: 'NL_2_FIELD_MAPPING',      |                    |                    |                    |                    |
 |                        |   dataSourceId, databaseName, schemaName,|                    |                    |                    |                    |
 |                        |   tableName, sourceFields[],             |                    |                    |                    |                    |
 |                        |   onMappingGenerated: callback           |                    |                    |                    |                    |
 |                        |   })                 |                    |                    |                    |                    |                    |
 |                        |--->|                 |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- setCurrentWorkspaceExtend('ai')        |                    |                    |                    |                    |
 |                        |--->|                 |                    |                    |                    |                    |                    |
 |                        |                      |-- 检测到 -------->|                    |                    |                    |                    |
 |                        |                      |   pendingAiChat    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |-- connectToEventSource({                |                    |                    |
 |                        |                      |                    |   url: '/api/ai/chat?params',           |                    |                    |
 |                        |                      |                    |   uid: sessionId                        |                    |                    |
 |                        |                      |                    |   })                |                    |                    |                    |
 |                        |                      |                    |------->|            |                    |                    |                    |
 |                        |                      |                    |                    |-- GET /api/ai/chat ->|                    |                    |
 |                        |                      |                    |                    |                    |-- 创建 ChatContext |                    |
 |                        |                      |                    |                    |                    |-- sendEvent(TABLES_PROVIDED) ->|      |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- IDLE → FETCHING_TABLE_SCHEMA --->|
 |                        |                      |                    |                    |                    |--- FetchSchemaAction 获取目标表DDL -->|
 |                        |                      |                    |                    |                    |    (从数据库自动获取)                    |
 |                        |                      |                    |                    |                    |--- 存储到 context.schemaDdl ----------|
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- FETCHING_TABLE_SCHEMA → BUILDING_PROMPT ->|
 |                        |                      |                    |                    |                    |--- 组装 prompt (源字段+目标表DDL)      |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- BUILDING_PROMPT → STREAMING ->|    |
 |                        |                      |                    |                    |                    |--- ChatClient.prompt().stream() ------->|
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |<-- 流式返回 JSON 结果 ---------------|
 |                        |                      |                    |                    |<-- SSE event: message {content, thinking} |      |
 |                        |                      |                    |<-- onMessage callback|                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |<-- [DONE] -------------------------|
 |                        |                      |                    |                    |                    |--- STREAMING → COMPLETED |          |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |<-- onDone callback  |                    |                    |                    |
 |                        |                      |                    |-- extractJsonFromContent()              |                    |                    |
 |                        |                      |                    |-- 解析出 field_mappings[]               |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |<-- onMappingGenerated(result) -------------|                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- 自动填充 targetField 下拉框              |                    |                    |                    |                    |
 |<-- 显示推荐映射结果 ---|                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |-- 确认或修改映射 ------>|                      |                    |                    |                    |                    |                    |
 |-- 点击"下一步" -------->|                      |                    |                    |                    |                    |                    |
 |                        |-- 继续导入流程       |                    |                    |                    |                    |                    |
```

### 4.2 生成数据 - 表达式猜一猜

```
用户                DataGenerationModal      WorkspaceStore        AiChat组件          EventSource        ChatController        状态机              AI模型
 |                        |                      |                    |                    |                    |                    |                    |
 |-- 打开生成数据弹窗 --->|                      |                    |                    |                    |                    |                    |
 |                        |-- 加载列配置表格 --->|                    |                    |                    |                    |                    |
 |                        |  (列名, 类型, 注释, 表达式)               |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |--- 点击"猜一猜" ------>|                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- setPendingAiChat({ |                    |                    |                    |                    |                    |
 |                        |   promptType: 'NL_2_DATA_EXPRESSION',    |                    |                    |                    |                    |
 |                        |   dataSourceId, databaseName, schemaName,|                    |                    |                    |                    |
 |                        |   tableName,                             |                    |                    |                    |                    |
 |                        |   onExpressionGenerated: callback        |                    |                    |                    |                    |
 |                        |   })                 |                    |                    |                    |                    |                    |
 |                        |--->|                 |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- setCurrentWorkspaceExtend('ai')        |                    |                    |                    |                    |
 |                        |--->|                 |                    |                    |                    |                    |                    |
 |                        |                      |-- 检测到 -------->|                    |                    |                    |                    |
 |                        |                      |   pendingAiChat    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |-- connectToEventSource({                |                    |                    |
 |                        |                      |                    |   url: '/api/ai/chat?params',           |                    |                    |
 |                        |                      |                    |   uid: sessionId                        |                    |                    |
 |                        |                      |                    |   })                |                    |                    |                    |
 |                        |                      |                    |------->|            |                    |                    |                    |
 |                        |                      |                    |                    |-- GET /api/ai/chat ->|                    |                    |
 |                        |                      |                    |                    |                    |-- 创建 ChatContext |                    |
 |                        |                      |                    |                    |                    |-- sendEvent(TABLES_PROVIDED) ->|      |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- IDLE → FETCHING_TABLE_SCHEMA --->|
 |                        |                      |                    |                    |                    |--- FetchSchemaAction 获取目标表DDL -->|
 |                        |                      |                    |                    |                    |    (从数据库自动获取)                    |
 |                        |                      |                    |                    |                    |--- 存储到 context.schemaDdl ----------|
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- FETCHING_TABLE_SCHEMA → BUILDING_PROMPT ->|
 |                        |                      |                    |                    |                    |--- 组装 prompt (目标表DDL+datafaker)  |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |--- BUILDING_PROMPT → STREAMING ->|    |
 |                        |                      |                    |                    |                    |--- ChatClient.prompt().stream() ------->|
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |<-- 流式返回 JSON 结果 ---------------|
 |                        |                      |                    |                    |<-- SSE event: message {content, thinking} |      |
 |                        |                      |                    |<-- onMessage callback|                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |<-- [DONE] -------------------------|
 |                        |                      |                    |                    |                    |--- STREAMING → COMPLETED |          |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |<-- onDone callback  |                    |                    |                    |
 |                        |                      |                    |-- extractJsonFromContent()              |                    |                    |
 |                        |                      |                    |-- 解析出 column_expressions[]           |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |<-- onExpressionGenerated(result) ----------|                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |                        |-- 自动填充表达式列                          |                    |                    |                    |                    |
 |<-- 显示推荐表达式 -----|                      |                    |                    |                    |                    |                    |
 |                        |                      |                    |                    |                    |                    |                    |
 |-- 确认/修改表达式 ----->|                      |                    |                    |                    |                    |                    |
 |-- 点击"预览"或"确定生成"->|                      |                    |                    |                    |                    |                    |
 |                        |-- 执行数据生成流程    |                    |                    |                    |                    |                    |
```

## 五、详细设计

### 5.1 新增 PromptType

#### 后端 - PromptType.java

文件路径: `chat2db-server/chat2db-server-web/chat2db-server-web-api/src/main/java/ai/chat2db/server/web/api/controller/ai/enums/PromptType.java`

```java
NL_2_COMMENT("猜测表和字段注释"),
NL_2_COMMENT_BATCH("批量猜测表注释"),
NL_2_FIELD_MAPPING("智能字段映射推荐"),              // 新增
NL_2_DATA_EXPRESSION("智能数据生成表达式推荐"),      // 新增
```

#### 前端 - common.ts

文件路径: `chat2db-client/src/pages/main/workspace/store/common.ts`

```typescript
export type IAiChatPromptType = 
  | 'NL_2_SQL' 
  | 'SQL_EXPLAIN' 
  | 'SQL_OPTIMIZER' 
  | 'SQL_2_SQL' 
  | 'NL_2_COMMENT' 
  | 'NL_2_COMMENT_BATCH'
  | 'NL_2_FIELD_MAPPING'       // 新增
  | 'NL_2_DATA_EXPRESSION';    // 新增
```

### 5.2 扩展 pendingAiChat 接口

文件路径: `chat2db-client/src/pages/main/workspace/store/common.ts`

```typescript
export interface IPendingAiChat {
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string | null;
  tableNames?: string[] | null;
  message: string;
  promptType: IAiChatPromptType;
  onCommentGenerated?: (result: ITableCommentResult) => void;
  onBatchCommentGenerated?: (result: IBatchTableCommentResult) => void;
  
  // 新增回调
  onMappingGenerated?: (result: IFieldMappingResult) => void;
  onExpressionGenerated?: (result: IDataExpressionResult) => void;
  
  // 扩展参数（JSON 字符串，用于传递额外数据）
  ext?: string;
}

// 导入映射结果
export interface IFieldMappingResult {
  mappings: {
    sourceField: string;
    targetField: string;
    confidence: number;  // 匹配置信度 0-1
  }[];
}

// 生成数据表达式结果
export interface IDataExpressionResult {
  column_expressions: {
    column_name: string;
    expression: string;  // datafaker 表达式
    reason: string;      // 推荐理由
  }[];
}
```

### 5.3 Prompt 模板设计

文件路径: `chat2db-server/chat2db-server-web/chat2db-server-web-api/src/main/resources/prompt-templates.yml`

#### nl_2_field_mapping - 字段映射

```yaml
nl_2_field_mapping:
  name: "nl_2_field_mapping"
  description: "智能字段映射推荐"
  template: |
    ### 任务：根据源文件字段和目标数据库表结构，推荐最佳字段映射方案
    
    **目标表**: {table_name}
    **数据库类型**: {db_type}
    
    **源文件字段列表**:
    {source_fields}
    
    **目标表字段结构** (自动获取):
    {schema}
    
    **要求**:
    1. 根据字段名、数据类型、语义智能匹配源字段到目标字段
    2. 考虑数据类型兼容性
    3. 考虑字段命名语义相似性（如 name -> user_name, email -> user_email）
    4. 为每个源字段推荐最合适的目标字段
    5. 如果某个源字段没有合适的目标字段，可以省略
    
    **输出格式（严格 JSON，不要包含其他文字）**:
    ```json
    {
      "mappings": [
        {
          "sourceField": "源字段名",
          "targetField": "目标字段名",
          "confidence": 0.95
        }
      ]
    }
    ```
    
    **注意事项**:
    1. 只输出 JSON 内容，不要包含其他解释文字
    2. confidence 值为 0-1 之间的小数，表示匹配置信度
    3. 确保所有 sourceField 都在源字段列表中存在
    4. 确保所有 targetField 都在目标表字段结构中存在
```

**说明**: 
- `{schema}` 占位符由后端 `FetchSchemaAction` 自动获取目标表 DDL 填充
- 前端只需传 `sourceFields`（源文件字段列表）

#### nl_2_data_expression - 数据生成表达式

```yaml
nl_2_data_expression:
  name: "nl_2_data_expression"
  description: "智能数据生成表达式推荐"
  template: |
    ### 任务：为数据库表的每个字段推荐合适的 datafaker 表达式
    
    **目标表**: {table_name}
    **数据库类型**: {db_type}
    
    **表字段信息** (自动获取):
    {schema}
    
    **可用的 datafaker 表达式示例**:
    - 姓名: #{Name.first_name}, #{Name.last_name}, #{Name.full_name}
    - 邮箱: #{Internet.email_address}, #{Internet.url}
    - 电话: #{Phone.cell_phone}, #{Phone.phone_number}
    - 地址: #{Address.full_address}, #{Address.city}, #{Address.country}
    - 日期: #{date.past}, #{date.future}, #{date.birthday}
    - 数值: #{number.number_between '1,1000'}, #{number.random_double}
    - 文本: #{lorem.sentence}, #{lorem.word}, #{lorem.paragraph}
    - 公司: #{Company.name}, #{Company.industry}, #{Company.catch_phrase}
    - ID: #{Code.isbn}, #{Code.asin}, #{Number.uuid}
    - 布尔: #{bool.bool}
    
    **要求**:
    1. 根据字段名、数据类型、注释推荐合适的表达式
    2. 考虑数据类型和长度限制（如 VARCHAR 长度、DECIMAL 精度）
    3. 表达式必须符合 datafaker 语法
    4. 如果字段是主键或自增，可以跳过
    5. 如果字段允许 NULL 且没有合适表达式，可以留空
    
    **输出格式（严格 JSON，不要包含其他文字）**:
    ```json
    {
      "column_expressions": [
        {
          "column_name": "字段名",
          "expression": "#{Name.first_name}",
          "reason": "推荐原因"
        }
      ]
    }
    ```
    
    **注意事项**:
    1. 只输出 JSON 内容，不要包含其他解释文字
    2. expression 必须是有效的 datafaker 表达式
    3. reason 简要说明为什么推荐这个表达式
    4. 确保所有 column_name 都在表字段信息中存在
```

**说明**:
- `{schema}` 占位符由后端 `FetchSchemaAction` 自动获取目标表 DDL 填充
- 前端只需传 `tableName`，不需要传列信息

### 5.4 前端集成实现

#### 5.4.1 ImportDataModal - 字段映射猜一猜

文件路径: `chat2db-client/src/components/ImportDataModal/index.tsx`

**导入依赖：**
```typescript
import { MagicOutlined } from '@ant-design/icons';
import { setPendingAiChat, setCurrentWorkspaceExtend } from '@/pages/main/workspace/store';
import { IFieldMappingResult } from '@/pages/main/workspace/store/common';
```

**添加状态和回调：**
```typescript
const ImportDataModal = () => {
  // ... 现有状态
  
  // AI 猜一猜处理
  const handleAiGuessMapping = useCallback(() => {
    if (!params || !previewData) {
      message.warning('请先上传文件并预览');
      return;
    }
    
    setPendingAiChat({
      dataSourceId: params.dataSourceId,
      databaseName: params.databaseName,
      schemaName: params.schemaName,
      tableNames: [params.tableName],
      message: `请为表 ${params.tableName} 推荐字段映射方案`,
      promptType: 'NL_2_FIELD_MAPPING',
      ext: JSON.stringify({
        sourceFields: previewData.headers,  // 只需要传源文件字段
      }),
      onMappingGenerated: handleMappingGenerated,
    });
    setCurrentWorkspaceExtend('ai');
  }, [params, previewData]);
  
  const handleMappingGenerated = useCallback((result: IFieldMappingResult) => {
    if (!result || !result.mappings || result.mappings.length === 0) {
      message.warning('未获取到映射推荐');
      return;
    }
    
    // 自动填充映射
    const newMappings = fieldMappings.map(m => {
      const matched = result.mappings.find(r => r.sourceField === m.sourceField);
      if (matched && matched.targetField) {
        // 更新主键标识
        const targetCol = previewData?.tableColumns.find(col => col.name === matched.targetField);
        return {
          ...m,
          targetField: matched.targetField,
          primaryKey: !!targetCol?.primaryKey,
        };
      }
      return m;
    });
    
    setFieldMappings(newMappings);
    message.success(`AI 已推荐 ${result.mappings.length} 个字段映射，请查看并确认`);
  }, [fieldMappings, previewData]);
  
  // ...
```

**修改字段映射步骤 UI：**
```typescript
case 1:
  return (
    <div style={{ padding: '16px 0' }}>
      <div style={{ 
        marginBottom: 16, 
        display: 'flex', 
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <div style={{ color: 'var(--color-text)' }}>
          {i18n('workspace.table.import.fieldMapping.description')}
        </div>
        <Button
          type="primary"
          icon={<MagicOutlined />}
          onClick={handleAiGuessMapping}
          disabled={!previewData || previewLoading}
          size="small"
        >
          猜一猜
        </Button>
      </div>
      
      {/* 其余内容保持不变 */}
      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
          {i18n('workspace.table.import.fieldMapping.source')}:
        </label>
        <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
          {file?.name || '-'}
        </div>
      </div>
      {/* ... */}
    </div>
  );
```

#### 5.4.2 DataGenerationModal - 表达式猜一猜

文件路径: `chat2db-client/src/components/DataGenerationModal/index.tsx`

**导入依赖：**
```typescript
import { MagicOutlined } from '@ant-design/icons';
import { setPendingAiChat, setCurrentWorkspaceExtend } from '@/pages/main/workspace/store';
import { IDataExpressionResult } from '@/pages/main/workspace/store/common';
```

**添加状态和回调：**
```typescript
const DataGenerationModal: React.FC = () => {
  // ... 现有状态
  
  // AI 猜一猜处理
  const handleAiGuessExpression = useCallback(() => {
    if (!tableInfo || columns.length === 0) {
      message.warning('请先加载表列信息');
      return;
    }
    
    setPendingAiChat({
      dataSourceId: tableInfo.dataSourceId,
      databaseName: tableInfo.databaseName,
      schemaName: tableInfo.schemaName,
      tableNames: [tableInfo.tableName],
      message: `请为表 ${tableInfo.tableName} 的字段推荐 datafaker 表达式`,
      promptType: 'NL_2_DATA_EXPRESSION',
      onExpressionGenerated: handleExpressionGenerated,
    });
    setCurrentWorkspaceExtend('ai');
  }, [tableInfo, columns]);
  
  const handleExpressionGenerated = useCallback((result: IDataExpressionResult) => {
    if (!result || !result.column_expressions || result.column_expressions.length === 0) {
      message.warning('未获取到表达式推荐');
      return;
    }
    
    // 自动填充表达式
    const newColumns = columns.map(col => {
      const matched = result.column_expressions.find(e => e.column_name === col.columnName);
      if (matched && matched.expression) {
        return { ...col, expression: matched.expression };
      }
      return col;
    });
    
    setColumns(newColumns);
    message.success(`AI 已推荐 ${result.column_expressions.length} 个字段表达式，请查看并确认`);
  }, [columns]);
  
  // ...
```

**修改 UI 添加猜一猜按钮：**
```typescript
return (
  <>
    <Modal
      title="生成数据"
      open={open}
      onCancel={() => setOpen(false)}
      width={1100}
      footer={[
        <Button key="cancel" onClick={() => setOpen(false)}>取消</Button>,
        <Button key="preview" onClick={handlePreview} loading={loading}>预览</Button>,
        <Button key="generate" type="primary" onClick={handleGenerate} loading={loading}>确定生成</Button>,
      ]}
    >
      <Form form={form} layout="vertical">
        <Form.Item label="生成行数" name="rowCount" initialValue={100}>
          <InputNumber min={1} max={100000} style={{ width: 200 }} />
        </Form.Item>
        
        {/* 添加猜一猜按钮 */}
        <div style={{ 
          marginBottom: 16, 
          display: 'flex', 
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <h4 style={{ margin: 0 }}>列配置</h4>
          <Button
            type="primary"
            icon={<MagicOutlined />}
            onClick={handleAiGuessExpression}
            disabled={columns.length === 0 || loading}
            size="small"
          >
            猜一猜
          </Button>
        </div>

        <Table
          columns={tableColumns}
          dataSource={columns}
          rowKey="columnName"
          pagination={false}
          size="small"
          loading={loading}
          scroll={{ y: 300 }}
        />
        
        {/* 其余内容保持不变 */}
      </Form>
    </Modal>
    {/* ... */}
  </>
);
```

### 5.5 后端扩展实现

#### 5.5.1 BuildPromptAction 扩展

文件路径: `chat2db-server/chat2db-server-web/chat2db-server-web-api/src/main/java/ai/chat2db/server/web/api/controller/ai/actions/BuildPromptAction.java`

```java
@Component
@Slf4j
public class BuildPromptAction implements Action<ChatState, ChatEvent> {
    
    @Autowired
    private PromptTemplateRegistry promptTemplateRegistry;
    
    @Override
    public void execute(StateMachine<ChatState, ChatEvent> stateMachine, 
                       Event<ChatEvent> event, 
                       ChatContext context) {
        try {
            ChatQueryRequest request = context.getRequest();
            String promptType = request.getPromptType();
            
            PromptTemplate template = promptTemplateRegistry.getTemplate(promptType.toLowerCase());
            if (template == null) {
                throw new IllegalStateException("Prompt template not found: " + promptType);
            }
            
            String prompt = template.getTemplate();
            
            // 替换通用占位符
            prompt = prompt.replace("{db_type}", getDbType(context));
            
            // 根据不同类型处理特殊逻辑
            if (PromptType.NL_2_COMMENT.name().equals(promptType) || 
                PromptType.NL_2_COMMENT_BATCH.name().equals(promptType)) {
                // 原有逻辑：schema DDL 已由 FetchSchemaAction 获取并存入 context
                String ddl = context.getSchemaDdl();
                prompt = prompt.replace("{schema}", ddl);
                prompt = prompt.replace("{description}", "");
                prompt = prompt.replace("{ext}", StringUtils.defaultString(request.getExt(), ""));
                prompt = prompt.replace("{message}", StringUtils.defaultString(request.getMessage(), ""));
                
            } else if (PromptType.NL_2_FIELD_MAPPING.name().equals(promptType)) {
                // 新增：字段映射 - schema DDL 已由 FetchSchemaAction 获取
                prompt = handleFieldMappingPrompt(prompt, context);
                
            } else if (PromptType.NL_2_DATA_EXPRESSION.name().equals(promptType)) {
                // 新增：数据生成表达式 - schema DDL 已由 FetchSchemaAction 获取
                prompt = handleDataExpressionPrompt(prompt, context);
            }
            
            context.setBuiltPrompt(prompt);
            log.info("Prompt built for type: {}, length: {}", promptType, prompt.length());
            
        } catch (Exception e) {
            log.error("Failed to build prompt", e);
            throw e;
        }
    }
    
    private String handleFieldMappingPrompt(String prompt, ChatContext context) {
        ChatQueryRequest request = context.getRequest();
        String tableName = CollectionUtils.isEmpty(request.getTableNames()) 
            ? "" : request.getTableNames().get(0);
        
        // schema DDL 已由 FetchSchemaAction 获取
        String schemaDdl = context.getSchemaDdl();
        
        // 从 ext 解析源字段列表
        String ext = request.getExt();
        String sourceFieldsText = "";
        if (StringUtils.isNotBlank(ext)) {
            FieldMappingExt extData = JSON.parseObject(ext, FieldMappingExt.class);
            if (extData != null && extData.getSourceFields() != null) {
                sourceFieldsText = extData.getSourceFields().stream()
                    .map(f -> "- " + f)
                    .collect(Collectors.joining("\n"));
            }
        }
        
        prompt = prompt.replace("{table_name}", tableName);
        prompt = prompt.replace("{source_fields}", sourceFieldsText);
        prompt = prompt.replace("{schema}", schemaDdl);
        prompt = prompt.replace("{message}", StringUtils.defaultString(request.getMessage(), ""));
        
        return prompt;
    }
    
    private String handleDataExpressionPrompt(String prompt, ChatContext context) {
        ChatQueryRequest request = context.getRequest();
        String tableName = CollectionUtils.isEmpty(request.getTableNames()) 
            ? "" : request.getTableNames().get(0);
        
        // schema DDL 已由 FetchSchemaAction 获取
        String schemaDdl = context.getSchemaDdl();
        
        prompt = prompt.replace("{table_name}", tableName);
        prompt = prompt.replace("{schema}", schemaDdl);
        prompt = prompt.replace("{message}", StringUtils.defaultString(request.getMessage(), ""));
        
        return prompt;
    }
    
    // 辅助类
    @Data
    public static class FieldMappingExt {
        private List<String> sourceFields;  // 只需要源文件字段
    }
}
```

**关键说明**:
- `FetchSchemaAction` 会在 `FETCHING_TABLE_SCHEMA` 状态时自动获取目标表 DDL 并存储到 `context.schemaDdl`
- `BuildPromptAction` 直接使用 `context.getSchemaDdl()` 即可，不需要前端传列信息
- 前端只需要传 `sourceFields`（导入映射场景）或直接传 `tableName`（生成表达式场景）

#### 5.5.2 AiChat 组件扩展 - JSON 解析

文件路径: `chat2db-client/src/components/AiChat/index.tsx`

**添加新的 JSON 提取函数：**
```typescript
// 字段映射 JSON 提取
function extractFieldMappingFromContent(content: string): IFieldMappingResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"mappings"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as IFieldMappingResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.mappings) {
      return directJson as IFieldMappingResult;
    }
  } catch (e) {
    console.error('[extractFieldMappingFromContent] Parse error:', e);
  }
  return null;
}

// 数据表达式 JSON 提取
function extractDataExpressionFromContent(content: string): IDataExpressionResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"column_expressions"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as IDataExpressionResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.column_expressions) {
      return directJson as IDataExpressionResult;
    }
  } catch (e) {
    console.error('[extractDataExpressionFromContent] Parse error:', e);
  }
  return null;
}
```

**修改 onDone 回调处理：**
```typescript
onDone: () => {
  console.log('[AiChat] onDone callback, sessionId:', sessionId);
  updateState(sessionId, 'COMPLETED');
  const currentSessions = useAiChatStore.getState().sessions;
  const session = currentSessions.get(sessionId);
  
  if (session?.currentContent) {
    addMessage(sessionId, {
      id: uuidv4(),
      role: 'assistant',
      content: session.currentContent,
      thinking: session.currentThinking || undefined,
    });

    // NL_2_COMMENT 处理（原有逻辑）
    if (promptType === 'NL_2_COMMENT' && commentCallbackRef.current) {
      try {
        const jsonContent = extractJsonFromContent(session.currentContent);
        if (jsonContent) {
          commentCallbackRef.current(jsonContent);
          message.success('AI 注释已生成，请查看并确认');
        }
      } catch (e) {
        console.error('[AiChat] Failed to parse comment JSON:', e);
        message.warning('无法解析 AI 生成的注释，请手动查看');
      }
      commentCallbackRef.current = undefined;
    }

    // NL_2_COMMENT_BATCH 处理（原有逻辑）
    if (promptType === 'NL_2_COMMENT_BATCH' && batchCommentCallbackRef.current) {
      try {
        const jsonContent = extractBatchJsonFromContent(session.currentContent);
        if (jsonContent) {
          batchCommentCallbackRef.current(jsonContent);
          message.success('AI 批量注释已生成');
        }
      } catch (e) {
        console.error('[AiChat] Failed to parse batch comment JSON:', e);
        message.warning('无法解析 AI 生成的批量注释，请手动查看');
      }
      batchCommentCallbackRef.current = undefined;
    }

    // NL_2_FIELD_MAPPING 处理（新增）
    if (promptType === 'NL_2_FIELD_MAPPING' && mappingCallbackRef.current) {
      try {
        const jsonContent = extractFieldMappingFromContent(session.currentContent);
        if (jsonContent) {
          console.log('[AiChat] Parsed field mapping result:', jsonContent);
          mappingCallbackRef.current(jsonContent);
          message.success('AI 字段映射推荐已生成，请查看并确认');
        }
      } catch (e) {
        console.error('[AiChat] Failed to parse field mapping JSON:', e);
        message.warning('无法解析 AI 生成的映射推荐，请手动查看');
      }
      mappingCallbackRef.current = undefined;
    }

    // NL_2_DATA_EXPRESSION 处理（新增）
    if (promptType === 'NL_2_DATA_EXPRESSION' && expressionCallbackRef.current) {
      try {
        const jsonContent = extractDataExpressionFromContent(session.currentContent);
        if (jsonContent) {
          console.log('[AiChat] Parsed data expression result:', jsonContent);
          expressionCallbackRef.current(jsonContent);
          message.success('AI 表达式推荐已生成，请查看并确认');
        }
      } catch (e) {
        console.error('[AiChat] Failed to parse data expression JSON:', e);
        message.warning('无法解析 AI 生成的表达式，请手动查看');
      }
      expressionCallbackRef.current = undefined;
    }
  }
  closeEventSource.current = undefined;
},
```

**添加新的 ref：**
```typescript
export default memo<IProps>(() => {
  // ... 现有 ref
  const commentCallbackRef = useRef<(result: ITableCommentResult) => void>();
  const batchCommentCallbackRef = useRef<(result: IBatchTableCommentResult) => void>();
  
  // 新增 ref
  const mappingCallbackRef = useRef<(result: IFieldMappingResult) => void>();
  const expressionCallbackRef = useRef<(result: IDataExpressionResult) => void>();
  
  // ...
```

**在 pendingAiChat 检测中设置新回调：**
```typescript
useEffect(() => {
  if (pendingAiChat && pendingAiChat.message) {
    // ... 原有逻辑
    
    if (pendingAiChat.onCommentGenerated) {
      commentCallbackRef.current = pendingAiChat.onCommentGenerated;
    }
    if (pendingAiChat.onBatchCommentGenerated) {
      batchCommentCallbackRef.current = pendingAiChat.onBatchCommentGenerated;
    }
    // 新增
    if (pendingAiChat.onMappingGenerated) {
      mappingCallbackRef.current = pendingAiChat.onMappingGenerated;
    }
    if (pendingAiChat.onExpressionGenerated) {
      expressionCallbackRef.current = pendingAiChat.onExpressionGenerated;
    }
    
    sendAiChatInternal(pendingAiChat.message, pendingAiChat.promptType, overrideBoundInfo);
    useWorkspaceStore.setState({ pendingAiChat: null });
  }
}, [pendingAiChat, boundInfo, sendAiChatInternal]);
```

### 5.6 ChatStateMachineConfig 扩展

文件路径: `chat2db-server/chat2db-server-web/chat2db-server-web-api/src/main/java/ai/chat2db/server/web/api/controller/ai/statemachine/ChatStateMachineConfig.java`

**说明**: 状态机不需要修改，因为新类型也走相同的流程：
```
IDLE → FETCHING_TABLE_SCHEMA → BUILDING_PROMPT → STREAMING → COMPLETED
```

`FetchSchemaAction` 会根据 `tableNames` 自动获取目标表 DDL 并存储到 `context.schemaDdl`。

**ChatController 中判断初始事件：**
```java
private ChatEvent determineInitialEvent(ChatQueryRequest request) {
    // 所有需要表结构的场景都使用 TABLES_PROVIDED
    if (CollectionUtils.isNotEmpty(request.getTableNames())) {
        return ChatEvent.TABLES_PROVIDED;
    } else {
        return ChatEvent.TABLES_NOT_PROVIDED;
    }
}
```

**关键说明**:
- `NL_2_FIELD_MAPPING` 和 `NL_2_DATA_EXPRESSION` 都需要表结构
- 直接复用现有的 `TABLES_PROVIDED` 事件即可
- `FetchSchemaAction` 会自动处理 DDL 获取逻辑

## 六、i18n 国际化

文件路径: `chat2db-client/src/i18n/zh-cn/common.ts`

```typescript
// 在 common.button 部分添加
'common.button.guess': '猜一猜',
'common.button.aiGuess': 'AI 猜一猜',
```

## 七、完整实现清单

### 前端修改

| 文件 | 修改内容 |
|------|----------|
| `src/pages/main/workspace/store/common.ts` | 新增 2 个 PromptType、2 个结果接口、扩展 IPendingAiChat |
| `src/components/ImportDataModal/index.tsx` | 添加猜一猜按钮、handleAiGuessMapping、handleMappingGenerated<br/>**只需传 sourceFields** |
| `src/components/DataGenerationModal/index.tsx` | 添加猜一猜按钮、handleAiGuessExpression、handleExpressionGenerated<br/>**只需传 tableName** |
| `src/components/AiChat/index.tsx` | 添加 2 个 JSON 解析函数、2 个 callback ref、onDone 中处理新类型 |
| `src/i18n/zh-cn/common.ts` | 添加国际化文案 |

### 后端修改

| 文件 | 修改内容 |
|------|----------|
| `PromptType.java` | 新增 NL_2_FIELD_MAPPING、NL_2_DATA_EXPRESSION |
| `prompt-templates.yml` | 新增 nl_2_field_mapping、nl_2_data_expression 模板 |
| `BuildPromptAction.java` | 扩展 handleFieldMappingPrompt、handleDataExpressionPrompt 方法<br/>**使用 context.getSchemaDdl() 获取 DDL** |

**关键优化**:
- 前端**不需要**传 schema 或列信息
- `FetchSchemaAction` 自动从数据库获取表 DDL 存入 `context.schemaDdl`
- `BuildPromptAction` 直接使用 `context.getSchemaDdl()` 填充 `{schema}` 占位符
- 完全复用现有状态机流程，无需修改状态机配置

## 八、测试要点

### 导入数据猜一猜

1. 上传 CSV/Excel 文件，进入步骤2
2. 点击"猜一猜"按钮
3. 验证 AI 面板打开并显示生成过程
4. 验证返回的映射结果自动填充到下拉框
5. 验证主键字段正确识别
6. 测试手动修改映射后继续导入
7. 测试无合适映射时的处理

### 生成数据猜一猜

1. 打开生成数据弹窗
2. 点击"猜一猜"按钮
3. 验证 AI 面板打开并显示生成过程
4. 验证返回的表达式自动填充到表达式列
5. 验证表达式语法正确性
6. 测试预览功能验证生成数据
7. 测试修改表达式后生成数据

### 边界情况

1. 无表名时的处理
2. AI 返回格式错误的容错处理
3. 网络超时的错误提示
4. 重复点击按钮的防抖处理
5. 大量字段（50+）的性能测试

## 九、后续优化建议

1. **置信度显示**: 在字段映射结果中显示 AI 匹配置信度，用颜色区分
2. **部分应用**: 允许用户选择性地应用 AI 推荐的部分字段
3. **历史记录**: 保存用户确认后的映射/表达式，用于训练优化
4. **模板推荐**: 基于历史数据推荐最常用的表达式模板
5. **批量导入**: 支持批量文件的智能映射
6. **自定义 Prompt**: 允许高级用户自定义 prompt 模板
