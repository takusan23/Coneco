package io.github.takusan23.conecocore.merge

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.view.Surface
import io.github.takusan23.conecocore.tool.MediaExtractorTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


/**
 * 映像データを結合する
 *
 * 例外処理を忘れずにやってください。
 *
 * @param videoList 結合する動画、音声ファイルの配列。入っている順番どおりに結合します。
 * @param resultFile 結合したファイル
 * @param bitRate ビットレート。何故か取れなかった
 * @param frameRate フレームレート。何故か取れなかった
 * */
class VideoDataMerge(
    videoList: List<File>,
    private val resultFile: File,
    private val bitRate: Int = 1_000_000, // 1Mbps
    private val frameRate: Int = 30, // 30fps
) : VideoDataMergeAbstract(videoList, resultFile) {

    /** 取り出した[MediaFormat] */
    private var currentMediaFormat: MediaFormat? = null

    /** 現在進行中の[MediaExtractor] */
    private var currentMediaExtractor: MediaExtractor? = null

    /** エンコード用 [MediaCodec] */
    private var encodeMediaCodec: MediaCodec? = null

    /** デコード用 [MediaCodec] */
    private var decodeMediaCodec: MediaCodec? = null

    /** エンコーダーとデコーダーの橋渡しをするSurface */
    private var encoderSurface: Surface? = null

    /**
     * 結合を開始する
     * */
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun merge() = withContext(Dispatchers.Default) {

        /**
         * MediaExtractorで動画ファイルを読み出す
         *
         * @param path 動画パス
         * */
        suspend fun extractVideoFile(path: String) {
            // 動画の情報を読み出す
            val (_mediaExtractor, index, format) = MediaExtractorTool.extractMedia(path, MediaExtractorTool.ExtractMimeType.EXTRACT_MIME_VIDEO) ?: return
            currentMediaExtractor = _mediaExtractor
            currentMediaFormat = format
            // 音声のトラックを選択
            currentMediaExtractor?.selectTrack(index)
            currentMediaExtractor?.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
        }

        // 最初の動画を解析
        extractVideoFile(videoListIterator.next().path)

        // 解析結果から各パラメータを取り出す
        // 動画の幅、高さは16の倍数である必要があります。（どこに書いてんねんクソが）
        val mimeType = currentMediaFormat?.getString(MediaFormat.KEY_MIME)!! // video/avc
        val width = currentMediaFormat?.getInteger(MediaFormat.KEY_WIDTH)!! // 1280
        val height = currentMediaFormat?.getInteger(MediaFormat.KEY_HEIGHT)!! // 720

        // エンコーダーにセットするMediaFormat
        val videoMediaFormat = MediaFormat.createVideoFormat(mimeType, width, height).apply {
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, INPUT_BUFFER_SIZE)
            setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        }

        // 後に映像トラックのトラック番号が入る
        // encodeMediaCodec.outputFormat を MediaMuxer へ渡す
        var videoTrackIndex = UNDEFINED_TRACK_INDEX

        // エンコード用（生データ -> H.264）MediaCodec
        encodeMediaCodec = MediaCodec.createEncoderByType(mimeType).apply {
            configure(videoMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        }

        // エンコーダーのSurfaceを取得
        // デコーダーの出力Surfaceの項目にこれを指定して、エンコーダーに映像データがSurface経由で行くようにする
        encoderSurface = encodeMediaCodec!!.createInputSurface()

        // デコード用（H.264 -> 生データ）MediaCodec
        decodeMediaCodec = MediaCodec.createDecoderByType(mimeType).apply {
            // デコード時は MediaExtractor の MediaFormat で良さそう
            configure(currentMediaFormat!!, encoderSurface, null, 0)
        }

        // nonNull
        val decodeMediaCodec = decodeMediaCodec!!
        val encodeMediaCodec = encodeMediaCodec!!
        encodeMediaCodec.start()
        decodeMediaCodec.start()

        // 前回の動画ファイルを足した動画時間
        var totalPresentationTime = 0L
        var prevPresentationTime = 0L

        // メタデータ格納用
        val bufferInfo = MediaCodec.BufferInfo()

        // ループ制御
        var outputDone = false
        var inputDone = false

        /**
         *  --- 複数ファイルを全てデコードする ---
         * */
        while (!outputDone) {
            if (!inputDone) {

                val inputBufferId = decodeMediaCodec.dequeueInputBuffer(TIMEOUT_US)
                if (inputBufferId >= 0) {
                    val inputBuffer = decodeMediaCodec.getInputBuffer(inputBufferId)!!
                    val size = currentMediaExtractor!!.readSampleData(inputBuffer, 0)
                    if (size > 0) {
                        // デコーダーへ流す
                        // 今までの動画の分の再生位置を足しておく
                        decodeMediaCodec.queueInputBuffer(inputBufferId, 0, size, currentMediaExtractor!!.sampleTime + totalPresentationTime, 0)
                        currentMediaExtractor!!.advance()
                        // 一個前の動画の動画サイズを控えておく
                        // else で extractor.sampleTime すると既に-1にっているので
                        if (currentMediaExtractor!!.sampleTime != -1L) {
                            prevPresentationTime = currentMediaExtractor!!.sampleTime
                        }
                    } else {
                        totalPresentationTime += prevPresentationTime
                        // データがないので次データへ
                        if (videoListIterator.hasNext()) {
                            // 次データへ
                            val file = videoListIterator.next()
                            // 多分いる
                            decodeMediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0, 0)
                            // 動画の情報を読み出す
                            currentMediaExtractor!!.release()
                            extractVideoFile(file.path)
                        } else {
                            // データなくなった場合は終了
                            decodeMediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            // 開放
                            currentMediaExtractor!!.release()
                            // 終了
                            inputDone = true
                        }
                    }
                }
            }

            var decoderOutputAvailable = true
            while (decoderOutputAvailable) {
                // Surface経由でデータを貰って保存する
                val encoderStatus = encodeMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                if (encoderStatus >= 0) {
                    val encodedData = encodeMediaCodec.getOutputBuffer(encoderStatus)!!
                    if (bufferInfo.size > 1) {
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                            // ファイルに書き込む...
                            mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
                        } else if (videoTrackIndex == UNDEFINED_TRACK_INDEX) {
                            // MediaMuxerへ映像トラックを追加するのはこのタイミングで行う
                            // このタイミングでやると固有のパラメーターがセットされたMediaFormatが手に入る(csd-0 とか)
                            // 映像がぶっ壊れている場合（緑で塗りつぶされてるとか）は多分このあたりが怪しい
                            val newFormat = encodeMediaCodec.outputFormat
                            videoTrackIndex = mediaMuxer.addTrack(newFormat)
                            mediaMuxer.start()
                        }
                    }
                    outputDone = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    encodeMediaCodec.releaseOutputBuffer(encoderStatus, false)
                }
                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    continue
                }
                // Surfaceへレンダリングする。そしてOpenGLでゴニョゴニョする
                val outputBufferId = decodeMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    decoderOutputAvailable = false
                } else if (outputBufferId >= 0) {
                    val doRender = bufferInfo.size != 0
                    decodeMediaCodec.releaseOutputBuffer(outputBufferId, doRender)
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        decoderOutputAvailable = false
                        encodeMediaCodec.signalEndOfInputStream()
                    }
                }
            }
        }

        // あとしまつ
        stop()
    }

    /** 終了時に呼ぶ */
    override suspend fun stop() {
        // デコーダー終了
        decodeMediaCodec?.stop()
        decodeMediaCodec?.release()
        // Surface開放
        encoderSurface?.release()
        // エンコーダー終了
        encodeMediaCodec?.stop()
        encodeMediaCodec?.release()
        // MediaMuxerも終了
        mediaMuxer.stop()
        mediaMuxer.release()
    }

}