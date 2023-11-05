# 3.0.10
`2023-11-05`
**Changelog**
- ⭐【New Features】 Edit data support right click operation 
  1. Supports single-row replication of Insert, Update, table header fields, and row data 
  2. Clone the selected row 
  3. Replication of cell data is supported 
  4. You can set the cell to Null or Default 
  5. Row deletion is supported
  6. Supports zooming in to view or modify data
- ⭐【New Features】Supports the ctrl/cmd+c shortcut to copy row data or cell data
- ⭐【New Features】Supports the shortcut key ctrl/cmd+v to paste and copy row data/cell data to row/cell
- ⭐【New Features】Edit table structure supports setting primary keys in columns
- ⭐【New Features】History is added to the foldable panel on the right
- ⭐【New Features】Edit data to support cell-level undo changes
- ⭐【New Features】The Table tree node operation menu on the left supports copying table, field, key, index, and function names
- ⭐【New Features】The node in the left Table tree supports ctrl/cmd+c to copy the node text
- ⚡️【Optimize】Edit the table structure to add loading
- ⚡️【Optimize】The tree node operation menu supports right-clicking
- 🐞【Fixed】Fixed table structure editing floating-point decimal Settings display exception

**更新日志**
- ⭐【新功能】编辑数据支持右键操作
  1. 支持单行复制 Insert、Update、表头字段、行数据
  2. 支持克隆选中行
  3. 支持复制单元格数据
  4. 支持设置单元格为Null和Default
  5. 支持删除行
  6. 支持放大查看或修改数据
- ⭐【新功能】支持快捷键ctrl/cmd+c 复制行数据/单元格数据
- ⭐【新功能】支持快捷键ctrl/cmd+v 粘贴复制行数据/单元格数据到行/单元格
- ⭐【新功能】编辑表结构支持在列中设置主键
- ⭐【新功能】编辑数据支持单元格级别撤销修改
- ⭐【新功能】左侧Table树节点操作菜单支持复制表、字段、key、index、函数等名称
- ⭐【新功能】左侧Table树节点支持ctrl/cmd+c 复制节点文本
- ⭐【新功能】右侧可折叠面板中增加历史记录
- ⚡️【优化】编辑表结构添加loading
- ⚡️【优化】树节点操作菜单支持右键唤出
- 🐞【修复】修复表结构编辑浮点数小数位设置显示异常


# 3.0.9
`2023-11-01`
**Changelog**
- ⭐【New Features】Query results can be refreshed
- ⚡️【Optimize】Console Tabs adaptive width
- 🐞【Fixed】console save bug
- 🐞【Fixed】sqlite can only retrieve one piece of data

**更新日志**
- ⭐【新功能】查询结果支持刷新
- ⚡️【优化】控制台Tabs自适应宽度
- 🐞【修复】console保存bug
- 🐞【修复】sqlite只能查到一条数据问题

# 3.0.5
`2023-10-23`
**Changelog**
- ⭐【New Features】Supports visual database creation
- ⭐【New Features】Support hot update
- ⭐【New Features】Double-click the table to open it directly
- ⚡️【Optimize】The search table supports size fuzzy matching
- ⚡️【Optimize】Sort Database and Schema at the top
- ⚡️【Optimize】The queried data supports editing and modification in the large popup window of the view
- ⚡️【Optimize】Example Query the page loading effect of data
- ⚡️【Optimize】Keep the top focused tab always in the viewable area
- ⚡️【Optimize】Query data cell does not have scroll bar problem

**更新日志**
- ⭐【新功能】支持可视化创建数据库
- ⭐【新功能】支持热更新
- ⭐【新功能】双击表直接打开表
- ⚡️【优化】搜索表支持大小模糊匹配
- ⚡️【优化】Database 和 Schema 排序
- ⚡️【优化】查询的数据支持在查看的大的弹窗中编辑修改
- ⚡️【优化】查询数据翻页loading效果
- ⚡️【优化】保持顶部聚焦的tab永远在可视区域内
- ⚡️【优化】查询数据单元格没有滚动条问题


# 3.0.4
`2023-10-20`

**Changelog**
- 🐞【Fixed】Bugs are displayed when more than 100 data items are queried

**更新日志**
- 🐞【修复】查询数据超过100条时显示bug

# 3.0.1
`2023-10-19`

**Changelog**
- ⚡️【Optimize】Search result scroll bar
- ⚡️【Fixed】Oracle update result data bug

**更新日志**
- ⚡️【优化】查询结果滚动条
- 🐞【修复】Oracle更新结果数据错误

