package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.MergeHlsPlaylistUrlConfigComponent
import io.github.takusan23.coneco.ui.component.MergeHlsQualityListConfigComponent
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * HLSのURLと、マスタープレイリストの場合は画質を選ぶ
 *
 * @param mergeScreenViewModel 共有するViewModel
 * @param navController 画面遷移
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeVideoHlsConfigScreen(
    mergeScreenViewModel: MergeScreenViewModel,
    navController: NavHostController,
) {
    val hlsMasterPlaylistUrl = mergeScreenViewModel.hlsMasterPlaylistUrl.collectAsState()
    val hlsPlaylistUrl = mergeScreenViewModel.hlsPlaylistUrl.collectAsState()
    val qualityList = mergeScreenViewModel.hlsQualityList.collectAsState()

    Scaffold {
        Column {
            // タイトル
            HlsConfigScreenHeader()
            // URL入力欄
            MergeHlsPlaylistUrlConfigComponent(
                m3u8Url = hlsMasterPlaylistUrl.value,
                onM3u8UrlChange = { mergeScreenViewModel.setHlsPlaylistUrl(it) },
                onRequestClick = { mergeScreenViewModel.getHlsMasterPlaylistQualityList() }
            )
            Spacer(modifier = Modifier.size(20.dp))
            // 画質選択
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qualityList.value != null) {
                    if (qualityList.value!!.isEmpty()) {
                        // 画質選択が不要（マスタープレイリストではなかった）
                        Text(
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            text = "画質選択は不要です（入力されたプレイリストには画質情報がありませんでした）"
                        )
                    } else {
                        // 画質選んで！
                        MergeHlsQualityListConfigComponent(
                            list = qualityList.value!!,
                            selectUrl = hlsPlaylistUrl.value ?: "",
                            onClick = {
                                // 画質選んだ！
                                mergeScreenViewModel.setHlsQualityPlaylistUrl(it.url)
                            }
                        )
                    }
                }
            }
            // 結合する
            if (hlsPlaylistUrl.value != null) {
                Button(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    onClick = {
                        // 画面切り替え
                        navController.navigate(NavigationScreenData.VideoConfigScreenData.screenName)
                    }
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_outline_navigate_next_24), contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "動画の情報設定")
                }
            }
        }
    }
}

/**
 * ヘッダー部
 * */
@Composable
private fun HlsConfigScreenHeader() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "HLSのプレイリストと画質の選択をします。",
            fontSize = 20.sp
        )
    }
}
