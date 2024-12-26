# Blog API
Spring Framework を使用した RESTful API サービス

## Tech Stack
言語: Java 21
フレームワーク: Spring Boot 3.x
データベース: MySQL 8.1
ビルドツール: Gradle
テスト: JUnit、Mockito
ドキュメント: OpenAPI/Swagger による API ドキュメント

## Setup
1. Dockerコンテナを起動
```shell
docker compose up -d
```

2. テーブル作成、ローカル開発用のサンプルデータ投入
```shell
./gradlew flywayMigrate
```

3. アプリケーションの起動
```shell
./gradlew bootRun 
```
