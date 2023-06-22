
<h1 align="center">Chat2DB</h1>

<div align="center">

🔥🔥🔥 ChatGPT 機能を統合した、インテリジェントで汎用的なデータベース用 SQL クライアントおよびレポート作成ツールです。

</div>

<div align="center">
<p align="center"><b>Chat2DB リポジトリをシェアする</b></p>
<p align="center">
<a href="https://twitter.com/intent/tweet?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/chat2db/Chat2DB&hashtags=ChatGPT,AGI,SQL%20Client,Reporting%20tool" target="blank" > <img src="https://img.shields.io/twitter/follow/_Chat2DB?label=Share Repo on Twitter&style=social" alt=""/> </a>
<a href="https://t.me/share/url?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.&url=https://github.com/chat2db/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Telegram&logo=Telegram&style=social&url=https://github.com/chat2db/Chat2DB" alt="Share on Telegram"/></a>
<a href="https://api.whatsapp.com/send?text=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%20https://github.com/chat2db/Chat2DB"><img src="https://img.shields.io/twitter/url?label=whatsapp&logo=whatsapp&style=social&url=https://github.com/chat2db/Chat2DB" /></a>
<a href="https://www.reddit.com/submit?url=https://github.com/chat2db/Chat2DB&title=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities." target="blank"><img src="https://img.shields.io/twitter/url?label=Reddit&logo=Reddit&style=social&url=https://github.com/chat2db/Chat2DB" alt="Share on Reddit"/></a>
<a href="mailto:?subject=Check%20this%20GitHub%20repository%20out.&body=Chat2DB-An%20intelligent%20and%20versatile%20general-purpose%20SQL%20client%20and%20reporting%20tool%20for%20databases%20which%20integrates%20ChatGPT%20capabilities.%3A%0Ahttps://github.com/chat2db/Chat2DB" target="_blank"><img src="https://img.shields.io/twitter/url?label=Gmail&logo=Gmail&style=social&url=https://github.com/chat2db/Chat2DB"/></a>
</p>

**ライセンス表記**: Chat2DB は、個人的かつ非商業的な利用のみを目的として構築・配布されています。このプロジェクトの商用利用については、対応する著者に連絡してください。

言語： 日本語 | [English](README.md) | [中文](README_CN.md)
</div>

## 📖 はじめに
&emsp; &emsp;Chat2DB はオープンソースでフリーのマルチデータベースクライアントツールです。Windows や Mac へのローカルインストールはもちろん、サーバーサイドへの展開や Web ページへのアクセスもサポートしています。Navicat や DBeaver のような従来のデータベースクライアントソフトウェアと比較して、Chat2DB は AIGC の機能を統合し、自然言語を SQL に変換することができます。また、SQL を自然言語に変換し、SQL の最適化提案を行い、開発者の作業効率を大幅に向上させることができます。AI 時代のデータベース開発者のためのツールであり、将来的には SQL を使わない業務オペレータでも、業務データの照会やレポート作成が迅速に行えるようになる。
## ✨ 特徴
- 🌈 AI インテリジェントアシスタント、自然言語から SQL への変換、SQL から自然言語への変換、SQL 最適化の提案をサポート
- 👭 チームコラボレーションをサポートし、開発者はオンラインデータベースパスワードを知る必要がなく、企業データベースアカウントセキュリティの問題を解決します。
- ⚙️ 強力なデータ管理機能。データテーブル、ビュー、ストアドプロシージャ、ファンクション、トリガー、インデックス、シーケンス、ユーザー、ロール、権限などの管理をサポート。
- 🔌 強力な拡張機能。現在、MySQL、PostgreSQL、Oracle、SQLServer、ClickHouse、OceanBase、H2、SQLite などをサポート。
- 🛡 Electron を使用したフロントエンド開発、Windows、Mac、Linux クライアント、および Web バージョンを統合するソリューションを提供します。
- 🎁 環境分離、オンライン、日々のデータ権限分離をサポート


