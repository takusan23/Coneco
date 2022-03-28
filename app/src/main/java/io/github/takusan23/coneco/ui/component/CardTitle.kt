package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * 各カードのタイトル部分
 *
 * @param icon アイコンのリソースID
 * @param label ラベルの文言
 * @param expandedIcon 展開、格納ボタンのリソースID
 * */
@Composable
fun CardTitle(
    icon: Int,
    label: String,
    expandedIcon: Int,
) {
    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(1f),
                text = label
            )
            Icon(
                painter = painterResource(id = expandedIcon),
                contentDescription = null
            )
        }
    )
}