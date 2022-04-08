package io.github.takusan23.coneco.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.coneco.ui.screen.setting.SettingScreen
import io.github.takusan23.coneco.ui.theme.ConecoTheme
import io.github.takusan23.coneco.ui.tool.SetNavigationBarColor
import io.github.takusan23.coneco.ui.tool.SetStatusBarColor
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 全ての親になる、原点となる部品
 *
 * @param mergeScreenViewModel ViewModel
 * */
@Composable
fun MainScreen(mergeScreenViewModel: MergeScreenViewModel) {
    ConecoTheme {

        // システムバーの色
        SetStatusBarColor()
        SetNavigationBarColor()

        // すべての親のルーティング
        val mainScreenNavigation = rememberNavController()

        // メイン画面
        Surface(color = MaterialTheme.colorScheme.surface) {
            NavHost(navController = mainScreenNavigation, startDestination = MainScreenNavigationData.MERGE_SCREEN.screenName) {
                // 合成画面、この中の画面でナビゲーションがある
                composable(MainScreenNavigationData.MERGE_SCREEN.screenName) {
                    MergeScreen(mergeScreenViewModel, mainScreenNavigation)
                }
                // 設定画面
                composable(MainScreenNavigationData.SETTING.screenName) {
                    SettingScreen()
                }
            }
        }
    }
}