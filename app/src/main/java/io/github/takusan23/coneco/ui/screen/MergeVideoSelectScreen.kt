package io.github.takusan23.coneco.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.ui.component.PageButton
import io.github.takusan23.coneco.ui.component.SelectVideoList
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 結合する動画を選ぶ画面
 *
 * @param mergeScreenViewModel 共有するViewModel
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeVideoSelectScreen(mergeScreenViewModel: MergeScreenViewModel) {
    val selectedVideoList = mergeScreenViewModel.selectedVideoList.collectAsState()

    // 選んだ動画を受け取る
    val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents(), onResult = { uriList ->
        mergeScreenViewModel.selectVideo(uriList)
    })

    Scaffold {
        Column {
            // ヘッダー
            SelectScreenHeader(onPickerOpenClick = { videoPicker.launch("video/*") })
            // 選択した動画
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                SelectVideoList(
                    list = selectedVideoList.value,
                    onDeleteClick = { mergeScreenViewModel.deleteVideo(it) }
                )
            }
            // つぎボタン
            PageButton(
                onNext = { },
                onPrev = { }
            )
        }
    }
}

/**
 * ヘッダー部
 *
 * @param onPickerOpenClick 選択画面を開いてほしいときに呼ばれる
 * */
@Composable
private fun SelectScreenHeader(onPickerOpenClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "結合したい動画を選んでください。",
            fontSize = 20.sp
        )
        // 選択ボタン
        Button(
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.End),
            onClick = onPickerOpenClick
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_outline_video_file_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "動画を選択")
        }
    }
}