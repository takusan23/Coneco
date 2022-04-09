package io.github.takusan23.coneco.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

/**
 * 戻る付きのアプリバー
 *
 * @param title タイトル
 * @param iconRes アイコンリソースId
 * */
@Composable
fun BackArrowTopAppBar(
    title: String,
    iconRes: Int,
    onBack: () -> Unit,
) {
    MediumTopAppBar(
        title = { Text(text = title, fontSize = 25.sp) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(id = iconRes), contentDescription = null)
            }
        },
    )
}