# 3.0.0
`2023-10-17`

**Changelog**
- 🔥【New Features】Support for team collaboration mode
- 🔥【New Features】Support for visual table structure creation, editing, and deletion
- 🔥【New Features】Support for editing, adding, and deleting query data results
- ⭐【New Features】Support the feature of importing Navicat/DBever data source links
- ⭐【New Features】Support for AI automatic sync table structure。
- ⭐【New Features】Support export table structure
- ⭐【New Features】Support importing SQL files
- ⭐【New Features】Support the connection supports adding an environment,better distinguishing between online and daily
- ⚡️【Optimize】Optimize Editor Intellisense
- ⚡️【Optimize】Optimize AI Input
- ⚡️【Optimize】Sql query support is stopped
- ⚡️【Optimize】Sql execution supports viewing the number of affected rows
- ⚡️【Optimize】Reclaiming non-administrator permissions to edit shared connections
- ⚡️【Optimize】`Cmd/Ctrl + R` Run SQL， `Cmd/Ctrl + Shift + R` Refresh Page
- 🐞【Fixed】Table operation columns are overridden by table comments
- 🐞【Fixed】The last Tab in the query result cannot be closed

**更新日志**
- 🔥【新功能】支持团队协作模式
- 🔥【新功能】支持可视化表结构新增、编辑、删除
- 🔥【新功能】支持查询数据结果编辑、新增、删除
- ⭐【新功能】支持导入Navicat/DBeaver数据源链接的功能
- ⭐【新功能】支持AI自动同步表结构
- ⭐【新功能】支持导出表结构
- ⭐【新功能】支持导入sql文件
- ⭐【新功能】连接支持添加环境标识，更好地区分在线和日常
- ⚡️【优化】优化编辑器提示功能
- ⚡️【优化】优化AI输入
- ⚡️【优化】sql查询支持停止
- ⚡️【优化】sql执行支持查看影响行数
- ⚡️【优化】回收非管理员编辑共享连接权限
- ⚡️【优化】`Cmd/Ctrl + R` 运行SQL， `Cmd/Ctrl + Shift + R` 刷新页面
- 🐞【修复】表操作列被表注释覆盖问题
- 🐞【修复】查询结果最后一个Tab无法关闭问题

# 2.1.0

## ⭐ New Features

- 🔥The team function is newly launched, supporting team collaboration. R&D does not require knowing the online database
  password, solving the security issue of enterprise database accounts. It is recommended to directly deploy the team
  function using 'docker'
- Added support for environment selection, better distinguishing between online and daily

## ⭐ 新特性

-🔥 新推出团队功能，支持团队协作。研发不需要知道在线数据库
密码，解决企业数据库帐号的安全问题。建议直接部署团队
使用'docker'的函数 -增加了环境选择的支持，更好地区分在线和日常

# 2.0.14

## 🐞 Bug Fixes

- Fix the issue of 'Oracle' query 'Blob' reporting errors
- Modify the paging logic and fix some SQL queries that cannot be queried

## ⭐ 新特性

- 🔥 团队功能全新上线，支持团队协作，研发无需知道线上数据库密码，解决企业数据库账号安全问题,团队功能建议直接使用 `docker` 部署
- 新增支持环境选择，更好的区分线上、日常环境

## 🐞 问题修复

- 修复 `Oracle` 查询 `Blob` 报错的问题
- 修改分页逻辑，修复部分 SQL 无法查询

# 2.0.13

- 修改分页逻辑，修复部分 SQL 无法查询

# 2.0.13

## ⭐ New Features

## 🐞 Bug Fixes

- Fixed a bug where sql formatting was not selected
- Fixed open view lag issue
- Solve the white screen problem of connected non-relational databases (non-relational databases are not supported)

## ⭐ 新特性

## 🐞 问题修复

- 修复不选中 sql 格式化的 bug
- 修复打开视图卡顿问题
- 解决已连接的非关系型数据库打开白屏问题（暂不支持非关系性数据库）

# 2.0.12

## ⭐ New Features

- 🔥Supports viewing views, functions, triggers, and procedures
- Support selected sql formatting
- Added new dark themes

## 🐞 Bug Fixes

- Fixed sql formatting failure issue
- Fixed an issue where locally stored theme colors and background colors are incompatible with the new version, causing
  page crashes
