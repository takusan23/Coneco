package io.github.takusan23.conecocore.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import io.github.takusan23.conecocore.ConecoCore
import io.github.takusan23.conecocore.data.ConecoRequestUriData.Companion.resultFileFolder
import io.github.takusan23.conecocore.tool.MediaStoreTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * [ConecoCore]へリクエストする際に渡すデータ。[Uri]対応版
 *
 * 保存先は [resultFileFolder] になります。
 *
 * @param context [Context]
 * @param videoUriList 結合したい動画のUriのリスト
 * @param resultFileName ファイル名
 * @param tempFileFolder 一時保存先
 * */
data class ConecoRequestUriData(
    val context: Context,
    val videoUriList: List<Uri>,
    val resultFileName: String,
    val folderName: String = "Coneco",
    val tempFileFolder: File,
) : ConecoRequestInterface {

    /** 結合する動画の保存先 */
    private val mergeVideoFolder = File(context.getExternalFilesDir(null), MERGE_ITEM_FOLDER).apply { mkdir() }

    /** 結合した動画の一時保存先フォルダ */
    private val tempResultFile = File(context.getExternalFilesDir(null), TEMP_RESULT_FILE).apply { createNewFile() }

    /** 結合する動画。固有ストレージコピー版 */
    private val copedVideoList = arrayListOf<String>()

    /** 結合先のファイルを返す */
    override val mergeResultFile: File
        get() = tempResultFile

    /** 一時保存先 */
    override val tempFolder: File
        get() = tempFileFolder.apply { mkdir() }

    /** Uriが扱えないので、一旦内部ストレージへコピーしてその保存先を返す */
    override suspend fun getMergeVideoList(): List<String> {
        // 一度だけコピーする
        if (copedVideoList.isEmpty()) {
            copedVideoList += videoUriList
                .mapIndexed { index, uri -> copyFileFromUri(uri, index.toString()) }
                .map { it.path }
        }
        return copedVideoList
    }

    /** あとしまつ */
    override suspend fun release() {
        // tempResultFile を MediaStore へ登録する
        MediaStoreTool.copyDeviceMovieFolder(context, tempResultFile, folderName, resultFileName)
        // 一時ファイルの削除
        mergeVideoFolder.deleteRecursively()
        tempFileFolder.deleteRecursively()
        tempResultFile.delete()
    }

    /**
     * Uriなファイルを内部固有ストレージへ保存する。Uriは扱えないので
     *
     * @param uri コピーするファイルのUri
     * @param fileName ファイル名
     * @return コピー先の [File]
     * */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun copyFileFromUri(uri: Uri, fileName: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)!!
        File(mergeVideoFolder, fileName).apply {
            createNewFile()
            writeBytes(inputStream.readBytes()) // 2GBを超えると使えない
        }
    }

    companion object {
        /** Storage Access FrameworkでもらったUriをコピーする先 */
        private const val MERGE_ITEM_FOLDER = "merge_videos"

        /** 結合後のファイル */
        private const val TEMP_RESULT_FILE = "temp_result_file"

        /**
         * 保存先を返す。FileAPIで直接アクセスは出来ません
         *
         * @param folderName フォルダ名
         * */
        fun resultFileFolder(folderName: String): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // MediaStore.MediaColumns.RELATIVE_PATH が Android10 以降のみ
            "${Environment.DIRECTORY_MOVIES}/$folderName"
        } else {
            Environment.DIRECTORY_MOVIES
        }

    }
}