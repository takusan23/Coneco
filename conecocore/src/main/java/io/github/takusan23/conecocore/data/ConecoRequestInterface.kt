package io.github.takusan23.conecocore.data

import java.io.File

/**
 * [ConecoRequestData]の共通部分。
 *
 * UriだろうとFileだろうと扱えるようにする。
 * */
interface ConecoRequestInterface {

    /** 一時音声ファイル保存先、一旦すべてのファイルをデコードして一つのファイルにする */
    val tempRawAudioFile get() = File(tempFolder, TEMP_RAW_VIDEO_FILE)

    /** 一時音声ファイル保存先 */
    val tempAudioFile get() = File(tempFolder, TEMP_AUDIO_FILE)

    /** 一時映像ファイル保存先 */
    val tempVideoFile get() = File(tempFolder, TEMP_VIDEO_FILE)

    /** 結合先のファイル */
    val mergeResultFile: File

    /** 一時保存先。一時的に結合した音声、映像ファイルを置くためのフォルダです。 */
    val tempFolder: File

    /**
     * 動画一覧のパスを返す。file://かhttps://で受け付ける。複数回呼ばれるので注意してね。
     *
     * ファイルアクセスがあるかもなんでサスペンドになってる。
     * */
    suspend fun getMergeVideoList(): List<String>

    /** あとしまつ。一時ファイルの削除など */
    suspend fun release()

    companion object {
        /** 一時映像ファイル保存先 */
        const val TEMP_VIDEO_FILE = "temp_video_file"

        /** 一時音声ファイル保存先、一旦すべてのファイルをデコードして一つのファイルにする */
        const val TEMP_RAW_VIDEO_FILE = "temp_raw_video_file"

        /** 一時音声ファイル保存先 */
        const val TEMP_AUDIO_FILE = "temp_audio_file"
    }
}