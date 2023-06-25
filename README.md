<h1 align="center">Chat2DB</h1>

<div align="center">

 An intelligent and versatile general-purpose SQL client and reporting tool for databases which integrates ChatGPT capabilities.

[![License](https://img.shields.io/github/license/alibaba/fastjson2?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub release](https://img.shields.io/github/release/alibaba/ali-dbhub)](https://github.com/alibaba/ali-dbhub/releases)
[![GitHub Stars](https://img.shields.io/github/stars/alibaba/ali-dbhub)](https://github.com/alibaba/ali-dbhub/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/alibaba/ali-dbhub)](https://github.com/alibaba/ali-dbhub/fork)
[![GitHub Contributors](https://img.shields.io/github/contributors/alibaba/ali-dbhub)](https://github.com/alibaba/ali-dbhub/graphs/contributors)

</div>

<div align="center">
<p align="center"><b>Share Chat2DB Repository </b></p>
<p align="center">
<a href="https://twitter.com/intent/tweet?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/alibaba/Chat2DB&hashtags=ChatGPT,AGI,SQL%20Client,Reporting%20tool" target="blank" > <img src="https://img.shields.io/twitter/follow/_Chat2DB?label=Share Repo on Twitter&style=social" alt=""/> </a> 
<a href="https://t.me/share/url?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/alibaba/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Telegram&logo=Telegram&style=social&url=https://github.com/alibaba/Chat2DB" alt="Share on Telegram"/></a>
<a href="https://api.whatsapp.com/send?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%20https://github.com/alibaba/Chat2DB"><img src="https://img.shields.io/twitter/url?label=whatsapp&logo=whatsapp&style=social&url=https://github.com/alibaba/Chat2DB" /></a>
<a href="https://www.reddit.com/submit?url=https://github.com/alibaba/Chat2DB&title=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities." target="blank"><img src="https://img.shields.io/twitter/url?label=Reddit&logo=Reddit&style=social&url=https://github.com/alibaba/Chat2DB" alt="Share on Reddit"/></a>
<a href="mailto:?subject=Check%20this%20GitHub%20repository%20out.&body=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%3A%0Ahttps://github.com/alibaba/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Gmail&logo=Gmail&style=social&url=https://github.com/alibaba/Chat2DB"/></a>
</p>

**License Notation**: Chat2DB is constructed and distributed for personal and non-commercial use only. For commercial use of this project, please contact corresponding authors.

Languages： English | [中文](README_CN.md)
</div>

## 📖 Introduction
&emsp; &emsp;Chat2DB is a multi-database client tool that is open-source and free from Alibaba. It supports local installation on Windows and Mac, as well as server-side deployment and web page access. Compared to traditional database client software such as Navicat and DBeaver, Chat2DB integrates AIGC's capabilities and is able to convert natural language into SQL. It can also convert SQL into natural language and provide optimization suggestions for SQL to greatly enhance the efficiency of developers. It is a tool for database developers in the AI era, and even non-SQL business operators in the future can use it to quickly query business data and generate reports.
## ✨ Features
- 🌈 AI intelligent assistant, supporting natural language to SQL conversion, SQL to natural language conversion, and SQL optimization suggestions
- 👭 Support team collaboration, developers do not need to know the online database password, solving the problem of enterprise database account security
- ⚙️ Powerful data management capability, supporting management of data tables, views, stored procedures, functions, triggers, indexes, sequences, users, roles, authorizations, etc.
- 🔌 Powerful extension capability, currently supporting MySQL, PostgreSQL, Oracle, SQLServer, ClickHouse, OceanBase, H2, SQLite, etc., and more databases will be supported in the future
- 🛡 Front-end development using Electron, providing a solution that integrates Windows, Mac, Linux clients, and web versions
- 🎁 Support environment isolation, online, and daily data permission separation


## ⏬ Download and Install

| Description                   | Download                                                                                                                                                   |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Windows      | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB%20Setup%201.0.11.exe](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB%20Setup%201.0.11.exe) |
| MacOS ARM64 | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11-arm64.dmg](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11-arm64.dmg)         |
| MacOS X64  | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11.dmg](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11.dmg)                     |
| Jar包         | [https://oss-chat2db.alibaba.com/release/1.0.11/ali-dbhub-server-start.jar](https://oss-chat2db.alibaba.com/release/1.0.11/ali-dbhub-server-start.jar)     |

## 🚀 Supported databases
| Databases     | Status    |
|---------------|---------|
| Mysql         | ✅       |
| H2            | ✅       |
| Oracle        | ✅       |       
| PostgreSQL    | ✅       |                                                                                                                
| SQLServer     | ✅       |   
| SQLLite       | ✅       |   
| MariaDB       | ✅       |   
| ClickHouse    | ✅       |   
| DM            | ✅       |   
| Presto        | ✅       |   
| DB2           | ✅       |   
| OceanBase     | ✅       |   
| Redis         | ✅       |   
| Hive          | ✅       |   
| KingBase      | ✅       |   
| MongoDB       | ✅       |   
| Hbase         |Planning |  
| Elasticsearch | Planning | 
| openGauss     | Planning | 
| TiDB          | Planning |
| InfluxDB      | Planning |

## 🌰 Demo
### Create data source
  <a><img src="https://gw.alicdn.com/imgextra/i3/O1CN01PlpLYy1hIq5aMugpg_!!6000000004255-0-tps-3446-1750.jpg" width="100%"/></a>
### Data source management
  <a><img src="https://gw.alicdn.com/imgextra/i2/O1CN01DpzZJL1T7w2Xv9VMl_!!6000000002336-0-tps-3410-1662.jpg" width="100%"/></a>
### SQL console
  <a><img src="https://gw.alicdn.com/imgextra/i2/O1CN01aidnkx1Oo0LJ1Pdty_!!6000000001751-0-tps-3440-1736.jpg" width="100%"/></a>
### AI intelligent assistant
  <a><img src="https://gw.alicdn.com/imgextra/i4/O1CN01iaSXot1W6VeaDFbK2_!!6000000002739-0-tps-3430-1740.jpg" width="100%"/></a>

## 🔥 AI Configuration
### CONFIGURE OPENAI

Option 1 (recommended): To use the ChatSql function of OPENAI, two conditions must be met:

- You need an OPENAI_API_KEY.
- The client's network can connect to the OPENAI website, and for users in China, a VPN is required. Note: If the local VPN is not fully effective, the network connectivity can be ensured by setting the network proxy HOST and PORT in the client.

<a><img src="https://img.alicdn.com/imgextra/i2/O1CN01anrJMI1FEtSBbmTau_!!6000000000456-0-tps-1594-964.jpg" width="60%"/></a>

Option 2 (recommended): We provide a unified proxy service.

- No OPENAI_API_KEY is required.
- No proxy or VPN is required, as long as the network is connected.

To facilitate users' quick use of AI capabilities, you can scan the QR code below to follow our WeChat public account and apply for our custom API_KEY. 

<a><img src="https://oss-chat2db.alibaba.com/static/%E5%85%AC%E4%BC%97%E5%8F%B7.jpg" width="40%"/></a>

After the application is completed, refer to the following figure for configuration and usage. Config Api Host as http://test.sqlgpt.cn/gateway/api/.

<a><img src="https://img.alicdn.com/imgextra/i2/O1CN01xNobD21mo3B1ILrs2_!!6000000005000-0-tps-592-515.jpg" width="60%"/></a>

### CONFIGURE CUSTOM AI
- Customized AI can be any LLM that you deployed, such as ChatGLM、ChatGPT、ERNIE Bot、Tongyi Qianwen, and so on. However, the customized interface need to conform to the protocol definition. Otherwise, secondary development may be required. Two DEMOs are provided in the code, the configuration is as shown below. In specific use, you can refer to the DEMO interface to write a custom interface, or directly perform secondary development in the DEMO interface.
- DEMO for configuring customized stream output interface.
  <a><img src="https://img.alicdn.com/imgextra/i1/O1CN01xMqnRH1DlkdSekvSF_!!6000000000257-0-tps-591-508.jpg" width="60%"/></a>
- DEMO for configuring customized non-stream output interface.
  <a><img src="https://img.alicdn.com/imgextra/i1/O1CN01JqmbGo1fW0GAQhRu4_!!6000000004013-0-tps-587-489.jpg" width="60%"/></a>



## 📦 Docker installation

```bash
docker pull chat2db/chat2db:latest
```

## 🎯 Operating Environment
Note: If local debugging is required
- Java runtime Open JDK 17
- JRE reference packaging and deployment method of jre.
- Node runtime environment Node16 Node.js.

## 💻 Local Debugging
- git clone to local
```bash
$ git clone git@github.com:alibaba/Chat2DB.git
```
- Front-End installation
```bash
$ cd Chat2DB/ali-dbhub-client
$ npm install # Mounting front-end dependency
$ npm run build:prod # Package js to the source directory on the back end
```
- Backend debug
```bash
$ cd ../ali-dbhub-server
$ mvn clean install # maven 3.8 or later needs to be installed
$ cd ali-dbhub-server/ali-dbhub-server-start/target/
$ java -jar -Dchatgpt.apiKey=xxxxx ali-dbhub-server-start.jar  # To launch the chat application, you need to enter the ChatGPT key for the chatgpt.apiKey. Without entering it, you won't be able to use the AIGC function.
$ # open http://127.0.0.1:10821 to start debug Note: Front-end installation is required
```

- Front-End debug
```bash
$ cd Chat2DB/ali-dbhub-client
$ npm install 
$ npm run start 
$ # open http://127.0.0.1:10821  to start Front-End debug
$ # Note Front-end page completely depends on the service, so front-end students need to debug the back-end project
```
But front debugging need mapping of resources, you can download [XSwitch](https://chrome.google.com/webstore/detail/idkjhjggpffolpidfkikidcokdkdaogg), add the following configuration file
``` json
{
  "proxy": [
    [
      "http://127.0.0.1:10821/(.*).js$",
      "http://127.0.0.1:8001/$1.js",
    ],
    [
      "http://127.0.0.1:10821/(.*).css$",
      "http://127.0.0.1:8001/$1.css",
    ],
    [
      "http://127.0.0.1:10821/static/front/(.*)",
      "http://127.0.0.1:8001/$1",
    ],
    [
      "http://127.0.0.1:10821/static/(.*)$",
      "http://127.0.0.1:8001/static/$1",
    ],
  ],
}

```

## 📑 Documentation

* <a href="https://chat2db.opensource.alibaba.com">Official website document</a>
* <a href="https://github.com/alibaba/ali-dbhub/issues">Issue </a>

## Stargazers
[![Stargazers repo roster for @alibaba/Chat2DB](https://reporoster.com/stars/alibaba/Chat2DB)](https://github.com/alibaba/Chat2DB/stargazers)

## Forkers
[![Forkers repo roster for @alibaba/Chat2DB](https://reporoster.com/forks/alibaba/Chat2DB)](https://github.com/alibaba/Chat2DB/network/members)

## ☎️ Contact Us
Please star and fork on GitHub before joining the group.
Follow our WeChat public account
<a><img src="https://oss-chat2db.alibaba.com/static/%E5%85%AC%E4%BC%97%E5%8F%B7.jpg" width="40%"/></a>

## ❤️ Acknowledgements
Thanks to all the students who contributed to Chat2DB~

<a href="https://github.com/alibaba/ali-dbhub/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=alibaba/ali-dbhub" />
</a>


## Star History

<a href="https://star-history.com/#alibaba/chat2db&Date">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=alibaba/chat2db&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=alibaba/chat2db&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=alibaba/chat2db&type=Date" />
  </picture>
</a>

