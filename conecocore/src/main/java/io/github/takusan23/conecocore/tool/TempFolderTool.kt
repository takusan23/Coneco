package io.github.takusan23.conecocore.tool

import java.io.File

/**
 * 一時保存先を作成する
 *
 * @param tempFolder 一時保存先
 * */
class TempFolderTool(tempFolder: File) {

    /** 一時音声ファイル保存先、一旦すべてのファイルをデコードして一つのファイルにする */
    val tempRawAudioFile = File(tempFolder, TEMP_RAW_VIDEO_FILE)

    /** 一時音声ファイル保存先 */
    val tempAudioFile = File(tempFolder, TEMP_AUDIO_FILE)

    /** 一時映像ファイル保存先 */
    val tempVideoFile = File(tempFolder, TEMP_VIDEO_FILE)

    /** 一時ファイルを削除する */
    fun delete() {
        tempRawAudioFile.delete()
        tempAudioFile.delete()
        tempVideoFile.delete()
    }

    companion object {
        /** 一時映像ファイル保存先 */
        private const val TEMP_VIDEO_FILE = "temp_video_file"

        /** 一時音声ファイル保存先、一旦すべてのファイルをデコードして一つのファイルにする */
        private const val TEMP_RAW_VIDEO_FILE = "temp_raw_video_file"

        /** 一時音声ファイル保存先 */
        private const val TEMP_AUDIO_FILE = "temp_audio_file"
    }
}