package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.MergeAudioConfigComponent
import io.github.takusan23.coneco.ui.component.MergeResultFileNameConfigComponent
import io.github.takusan23.coneco.ui.component.MergeVideoConfigComponent
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 保存先とかビットレートを選ぶ画面
 *
 * @param mergeScreenViewModel 共有するViewModel
 * @param navController 画面遷移
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeConfigScreen(
    mergeScreenViewModel: MergeScreenViewModel,
    navController: NavHostController,
) {
    val audioMergeEditData = mergeScreenViewModel.audioMergeEditData.collectAsState()
    val videoMergeEditData = mergeScreenViewModel.videoMergeEditData.collectAsState()
    val fileName = mergeScreenViewModel.resultFileName.collectAsState()

    Scaffold {
        Column {
            // ヘッダー
            MergeEditScreenHeader()
            // 各項目
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 結合後のファイルの名前
                MergeResultFileNameConfigComponent(
                    fileName = fileName.value,
                    onFileNameChange = { mergeScreenViewModel.setResultFileName(it) }
                )
                Spacer(modifier = Modifier.size(20.dp))
                // 音声の設定
                MergeAudioConfigComponent(
                    data = audioMergeEditData.value,
                    onDataChange = { mergeScreenViewModel.updateAudioMergeEditData(it) }
                )
                Spacer(modifier = Modifier.size(20.dp))
                // 映像の設定
                MergeVideoConfigComponent(
                    data = videoMergeEditData.value,
                    onDataChange = { mergeScreenViewModel.updateVideoMergeEditData(it) }
                )
            }
            // 結合する
            Button(
                modifier = Modifier.align(alignment = CenterHorizontally),
                onClick = {
                    // 画面切り替え
                    navController.navigate(MergeScreenNavigationData.VideoMergeScreenData.screenName, navOptions {
                        // もう戻せないので終了させる
                        popUpTo(MergeScreenNavigationData.VideoSelectScreenData.screenName) {
                            inclusive = true
                        }
                    })
                    // 結合開始
                    mergeScreenViewModel.startMerge()
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_navigate_next_24), contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "動画を繋げる")
            }
        }
    }

}

/**
 * ヘッダー部
 * */
@Composable
private fun MergeEditScreenHeader() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "動画の情報を編集します",
            fontSize = 20.sp
        )
    }
}
