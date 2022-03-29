package io.github.takusan23.coneco.ui.screen

import io.github.takusan23.coneco.R

/**
 * 遷移先画面の定義
 *
 * @param screenName 内部で使う名前
 * @param titleBarLabelResId タイトルバーの文言
 * @param indicatorProgress インジケーターの位置
 * */
enum class NavigationScreenData(
    val screenName: String,
    val titleBarLabelResId: Int,
    val indicatorProgress: Int,
) {
    /** 動画選択画面 */
    VideoSelectScreenData("video_select_screen", R.string.screen_title_video_select, 1),

    /** 動画情報編集画面 */
    VideoEditScreenData("video_edit_screen", R.string.screen_title_video_edit, 2),

    /** 結合画面 */
    VideoMergeScreenData("video_merge_screen", R.string.screen_title_video_merge, 3);

    companion object {

        /** 遷移数画面数 */
        val pageSize: Int
            get() = values().size

        /**
         * [screenName]から[NavigationScreenData]を探して返す
         *
         * @param screenName 名前
         * @return [NavigationScreenData]
         * */
        fun findScreenOrNull(screenName: String): NavigationScreenData? {
            return values().firstOrNull { it.screenName == screenName }
        }
    }
}