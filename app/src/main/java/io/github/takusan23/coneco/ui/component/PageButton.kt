package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.coneco.R

/**
 * ページ切り替えボタン
 *
 * @param isEnableNext 次へボタンを有効にするならtrue
 * @param isEnablePrev 前へボタンを有効にするならtrue
 * @param onNext 次へボタンを押したら呼ばれる
 * @param onPrev 前へボタンを押したら呼ばれる
 * */
@Composable
fun PageButton(
    isEnableNext: Boolean = true,
    isEnablePrev: Boolean = true,
    onNext: () -> Unit = { },
    onPrev: () -> Unit = { },
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isEnablePrev) {
            Button(
                modifier = Modifier.padding(5.dp),
                onClick = onPrev
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_navigate_before_24), contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "前へ")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isEnableNext) {
            Button(
                modifier = Modifier.padding(5.dp),
                onClick = onNext
            ) {
                Text(text = "次へ")
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(painter = painterResource(id = R.drawable.ic_outline_navigate_next_24), contentDescription = null)
            }
        }
    }
}