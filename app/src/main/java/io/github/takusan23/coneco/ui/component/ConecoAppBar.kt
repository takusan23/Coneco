package io.github.takusan23.coneco.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
 * @param title タイトル
 * @param indicatorCount ステップ数
 * @param indicatorProgress 現在のステップ
 * @param onOpenSettingClick 設定押したとき
 * */
@Composable
fun ConecoAppBar(
    title: String = stringResource(id = R.string.app_name),
    indicatorProgress: Int = 0,
    indicatorCount: Int = 3,
    onOpenSettingClick: () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onOpenSettingClick) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_settings_24), contentDescription = null)
            }
        }
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            text = title,
            fontSize = 25.sp
        )
        // 各ステップを表示するインジケーター
        TitleBarIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            count = indicatorCount,
            progress = indicatorProgress
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
                        color = animateColorAsState(
                            targetValue = if (index < progress) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        ).value,
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
    }
}