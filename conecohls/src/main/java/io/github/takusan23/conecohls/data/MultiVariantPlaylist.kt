package io.github.takusan23.conecohls.data

/**
 * 与えられたプレイリストファイルが複数の画質を返すマルチバリアントプレイリストの場合のデータクラス
 *
 * @param url 指定画質のプレイリストへのURL
 * @param bandWidth ビットレートの上限
 * @param resolution 解像度。音声の場合はない
 * */
data class MultiVariantPlaylist(
    val url: String,
    val bandWidth: Long?,
    val resolution: String?,
)