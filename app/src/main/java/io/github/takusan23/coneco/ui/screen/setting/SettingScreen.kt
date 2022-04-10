package io.github.takusan23.coneco.ui.screen.setting

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.coneco.ui.screen.SettingScreenNavigationData

/**
 * 設定画面、オープンソースライセンスとかこのアプリについてとか。
 *
 * @param onBack 戻ってほしいときに呼ばれる
 * */
@Composable
fun SettingScreen(
    onBack: () -> Unit,
) {
    val settingNavigation = rememberNavController()

    NavHost(navController = settingNavigation, startDestination = SettingScreenNavigationData.MASTER.screenName) {
        composable(SettingScreenNavigationData.MASTER.screenName) {
            SettingMasterScreen(
                onNavigation = { settingNavigation.navigate(it) },
                onBack = onBack
            )
        }
        composable(SettingScreenNavigationData.LICENSE.screenName) {
            LicenseScreen(onBack = { settingNavigation.popBackStack() })
        }
        composable(SettingScreenNavigationData.KONO_APP.screenName) {
            KonoAppScreen(onBack = { settingNavigation.popBackStack() })
        }
    }
}