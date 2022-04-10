package io.github.takusan23.coneco.ui.screen.setting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.BackArrowTopAppBar
import io.github.takusan23.coneco.ui.component.KonoAppCard
import io.github.takusan23.coneco.ui.component.KonoAppHeader
import io.github.takusan23.coneco.ui.component.KonoAppLibraryCard

/**
 * このアプリについて 画面
 *
 * @param onBack 画面戻ってほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonoAppScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember { mutableStateOf(context.packageManager.getPackageInfo(context.packageName, 0).versionName) }

    Scaffold(
        topBar = {
            BackArrowTopAppBar(
                title = stringResource(id = R.string.setting_konoapp_title),
                iconRes = R.drawable.ic_outline_arrow_back_24,
                onBack = onBack
            )
        }
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            KonoAppHeader(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(20.dp))

            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_settings_24,
                label = stringResource(id = R.string.setting_konoapp_version),
                description = version.value,
                onClick = { version.value += ".0" }
            )
            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_account_box_24,
                label = stringResource(id = R.string.setting_konoapp_contact),
                description = TwitterId,
                onClick = { openBrowser(context, TwitterUrl) }
            )
            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_code_24,
                label = stringResource(id = R.string.setting_konoapp_source_code),
                description = "takusan23/Coneco",
                onClick = { openBrowser(context, GitHubUrl) }
            )

            KonoAppLibraryCard(
                modifier = Modifier.padding(5.dp),
                libTitle = stringResource(id = R.string.setting_konoapp_lib_title),
                coreText = stringResource(id = R.string.setting_konoapp_lib_core_title),
                hlsText = stringResource(id = R.string.setting_konoapp_lib_hls_title),
                onCoreLibClick = { openBrowser(context, CoreLibGitHubUrl) },
                onHlsLibClick = { openBrowser(context, HlsLibGitHubUrl) }
            )
        }
    }
}

/** GitHubリンク */
private val GitHubUrl = "https://github.com/takusan23/Coneco"

/** conecocoreリンク */
private val CoreLibGitHubUrl = "https://github.com/takusan23/Coneco/tree/master/conecocore"

/** corehlsリンク */
private val HlsLibGitHubUrl = "https://github.com/takusan23/Coneco/tree/master/conecohls"

/** Twitter ID */
private val TwitterId = "@takusan__23"

/** Twitter Url */
private val TwitterUrl = "https://twitter.com/takusan__23"

/**
 * ブラウザを開く
 *
 * @param context [Context]
 * @param url URL
 * */
private fun openBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}