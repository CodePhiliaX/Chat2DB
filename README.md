<h1 align="center">Chat2DB</h1>

<div align="center">

 An intelligent and versatile general-purpose SQL client and reporting tool for databases which integrates ChatGPT capabilities.

[![License](https://img.shields.io/github/license/alibaba/fastjson2?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub release](https://img.shields.io/github/release/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/releases)
[![GitHub Stars](https://img.shields.io/github/stars/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/fork)
[![GitHub Contributors](https://img.shields.io/github/contributors/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/graphs/contributors)

</div>

<div align="center">
<p align="center"><b>Share Chat2DB Repository </b></p>
<p align="center">
<a href="https://twitter.com/intent/tweet?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/chat2db/Chat2DB&hashtags=ChatGPT,AGI,SQL%20Client,Reporting%20tool" target="blank" > <img src="https://img.shields.io/twitter/follow/_Chat2DB?label=Share Repo on Twitter&style=social" alt=""/> </a> 
<a href="https://t.me/share/url?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/chat2db/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Telegram&logo=Telegram&style=social&url=https://github.com/chat2db/Chat2DB" alt="Share on Telegram"/></a>
<a href="https://api.whatsapp.com/send?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%20https://github.com/chat2db/Chat2DB"><img src="https://img.shields.io/twitter/url?label=whatsapp&logo=whatsapp&style=social&url=https://github.com/chat2db/Chat2DB" /></a>
<a href="https://www.reddit.com/submit?url=https://github.com/chat2db/Chat2DB&title=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities." target="blank"><img src="https://img.shields.io/twitter/url?label=Reddit&logo=Reddit&style=social&url=https://github.com/chat2db/Chat2DB" alt="Share on Reddit"/></a>
<a href="mailto:?subject=Check%20this%20GitHub%20repository%20out.&body=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%3A%0Ahttps://github.com/chat2db/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Gmail&logo=Gmail&style=social&url=https://github.com/chat2db/Chat2DB"/></a>
</p>

**License Notation**: Chat2DB is constructed and distributed for personal and non-commercial use only. For commercial use of this project, please contact corresponding authors.

Languages： English | [中文](README_CN.md)
</div>


## DEMO

https://github.com/chat2db/Chat2DB/assets/22975773/79e9dded-375b-44cf-9979-bb7572465a2e


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
| Windows      | [https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB%20Setup%202.0.1.exe](https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB%20Setup%202.0.1.exe) |
| MacOS ARM64 | [https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB-2.0.1-arm64.dmg](https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB-2.0.1-arm64.dmg)         |
| MacOS X64  | [https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB-2.0.1.dmg](https://oss-chat2db.alibaba.com/release/2.0.1/Chat2DB-2.0.1.dmg)                     |
| Jar包         | [https://oss-chat2db.alibaba.com/release/2.0.1/chat2db-server-start.jar](https://oss-chat2db.alibaba.com/release/2.0.1/chat2db-server-start.jar)         |

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
<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/1d7f2d05-9c3b-4308-a693-39aed44a4b39.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>
### Data source management
<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/d5634953-9fe5-4a03-8024-3aa4774b2955.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>
### SQL console
<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/f1a111bd-38cf-42d2-bfd3-f1d7f57aec3c.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>
### AI intelligent assistant
<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/16e3c632-f896-45c3-a7a2-91c338e82f73.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>

## 🔥 AI Configuration
### CONFIGURE OPENAI

Option 1 (recommended): To use the ChatSql function of OPENAI, two conditions must be met:

- You need an OPENAI_API_KEY.
- The client's network can connect to the OPENAI website, and for users in China, a VPN is required. Note: If the local VPN is not fully effective, the network connectivity can be ensured by setting the network proxy HOST and PORT in the client.

<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/0218daf0-7d93-43c5-a5f7-decd104c0847.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>

Option 2 (recommended): We provide a unified proxy service.

- No OPENAI_API_KEY is required.
- No proxy or VPN is required, as long as the network is connected.

To facilitate users' quick use of AI capabilities, you can scan the QR code below to follow our WeChat public account and apply for our custom API_KEY. 

<a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/5ee43d26-05bb-4b12-b705-2b263f167975.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>


### CONFIGURE CUSTOM AI
- Customized AI can be any LLM that you deployed, such as ChatGLM、ChatGPT、ERNIE Bot、Tongyi Qianwen, and so on. However, the customized interface need to conform to the protocol definition. Otherwise, secondary development may be required. Two DEMOs are provided in the code, the configuration is as shown below. In specific use, you can refer to the DEMO interface to write a custom interface, or directly perform secondary development in the DEMO interface.
- DEMO for configuring customized stream output interface.
  <a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/3b74fea5-526d-40ea-9edb-27484921c578.png?x-oss-process=image/resize,w_1280,m_lfit,limit_1"/></a>
- DEMO for configuring customized non-stream output interface.
  <a><img src="https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/KM7qezzyAVX2Opj8/img/eb82d1ee-ab13-46f8-a454-60968482e584.png#1011"/></a>



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
$ cd Chat2DB/chat2db-client
$ npm install # Mounting front-end dependency
$ npm run build:prod # Package js to the source directory on the back end
```
- Backend debug
```bash
$ cd ../chat2db-server
$ mvn clean install # maven 3.8 or later needs to be installed
$ cd chat2db-server/chat2db-server-start/target/
$ java -jar -Dchatgpt.apiKey=xxxxx chat2db-server-start.jar  # To launch the chat application, you need to enter the ChatGPT key for the chatgpt.apiKey. Without entering it, you won't be able to use the AIGC function.
$ # open http://127.0.0.1:10821 to start debug Note: Front-end installation is required
```

- Front-End debug
```bash
$ cd Chat2DB/chat2db-client
$ yarn 
$ npm run start:web
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
[![Stargazers repo roster for @chat2db/Chat2DB](https://reporoster.com/stars/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/stargazers)

## Forkers
[![Forkers repo roster for @chat2db/Chat2DB](https://reporoster.com/forks/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/network/members)

## ☎️ Contact Us
Please star and fork on GitHub before joining the group.
Follow our WeChat public account.

<a><img src="https://oss-chat2db.alibaba.com/static/%E5%85%AC%E4%BC%97%E5%8F%B7.jpg" width="40%"/></a>

## ❤️ Acknowledgements
Thanks to all the students who contributed to Chat2DB~

<a href="https://github.com/chat2db/Chat2DB/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=chat2db/Chat2DB" />
</a>


## Star History

<a href="https://star-history.com/#chat2db/chat2db&Date">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=chat2db/chat2db&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=chat2db/chat2db&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=chat2db/chat2db&type=Date" />
  </picture>
</a>