- Logs desensitize sensitive data
- Fix the issue of 'CLOB' not displaying specific content [Issue #440](https://github.com/chat2db/Chat2DB/issues/440)
- Fix the problem that non-Select does not display query results
- Fix the problem that Oracle cannot query without schema
- Fix the problem of special type of SQL execution error reporting
- Fix the problem that the test link is successful, but the error is reported when saving the link

## ⭐ 新特性

- 🔥 支持查看视图、函数、触发器、存储过程
- 支持选中 sql 格式化
- 增加新的暗色主题

## 🐞 问题修复

- 修复 sql 格式化会失败问题
- 修复本地存储的主题色、背景色与新版本不兼容时会导致页面崩溃问题
- 日志对敏感数据进行脱敏
- 修复 `CLOB` 不展示具体内容的问题 [Issue #440](https://github.com/chat2db/Chat2DB/issues/440)
- 修复非 Select 不展示查询结果的问题
- 修复 Oracle 不带 schema 无法查询的问题
- 修复特殊类型的 SQL 执行报错的问题
- 修复测试链接成功，但保存链接报错的问题

# 2.0.11

## 🐞 Bug Fixes

- Fix the issue where SSH does not support older versions of encryption algorithms
- Fix the issue of SQL Server 2008 not being able to connect
- Fix the issue of not being able to view table name notes and field notes

## 🐞 问题修复

- 修复 SSH 不支持老版本加密算法的问题
- 修复 SQLServer2008 无法连接的问题
- 修复无法查看表名备注、字段备注的问题

# 2.0.10

## 🐞 Bug Fixes

- Activate the console for the latest operation when you create or start a console、Records the last console used
- The replication function of the browser, such as edge, is unavailable
- table Indicates an error when ddl is exported after the search
- Adds table comments and column field types and comments

## 🐞 问题修复

- 新建、开打 console 时激活最新操作的 console、记录最后一次使用的 console
- edge 等浏览器复制功能无法正常使用
- table 搜索后导出 ddl 报错
- 增加表注释以及列字段类型和注释
- 当数据源添加了 database 默认选择第一个 database

# 2.0.9

## 🐞 Bug Fixes

-Fix the issue of Windows flash back

## 🐞 问题修复

- 修复 windows 闪退的问题

# 2.0.8

## 🐞 Bug Fixes

- Repair the Scientific notation in some databases [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- Fix some cases where data is not displayed

## 🐞 问题修复

- 修复部分数据库出现科学计数法的情况 [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- 修复部分情况数据不展示

# 2.0.7

## ⭐ New Features

- Export query result as file is supported

## 🐞 Bug Fixes

- Fixed ai config issues [Issue #346](https://github.com/chat2db/Chat2DB/issues/346)

## ⭐ 新特性

- 支持导出查询结果

## 🐞 问题修复

- 修复 ai 配置 [Issue #346](https://github.com/chat2db/Chat2DB/issues/346)

# 2.0.6

## 🐞 Bug Fixes

- Fixed: When there are too many tables under the selected library, the "New Console" button at the bottom
  disappears [Issue #314](https://github.com/chat2db/Chat2DB/issues/314)

## 🐞 问题修复

- Fixed: 当选择的库下面表过多时最下面的“新建控制台”按钮消失 [Issue #314](https://github.com/chat2db/Chat2DB/issues/314)

# 2.0.5

## ⭐ New Features

- Supports 25 free uses of AIGC every day.
- Support for querying data pagination.
- Support switching between multiple databases in PostgreSQL.
- Support for hot updating of client-side code allows for rapid bug fixes.

## 🐞 Bug Fixes

- Default return alias for returned results [Issue #270](https://github.com/chat2db/Chat2DB/issues/270)
- Fixed around 100 bugs, of course, many were repetitive bugs.

## ⭐ 新特性

- 支持每天 25 次免费使用 AIGC
- 支持查询数据分页
- 支持 PostgreSQL 数据库多个 database 的切换
- 支持客户端代码热更新可以快速修复 bug
- 支持客户端字体放大缩小

## 🐞 问题修复

- 返回结果默认返回别名 [Issue #270](https://github.com/chat2db/Chat2DB/issues/270)
- 修复了 100 个左右的 bug，当然很多是重复 bug

# 2.0.4

## ⭐ New Features

- Support DB2 database
- Support renaming after console saving
- Support prompts during SQL execution

## 🐞 Bug Fixes

- Fix the bug that the database in sqlserver is all numbers
- Fix ssh connection bug

## ⭐ 新特性

- 支持 DB2 数据库
- 支持控制台保存后重命名
- 支持 SQL 执行中提示

## 🐞 问题修复

- 修复 sqlserver 中 database 全是数字的 bug
- 修复 ssh 连接 bug

# 2.0.2

## ⭐ New Features

- Brand new AI binding process
- Support for custom drivers

## 🐞 Bug Fixes

- Optimized dataSource link editing
- Enhanced error messages
- Improved table selection interaction
- Enhanced table experience

## ⭐ 新特性

- 全新的 AI 绑定流程
- 支持自定义驱动

## 🐞 问题修复

- 优化 dataSource 链接编辑
- 优化错误提示
- 优化选表交互
- 优化表格体验

# 2.0.1

## 🐞 Bug Fixes

- Fix bug where executing multiple SQL statements at once will prompt for exceptions
- Fix getJDBCDriver error: null [Issue #123](https://github.com/chat2db/Chat2DB/issues/123)
- Fixing the Hive connection and then viewing columns results in an
  error. [Issue #136](https://github.com/chat2db/Chat2DB/issues/136)

## 🐞 问题修复

- 修复一次性执行多条 SQL 会提示异常的 BUG
- 修复 getJDBCDriver error: null [Issue #123](https://github.com/chat2db/Chat2DB/issues/123)
- 修复 hive 方式连接，然后查看 columns 报错 [Issue #136](https://github.com/chat2db/Chat2DB/issues/136)

# 2.0.0

## What's Changed

- 🔥An intelligent solution that perfectly integrates SQL queries, AI assistant, and data analysis.
- 🔥New focused mode experience for advanced datasource management.
- AI integration of more LLM.
- Bilingual in Chinese and English support for client.

## 更新内容

- 🔥SQL 查询、AI 查询和数据报表完美集成的一体化解决方案设计与实现
- 🔥 数据源连接和管理进阶为专注模式的全新体验设计与实现
- 🔥AI 对话 SQL 升级为极简模式的全新交互设计与实现
- 客户端 AI 体验重大升级，响应更多用户的诉求
- 集成更多 AI 模型
- 客户端双语支持
- SQL 查询基础功能优化
- Issue 问题修复

# 1.0.11

- fixed: SQL 有特殊字符时 AI 功能无法正常使用
- 增减版本信息检测

# 1.0.10

- fixed: 格式化 SQL 异常
- 优化 AI 网络连接异常提示
- 自定义 AI 添加本地样例
- Support OceanBase Presto DB2 Redis MongoDB Hive KingBase

# 1.0.9

- 修复 Open Ai 无法连接的问题

- 支持国产达梦数据库
- 支持自定义 OPEN AI API_HOST
- 🔥 支持自定义 AI 接口
- 支持主题色跟随系统

# 1.0.6

- 修复 Oracle 数据库字符集问题
- 修复 mac 安装提示的安全问题

# 1.0.5

- 🔥 优化 Apple 芯片的启动速度
- 修复 Windows 端数据库连接问题
- 修改 database 不生效
- NullPointerException

# 1.0.4

- 修复 ClickHouse jdbc 问题
- 修复连接池管理的 NPE
- 修复前端编辑数据源报错问题
- 增加数据库默认属性配置

# 1.0.3

- 🔥 支持 SSH 连接数据库
- 🎉 支持客户端查看日志
- 🎉 支持在 Console 中聊天对话
- 支持在客户端内设置 OPENAI 代理
- 已经启动过应用不会再重复启动

# 1.0.1

- 修复 oracle 连接配置编辑、以及连接查询问题
- 修复 Apikey 输出到日志可能存在的风险
- 修复 web 版本登录的 bug

# 1.0.0

Chat2DB 的 1.0.0 正式版来啦 🎉🎉🎉🎉🎉🎉🎉🎉🎉

- 🌈 AI 智能助手，支持自然语言转 SQL、SQL 转自然语言、SQL 优化建议
- 👭 支持团队协作，研发无需知道线上数据库密码，解决企业数据库账号安全问题
- ⚙️ 强大的数据管理能力，支持数据表、视图、存储过程、函数、触发器、索引、序列、用户、角色、授权等管理
- 🔌 强大的扩展能力，目前已经支持 Mysql、PostgreSQL、Oracle、SQLServer、ClickHouse、Oceanbase、H2、SQLite 等等，未来会支持更多的数据库
- 🛡 前端使用 Electron 开发，提供 Windows、Mac、Linux 客户端、网页版本一体化的解决方案
- 🎁 支持环境隔离、线上、日常数据权限分离


# 0.0.0
`2023--`

**Changelog**
- ⭐【New Features】
- ⚡️【Optimize】
- 🐞【Fixed】


**更新日志**
- ⭐【新功能】
- ⚡️【优化】
- 🐞【修复】



