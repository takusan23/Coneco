package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.takusan23.coneco.ui.component.MergeEditAudioConfig
import io.github.takusan23.coneco.ui.component.MergeEditResultFilePicker
import io.github.takusan23.coneco.ui.component.MergeEditVideoConfig
import io.github.takusan23.coneco.ui.component.PageButton
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 保存先とかビットレートを選ぶ画面
 *
 * @param mergeScreenViewModel 共有するViewModel
 * @param navController 画面遷移
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeEditScreen(
    mergeScreenViewModel: MergeScreenViewModel,
    navController: NavHostController,
) {
    val audioMergeEditData = mergeScreenViewModel.audioMergeEditData.collectAsState()
    val videoMergeEditData = mergeScreenViewModel.videoMergeEditData.collectAsState()

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
                // 保存先選択
                MergeEditResultFilePicker(onFilePickerOpen = { }, uriPath = "demo")
                Spacer(modifier = Modifier.size(20.dp))
                // 音声の設定
                MergeEditAudioConfig(
                    data = audioMergeEditData.value,
                    onDataChange = { mergeScreenViewModel.updateAudioMergeEditData(it) }
                )
                Spacer(modifier = Modifier.size(20.dp))
                // 映像の設定
                MergeEditVideoConfig(
                    data = videoMergeEditData.value,
                    onDataChange = { mergeScreenViewModel.updateVideoMergeEditData(it) }
                )
            }
            // 次ボタン
            PageButton(
                onNext = { navController.navigate(NavigationScreenData.VideoEditScreenData.screenName) },
                onPrev = { navController.popBackStack() }
            )
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
