package io.github.takusan23.coneco.data

/**
 * 結合する際の映像の設定をまとめたデータクラス
 *
 * @param bitRate ビットレート。192kとか？
 * @param frameRate フレームレート。多分意味ない
 * @param isUseOpenGl OpenGL版で結合する場合はtrueにする
 * @param videoHeight 動画の高さを変えたい場合
 * @param videoWidth 動画の幅を変えたい場合
 * */
data class VideoMergeEditData(
    val bitRate: Int = 1_000_000,
    val frameRate: Int = 30,
    val videoWidth: Int? = null,
    val videoHeight: Int? = null,
    val isUseOpenGl: Boolean = false,
) {

    /** OpenGL版で結合が必要な場合はtrue */
    val isRequireOpenGl = isUseOpenGl || (videoWidth != null && videoHeight != null)

}