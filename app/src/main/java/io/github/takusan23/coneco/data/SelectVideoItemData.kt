package io.github.takusan23.coneco.data

import android.graphics.Bitmap
import android.net.Uri

/**
 * 選択した動画のデータクラス
 *
 * @param uri Uri
 * @param title 動画タイトル
 * @param bytes バイト数
 * @param thumb サムネイル
 * */
data class SelectVideoItemData(
    val uri: Uri,
    val title: String,
    val bytes: Long,
    val thumb: Bitmap,
)