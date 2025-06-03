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
- API仕様書: OpenAPI 3.0.3 による自動生成ドキュメント

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
> Migrationファイルは以下に配置。build.gradleに定義。
> - テーブル定義：`src/main/resources/db/migration`
> - ローカル開発用のサンプルデータ：`src/main/resources/db/migration_dev`

3. テストの実施
```shell
./gradlew test
```

4. アプリケーションの起動
```shell
./gradlew bootRun 
```
