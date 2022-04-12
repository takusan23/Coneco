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
    - conecocore / conecohls
```
gradle :conecocore:publishToSonatype
```

- Sonatype OSSRH nexus repository manager へログイン
    - Staging repository を開く
    - 該当の項目を押す
    - リリースするなら Close 、辞めるなら Drop を押す
    - Close を押したあと暫く待つと Release が押せるようになる
        - 気が変わったら Drop 出来る。
        - Release 後は戻せない。