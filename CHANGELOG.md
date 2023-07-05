# 2.0.1
## 🐞 Bug Fixes
* Fix bug where executing multiple SQL statements at once will prompt for exceptions
* Fix getJDBCDriver error: null [Issue #123](https://github.com/chat2db/Chat2DB/issues/123)
* Fixing the Hive connection and then viewing columns results in an error. [Issue #136](https://github.com/chat2db/Chat2DB/issues/136)

## 🐞 问题修复
* 修复一次性执行多条SQL会提示异常的BUG
* 修复 getJDBCDriver error: null [Issue #123](https://github.com/chat2db/Chat2DB/issues/123)
* 修复hive方式连接，然后查看columns报错 [Issue #136](https://github.com/chat2db/Chat2DB/issues/136)

# 2.0.0
## What's Changed
* 🔥An intelligent solution that perfectly integrates SQL queries, AI assistant, and data analysis.
* 🔥New focused mode experience for advanced datasource management.
* AI integration of more LLM.
* Bilingual in Chinese and English support for client.
## 更新内容
* 🔥SQL查询、AI查询和数据报表完美集成的一体化解决方案设计与实现
* 🔥数据源连接和管理进阶为专注模式的全新体验设计与实现
* 🔥AI对话SQL升级为极简模式的全新交互设计与实现
* 客户端AI体验重大升级，响应更多用户的诉求
* 集成更多AI模型
* 客户端双语支持
* SQL查询基础功能优化
* Issue问题修复

# 1.0.11
* fixed: SQL有特殊字符时AI功能无法正常使用
* 增减版本信息检测

# 1.0.10
* fixed: 格式化SQL异常
* 优化AI网络连接异常提示
* 自定义AI添加本地样例
* Support OceanBase Presto DB2 Redis MongoDB Hive KingBase

# 1.0.9
* 修复Open Ai 无法连接的问题

* 支持国产达梦数据库
* 支持自定义OPEN AI API_HOST
* 🔥 支持自定义AI接口
* 支持主题色跟随系统

# 1.0.6
* 修复Oracle数据库字符集问题
* 修复mac安装提示的安全问题

# 1.0.5
* 🔥 优化Apple芯片的启动速度
* 修复Windows端数据库连接问题
* 修改database不生效
* NullPointerException

# 1.0.4
* 修复ClickHouse jdbc问题
* 修复连接池管理的NPE
* 修复前端编辑数据源报错问题 
* 增加数据库默认属性配置

# 1.0.3
* 🔥 支持SSH连接数据库
* 🎉 支持客户端查看日志
* 🎉 支持在Console中聊天对话
* 支持在客户端内设置OPENAI代理
* 已经启动过应用不会再重复启动

# 1.0.1
* 修复oracle连接配置编辑、以及连接查询问题
* 修复Apikey输出到日志可能存在的风险
* 修复web版本登录的bug

# 1.0.0
Chat2DB的 1.0.0 正式版来啦🎉🎉🎉🎉🎉🎉🎉🎉🎉

* 🌈 AI智能助手，支持自然语言转SQL、SQL转自然语言、SQL优化建议
* 👭 支持团队协作，研发无需知道线上数据库密码，解决企业数据库账号安全问题
* ⚙️ 强大的数据管理能力，支持数据表、视图、存储过程、函数、触发器、索引、序列、用户、角色、授权等管理
* 🔌 强大的扩展能力，目前已经支持Mysql、PostgreSQL、Oracle、SQLServer、ClickHouse、Oceanbase、H2、SQLite等等，未来会支持更多的数据库
* 🛡 前端使用 Electron 开发，提供 Windows、Mac、Linux 客户端、网页版本一体化的解决方案
* 🎁 支持环境隔离、线上、日常数据权限分离
