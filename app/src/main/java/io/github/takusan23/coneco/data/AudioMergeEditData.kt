package io.github.takusan23.coneco.data

/**
 * 結合する際の音声の設定をまとめたデータクラス
 *
 * @param bitRate ビットレート。128kとか192kで良いと思う
 * */
data class AudioMergeEditData(
    val bitRate: Int = 192_000,
)