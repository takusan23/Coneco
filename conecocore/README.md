# conecocore

複数の動画をひとつに繋げるライブラリ。
モジュールなので、`app`がこのライブラリを参照して使ってます。また、MavenCentralから参照可能です。

## 最低条件
- Android 5 以上
- Kotlin
    - コルーチンを利用しているためKotlinは必須です。

## 導入方法
MavenCentralから引っ張ってこれます。
`app`の方の`build.gradle`に書き足します。

```gradle
dependencies {
    // これ
    implementation("io.github.takusan23:conecocore:1.0.0")

    // 省略...
}
```

## 使い方
### 繋げたい動画がどこにあるかで作るデータクラスが変わります。

- Uriにある場合（MediaStore や Storage Access Framework 経由ならこっち）
    - ConecoRequestUriData のインスタンスを作成
- Fileで扱える場合（Context#getExternalFilesDir 経由ならこっち）
    - ConecoRequestData のインスタンスを作成

```Kotlin
// Storage Access Framework 等で動画のUriを受け取る
val mergeUriList = listOf<Uri>()
// Movies内のフォルダ名
val folderName = "coneco"
// ファイル名
val fileName = "concat_video.mp4"
// 一時保存先、Fileが使える場所ならおk
val tempFolder = File(appContext.getExternalFilesDir(null), TEMP_FILE_FOLDER)

// Uriで動画をもらう
val requestData = ConecoRequestUriData(
    context = context,
    videoUriList = mergeUriList,
    folderName = folderName,
    resultFileName = fileName,
    tempFileFolder = tempFolder
)
```

### データクラスが出来たら、`ConecoCore`のインスタンスを作成して結合させます。

音声と映像のエンコーダー設定が必要です。

```kotlin
val conecoCore = ConecoCore(requestData).apply {
    configureAudioFormat(
        bitRate = 192_000 // 音声ビットレート
    )
    configureVideoFormat(
        bitRate = 1_000_000, // 映像ビットレート
        frameRate = 30, // フレームレート
        isUseOpenGl = false // 繋げる動画の解像度等が違う場合はtrue
    )
}
```

### 結合
なんかしらんけどすごい時間かかる。

```kotlin
// Fragment とか Activity なら
lifecycleScope.launch {

    // 簡単な進捗状態をFlowで受け取れます
    conecoCore.status.onEach { status ->
        println(status)
    }.launchIn(this)

    // 結合開始。返り値は合計時間。合成が終わるまで一時停止します。
    val mergeTime = conecoCore.merge()
}
```