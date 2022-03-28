package io.github.takusan23.coneco.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import io.github.takusan23.coneco.ui.theme.ConecoTheme
import io.github.takusan23.coneco.ui.tool.SetNavigationBarColor
import io.github.takusan23.coneco.ui.tool.SetStatusBarColor
import io.github.takusan23.coneco.viewmodel.HomeScreenViewModel

/**
 * 全ての親になる、原点となる部品
 *
 * @param homeScreenViewModel ViewModel
 * */
@Composable
fun MainScreen(homeScreenViewModel: HomeScreenViewModel) {
    ConecoTheme {

        // システムバーの色
        SetStatusBarColor()
        SetNavigationBarColor()

        // メイン画面
        Surface(color = MaterialTheme.colorScheme.surface) {
            HomeScreen(homeScreenViewModel)
        }
    }
}