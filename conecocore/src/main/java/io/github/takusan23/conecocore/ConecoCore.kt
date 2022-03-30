package io.github.takusan23.conecocore

import io.github.takusan23.conecocore.merge.AudioDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMergeAbstract
import io.github.takusan23.conecocore.merge.VideoDataOpenGlMerge
import java.io.File

/**
 * 複数の動画を繋げて一つにする
 *
 * [configureAudioFormat]、[configureVideoFormat]を呼んでから[merge]してください。
 *
 * @param videoList 繋げたい動画一覧
 * @param resultFile 結合後のファイル
 * @param tempFileFolder 一時的にファイルを生成するので、それの保存先フォルダ
 * */
class ConecoCore(
    private val videoList: List<File>,
    private val resultFile: File,
    private val tempFileFolder: File,
) {

    /** 音声を合成する */
    private var audioDataMerge: AudioDataMerge? = null

    /** 映像を合成する */
    private var videoDataMerge: VideoDataMergeAbstract? = null

    /**
     * 音声の情報をセットする
     *
     * @param bitRate ビットレート
     * */
    fun configureAudioFormat(bitRate: Int = 192_000) {
        // 一時保存先
        val tempRawAudioFile = File(tempFileFolder, "temp_raw_audio_file")
        audioDataMerge = AudioDataMerge(
            videoList = videoList,
            resultFile = resultFile,
            tempRawDataFile = tempRawAudioFile,
            bitRate = bitRate
        )
    }

    /**
     * 映像の情報をセットする
     *
     * 解像度を変える機能は動くか怪しいです。
     *
     * @param bitRate ビットレート
     * @param frameRate フレームレート
     * @param isUseOpenGl OpenGLを利用して動画を繋げる場合はtrue
     * @param videoHeight 動画の高さを変える場合は変えられます。16の倍数であることが必須です。[isUseOpenGl]がtrueになります。
     * @param videoWidth 動画の幅を変える場合は変えられます。16の倍数であることが必須です。[isUseOpenGl]がtrueになります。
     * */
    fun configureVideoFormat(
        bitRate: Int = 1_000_000,
        frameRate: Int = 30,
        isUseOpenGl: Boolean = false,
        videoWidth: Int? = null,
        videoHeight: Int? = null,
    ) {
        // 解像度の変更をする場合はOpenGLモードで
        videoDataMerge = when {
            isUseOpenGl || (videoHeight != null && videoWidth != null) -> {
                VideoDataOpenGlMerge(videoList, resultFile, bitRate, frameRate, videoWidth, videoHeight)
            }
            else -> {
                VideoDataMerge(videoList, resultFile, bitRate, frameRate)
            }
        }
    }

    /**
     * 結合を開始する
     * */
    suspend fun merge() {
        videoDataMerge?.merge()
        audioDataMerge?.merge()
        // 合わせる

    }

    /**
     * 終了時に呼ぶ
     * */
    suspend fun stop() {
        audioDataMerge?.stop()
        videoDataMerge?.stop()
    }

}