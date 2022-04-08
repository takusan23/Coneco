package io.github.takusan23.coneco.ui.screen

/**
 * [MainScreen]の遷移先情報
 *
 * @param screenName 内部で使う名前
 * */
enum class MainScreenNavigationData(
    val screenName: String,
) {
    /** 動画合成画面。長かったのでナビゲーションをネストしてる */
    MERGE_SCREEN("merge_screen"),

    /** 設定画面 */
    SETTING("setting")
}