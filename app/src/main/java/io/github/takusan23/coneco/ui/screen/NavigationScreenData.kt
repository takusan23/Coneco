package io.github.takusan23.coneco.ui.screen

import io.github.takusan23.coneco.R

/**
 * 遷移先画面の定義
 *
 * @param screenName 内部で使う名前
 * @param titleBarLabelResId タイトルバーの文言
 * */
enum class NavigationScreenData(
    val screenName: String,
    val titleBarLabelResId: Int,
) {
    /** ローカルの動画 or HLS 選択 */
    VideoSourceSelectScreenData("video_source_select_select", R.string.screen_title_video_source_select),

    /** 動画選択画面 */
    VideoSelectScreenData("video_select_screen", R.string.screen_title_video_select),

    /** HLSのアドレスと画質選択画面 */
    VideoHlsConfigScreenData("video_hls_config_screen", R.string.screen_title_video_hls_select),

    /** 動画情報編集画面 */
    VideoConfigScreenData("video_config_screen", R.string.screen_title_video_edit),

    /** 結合画面 */
    VideoMergeScreenData("video_merge_screen", R.string.screen_title_video_merge);

    companion object {

        /** 遷移数画面数 */
        const val VIDEO_MERGE_STEP = 4

        /**
         * [screenName]から[NavigationScreenData]を探して返す
         *
         * @param screenName 名前
         * @return [NavigationScreenData]
         * */
        fun findScreenOrNull(screenName: String): NavigationScreenData? {
            return values().firstOrNull { it.screenName == screenName }
        }

        /**
         * [NavigationScreenData]から進捗状態を取得する
         *
         * @param data 現在のスクリーン
         * @return 進捗
         * */
        fun getProgress(data: NavigationScreenData): Int {
            return when (data) {
                VideoSourceSelectScreenData -> 1
                VideoSelectScreenData, VideoHlsConfigScreenData -> 2
                VideoConfigScreenData -> 3
                VideoMergeScreenData -> 4
            }
        }
    }
}