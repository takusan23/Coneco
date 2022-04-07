package io.github.takusan23.conecohls.playlist

import io.github.takusan23.conecohls.parser.PlaylistParser
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RequestPlaylistTest {

    /** m3u8リクエストテスト、パーステスト */
    @Test
    fun getPlaylistAndParse() {
        runBlocking {
            val (playlistText, appendUrl) = RequestPlaylist.getPlaylist(MASTER_PLAYLIST_URL)
            if (PlaylistParser.isMultiVariantPlaylist(playlistText)) {
                // 画質選択あり
                val multiVariantPlaylist = PlaylistParser.parseMultiVariantPlaylist(playlistText, appendUrl)
                multiVariantPlaylist.forEach {
                    println("ビットレート=${it.bandWidth}")
                    println("解像度=${it.resolution}")
                    println("プレイリストURL=${it.url}")
                    println("---")
                }

                // プレイリスト取得
                val lowQualityPlaylist = multiVariantPlaylist[0]
                val (lowPlaylistText, lowAppendUrl) = RequestPlaylist.getPlaylist(lowQualityPlaylist.url)
                PlaylistParser.parsePlaylist(lowPlaylistText, lowAppendUrl).forEach {
                    println(it)
                    println("---")
                }
            } else {
                PlaylistParser.parsePlaylist(playlistText, appendUrl).forEach {
                    println(it)
                    println("---")
                }
            }
        }
    }

    companion object {

        /** マスタープレイリストのURL */
        const val MASTER_PLAYLIST_URL = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"

        /** プレイリストのURL */
        const val PLAYLIST_URL = "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8"

    }
}