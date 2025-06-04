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

## プロジェクト概要
本リポジトリは Spring Boot 3.x (Java 21) を利用した簡易ブログ向け REST API です。MySQL をデータベースとして使用し、Redis をセッションストアに採用しています。ビルドは Gradle で管理され、データベースマイグレーションには Flyway を使用します。OpenAPI で定義された API 仕様からは OpenAPI Generator を用いてソースコードを自動生成します。

### ディレクトリ構成
```
src/
 ├─ main/
 │   ├─ java/com/example/blog/...
 │   │   ├─ BlogApplication.java          # Spring Boot のエントリポイント
 │   │   ├─ config/                       # セキュリティや ObjectMapper の設定
 │   │   ├─ repository/                   # MyBatis のマッパー
 │   │   ├─ security/                     # CustomUserDetailsService など
 │   │   ├─ service/                      # ビジネスロジック
 │   │   └─ web/                          # コントローラやフィルタ、例外ハンドラ
 │   └─ resources/
 │       ├─ application.yml               # Spring Boot の設定
 │       ├─ openapi.yaml                  # OpenAPI 定義
 │       └─ db/                           # Flyway のマイグレーション
 └─ test/
     └─ java/com/example/blog/...         # 単体テスト・統合テスト
```

### 主な構成ファイル
- **`application.yml`** – データベースや Redis の設定、ログレベル、セッションタイムアウトなど。
- **`openapi.yaml`** – `/csrf-cookie` や `/users` などの API エンドポイント定義。
- **`build.gradle`** – 依存関係、Flyway 設定、OpenAPI Generator、Spotless のルールを記載。

### OpenAPI のコード生成
`openapi.yaml` を更新した際は、次のコマンドでコードを再生成します。

```shell
./gradlew openApiGenerate
```

生成されたファイルは `build/spring` 以下に出力され、Spotless により自動で整形されます。

### 重要なコンポーネント
- **セキュリティ** – `SecurityConfig` で CSRF 対策や JSON ログインを設定し、`JsonUsernamePasswordAuthenticationFilter` などのカスタムフィルタを利用します。
- **リポジトリとサービス** – `repository/**` に MyBatis マッパー、`service/**` にビジネスロジックを配置しています。
- **Web レイヤ** – `web/controller/**` が REST エンドポイントを提供し、`ExceptionHandlerAdvice` が例外処理を一元化します。
- **テスト** – `src/test/java/com/example/blog` 以下の単体テスト・統合テストで挙動を確認できます。

### 新規参加者向けの学習ステップ
1. Docker Compose と Gradle タスクを使ってローカル環境を立ち上げ、アプリケーションを起動してみる。
2. `openapi.yaml` を読み、`build/spring` 以下の生成済みインターフェースを確認する。
3. `src/test/java/com/example/blog` 以下のテストを読み、期待される挙動を把握する。
4. `SecurityConfig` や各種フィルタを確認し、認証・CSRF 対策の実装を理解する。
5. `src/main/resources/db` の Flyway スクリプトからデータベース構成を把握する。
6. 機能追加時は OpenAPI 定義を更新し、新しいエンドポイントに対するテストを追加する。

### 今後の改善候補
- `application.yml` のセッションタイムアウトのコメントが値と矛盾しているため修正が必要。
- `ArticleRestController` の `/articles/{id}` エンドポイントが `openapi.yaml` に記載されていないため追加する。
- OpenAPI のコード生成手順を README に追記する（本ファイルで対応）。
