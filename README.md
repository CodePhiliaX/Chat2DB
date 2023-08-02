
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

&emsp; &emsp;Chat2DB is a multi-database client tool that is open-source and free. It supports local installation on Windows and Mac, as well as server-side deployment and web page access. Compared to traditional database client software such as Navicat and DBeaver, Chat2DB integrates AIGC's capabilities and is able to convert natural language into SQL. It can also convert SQL into natural language and provide optimization suggestions for SQL to greatly enhance the efficiency of developers. It is a tool for database developers in the AI era, and even non-SQL business operators in the future can use it to quickly query business data and generate reports.

## ✨ Features

- 🌈 AI intelligent assistant, supporting natural language to SQL conversion, SQL to natural language conversion, and SQL optimization suggestions
- 👭 Support team collaboration, developers do not need to know the online database password, solving the problem of enterprise database account security
- ⚙️ Powerful data management capability, supporting management of data tables, views, stored procedures, functions, triggers, indexes, sequences, users, roles, authorizations, etc.
- 🔌 Powerful extension capability, currently supporting MySQL, PostgreSQL, Oracle, SQLServer, ClickHouse, OceanBase, H2, SQLite, etc., and more databases will be supported in the future
- 🛡 Front-end development using Electron, providing a solution that integrates Windows, Mac, Linux clients, and web versions
- 🎁 Support environment isolation, online, and daily data permission separation

## ⏬ Download and Install

[Downloading installation package from GitHub](https://github.com/chat2db/Chat2DB/releases) 


[Downloading installation package from official website](https://sqlgpt.cn/docs/guides/download) 



## 🚀 Supported databases

Chat2DB supports connecting to the following databases:
- MySQL
- PostgreSQL
- H2
- Oracle
- SQLServer
- SQLLite
- MariaDB
- ClickHouseare
- DM
- Presto
- DB2
- OceanBase
- Hive
- KingBase

Redis and MongoDB are partially supported , Hbase、Elasticsearch、openGauss、TiDB、InfluxDB will support in the future.

## 🌰 Demo

### Create data source

<a><img width="1720" alt="crete datasource" src="https://github.com/chat2db/Chat2DB/assets/22975773/16050747-0f6c-4e98-ba91-323033584eec"></a>


### Data source management

<a><img width="1720" alt="2" src="https://github.com/chat2db/Chat2DB/assets/22975773/5d0332ca-8a65-4ed9-95fb-b12fae9209f3"></a>

### SQL console

<a><img width="1720" alt="2" src="https://github.com/chat2db/Chat2DB/assets/22975773/5d0332ca-8a65-4ed9-95fb-b12fae9209f3"></a>

### AI intelligent assistant

![image](https://github.com/chat2db/Chat2DB/assets/22975773/2dfc4aaa-c5a3-42c3-bc61-28ebc237a27b)

## 🔥 AI Configuration

### CONFIGURE OPENAI

Option 1 (recommended): To use the ChatSql function of OPENAI, two conditions must be met:

- You need an OPENAI_API_KEY.
- The client's network can connect to the OPENAI website, and for users in China, a VPN is required. Note: If the local VPN is not fully effective, the network connectivity can be ensured by setting the network proxy HOST and PORT in the client.

<img width="1717" alt="3" src="https://github.com/chat2db/Chat2DB/assets/22975773/95c8a766-cc6b-4767-90e6-a8b616a89bc7">


Option 2 (recommended): We provide a unified proxy service.

- No OPENAI_API_KEY is required.
- No proxy or VPN is required, as long as the network is connected.

To facilitate users' quick use of AI capabilities, you can scan the QR code below to follow our WeChat public account and apply for our custom API_KEY.

<img width="1726" alt="4" src="https://github.com/chat2db/Chat2DB/assets/22975773/9236ff01-e49f-4e45-96b0-201e85fcd756">


### CONFIGURE CUSTOM AI

- Customized AI can be any LLM that you deployed, such as ChatGLM、ChatGPT、ERNIE Bot、Tongyi Qianwen, and so on. However, the customized interface need to conform to the protocol definition. Otherwise, secondary development may be required. Two DEMOs are provided in the code, the configuration is as shown below. In specific use, you can refer to the DEMO interface to write a custom interface, or directly perform secondary development in the DEMO interface.
- DEMO for configuring customized stream output interface.
- DEMO for configuring customized non-stream output interface.
<img width="1722" alt="5" src="https://github.com/chat2db/Chat2DB/assets/22975773/aff8497e-3edb-449d-a7d4-bb0429abc67c">


## 📦 Docker installation

```bash
```bash
  // Pull the latest client, then run Docker with the name 'chat2db', and mount 'root. chat2db' to '~. chat2db Docker'`
  docker run --name=chat2db -ti -p 10824:10824 -v ~/.chat2db-docker:/root/.chat2db  chat2db/chat2db:latest
  // The normal prompt here is' Tomcat started on port (s): 10824 (http) with context path ', which will complete the process

  // If the prompt 'The container name "chat2db" is already in use by container' appears here, it means that the container already exists and is running
  docker start chat2db
  // If you want to update chat2db, you need to first rm
  docker rm chat2db
```
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

- Front-End debug

```bash
node version must be 16 or later
Be sure to use yarn
$ cd Chat2DB/chat2db-client
$ yarn
$ yarn run start:web
```

- Backend debug

```bash
$ cd ../chat2db-server
$ mvn clean install # maven 3.8 or later needs to be installed
$ cd chat2db-server/chat2db-server-start/target/
$ java -jar  -Dloader.path=/lib -Dchatgpt.apiKey=xxxxx chat2db-server-start.jar  # To launch the chat application, you need to enter the ChatGPT key for the chatgpt.apiKey. Without entering it, you won't be able to use the AIGC function.
```

## 📑 Documentation

- <a href="https://chat2db.opensource.alibaba.com">Official website document</a>
- <a href="https://github.com/alibaba/ali-dbhub/issues">Issue </a>

## Stargazers

[![Stargazers repo roster for @chat2db/Chat2DB](https://reporoster.com/stars/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/stargazers)

## Forkers

[![Forkers repo roster for @chat2db/Chat2DB](https://reporoster.com/forks/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/network/members)

## ☎️ Contact Us

Please star and fork on GitHub before joining the group.
Follow our WeChat public account.

<a><img src="https://github.com/chat2db/Chat2DB/assets/22975773/e4239d29-1426-4361-bf57-f1b0b67d1281" width="40%"/></a>

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
