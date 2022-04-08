package io.github.takusan23.conecohls.data

import android.content.Context
import io.github.takusan23.conecocore.data.ConecoRequestInterface
import io.github.takusan23.conecocore.tool.MediaStoreTool
import io.github.takusan23.conecohls.data.ConecoRequestHlsData.Companion.getMasterPlaylist
import io.github.takusan23.conecohls.parser.PlaylistParser
import io.github.takusan23.conecohls.playlist.RequestPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * [ConecoRequestInterface]のHLS対応版、生放送は対応してません（#EXT-X-PLAYLIST-TYPE:VOD のみ）
 *
 * 繋げたファイルはMediaStoreへ格納されます。ギャラリーアプリで見れます。
 *
 * @param context [Context]
 * @param m3u8PlaylistUrl なんとか.m3u8 のパス、プレイリストのみ受け付けます。マスタープレイリスト（マルチバリアントプレイリスト）の場合は[getMasterPlaylist]参照。
 * @param resultFile 繋げたファイルの保存先
 * @param tempFileFolder 一時保存フォルダをください
 * */
data class ConecoRequestHlsData(
    val context: Context,
    val m3u8PlaylistUrl: String,
    val resultFileName: String,
    val folderName: String = "Coneco",
    val tempFileFolder: File,
) : ConecoRequestInterface {

    /** 結合した動画の一時保存先フォルダ */
    private val tempResultFile = File(context.getExternalFilesDir(null), TEMP_RESULT_FILE).apply { createNewFile() }

    override val mergeResultFile: File
        get() = tempResultFile

    override val tempFolder: File
        get() = tempFileFolder.apply { mkdir() }

    /** プレイリストのMPEG2-TS一覧 */
    private val mpeg2TsUrlList = arrayListOf<String>()

    override suspend fun getMergeVideoList(): List<String> {
        if (mpeg2TsUrlList.isEmpty()) {
            // プレイリストを取得する
            val (playlist, appendUrl) = RequestPlaylist.getPlaylist(m3u8PlaylistUrl)
            // MPEG2-TSだけが書いてあるタイプのプレイリスト
            mpeg2TsUrlList.addAll(PlaylistParser.parsePlaylist(playlist, appendUrl))
        }
        return mpeg2TsUrlList
    }

    override suspend fun release() {
        // MediaStoreへ移動させる
        MediaStoreTool.copyDeviceMovieFolder(context, tempResultFile, folderName, resultFileName)
        tempResultFile.delete()
        tempFileFolder.deleteRecursively()
    }

    companion object {

        /** 結合後のファイル */
        private const val TEMP_RESULT_FILE = "temp_result_file"

        /**
         * 指定した[m3u8Url]がマスタープレイリスト（マルチバリアントプレイリスト）かどうか確認する。
         *
         * [ConecoRequestHlsData]ではプレイリストしか扱わないため、この関数を利用してほしい画質のURLを選ぶ必要があります。
         *
         * プレイリストの場合はnullです。
         * マスタープレイリストの場合は画質一覧を返しますので、
         * 好きな画質の[MultiVariantPlaylist.url]を[ConecoRequestHlsData.m3u8PlaylistUrl]に渡してください。
         *
         * @return 画質の選択があれば画質一覧
         * */
        suspend fun getMasterPlaylist(m3u8Url: String): List<MultiVariantPlaylist>? = withContext(Dispatchers.IO) {
            val (playlist, appendUrl) = RequestPlaylist.getPlaylist(m3u8Url)

            if (PlaylistParser.isMultiVariantPlaylist(playlist)) {
                // 複数の画質が選べるタイプのプレイリスト
                return@withContext PlaylistParser.parseMultiVariantPlaylist(playlist, appendUrl)
            } else {
                // 画質選択無いのでnull
                return@withContext null
            }
        }
    }
}