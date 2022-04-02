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

        /**
         * 進捗に出すタスク数
         *
         * VIDEO_MERGE AUDIO_MERGE CONCAT の3つ
         * */
        const val TASK_COUNT = 3

        /** 進捗をFloatで返す */
        fun progress(status: VideoMergeStatus) = when (status) {
            VIDEO_MERGE -> 1f
            AUDIO_MERGE -> 2f
            CONCAT -> 3f
            else -> 1f // それ以外は
        } / TASK_COUNT
    }

}