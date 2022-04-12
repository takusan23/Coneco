# こねこ

複数の動画をつなげて一つにするアプリ、ライブラリ。

## 開発者向け

### わたし向け MavenCentralへデプロイ

- local.propertiesにログイン情報を記載する
```properties
# 鍵IDの最後8桁
signing.keyId=xxxxxx
# パスワード
signing.password=password
# 秘密鍵のBase64
signing.key=xxxxxxxxx
# Sonatype OSSRH のユーザー名
ossrhUsername=takusan_23
# Sonatype OSSRH のパスワード
ossrhPassword=password
# Sonatype ステージングプロファイルId
sonatypeStagingProfileId=0000...
```

- コマンドを実行する
```
gradle :conecocore:publishToSonatype
```