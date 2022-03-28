package io.github.takusan23.coneco.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.takusan23.coneco.ui.component.ConecoAppBar
import io.github.takusan23.coneco.ui.component.SelectVideoItemUI
import io.github.takusan23.coneco.viewmodel.HomeScreenViewModel

/**
 * 合成画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {

    val isOpenVideoItemUI = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { ConecoAppBar() },
        content = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                SelectVideoItemUI(
                    modifier = Modifier.padding(10.dp),
                    isOpen = isOpenVideoItemUI.value,
                    onOpenClick = { isOpenVideoItemUI.value = !isOpenVideoItemUI.value }
                )

            }
        }
    )
}