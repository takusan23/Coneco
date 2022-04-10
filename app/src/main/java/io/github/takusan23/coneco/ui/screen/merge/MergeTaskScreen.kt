package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.tool.TimeTool
import io.github.takusan23.coneco.tool.WorkManagerTool
import io.github.takusan23.conecocore.data.VideoMergeStatus

/**
 * マージ中画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeTaskScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // WorkManagerのステータスをLiveDataで受け取る
    val workStatus = remember { WorkManagerTool.collectMergeState(context, lifecycleOwner) }.collectAsState()
    val workInfo = remember { WorkManagerTool.existsRunningTask(context, lifecycleOwner) }.collectAsState()
    val totalMergeTime = remember { WorkManagerTool.collectMergeTotalTime(context, lifecycleOwner) }.collectAsState()

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
                if (workInfo.value?.state == WorkInfo.State.RUNNING) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(5.dp)
                    )
                    MergeProgressIndicator(
                        modifier = Modifier
                            .padding(5.dp)
                            .width(50.dp),
                        status = workStatus.value
                    )
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = workStatus.value.toLocaleString()
                    )
                    // 終了ボタン
                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = { WorkManagerTool.cancel(context) }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_outline_clear_24), contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.merge_video_merge_force_stop))
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(id = R.string.merge_video_merge_finish)
                    )
                    // 合計時間
                    if (totalMergeTime.value > 0) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center,
                            text = """
                                ${stringResource(id = R.string.merge_video_merge_total_time)}：${TimeTool.millSecToFormat(totalMergeTime.value)}
                                (${totalMergeTime.value} ms)
                            """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 進捗状況、ローカライズ版
 * */
private fun VideoMergeStatus.toLocaleString(): String {
    return when (this) {
        else -> this.name
    }
}

/**
 * [VideoMergeStatus]の進捗を表示するインジケーター
 *
 * @param status [VideoMergeStatus]
 * */
@Composable
private fun MergeProgressIndicator(
    modifier: Modifier = Modifier,
    status: VideoMergeStatus = VideoMergeStatus.NO_TASK,
) {
    LinearProgressIndicator(
        modifier = modifier,
        progress = VideoMergeStatus.progress(status),
    )
}

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
            text = stringResource(id = R.string.merge_video_merge_subtitle),
            fontSize = 20.sp
        )
    }
}
