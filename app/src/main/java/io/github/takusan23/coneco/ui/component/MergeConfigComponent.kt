package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.data.AudioConfData
import io.github.takusan23.coneco.data.VideoConfData
import io.github.takusan23.coneco.workmanager.VideoMergeWork

/**
 * 結合後のファイル名を決めるやつ。面倒いので速攻TextFieldへフォーカスを当ててます。
 *
 * @param fileName ファイル名
 * @param onFileNameChange ファイル名が変わったら呼ばれる
 * */
@Composable
fun MergeResultFileNameConfigComponent(
    fileName: String,
    onFileNameChange: (String) -> Unit,
) {
    val isOpen = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })

    MergeConfigCommon(
        isOpen = isOpen.value,
        iconRes = R.drawable.ic_outline_drive_folder_upload_24,
        labelRes = R.string.merge_edit_screen_save_folder,
        onOpenClick = { isOpen.value = !isOpen.value }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                value = fileName,
                label = { Text(text = "繋げた動画のファイル名") },
                onValueChange = { onFileNameChange(it) }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp),
                    painter = painterResource(R.drawable.ic_outline_drive_folder_upload_24),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = """
                    保存先は以下になります：
                    ${VideoMergeWork.resultMovieSaveFolder}
                    """.trimIndent()
                )
            }
        }
    }
}

/**
 * 音声の設定を編集するUI
 *
 * @param data 音声の情報
 * @param onDataChange データが変わったら呼ばれる
 */
@Composable
fun MergeAudioConfigComponent(
    data: AudioConfData,
    onDataChange: (AudioConfData) -> Unit,
) {
    val isOpen = remember { mutableStateOf(false) }

    MergeConfigCommon(
        isOpen = isOpen.value,
        iconRes = R.drawable.ic_outline_audiotrack_24,
        labelRes = R.string.merge_edit_screen_audio_conf,
        onOpenClick = { isOpen.value = !isOpen.value }
    ) {

        NumberOnlyOutlinedTextField(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            initialNum = (data.bitRate / 1000),
            label = {
                Text(
                    text = "音声のビットレート (単位 : K)",
                    maxLines = 1
                )
            },
            onNumberChange = { onDataChange(data.copy(bitRate = it * 1000)) }
        )
    }
}

/**
 * 動画の設定を編集するUI
 *
 * @param data 音声の情報
 * @param onDataChange データが変わったら呼ばれる
 * */
@Composable
fun MergeVideoConfigComponent(
    data: VideoConfData,
    onDataChange: (VideoConfData) -> Unit,
) {
    val isOpen = remember { mutableStateOf(false) }

    MergeConfigCommon(
        isOpen = isOpen.value,
        iconRes = R.drawable.ic_outline_videocam_24,
        labelRes = R.string.merge_edit_screen_video_conf,
        onOpenClick = { isOpen.value = !isOpen.value }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            NumberOnlyOutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "映像のビットレート (単位 : K)",
                        maxLines = 1
                    )
                },
                initialNum = (data.bitRate / 1000),
                onNumberChange = { onDataChange(data.copy(bitRate = it * 1000)) }
            )
            NumberOnlyOutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "フレームレート (fps)",
                        maxLines = 1
                    )
                },
                initialNum = data.frameRate,
                onNumberChange = { onDataChange(data.copy(frameRate = it)) }
            )
            // 縦、横
            Row {
                NullableNumberOnlyOutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .fillMaxWidth(),
                    initialNum = data.videoHeight,
                    label = {
                        Text(
                            text = "縦のサイズ (空白でも可)",
                            maxLines = 1
                        )
                    },
                    onNumberChange = { onDataChange(data.copy(videoHeight = it)) }
                )
                NullableNumberOnlyOutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .fillMaxWidth(),
                    initialNum = data.videoWidth,
                    label = {
                        Text(
                            text = "横のサイズ (空白でも可)",
                            maxLines = 1
                        )
                    },
                    onNumberChange = { onDataChange(data.copy(videoWidth = it)) }
                )
            }
            // OpenGLモードを使う。他の動画とサイズが違う場合は利用する必要あり
            AndroidSnowConeSwitch(
                modifier = Modifier
                    .padding(5.dp),
                isEnable = data.isUseOpenGl,
                label = {
                    Text(text = """
                        OpenGLモードを利用して結合する。
                        (繋げる動画が同じ形式じゃない場合は有効にしてください、実験的機能です。)
                        """.trimIndent()
                    )
                },
                onValueChange = { onDataChange(data.copy(isUseOpenGl = it)) }
            )
        }
    }
}

/**
 * 共通しているUI
 *
 * @param modifier [Modifier]
 * @param iconRes アイコン
 * @param labelRes ラベル
 * @param isOpen 展開するか
 * @param onOpenClick 押したら呼ばれる
 * @param content コンテンツ
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MergeConfigCommon(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    iconRes: Int,
    labelRes: Int,
    onOpenClick: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val openIconRes = if (isOpen) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column {
            Surface(
                color = Color.Transparent,
                onClick = { onOpenClick(!isOpen) }
            ) {
                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = iconRes),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = labelRes),
                        fontSize = 18.sp,
                    )
                    Icon(
                        modifier = Modifier.padding(5.dp),
                        painter = painterResource(id = openIconRes),
                        contentDescription = null
                    )
                }
            }
            if (isOpen) {
                content()
            }
        }
    }
}