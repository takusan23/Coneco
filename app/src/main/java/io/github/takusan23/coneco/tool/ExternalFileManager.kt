package io.github.takusan23.coneco.tool

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 内部固有ストレージ関係のクラス
 *
 * @param context [Context]
 * */
@Suppress("BlockingMethodInNonBlockingContext")
class ExternalFileManager(private val context: Context) {

    /** 結合する動画の保存先 */
    private val mergeVideoFolder = File(context.getExternalFilesDir(null), MERGE_ITEM_FOLDER).apply { mkdir() }

    /** 結合した動画の一時保存先 */
    val tempResultFile = File(context.getExternalFilesDir(null), TEMP_RESULT_FILE).apply { createNewFile() }

    /** 一時作業フォルダを提供しないといけないので作る */
    val tempFileFolder = File(context.getExternalFilesDir(null), TEMP_FILE_FOLDER).apply { mkdir() }

    /**
     * Uriなファイルを内部固有ストレージへ保存する。Uriは扱えないので
     *
     * @param uri コピーするファイルのUri
     * @param fileName ファイル名
     * @return コピー先の [File]
     * */
    suspend fun copyFileFromUri(uri: Uri, fileName: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)!!
        File(mergeVideoFolder, fileName).apply {
            createNewFile()
            writeBytes(inputStream.readBytes()) // 2GBを超えると使えない
        }
    }

    /** フォルダ、ファイルを全部消す */
    suspend fun delete() = withContext(Dispatchers.IO) {
        mergeVideoFolder.deleteRecursively()
        tempResultFile.delete()
    }

    /**
     * 結合したファイルを端末のギャラリー（Movie）に入れる。
     *
     * くそみたいな MediaStore API を叩く必要がある。
     *
     * @param fileName ファイル名
     * */
    suspend fun createFileAndMoveVideoFile(fileName: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        // MediaStoreに入れる中身
        val contentValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to fileName,
                MediaStore.MediaColumns.RELATIVE_PATH to resultMovieSaveFolder
            )
        } else {
            contentValuesOf(MediaStore.MediaColumns.DISPLAY_NAME to fileName)
        }
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        val outputStream = contentResolver.openOutputStream(uri)!!
        outputStream.write(tempResultFile.readBytes())
        tempResultFile.delete()
    }


    companion object {
        /** Storage Access FrameworkでもらったUriをコピーする先 */
        private const val MERGE_ITEM_FOLDER = "merge_videos"

        /** 結合後のファイル */
        private const val TEMP_RESULT_FILE = "temp_result_file"

        /** 一時保存先 */
        private const val TEMP_FILE_FOLDER = "temp_file_folder"

        /**
         * 保存先パスを返す
         *
         * 注意：これはユーザーに提示するためだけに利用されるパス、JavaのFileAPIでは利用できない
         * */
        val resultMovieSaveFolder: String
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // MediaStore.MediaColumns.RELATIVE_PATH が Android10 以降のみ
                "${Environment.DIRECTORY_MOVIES}/Coneco"
            } else {
                Environment.DIRECTORY_MOVIES
            }
    }
}