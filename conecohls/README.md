# conecohls

conecocore の ConecoRequestInterface を継承してHLS形式で配信している動画に対応してみる。
プレイリストが変わらない場合のみです。生放送は無理...

## 使い方

MavenCentralからライブラリを入れます。

```gradle
dependencies {
    // これ
    implementation("io.github.takusan23:conecohls:1.0.0")
}
```

## インターネットの権限を書きます
書いておいてください。

## HLS対応のリクエストデータを作ります

```kotlin
// プレイリストのURL
val hlsPlaylistUrl = "なんとか.m3u8"
// ファイル名
val fileName = "download_hls.mp4"
// Movies内のフォルダ名
val folderName = "coneco"
// 一時保存先、Fileが使える場所ならおk
val tempFolder = File(appContext.getExternalFilesDir(null), TEMP_FILE_FOLDER)

val requestData = ConecoRequestUriData(
    context = context,
    m3u8PlaylistUrl = hlsPlaylistUrl,
    resultFileName = fileName,
    tempFileFolder = tempFolder
)
```

あとは conecocore と同じ使い方でいいです。

### マスタープレイリスト、マルチバリアントプレイリスト の場合
ほしい画質のプレイリストのURLを見つけて、ConecoRequestUriDataへ渡す必要があります。

```kotlin
// 画質一覧
val masterPlaylist = ConecoRequestHlsData.getMasterPlaylist("なんとか/master.m3u8") ?: return
val playlistUrl = masterPlaylist?.get(0)?.url // 適当に...
```