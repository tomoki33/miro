# 簡易版MIRO
付箋のドラッグ&ドロップ　　

付箋、文字色変更可能　　

リアルタイム通信（ドラッグ&ドロップ除き）

# Vue立ち上げ：frontendのREAD.ME参照

# Redisへの入り方
docker配下で
```
docker-compose up
```
別ターミナルを開いて
```
redis-cli
```

docker落とすコマンド
```
docker-compose down
```
# SpringBootの立ち上げコマンド
```
mvn spring-boot:run
```

# Redis基本コマンド
SET key value: 指定されたキーに値を設定します。
GET key: 指定されたキーに設定された値を取得します。
DEL key: 指定されたキーを削除します。
EXISTS key: 指定されたキーが存在するかどうかを確認します。
INCR key: 指定されたキーに設定された値を1増やします。
DECR key: 指定されたキーに設定された値を1減らします。
HSET key field value: 指定されたハッシュのキーに、指定されたフィールドと値を設定します。
HGET key field: 指定されたハッシュのキーに設定された指定されたフィールドの値を取得します。
HDEL key field: 指定されたハッシュのキーから、指定されたフィールドを削除します。
HGETALL key: 指定されたハッシュのキーに設定されたすべてのフィールドと値を取得します。
LPUSH key value: 指定されたリストのキーの先頭に値を追加します。
RPUSH key value: 指定されたリストのキーの末尾に値を追加します。
LPOP key: 指定されたリストのキーの先頭から値を取得して削除します。
RPOP key: 指定されたリストのキーの末尾から値を取得して削除します。
LLEN key: 指定されたリストのキーに設定された要素の数を取得します。
SADD key member: 指定されたセットのキーに、指定されたメンバーを追加します。
SMEMBERS key: 指定されたセットのキーに設定されたすべてのメンバーを取得します。
SREM key member: 指定されたセットのキーから、指定されたメンバーを削除します。
