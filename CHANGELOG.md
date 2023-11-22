## 3.0.14

`2023-11-20`

**Changelog**

- 🐞【Fixed】Team paging problem
- 🐞【Fixed】Oracle service name bug
- 🐞【Fixed】Oracle datatype error
- 🐞【Fixed】Fixed an issue where MySQL changed table structure without displaying comments.
- ⚡️【Optimize】Support database or schema
- 【Developer】Friends don't worry, the company has some things recently, and is preparing 3.1.0, be patient

## 3.0.13

`2023-11-15`

**Changelog**

- 🐞【Fixed】oracle datatype error
- 🐞【Fixed】DM index error


## 3.0.12

`2023-11-13`

**更新日志**

- 🐞【Fixed】Copy as insert first row lost problem


## 3.0.11

`2023-11-08`

**Changelog**

- ⭐【New Features】Oracle connections support the Service name mode
- ⭐【New Features】[New function] Edit table data to support batch copy, clone, delete (click 1X1 cell to select/cancel, hold down shift/ctrl/cmd to select multiple)
- ⚡️【Optimize】After the update is completed, click restart to close the problem that cannot be automatically opened (hot update cannot fix this problem, you need to download a new version to cover the client)
- 🐞【Fixed】database and schema searches support case ambiguity matching
- 🐞【Fixed】Where database was not displayed after being added
- 🐞【Fixed】sql formatting to ·now()· format error


## 3.0.10

`2023-11-06`

**Changelog**
- ⭐【New Features】Add multiple CN AI configurations Add multiple domestic AI configurations
  - Supports single-row replication of Insert, Update, table header fields, and row data 
  - Clone the selected row 
  - Replication of cell data is supported 
  - You can set the cell to Null or Default 
  - Row deletion is supported
  - Supports zooming in to view or modify data
- ⭐【New Features】Supports the ctrl/cmd+c shortcut to copy row data or cell data
- ⭐【New Features】Supports the shortcut key ctrl/cmd+v to paste and copy row data/cell data to row/cell
- ⭐【New Features】Edit table structure supports setting primary keys in columns
- ⭐【New Features】History is added to the foldable panel on the right
- ⭐【New Features】Edit data to support cell-level undo changes
- ⭐【New Features】The Table tree node operation menu on the left supports copying table, field, key, index, and function names
- ⭐【New Features】The node in the left Table tree supports ctrl/cmd+c to copy the node text
- ⭐【New Features】You can right-click to close tabs, close other tabs, or close all tabs
- ⭐【New Features】Top database and schema support search
- ⚡️【Optimize】Smart prompts for SQL editing
- ⚡️【Optimize】Edit the table structure to add loading
- ⚡️【Optimize】The tree node operation menu supports right-clicking
- 🐞【Fixed】Fixed table structure editing floating-point decimal Settings display exception
- 🐞【Fixed】Fixed switching the saved sql on the console will eliminate the problem
- 🐞【Fixed】After multiple tables are paged, the context cannot select a table other than the current page
- 🐞【Fixed】Console and resulting Tabs mouse wheel not scrolling

## 3.0.9

`2023-11-01`

**Changelog**
- ⭐【New Features】Query results can be refreshed
- ⚡️【Optimize】Console Tabs adaptive width
- 🐞【Fixed】console save bug
- 🐞【Fixed】sqlite can only retrieve one piece of data

## 3.0.5

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

## 3.0.4

`2023-10-20`

**Changelog**
- 🐞【Fixed】Bugs are displayed when more than 100 data items are queried

## 3.0.1

`2023-10-19`

**Changelog**
- ⚡️【Optimize】Search result scroll bar
- ⚡️【Fixed】Oracle update result data bug

## 3.0.0

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

## 2.1.0

## ⭐ New Features

- 🔥The team function is newly launched, supporting team collaboration. R&D does not require knowing the online database
  password, solving the security issue of enterprise database accounts. It is recommended to directly deploy the team
  function using 'docker'
- Added support for environment selection, better distinguishing between online and daily

## 2.0.14

## 🐞 Bug Fixes

- Fix the issue of 'Oracle' query 'Blob' reporting errors
- Modify the paging logic and fix some SQL queries that cannot be queried

## 2.0.13

## ⭐ New Features

## 🐞 Bug Fixes

- Fixed a bug where sql formatting was not selected
- Fixed open view lag issue
- Solve the white screen problem of connected non-relational databases (non-relational databases are not supported)

## 2.0.12

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

## 2.0.11

## 🐞 Bug Fixes

- Fix the issue where SSH does not support older versions of encryption algorithms
- Fix the issue of SQL Server 2008 not being able to connect
- Fix the issue of not being able to view table name notes and field notes

## 2.0.10

## 🐞 Bug Fixes

- Activate the console for the latest operation when you create or start a console、Records the last console used
- The replication function of the browser, such as edge, is unavailable
- table Indicates an error when ddl is exported after the search
- Adds table comments and column field types and comments

## 2.0.9

