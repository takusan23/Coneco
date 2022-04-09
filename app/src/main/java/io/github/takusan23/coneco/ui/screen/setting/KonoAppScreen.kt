package io.github.takusan23.coneco.ui.screen.setting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.KonoAppCard
import io.github.takusan23.coneco.ui.component.KonoAppLibraryCard

/**
 * このアプリについて 画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonoAppScreen() {
    val context = LocalContext.current
    val version = remember { mutableStateOf(context.packageManager.getPackageInfo(context.packageName, 0).versionName) }

    Scaffold(topBar = { MediumTopAppBar(title = { Text(text = "このアプリについて", fontSize = 25.sp) }) }) {
        Column {

            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_settings_24,
                label = "バージョン",
                description = version.value,
                onClick = { version.value += ".0" }
            )
            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_account_box_24,
                label = "何かあれば - Twitter",
                description = TwitterId,
                onClick = { openBrowser(context, TwitterUrl) }
            )
            KonoAppCard(
                modifier = Modifier.padding(5.dp),
                iconRes = R.drawable.ic_outline_code_24,
                label = "ソースコード - GitHub",
                description = "takusan23/Coneco",
                onClick = { openBrowser(context, GitHubUrl) }
            )

            KonoAppLibraryCard(
                modifier = Modifier.padding(5.dp),
                libTitle = "動画をつなげるコアの部分はライブラリとして独立してます。GitHubから見れます。",
                coreText = "複数の動画を繋げるライブラリ。いまいちよく分かっていない。",
                hlsText = "conecocoreへHLSで配信されている動画も扱えるようにしたもの。",
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