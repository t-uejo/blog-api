# Blog API
Spring Framework を使用した RESTful API サービス

## Tech Stack
- 言語: Java 21
- フレームワーク: Spring Boot 3.x
- データベース: MySQL 8.1
- キャッシュサーバ: Redis 7.2
- ビルドツール: Gradle
- テスト: JUnit、Mockito
- ドキュメント: OpenAPI/Swagger による API ドキュメント

## Setup
1. Dockerコンテナを起動
```shell
docker compose up -d
```

2. テーブル作成、ローカル開発用のサンプルデータ投入
```shell
./gradlew flywayMigrate
```
> [!NOTE]
> それぞれMigrationファイルは以下に配置。build.gradleにて定義。
> - テーブル定義：`src/main/resources/db/migration`
> - ローカル開発用のサンプルデータ：`src/main/resources/db/migration_dev`

3. アプリケーションの起動
```shell
./gradlew bootRun 
```
