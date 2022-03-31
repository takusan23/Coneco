package io.github.takusan23.conecocore.tool

/** 合成状況 */
enum class VideoMergeStatus {
    /** まだやってない */
    NO_TASK,

    /** 映像の連結中 */
    VIDEO_MERGE,

    /** 音声の連結中 */
    AUDIO_MERGE,

    /** 映像と音声をコンテナへ格納中 */
    CONCAT,

    /** 終了 */
    FINISH;

    companion object {

        /** 名前から [VideoMergeStatus] を返す */
        fun findFromName(name: String) = valueOf(name)

        /** 合計数 */
        val length get() = values().size
    }

}