## 🐞 Bug Fixes

-Fix the issue of Windows flash back

## 2.0.8

## 🐞 Bug Fixes

- Repair the Scientific notation in some databases [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- Fix some cases where data is not displayed

## 🐞 问题修复

- 修复部分数据库出现科学计数法的情况 [Issue #378](https://github.com/chat2db/Chat2DB/issues/378)
- 修复部分情况数据不展示

## 2.0.7

## ⭐ New Features

- Export query result as file is supported

## 🐞 Bug Fixes

- Fixed ai config issues [Issue #346](https://github.com/chat2db/Chat2DB/issues/346)

## 2.0.6

## 🐞 Bug Fixes

- Fixed: When there are too many tables under the selected library, the "New Console" button at the bottom
  disappears [Issue #314](https://github.com/chat2db/Chat2DB/issues/314)

## 2.0.5

## ⭐ New Features

- Supports 25 free uses of AIGC every day.
- Support for querying data pagination.
- Support switching between multiple databases in PostgreSQL.
- Support for hot updating of client-side code allows for rapid bug fixes.

## 🐞 Bug Fixes

- Default return alias for returned results [Issue #270](https://github.com/chat2db/Chat2DB/issues/270)
- Fixed around 100 bugs, of course, many were repetitive bugs.

## 2.0.4

## ⭐ New Features

- Support DB2 database
- Support renaming after console saving
- Support prompts during SQL execution

## 🐞 Bug Fixes

- Fix the bug that the database in sqlserver is all numbers
- Fix ssh connection bug

## 2.0.2

## ⭐ New Features

- Brand new AI binding process
- Support for custom drivers

## 🐞 Bug Fixes

- Optimized dataSource link editing
- Enhanced error messages
- Improved table selection interaction
- Enhanced table experience

## 2.0.1

## 🐞 Bug Fixes

- Fix bug where executing multiple SQL statements at once will prompt for exceptions
- Fix getJDBCDriver error: null [Issue #123](https://github.com/chat2db/Chat2DB/issues/123)
- Fixing the Hive connection and then viewing columns results in an
  error. [Issue #136](https://github.com/chat2db/Chat2DB/issues/136)


## 2.0.0

## What's Changed

- 🔥An intelligent solution that perfectly integrates SQL queries, AI assistant, and data analysis.
- 🔥New focused mode experience for advanced datasource management.
- AI integration of more LLM.
- Bilingual in Chinese and English support for client.

## 1.0.11

- fixed: SQL 有特殊字符时 AI 功能无法正常使用
- 增减版本信息检测

## 1.0.10

- fixed: The formatted SQL is abnormal 
- Optimized AI network connection exception message 
- Custom AI Adds a local example 
- Support OceanBase Presto DB2 Redis MongoDB Hive KingBase

## 1.0.9

- Fixed an issue where Open Ai could not connect 
 
- Support domestic Dameng database 
- Supports custom OPEN AI API_HOST 
- 🔥 Supports custom AI interfaces 
- Support theme color following system

## 1.0.6

- Fixed Oracle database character set issues 
- Fix mac installation prompts for security issues

## 1.0.5

- 🔥 Optimizes the boot speed of Apple chips 
- Rectify database connection problems on Windows 
- The database modification does not take effect 
- NullPointerException

## 1.0.4

- Fix ClickHouse jdbc issues 
- Restore the NPE managed by the connection pool 
- Fixed front-end edit data source error 
- Added default database properties

## 1.0.3

- 🔥 Supports SSH connection to the database 
- 🎉 Allows a client to view logs 
- 🎉 Supports chat sessions on the Console 
- Supports setting OPENAI agents on clients 
- An application that has been started will not be started again

## 1.0.1

- Fixed oracle connection configuration editing and connection query issues 
- Fix possible risks of Apikey output to logs 
- Fixed the login bug of web version

## 1.0.0

- Fixed oracle connection configuration editing and connection query issues 
- Fix possible risks of Apikey output to logs 
- repair bugChat2DB login web version 1.0.0 release come 🎉 🎉 🎉 🎉 🎉 🎉 🎉 🎉 🎉 
 
- 🌈 AI intelligent assistant, supports natural language to SQL, SQL to natural language, and SQL optimization suggestions 
- 👭 Support team collaboration, R & D does not need to know the online database password, to solve the security problem of enterprise database account 
- ⚙️ Provides powerful data management capabilities, including data tables, views, stored procedures, functions, triggers, indexes, sequences, users, roles, and authorization 
- 🔌 Powerful expansion ability, currently supports Mysql, PostgreSQL, Oracle, SQLServer, ClickHouse, Oceanbase, H2, SQLite and so on, the future will support more databases 
- 🛡 The front-end is developed using Electron, providing an integrated solution of Windows, Mac, Linux clients, and web versions 
- 🎁 Supports environment isolation and separation of online and daily data rights


## 0.0.0

`2023--`

**Changelog**
- ⭐【New Features】
- ⚡️【Optimize】
- 🐞【Fixed】




