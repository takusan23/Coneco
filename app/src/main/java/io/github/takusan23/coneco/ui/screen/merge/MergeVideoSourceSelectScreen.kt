package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.takusan23.coneco.R

/**
 * 繋げる動画の場所がローカルかHLSか選ぶ
 *
 * @param navController 画面遷移
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeVideoSourceSelectScreen(navController: NavHostController) {
    Scaffold {
        Column {
            // ヘッダー
            SelectScreenHeader()
            // どっちなの！？
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                    SelectButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        iconRes = R.drawable.ic_outline_aod_24,
                        text = "端末内の動画を繋げる",
                        onClick = { navController.navigate(MergeScreenNavigationData.VideoSelectScreenData.screenName) }
                    )
                    SelectButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        iconRes = R.drawable.ic_outline_language_24,
                        text = "HLS形式で配信している動画を繋げる（実験的）",
                        onClick = { navController.navigate(MergeScreenNavigationData.VideoHlsConfigScreenData.screenName) }
                    )
                }
            }
        }
    }
}

/**
 * 選択ボタン
 *
 * @param modifier [Modifier]
 * @param iconRes アイコンのリソースID
 * @param text テキスト
 * @param onClick 押したとき
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(40.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = text
            )
        }
    }
}

/**
 * ヘッダー部
 * */
@Composable
private fun SelectScreenHeader() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "元の動画の保存先を選んでください。",
            fontSize = 20.sp
        )
    }
}
