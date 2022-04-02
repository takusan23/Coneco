package io.github.takusan23.coneco.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.work.WorkInfo
import io.github.takusan23.coneco.tool.WorkManagerTool
import io.github.takusan23.coneco.ui.component.ConecoAppBar
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

/**
 * 結合画面遷移を行う画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeScreen(mergeScreenViewModel: MergeScreenViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // タイトルを解決するのに
    val currentScreenEntry = navController.currentBackStackEntryAsState().value?.destination?.route ?: NavigationScreenData.VideoSelectScreenData.screenName
    val currentNavigationScreen = NavigationScreenData.findScreenOrNull(currentScreenEntry)

    // 起動後に実行中タスクがある場合はそっちに飛ばす
    // 多重起動は想定しない（MediaCodecのデコーダー多分そんなに数が無い）
    val runningTask = remember { WorkManagerTool.existsRunningTask(context, lifecycleOwner) }.collectAsState(null)
    LaunchedEffect(key1 = Unit, block = {
        if (runningTask.value?.state == WorkInfo.State.RUNNING) {
            navController.navigate(
                route = NavigationScreenData.VideoMergeScreenData.screenName,
                navOptions = navOptions {
                    popUpTo(NavigationScreenData.VideoSelectScreenData.screenName) {
                        // 結果的にActivity終了へ
                        inclusive = true
                    }
                }
            )
        }
    })

    Scaffold(
        topBar = {
            ConecoAppBar(
                title = stringResource(id = currentNavigationScreen!!.titleBarLabelResId),
                indicatorCount = NavigationScreenData.pageSize,
                indicatorProgress = currentNavigationScreen.indicatorProgress
            )
        },
        content = {
            // 画面切り替え
            NavHost(navController = navController, startDestination = NavigationScreenData.VideoSelectScreenData.screenName) {
                composable(NavigationScreenData.VideoSelectScreenData.screenName) {
                    MergeVideoSelectScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(NavigationScreenData.VideoEditScreenData.screenName) {
                    MergeEditScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(NavigationScreenData.VideoMergeScreenData.screenName) {
                    MergeTaskScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
            }
        }
    )
}