package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R

/**
 * アプリのタイトルバー
 *
 * @param onOpenBrowserClick ブラウザ起動ボタン押したら
 * */
@Composable
fun ConecoAppBar(
    title: String = stringResource(id = R.string.app_name),
    onOpenBrowserClick: () -> Unit = {},
) {
    Column {
        MediumTopAppBar(
            title = { Text(text = title, fontSize = 25.sp) },
            actions = {
                IconButton(onClick = onOpenBrowserClick) {
                    Icon(painter = painterResource(id = R.drawable.ic_outline_open_in_browser_24), contentDescription = null)
                }
            }
        )
        // 各ステップを表示するインジケーター
        TitleBarIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            count = 3,
            progress = 1
        )
    }
}

/**
 * 各ステップを表示するインジケーター
 *
 * @param modifier [Modifier]
 * @param count ステップ数
 * @param progress 現在のステップ
 * */
@Composable
private fun TitleBarIndicator(
    modifier: Modifier = Modifier,
    count: Int,
    progress: Int,
) {
    Row(modifier = modifier) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .height(5.dp)
                    .weight(1f)
                    .background(
                        color = if (index < progress) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
    }
}