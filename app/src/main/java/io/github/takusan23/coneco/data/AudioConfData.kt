package io.github.takusan23.coneco.data

import kotlinx.serialization.Serializable

/**
 * 結合する際の音声の設定をまとめたデータクラス
 *
 * @param bitRate ビットレート。128kとか192kで良いと思う
 * */
@Serializable
data class AudioConfData(
    val bitRate: Int = 192_000,
)