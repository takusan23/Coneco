package io.github.takusan23.coneco.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R

/**
 * アプリのタイトルバー
 *
 * @param onOpenBrowserClick ブラウザ起動ボタン押したら
 * */
@Composable
fun ConecoAppBar(
    onOpenBrowserClick: () -> Unit = {},
) {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp
            )
        },
        actions = {
            IconButton(onClick = onOpenBrowserClick) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_open_in_browser_24), contentDescription = null)
            }
        }
    )
}