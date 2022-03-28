package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.coneco.R

/**
 * 選択した動画の一覧
 *
 * @param modifier [Modifier]
 * */
@Composable
fun SelectVideoItemList(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp)
    ) {
        LazyRow(content = {
            item {
                SelectVideoItem {

                }
            }
        })
    }
}

/**
 * 選択した動画一覧の各アイテム
 *
 *
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectVideoItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .width(250.dp)
            .height(100.dp),
        onClick = onClick,
        color = Color.Transparent,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 本来はサムネを出す
                Image(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null
                )
                // メタデータ
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "動画タイトル")
                    Text(text = "0:10")
                }
                // アイコン
                IconButton(onClick = { }) {
                    Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
                }
            }
        }
    )
}