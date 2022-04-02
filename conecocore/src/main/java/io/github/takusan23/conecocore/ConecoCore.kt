package io.github.takusan23.conecocore

import io.github.takusan23.conecocore.merge.AudioDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMerge
import io.github.takusan23.conecocore.merge.VideoDataMergeAbstract
import io.github.takusan23.conecocore.merge.VideoDataOpenGlMerge
import io.github.takusan23.conecocore.tool.MergedDataMuxer
import io.github.takusan23.conecocore.tool.TempFolderTool
import io.github.takusan23.conecocore.tool.VideoMergeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _status = MutableStateFlow(VideoMergeStatus.NO_TASK)

    /** 音声を合成する */
    private var audioDataMerge: AudioDataMerge? = null

    /** 映像を合成する */
    private var videoDataMerge: VideoDataMergeAbstract? = null

    /** 一時映像ファイル作成クラス */
    private val tempFileFolderTool = TempFolderTool(tempFileFolder)

    /** 進捗状態 */
    val status = _status as StateFlow<VideoMergeStatus>

    /**
     * 音声の情報をセットする
     *
     * @param bitRate ビットレート
     * */
    fun configureAudioFormat(bitRate: Int = 192_000) {
        // 一時保存先
        audioDataMerge = AudioDataMerge(
            videoList = videoList,
            resultFile = tempFileFolderTool.tempAudioFile,
            tempRawDataFile = tempFileFolderTool.tempRawAudioFile,
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
        val tempVideoFile = tempFileFolderTool.tempVideoFile
        // 解像度の変更をする場合はOpenGLモードで
        videoDataMerge = when {
            isUseOpenGl || (videoHeight != null && videoWidth != null) -> {
                VideoDataOpenGlMerge(videoList, tempVideoFile, bitRate, frameRate, videoWidth, videoHeight)
            }
            else -> {
                VideoDataMerge(videoList, tempVideoFile, bitRate, frameRate)
            }
        }
    }

    /**
     * 結合を開始する
     * */
    suspend fun merge() {
        _status.value = VideoMergeStatus.VIDEO_MERGE
        videoDataMerge?.merge()
        _status.value = VideoMergeStatus.AUDIO_MERGE
        audioDataMerge?.merge()
        // 合わせる
        _status.value = VideoMergeStatus.CONCAT
        MergedDataMuxer.mixed(resultFile, listOf(tempFileFolderTool.tempVideoFile, tempFileFolderTool.tempAudioFile))
        // 終了
        _status.value = VideoMergeStatus.FINISH
        tempFileFolderTool.delete()
    }

    /**
     * 終了時に呼ぶ
     * */
    suspend fun stop() {
        audioDataMerge?.stop()
        videoDataMerge?.stop()
    }

}