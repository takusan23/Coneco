package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel
import io.github.takusan23.coneco.workmanager.VideoMergeWork
import io.github.takusan23.conecocore.tool.VideoMergeStatus

/**
 * マージ中画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeTaskScreen(mergeScreenViewModel: MergeScreenViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // WorkManagerのステータスをLiveDataで受け取る
    val workStatus = mergeScreenViewModel.getVideoMergeWorkStatusLiveData()?.observeAsState()
    val videoMergeStatus = workStatus?.value?.progress?.getString(VideoMergeWork.WORK_STATUS_KEY)?.let { VideoMergeStatus.findFromName(it) }

    Scaffold {
        Column {
            // ヘッダー
            MergeTaskScreenHeader()

            // 真ん中に出す
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (videoMergeStatus != null && videoMergeStatus != VideoMergeStatus.FINISH) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(5.dp)
                    )
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(5.dp)
                            .width(50.dp),
                        progress = when (videoMergeStatus) {
                            VideoMergeStatus.VIDEO_MERGE -> 1f
                            VideoMergeStatus.AUDIO_MERGE -> 2f
                            VideoMergeStatus.CONCAT -> 3f
                            else -> 1f // それ以外は
                        } / VideoMergeStep,
                    )
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = videoMergeStatus.name
                    )
                } else {
                    Text(text = "終了です")
                }
            }
        }
    }
}

/** 映像の合成、音声の合成、コンテナへ格納 の3ステップ */
private const val VideoMergeStep = 3

/**
 * ヘッダー部
 * */
@Composable
private fun MergeTaskScreenHeader() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "動画を繋げています",
            fontSize = 20.sp
        )
    }
}
