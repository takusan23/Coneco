package io.github.takusan23.conecocore.tool

import android.media.MediaExtractor
import android.media.MediaFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [MediaExtractor]を簡単に扱えるようにしたもの
 * */
object MediaExtractorTool {

    /**
     * 引数に渡した動画パス[videoPath]の情報を[MediaExtractor]で取り出す
     *
     * @param videoPath 情報を取得したい動画の動画パス
     * @param mimeType [ExtractMimeType]。音声、動画どっちか
     * @return [MediaExtractor] / [mimeType]のトラック番号 / [MediaFormat]
     * */
    suspend fun extractMedia(videoPath: String, mimeType: ExtractMimeType): Triple<MediaExtractor, Int, MediaFormat>? = withContext(Dispatchers.IO) {
        val mediaExtractor = MediaExtractor().apply { setDataSource(videoPath) }
        // 映像トラックとインデックス番号のPairを作って返す
        val (index, track) = (0 until mediaExtractor.trackCount)
            .map { index -> index to mediaExtractor.getTrackFormat(index) }
            .firstOrNull { (_, track) -> track.getString(MediaFormat.KEY_MIME)?.startsWith(mimeType.startWidth) == true } ?: return@withContext null
        return@withContext Triple(mediaExtractor, index, track)
    }

    /** [extractMedia]で渡すデータ */
    enum class ExtractMimeType(val startWidth: String) {
        EXTRACT_MIME_AUDIO("audio/"),
        EXTRACT_MIME_VIDEO("video/")
    }

}