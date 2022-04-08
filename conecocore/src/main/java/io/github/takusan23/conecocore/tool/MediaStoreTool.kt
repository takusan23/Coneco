package io.github.takusan23.conecocore.tool

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import io.github.takusan23.conecocore.data.ConecoRequestUriData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object MediaStoreTool {

    /**
     * MediaStoreへ動画を移動させる。ギャラリーアプリから見れるようになる。
     *
     * クソみたいなAPIだな。
     *
     * @param context [Context]
     * @param copyFile コピーするファイル
     * @param fileName ファイル名
     * @param folderName 動画フォルダの中に作るフォルダ名。Android 10以降のみ
     * */
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun copyDeviceMovieFolder(context: Context, copyFile: File, folderName: String, fileName: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        // MediaStoreに入れる中身
        val contentValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to fileName,
                MediaStore.MediaColumns.RELATIVE_PATH to ConecoRequestUriData.resultFileFolder(folderName)
            )
        } else {
            contentValuesOf(MediaStore.MediaColumns.DISPLAY_NAME to fileName)
        }
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        val outputStream = contentResolver.openOutputStream(uri)!!
        outputStream.write(copyFile.readBytes())
        copyFile.delete()
    }

}