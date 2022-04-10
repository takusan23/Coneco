package io.github.takusan23.coneco.ui.screen.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.BackArrowTopAppBar
import io.github.takusan23.coneco.ui.component.SettingItem
import io.github.takusan23.coneco.ui.screen.SettingScreenNavigationData

/**
 * 設定の親画面
 *
 * @param onNavigation 画面遷移時に呼ぶ
 * @param onBack 前の画面に戻ってほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingMasterScreen(
    onNavigation: (String) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            BackArrowTopAppBar(
                title = stringResource(id = R.string.setting_license_title),
                iconRes = R.drawable.ic_outline_arrow_back_24,
                onBack = onBack
            )
        }
    ) {
        LazyColumn {
            item {
                SettingItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.setting_konoapp_title),
                    description = stringResource(id = R.string.setting_konoapp_subtitle),
                    iconRes = R.drawable.ic_outline_settings_24,
                    onClick = { onNavigation(SettingScreenNavigationData.KONO_APP.screenName) }
                )
            }
            item {
                SettingItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.setting_license_title),
                    description = stringResource(id = R.string.setting_license_subtitle),
                    iconRes = R.drawable.ic_outline_description_24,
                    onClick = { onNavigation(SettingScreenNavigationData.LICENSE.screenName) }
                )
            }
        }
    }
}