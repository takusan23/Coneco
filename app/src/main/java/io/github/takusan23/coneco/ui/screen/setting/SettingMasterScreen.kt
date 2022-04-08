package io.github.takusan23.coneco.ui.screen.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.SettingItem
import io.github.takusan23.coneco.ui.screen.SettingScreenNavigationData

/**
 * 設定の親画面
 *
 * @param onNavigation 画面遷移時に呼ぶ
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingMasterScreen(onNavigation: (String) -> Unit) {
    Scaffold(topBar = { MediumTopAppBar(title = { Text(text = "設定画面", fontSize = 25.sp) }) }) {
        LazyColumn {
            item {
                SettingItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = "ライセンス",
                    description = "ありがとうございます！",
                    iconRes = R.drawable.ic_outline_description_24,
                    onClick = { onNavigation(SettingScreenNavigationData.LICENSE.screenName) }
                )
            }
            item {
                SettingItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = "このアプリについて",
                    iconRes = R.drawable.ic_launcher_foreground,
                    onClick = { onNavigation(SettingScreenNavigationData.KONO_APP.screenName) }
                )
            }
        }
    }
}