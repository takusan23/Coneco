package io.github.takusan23.conecocore.merge

import android.media.MediaMuxer
import java.io.File

/**
 * 映像を結合する処理の共通部分を切り出した
 *
 * @param videoPathList 結合する動画のパス配列
 * @param resultFile 結合後のファイル
 * */
abstract class VideoDataMergeAbstract(
    videoPathList: List<String>,
    private val resultFile: File,
) {
    /** 結合する動画の配列のイテレータ */
    protected val videoListIterator = videoPathList.listIterator()

    /** ファイル合成 */
    protected val mediaMuxer by lazy { MediaMuxer(resultFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4) }

    /** 結合する */
    abstract suspend fun merge()

    /** 終了させる */
    abstract suspend fun stop()

    companion object {

        /** タイムアウト */
        const val TIMEOUT_US = 10_000L

        /** MediaCodecでもらえるInputBufferのサイズ */
        const val INPUT_BUFFER_SIZE = 655360

        /** トラック番号が空の場合 */
        const val UNDEFINED_TRACK_INDEX = -100

    }
}