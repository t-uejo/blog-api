# Blog API
Spring Bootを使用したSPAアプリケーション向けREST APIサービス

## Setup

1. Dockerコンテナを起動
```shell
docker compose up -d
```

2. テーブル作成、ローカル開発用のサンプルデータ投入
```shell
./gradlew flywayMigrate
./gradlew bootRun 
```

3. アプリケーションの起動
```shell
./gradlew bootRun 
```


