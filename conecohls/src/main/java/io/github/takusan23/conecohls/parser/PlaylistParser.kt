package io.github.takusan23.conecohls.parser

import io.github.takusan23.conecohls.data.MultiVariantPlaylist
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

/** Hlsのプレイリストを解析する */
object PlaylistParser {

    // --- HLSの予約語 ---

    /** HLSプレイリストのファイルであること */
    private const val EXT_PLAYLIST_FILE = "#EXTM3U"

    /** httpから始まっているか */
    private const val HTTP_SCHEME = "http"

    /** MPEG2-TSのURLが記載されている */
    private const val EXTINF = "#EXTINF:"

    /** マルチバリアントプレイリストのURLの前に記載されている */
    private const val EXT_X_STREAM_INF = "#EXT-X-STREAM-INF:"

    /** マルチバリアントプレイリストのビットレートを取得する正規表現 */
    private val EXT_X_STREAM_INF_BANDWIDTH_REGEX = "BANDWIDTH=(.*)".toRegex()

    /** マルチバリアントプレイリストの解像度を取得する正規表現 */
    private val EXT_X_STREAM_INF_RESOLUTION = "RESOLUTION=(.*)".toRegex()

    /**
     * 与えられたプレイリストがマルチバリアントプレイリストの場合はtrue
     *
     * @param playlist プレイリストの中身
     * @return マルチバリアントプレイリストならtrue
     * */
    fun isMultiVariantPlaylist(playlist: String) = playlist.contains(EXT_X_STREAM_INF)

    /**
     * 相対URLを絶対URLにするために先頭につけるURLを作成する
     *
     * @param m3u8Url m3u8のアドレス
     * @return m3u8を除いたURL
     * */
    fun createAppendUrl(m3u8Url: String): String {
        // 最後を消したURLをつくる
        val url = m3u8Url.toHttpUrl()
        val newUrl = HttpUrl.Builder().apply {
            scheme(url.scheme)
            host(url.host)
            url.encodedPathSegments.dropLast(1).forEachIndexed { _, segment ->
                addPathSegment(segment)
            }
        }.build()
        return newUrl.toUrl().toString()
    }

    /**
     * マルチバリアントなプレイリストを解析する
     *
     * @param baseUrl 相対URLで書かれていた場合に絶対URLにするのに必要。m3u8が置いてある場所？
     * @param playlist プレイリストの中身
     * @return 画質候補
     * */
    fun parseMultiVariantPlaylist(playlist: String, baseUrl: String? = null): List<MultiVariantPlaylist> {
        val multiVariantPlaylist = arrayListOf<MultiVariantPlaylist>()
        // 改行でイテレーターを作る
        val playlistIterator = playlist.lines().iterator()
        while (playlistIterator.hasNext()) {
            val readLine = playlistIterator.next()
            if (readLine.startsWith(EXT_X_STREAM_INF)) {
                // EXT-X-STREAM-INFを解析する。カンマ区切りの配列にする
                val variantParams = readLine.replace(EXT_X_STREAM_INF, "").split(",")
                // パラメーターはない場合があるかも？
                val bandWidth = variantParams.mapNotNull { EXT_X_STREAM_INF_BANDWIDTH_REGEX.find(it) }.firstOrNull()?.groupValues?.get(1)?.toLongOrNull()
                val resolution = variantParams.mapNotNull { EXT_X_STREAM_INF_RESOLUTION.find(it) }.firstOrNull()?.groupValues?.get(1)
                // EXT-X-STREAM-INFの下にURLが書いてあるので
                val url = playlistIterator.next().let {
                    // 絶対パスに変換する
                    if (it.startsWith(HTTP_SCHEME) || baseUrl == null) it else "$baseUrl/$it"
                }
                multiVariantPlaylist.add(MultiVariantPlaylist(url, bandWidth, resolution))
            }
        }
        return multiVariantPlaylist
    }

    /**
     * MPEG2-TSが記載されたプレイリストを解析する
     *
     * @param baseUrl 相対URLで書かれていた場合に絶対URLにするのに必要。m3u8が置いてある場所？
     * @param playlist プレイリストの中身
     * @return MPEG2-TSへのURL
     * */
    fun parsePlaylist(playlist: String, baseUrl: String? = null): List<String> {
        // #が含まれていないやつを取り出す
        return playlist.lines()
            .filter { !it.startsWith("#") && it.isNotEmpty() }
            .map { if (it.startsWith(HTTP_SCHEME) || baseUrl == null) it else "$baseUrl/$it" }
    }

}