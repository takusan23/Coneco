package io.github.takusan23.conecohls.parser

import org.junit.Assert
import org.junit.Test

/** [PlaylistParser]のてすとこーど */
class PlaylistParserTest {

    /** マルチバリアントプレイリスト判定テスト */
    @Test
    fun checkMultiVariantPlaylist() {
        val resultTrue = PlaylistParser.isMultiVariantPlaylist(MULTI_VARIANT_PLAYLIST)
        val resultFalse = PlaylistParser.isMultiVariantPlaylist(PLAYLIST)
        Assert.assertTrue(resultTrue)
        Assert.assertFalse(resultFalse)
    }

    /** マルチバリアントプレイリストパーステスト */
    @Test
    fun parseMultiVariantPlaylist() {
        val multiVariantPlaylist = PlaylistParser.parseMultiVariantPlaylist(MULTI_VARIANT_PLAYLIST)
        multiVariantPlaylist.forEach {
            println(it.url)
            println(it.bandWidth)
            println(it.resolution)
            println("---")
        }
    }

    /** プレイリストパーステスト */
    @Test
    fun parsePlaylist() {
        val playlist = PlaylistParser.parsePlaylist(PLAYLIST, "http://example.com/low")
        playlist.forEach {
            println(it)
            println("---")
        }
    }

    companion object {

        /** マルチバリアントプレイリストの例 */
        const val MULTI_VARIANT_PLAYLIST = """
#EXTM3U
#EXT-X-STREAM-INF:BANDWIDTH=150000,RESOLUTION=416x234,CODECS="avc1.42e00a,mp4a.40.2"
http://example.com/low/index.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=240000,RESOLUTION=416x234,CODECS="avc1.42e00a,mp4a.40.2"
http://example.com/lo_mid/index.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=440000,RESOLUTION=416x234,CODECS="avc1.42e00a,mp4a.40.2"
http://example.com/hi_mid/index.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=640000,RESOLUTION=640x360,CODECS="avc1.42e00a,mp4a.40.2"
http://example.com/high/index.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=64000,CODECS="mp4a.40.5"
http://example.com/audio/index.m3u8
"""

        /** プレイリストの例 */
        const val PLAYLIST = """
#EXTM3U
#EXT-X-PLAYLIST-TYPE:VOD
#EXT-X-TARGETDURATION:10
#EXT-X-VERSION:4
#EXT-X-MEDIA-SEQUENCE:0
#EXTINF:10.0,
fileSequenceA.ts
#EXTINF:10.0,
fileSequenceB.ts
#EXTINF:10.0,
fileSequenceC.ts
#EXTINF:9.0,
fileSequenceD.ts
#EXT-X-ENDLIST
"""

    }

}