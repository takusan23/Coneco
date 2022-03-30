package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * マージ中画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeTaskScreen(mergeScreenViewModel: MergeScreenViewModel, navController: NavHostController) {

    Scaffold {
        Column {
            // ヘッダー
            MergeTaskScreenHeader()

        }
    }
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
            text = "動画を繋げています",
            fontSize = 20.sp
        )
    }
}
