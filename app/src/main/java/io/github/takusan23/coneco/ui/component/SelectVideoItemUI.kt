package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.takusan23.coneco.R

/**
 * 動画選択UI
 *
 * @param modifier [Modifier]
 * @param isOpen 展開時はtrue
 * @param onOpenClick 展開ボタンを押したら呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectVideoItemUI(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    onOpenClick: (Boolean) -> Unit,
) {

    val icon = if (isOpen) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(20.dp),
        onClick = { onOpenClick(!isOpen) },
        content = {
            Column {
                // タイトル部
                CardTitle(
                    icon = R.drawable.ic_outline_video_file_24,
                    label = "結合する動画の選択",
                    expandedIcon = icon
                )
                if (isOpen) {
                    Divider()
                    SelectVideoItemList(Modifier.padding(10.dp))
                }
            }
        }
    )


}