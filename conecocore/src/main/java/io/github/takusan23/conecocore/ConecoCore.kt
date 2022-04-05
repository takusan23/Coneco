package io.github.takusan23.conecocore

import io.github.takusan23.conecocore.data.ConecoRequestInterface
import io.github.takusan23.conecocore.data.VideoMergeStatus
import io.github.takusan23.conecocore.merge.AudioDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMergeAbstract
import io.github.takusan23.conecocore.merge.VideoDataOpenGlMerge
import io.github.takusan23.conecocore.tool.MergedDataMuxer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 複数の動画を繋げて一つにする
 *
 * [configureAudioFormat]、[configureVideoFormat]を呼んでから[merge]してください。
 *
 * @param videoList 繋げたい動画一覧
 * @param resultFile 結合後のファイル
 * @param tempFileFolder 一時的にファイルを生成するので、それの保存先フォルダ
 * */
class ConecoCore(private val requestData: ConecoRequestInterface) {
    private val _status = MutableStateFlow(VideoMergeStatus.NO_TASK)

    /** 音声を合成する */
    private var audioDataMerge: AudioDataMerge? = null

    /** 映像を合成する */
    private var videoDataMerge: VideoDataMergeAbstract? = null

    /** 進捗状態 */
    val status = _status as StateFlow<VideoMergeStatus>

    /**
     * 音声の情報をセットする
     *
     * @param bitRate ビットレート
     * */
    suspend fun configureAudioFormat(bitRate: Int = 192_000) {
        // 一時保存先
        audioDataMerge = AudioDataMerge(
            videoPathList = requestData.getMergeVideoList(),
            resultFile = requestData.tempAudioFile,
            tempRawDataFile = requestData.tempRawAudioFile,
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
    suspend fun configureVideoFormat(
        bitRate: Int = 1_000_000,
        frameRate: Int = 30,
        isUseOpenGl: Boolean = false,
        videoWidth: Int? = null,
        videoHeight: Int? = null,
    ) {
        val tempVideoFile = requestData.tempVideoFile
        val videoList = requestData.getMergeVideoList()
        // 解像度の変更をする場合はOpenGLモードで
        videoDataMerge = when {
            isUseOpenGl || (videoHeight != null && videoWidth != null) -> {
                VideoDataOpenGlMerge(
                    videoPathList = videoList,
                    resultFile = tempVideoFile,
                    bitRate = bitRate,
                    frameRate = frameRate,
                    videoWidth = videoWidth,
                    videoHeight = videoHeight
                )
            }
            else -> {
                VideoDataMerge(
                    videoPathList = videoList,
                    resultFile = tempVideoFile,
                    bitRate = bitRate,
                    frameRate = frameRate
                )
            }
        }
    }

    /**
     * 結合を開始する
     *
     * @return 処理が終わるまでの時間（ミリ秒）
     * */
    suspend fun merge(): Long {
        // 時間を測ろう！
        val startMs = System.currentTimeMillis()

        // それぞれのファイルの結合
        _status.value = VideoMergeStatus.VIDEO_MERGE
        videoDataMerge?.merge()
        _status.value = VideoMergeStatus.AUDIO_MERGE
        audioDataMerge?.merge()
        // 合わせる
        _status.value = VideoMergeStatus.CONCAT
        MergedDataMuxer.mixed(requestData.mergeResultFile, listOf(requestData.tempVideoFile, requestData.tempAudioFile))
        // 一時ファイルの削除
        _status.value = VideoMergeStatus.CLEANUP
        requestData.release()
        // 終了
        _status.value = VideoMergeStatus.FINISH

        // 時間
        return System.currentTimeMillis() - startMs
    }

    /**
     * 終了時に呼ぶ
     * */
    suspend fun stop() {
        audioDataMerge?.stop()
        videoDataMerge?.stop()
    }

}