package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.coneco.R
import io.github.takusan23.conecohls.data.MultiVariantPlaylist

/**
 * プレイリスト(m3u8)のUrl入力欄
 *
 * @param modifier [Modifier]
 * @param m3u8Url m3u8のプレイリストがあるURL
 * @param onM3u8UrlChange URLが変わったら呼ばれる
 * @param onRequestClick 画質一覧取得ボタンを押したら呼ばれる
 * */
@Composable
fun MergeHlsPlaylistUrlConfigComponent(
    modifier: Modifier = Modifier,
    m3u8Url: String,
    onM3u8UrlChange: (String) -> Unit,
    onRequestClick: () -> Unit,
) {
    // 開いたら速攻フォーカスをあてるための
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.merge_video_hls_config_url_message),
                fontSize = 18.sp,
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .focusRequester(focusRequester),
                value = m3u8Url,
                label = { Text(text = stringResource(id = R.string.merge_video_hls_config_url_label)) },
                maxLines = 1,
                singleLine = true,
                onValueChange = onM3u8UrlChange
            )
            Button(
                modifier = Modifier.align(alignment = Alignment.End),
                onClick = onRequestClick
            ) {
                Text(text = stringResource(id = R.string.merge_video_hls_config_url_button))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(painter = painterResource(id = R.drawable.ic_outline_language_24), contentDescription = null)
            }
        }
    }
}

/**
 * 画質一覧を表示する
 *
 * @param modifier [Modifier]
 * @param list 画質一覧
 * @param onClick 選んだ画質
 * @param selectUrl 選択中のプレイリストURL
 * */
@Composable
fun MergeHlsQualityListConfigComponent(
    modifier: Modifier = Modifier,
    list: List<MultiVariantPlaylist>,
    selectUrl: String,
    onClick: (MultiVariantPlaylist) -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column {
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_outline_language_24),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.merge_video_hls_config_quality_list_title),
                    fontSize = 18.sp,
                )

            }
            LazyColumn {
                items(list) { item ->
                    HlsQualityListItem(
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                        data = item,
                        onClick = onClick,
                        isChecked = item.url == selectUrl
                    )
                    Divider(modifier = Modifier.padding(start = 5.dp, end = 5.dp))
                }
            }
        }
    }
}

/**
 * Hls画質一覧の各項目
 *
 * @param modifier [Modifier]
 * @param data 画質情報
 * @param isChecked チェックマークをつけるならtrue
 * @param onClick 押したとき
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HlsQualityListItem(
    modifier: Modifier = Modifier,
    data: MultiVariantPlaylist,
    isChecked: Boolean,
    onClick: (MultiVariantPlaylist) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        onClick = { onClick(data) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                if (data.resolution != null) {
                    Text(
                        modifier = Modifier.padding(3.dp),
                        text = "${stringResource(id = R.string.merge_video_hls_config_quality_list_resolution)}：${data.resolution}",
                        fontSize = 20.sp
                    )
                }
                if (data.bandWidth != null) {
                    Text(
                        modifier = Modifier.padding(3.dp),
                        text = "${stringResource(id = R.string.merge_video_hls_config_quality_list_bitrate)}：${data.bandWidth}",
                    )
                }
                Text(
                    modifier = Modifier.padding(3.dp),
                    text = "${stringResource(id = R.string.merge_video_hls_config_quality_list_playlist)}：${data.url}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
            if (isChecked) {
                Icon(
                    modifier = Modifier.padding(10.dp),
                    painter = painterResource(id = R.drawable.ic_outline_check_24),
                    contentDescription = null
                )
            }
        }
    }
}