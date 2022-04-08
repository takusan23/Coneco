package io.github.takusan23.coneco.ui.screen

/**
 * 設定遷移先
 *
 * @param screenName 内部で使う名前
 * */
enum class SettingScreenNavigationData(
    val screenName: String,
) {

    /** 設定の親ページ */
    MASTER("setting_master"),

    /** ライセンス画面 */
    LICENSE("setting_license"),

    /** このアプリについて */
    KONO_APP("setting_kono_app")

}