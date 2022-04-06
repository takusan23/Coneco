package io.github.takusan23.conecohls.data

import io.github.takusan23.conecocore.data.ConecoRequestInterface
import io.github.takusan23.conecohls.parser.PlaylistParser
import io.github.takusan23.conecohls.playlist.RequestPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * [ConecoRequestInterface]のHLS対応版、生放送は対応してません（#EXT-X-PLAYLIST-TYPE:VOD のみ）
 *
 * @param m3u8Url なんとか.m3u8 のパス、playlist、master両方行けるはず
 * @param resultFile 繋げたファイルの保存先
 * @param tempFileFolder 一時保存フォルダをください
 * */
data class ConecoRequestHlsData(
    val m3u8Url: String,
    val resultFile: File,
    val tempFileFolder: File,
) : ConecoRequestInterface {

    override val mergeResultFile: File
        get() = resultFile

    override val tempFolder: File
        get() = tempFileFolder

    /** プレイリストのMPEG2-TS一覧 */
    private val mpeg2TsUrlList = arrayListOf<String>()

    /**
     * [m3u8Url]を取りに行く。マルチバリアントプレイリスト（マスタープレイリスト）の場合は画質選択が必要です。
     *
     * 合成を始める前に呼ぶ必要があります。
     *
     * @return 画質の選択がない場合はnullです。画質の選択が必要な場合は画質一覧を返すので、[selectPlaylist]で画質をセットしておいてください。
     * */
    suspend fun loadPlaylist(): List<MultiVariantPlaylist>? = withContext(Dispatchers.IO) {
        mpeg2TsUrlList.clear()
        val (playlist, appendUrl) = RequestPlaylist.getPlaylist(m3u8Url)

        if (PlaylistParser.isMultiVariantPlaylist(playlist)) {
            // 複数の画質が選べるタイプのプレイリスト
            return@withContext PlaylistParser.parseMultiVariantPlaylist(playlist, appendUrl)
        } else {
            // MPEG2-TSだけが書いてあるタイプのプレイリスト
            mpeg2TsUrlList.addAll(PlaylistParser.parsePlaylist(playlist, appendUrl))
            // 画質選択無いのでnull
            return@withContext null
        }
    }

    /**
     * [loadPlaylist]で配列が返ってきた場合は画質を選んでこの関数で指定する必要があります。
     *
     * @param multiVariantPlaylist 選択する画質
     * */
    suspend fun selectPlaylist(multiVariantPlaylist: MultiVariantPlaylist) = withContext(Dispatchers.IO) {
        val (playlist, appendUrl) = RequestPlaylist.getPlaylist(m3u8Url)
        // MPEG2-TSだけが書いてあるタイプのプレイリスト
        mpeg2TsUrlList.addAll(PlaylistParser.parsePlaylist(playlist, appendUrl))
    }

    override suspend fun getMergeVideoList(): List<String> {
        return mpeg2TsUrlList
    }

    override suspend fun release() {
        // なにもしない
    }
}