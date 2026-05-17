# AI SQL错误修复功能

## 功能概述

当SQL执行出现错误时，在错误结果页面显示"AI修复"按钮。点击后，AI会分析错误原因并提供修复后的SQL。

## 使用流程

1. 执行SQL语句
2. 如果执行失败，显示错误信息
3. 点击"AI修复"按钮
4. 自动切换到AI聊天面板
5. AI分析错误并返回修复结果
6. 显示修复后的SQL和说明

## 实现细节

### 前端修改

1. **SearchResult组件** (`src/components/SearchResult/index.tsx`)
   - 添加 `handleAiFix` 函数
   - 错误显示区域添加"AI修复"按钮
   - 传递错误信息和原始SQL到AI

2. **新增 PromptType**: `SQL_FIX`
   - 在 `common.ts` 中添加类型定义
   - 添加 `ISqlFixResult` 接口

3. **AiChat组件** 
   - 添加 `extractSqlFixFromContent` JSON解析函数
   - 添加 `sqlFixCallbackRef` 回调
   - 在 `onDone` 中处理 `SQL_FIX` 类型结果

4. **国际化**
   - 中文: `common.button.aiFix`: 'AI修复'
   - 英文: `common.button.aiFix`: 'AI Fix'

### 后端修改

1. **PromptType枚举** 
   - 新增 `SQL_FIX("SQL错误修复")`

2. **prompt-templates.yml**
   - 新增 `sql_fix` 模板
   - 占位符: `{error_message}`, `{original_sql}`, `{db_type}`

3. **PromptBuilderImpl**
   - 更新 `validateContext` 允许 SQL_FIX 不需要 message
   - 更新 `fillTemplate` 处理 `{error_message}` 和 `{original_sql}` 占位符

## AI返回格式

```json
{
  "error_analysis": "错误原因分析",
  "fixed_sql": "修复后的SQL语句",
  "explanation": "修复说明",
  "can_fix": true
}
```

## 样式

- 错误容器使用 flex 布局垂直居中
- AI修复按钮显示在错误信息下方
- 使用 Iconfont 图标（code: \ue6ae）
