package io.github.takusan23.coneco.ui.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.data.SelectVideoItemData
import io.github.takusan23.coneco.tool.GetVideoData

/**
 * 選択した動画を一覧表示する
 *
 * @param list 選択した動画
 * @param onDeleteClick 削除クリックした際に呼ばれる
 * */
@Composable
fun SelectVideoList(
    modifier: Modifier = Modifier,
    list: List<SelectVideoItemData>,
    onDeleteClick: (Uri) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        content = {
            items(list) { item ->
                SelectVideoListItem(
                    videoItemData = item,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    )
}

/**
 * [SelectVideoList]の各レイアウト
 *
 * @param videoItemData [SelectVideoItemData]
 * @param onDeleteClick 削除クリックした際に呼ばれる
 * */
@Composable
private fun SelectVideoListItem(
    videoItemData: SelectVideoItemData,
    onDeleteClick: (Uri) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .padding(5.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .aspectRatio(1.7f),
                bitmap = videoItemData.thumb.asImageBitmap(),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            ) {
                Text(
                    text = videoItemData.title,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(text = "${GetVideoData.toMB(videoItemData.bytes)} MB")
            }
            IconButton(onClick = { onDeleteClick(videoItemData.uri) }) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            }
        }
    }
}