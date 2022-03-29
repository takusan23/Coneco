package io.github.takusan23.coneco.tool

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import io.github.takusan23.coneco.data.SelectVideoItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileTool {

    /**
     * [Uri]から[SelectVideoItemData]を作る
     *
     * @param context [Context]
     * @param uri 選択した動画のUri
     * */
    suspend fun getVideoData(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        // MediaStoreへ問い合わせる
        val contentResolver = context.contentResolver
        // 取り出すカラム
        val selection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE)
        // 問い合わせる
        val cursor = contentResolver.query(uri, selection, null, null, null)
        cursor?.moveToFirst()
        // 取得する
        val title = cursor?.getString(0)
        val bytes = cursor?.getLong(1)
        val thumb = MediaMetadataRetriever().apply { setDataSource(context, uri) }.frameAtTime
        cursor?.close()
        return@withContext SelectVideoItemData(
            uri,
            title!!,
            bytes!!,
            thumb!!
        )
    }

    /** Byte -> MB にする */
    fun toMB(bytes: Long): Long {
        return bytes / 1024 / 1024
    }

}