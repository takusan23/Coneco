package io.github.takusan23.coneco.tool

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 内部固有ストレージ関係のクラス
 *
 * @param context [Context]
 * */
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

    /**
     * 結合したファイルをUriへ書き込む
     *
     * @param uri 移動先
     * */
    suspend fun moveResultFileToUri(uri: Uri) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val outputStream = contentResolver.openOutputStream(uri)!!
        outputStream.write(tempResultFile.readBytes())
        tempResultFile.delete()
    }

    /** フォルダ、ファイルを全部消す */
    suspend fun delete() = withContext(Dispatchers.IO) {
        mergeVideoFolder.deleteRecursively()
        tempResultFile.delete()
    }

    companion object {
        /** Storage Access FrameworkでもらったUriをコピーする先 */
        private const val MERGE_ITEM_FOLDER = "merge_videos"

        /** 結合後のファイル */
        private const val TEMP_RESULT_FILE = "temp_result_file"

        /** 一時保存先 */
        private const val TEMP_FILE_FOLDER = "temp_file_folder"
    }
}