## ⏬ ダウンロードとインストール

| 説明                   | ダウンロード                                                                                                                                                   |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Windows      | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB%20Setup%201.0.11.exe](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB%20Setup%201.0.11.exe) |
| MacOS ARM64 | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11-arm64.dmg](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11-arm64.dmg)         |
| MacOS X64  | [https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11.dmg](https://oss-chat2db.alibaba.com/release/1.0.11/Chat2DB-1.0.11.dmg)                     |
| Jar包         | [https://oss-chat2db.alibaba.com/release/1.0.11/ali-dbhub-server-start.jar](https://oss-chat2db.alibaba.com/release/1.0.11/ali-dbhub-server-start.jar)     |

## 🚀 サポートしているデータベース
| データベース    | ステータス |
|---------------|----------|
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
| Hbase         | Planning |
| Elasticsearch | Planning |
| openGauss     | Planning |
| TiDB          | Planning |
| InfluxDB      | Planning |

## 🌰 デモ
### データソースの作成
  <a><img src="https://gw.alicdn.com/imgextra/i3/O1CN01PlpLYy1hIq5aMugpg_!!6000000004255-0-tps-3446-1750.jpg" width="100%"/></a>
### データソースの管理
  <a><img src="https://gw.alicdn.com/imgextra/i2/O1CN01DpzZJL1T7w2Xv9VMl_!!6000000002336-0-tps-3410-1662.jpg" width="100%"/></a>
### SQL コンソール
  <a><img src="https://gw.alicdn.com/imgextra/i2/O1CN01aidnkx1Oo0LJ1Pdty_!!6000000001751-0-tps-3440-1736.jpg" width="100%"/></a>
### AI インテリジェントアシスタント
  <a><img src="https://gw.alicdn.com/imgextra/i4/O1CN01iaSXot1W6VeaDFbK2_!!6000000002739-0-tps-3430-1740.jpg" width="100%"/></a>

## 🔥 AI コンフィグ
### OPENAI を設定する

オプション 1（推奨）: OPENAI の ChatSql 機能を使用するには、以下の 2 つの条件を満たす必要があります:

- OPENAI_API_KEY が必要です。
- クライアントのネットワークは OPENAI のウェブサイトに接続することができますが、中国のユーザの場合は VPN が必要です。注意：ローカル VPN が完全に有効でない場合、クライアントにネットワークプロキシの HOST と PORT を設定することで、ネットワークの接続性を確保することができます。

<a><img src="https://img.alicdn.com/imgextra/i2/O1CN01anrJMI1FEtSBbmTau_!!6000000000456-0-tps-1594-964.jpg" width="60%"/></a>

オプション 2（推奨）: ユニファイドプロキシサービスを提供します。

- OPENAI_API_KEY は不要です。
- ネットワークが接続されていれば、プロキシや VPN は不要です。

ユーザーが AI 機能を素早く利用できるよう、以下の QR コードをスキャンして WeChat の公開アカウントをフォローし、カスタム API_KEY を申請することができます。

<a><img src="https://oss-chat2db.alibaba.com/static/%E5%85%AC%E4%BC%97%E5%8F%B7.jpg" width="40%"/></a>

アプリケーションが完成したら、下図を参照して設定と使用方法を確認してください。Api ホストを http://test.sqlgpt.cn/gateway/api/ として設定します。

<a><img src="https://img.alicdn.com/imgextra/i2/O1CN01xNobD21mo3B1ILrs2_!!6000000005000-0-tps-592-515.jpg" width="60%"/></a>

### カスタム AI を設定する
- カスタマイズされた AI は、ChatGLM、ChatGPT、ERNIE Bot、Tongyi Qianwen など、導入した LLM であれば何でも構いません。ただし、カスタマイズされたインターフェースはプロトコル定義に準拠する必要があります。そうでない場合は、二次開発が必要になります。2つの DEMO がコードで提供され、構成は以下の通りです。具体的な使用方法としては、DEMO インターフェイスを参照してカスタムインターフェイスを記述するか、直接 DEMO インターフェイスで二次開発を行うことができます。
- カスタマイズされたストリーム出力インターフェースを設定するための DEMO。
  <a><img src="https://img.alicdn.com/imgextra/i1/O1CN01xMqnRH1DlkdSekvSF_!!6000000000257-0-tps-591-508.jpg" width="60%"/></a>
- カスタマイズされた非ストリーム出力インターフェースを設定するための DEMO。
  <a><img src="https://img.alicdn.com/imgextra/i1/O1CN01JqmbGo1fW0GAQhRu4_!!6000000004013-0-tps-587-489.jpg" width="60%"/></a>



## 📦 Docker インストール

```bash
docker pull chat2db/chat2db:latest
```

## 🎯 動作環境
注: ローカル・デバッグが必要な場合
- Java ランタイム Open JDK 17
- JRE のリファレンスパッケージングとデプロイ方法。
- Node 実行環境 Node16 Node.js。

## 💻 ローカルデバッグ
- ローカルに git clone
```bash
$ git clone git@github.com:chat2db/Chat2DB.git
```
- フロントエンドインストール
```bash
$ cd Chat2DB/ali-dbhub-client
$ npm install # フロントエンドの依存関係をマウントする
$ npm run build:prod # バックエンドのソースディレクトリに js をパッケージする
```
- バックエンドデバッグ
```bash
$ cd ../ali-dbhub-server
$ mvn clean install # maven 3.8 以降をインストールする必要があります
$ cd ali-dbhub-server/ali-dbhub-server-start/target/
$ java -jar -Dchatgpt.apiKey=xxxxx ali-dbhub-server-start.jar  # チャットアプリを起動するには、chatgpt.apiKey に ChatGPT キーを入力する必要があります。これを入力しないと AIGC 機能が使えません。
$ # http://127.0.0.1:10821 を開いてデバッグを開始する 注: フロントエンドのインストールが必要
```

- フロントエンドデバッグ
```bash
$ cd Chat2DB/ali-dbhub-client
$ npm install
$ npm run start
$ # http://127.0.0.1:10821 を開いてフロントエンドのデバッグを開始する
$ # 注 フロントエンドページは完全にサービスに依存するため、フロントエンドの学生はバックエンドプロジェクトのデバッグを行う必要がある
```
しかし、フロントデバッグにはリソースのマッピングが必要なので、[XSwitch](https://chrome.google.com/webstore/detail/idkjhjggpffolpidfkikidcokdkdaogg) をダウンロードして、以下の設定ファイルを追加してください
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

## 📑 ドキュメント

* <a href="https://chat2db.opensource.alibaba.com">公式 website ドキュメント</a>
* <a href="https://github.com/chat2db/Chat2DB/issues">Issue</a>

## Stargazers
[![Stargazers repo roster for @chat2db/Chat2DB](https://reporoster.com/stars/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/stargazers)

## Forkers
[![Forkers repo roster for @chat2db/Chat2DB](https://reporoster.com/forks/chat2db/Chat2DB)](https://github.com/chat2db/Chat2DB/network/members)

## ☎️ お問い合わせ
グループに参加する前に GitHub でスターを付けてフォークしてください。
WeChat 公開アカウントをフォローする
<a><img src="https://oss-chat2db.alibaba.com/static/%E5%85%AC%E4%BC%97%E5%8F%B7.jpg" width="40%"/></a>

<a><img src="./document/qrcode/weixinqun1.png" width="30%"/></a>
<a><img src="./document/qrcode/weixinqun2.png" width="30%"/></a>
<a><img src="./document/qrcode/weixinqun3.png" width="30%"/></a>

Ding Talk：9135032392

QQ:863576619


## ❤️ 謝辞
Chat2DBに貢献してくれたすべての学生に感謝します~

<a href="https://github.com/chat2db/Chat2DB/graphs/contributors">
<img src="https://contrib.rocks/image?repo=chat2db/Chat2DB" />
</a>



