package io.github.takusan23.conecohls.playlist

import io.github.takusan23.conecohls.HttpClient
import io.github.takusan23.conecohls.parser.PlaylistParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * master.m3u8 playlist.m3u8 をダウンロードしてプレイリスト一覧を取得するやつ
 *
 * - master.m3u8
 *  - 複数の画質が選べる場合に playlist.m3u8 のURLが複数記載されて返される
 * - playlist.m3u8
 *  - MPEG2-TS のURLがある（実際の映像データ）
 * */
object RequestPlaylist {

    /**
     * m3u8を解析する、プレイヤーじゃないので最低限の解析しかしない。
     *
     * 特にエラーハンドリングとかはやってない。
     *
     * @param m3u8Url m3u8のURL
     * @return プレイリストの中身 と m3u8を抜いたURL のPair
     * */
    suspend fun getPlaylist(m3u8Url: String) = withContext(Dispatchers.IO) {
        // とりあえずリクエスト
        val response = HttpClient.execGetRequest(m3u8Url)
        val playlist = response.body!!.string()
        // 絶対URLにするために先頭につけるURLを作る
        val appendUrl = PlaylistParser.createAppendUrl(m3u8Url)
        return@withContext playlist to appendUrl
    }

}