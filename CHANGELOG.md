
# 2.0.10

## 🐞 Bug Fixes

- Activate the console for the latest operation when you create or start a console、Records the last console used
- The replication function of the browser, such as edge, is unavailable
- table Indicates an error when ddl is exported after the search
- Adds table comments and column field types and comments

## 🐞 问题修复

- 新建、开打console时激活最新操作的console、记录最后一次使用的console
- edge等浏览器复制功能无法正常使用
- table搜索后导出ddl报错
- 增加表注释以及列字段类型和注释
- 当数据源添加了database默认选择第一个database

# 2.0.9

## 🐞 Bug Fixes

-Fix the issue of Windows flash back

## 🐞 问题修复

- 修复windows闪退的问题

# 2.0.8

## 🐞 Bug Fixes

- Repair the Scientific notation in some databases  [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- Fix some cases where data is not displayed

## 🐞 问题修复

- 修复部分数据库出现科学计数法的情况  [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- 修复部分情况数据不展示

# 2.0.7

## ⭐ New Features

- Export query result as file is supported

## 🐞 Bug Fixes

- Fixed ai config issues [Issue #346](https://github.com/chat2db/Chat2DB/issues/346)

## ⭐ 新特性

- 支持导出查询结果

## 🐞 问题修复

- 修复ai配置 [Issue #346](https://github.com/chat2db/Chat2DB/issues/346)

# 2.0.6

## 🐞 Bug Fixes

- Fixed: When there are too many tables under the selected library, the "New Console" button at the bottom
  disappears  [Issue #314](https://github.com/chat2db/Chat2DB/issues/314)

## 🐞 问题修复

- Fixed: 当选择的库下面表过多时最下面的“新建控制台”按钮消失  [Issue #314](https://github.com/chat2db/Chat2DB/issues/314)

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
