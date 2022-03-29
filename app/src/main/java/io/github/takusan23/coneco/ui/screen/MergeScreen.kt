package io.github.takusan23.coneco.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import io.github.takusan23.coneco.ui.component.ConecoAppBar
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 結合画面遷移を行う画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeScreen(mergeScreenViewModel: MergeScreenViewModel) {
    Scaffold(
        topBar = { ConecoAppBar("結合する動画を選ぶ") },
        content = {
            MergeVideoSelectScreen(mergeScreenViewModel)
        }
    )
}