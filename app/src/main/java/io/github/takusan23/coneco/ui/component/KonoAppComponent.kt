package io.github.takusan23.coneco.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R


/**
 * このアプリについての各カード
 *
 * @param modifier [Modifier]
 * @param iconRes アイコン
 * @param label アイコンの隣
 * @param description 説明
 * @param onClick 押したとき
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonoAppCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(5.dp),
                    painter = painterResource(id = iconRes),
                    contentDescription = null
                )
                Text(text = label)
            }
            Text(
                text = description,
                fontSize = 20.sp
            )
        }
    }
}

/**
 * ライブラリとして独立してますよ画面
 *
 * @param modifier [Modifier]
 * @param libTitle タイトル部分
 * @param coreText coreの説明
 * @param hlsText hlsの説明
 * @param onCoreLibClick core選択時
 * @param onHlsLibClick hls選択時
 * */
@Composable
fun KonoAppLibraryCard(
    modifier: Modifier = Modifier,
    libTitle: String,
    coreText: String,
    hlsText: String,
    onCoreLibClick: () -> Unit,
    onHlsLibClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(20.dp),
        contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 5.dp),
                    painter = painterResource(id = R.drawable.ic_outline_set_meal_24),
                    contentDescription = null
                )
                Text(text = libTitle)
            }
            Row(modifier = Modifier.padding(5.dp)) {
                KonoAppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .weight(1f),
                    iconRes = R.drawable.ic_outline_code_24,
                    label = "conecocore",
                    description = coreText,
                    onClick = onCoreLibClick
                )
                KonoAppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .weight(1f),
                    iconRes = R.drawable.ic_outline_code_24,
                    label = "conecohls",
                    description = hlsText,
                    onClick = onHlsLibClick
                )
            }
        }
    }
}

/**
 * このアプリについて ヘッター部
 *
 * 押したら色が変わるよ
 *
 * @param modifier [Modifier]
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonoAppHeader(modifier: Modifier = Modifier) {
    val colorList = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )
    val currentIconColor = remember { mutableStateOf(colorList.random()) }
    val colorAnim = animateColorAsState(targetValue = currentIconColor.value)

    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        onClick = { currentIconColor.value = colorList.random() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.coneco_android),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorAnim.value)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                color = colorAnim.value
            )
        }
    }
}