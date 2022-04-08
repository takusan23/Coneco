package io.github.takusan23.coneco.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.takusan23.coneco.tool.WorkManagerTool
import io.github.takusan23.coneco.ui.component.ConecoAppBar
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel
import kotlinx.coroutines.flow.filterNotNull

/**
 * 画像選択から合成までを担当する画面
 *
 * @param mergeScreenViewModel 共通ViewModel
 * @param mainScreenNavigation [MainScreen]のナビゲーション
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeScreen(
    mergeScreenViewModel: MergeScreenViewModel,
    mainScreenNavigation: NavHostController,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // タイトルを解決するのに
    val currentScreenEntry = navController.currentBackStackEntryAsState().value?.destination?.route ?: MergeScreenNavigationData.VideoSelectScreenData.screenName
    val currentNavigationScreen = MergeScreenNavigationData.findScreenOrNull(currentScreenEntry)

    // 起動後に実行中タスクがある場合はそっちに飛ばす
    // 多重起動は想定しない（MediaCodecのデコーダー多分そんなに数が無い）
    LaunchedEffect(key1 = Unit, block = {
        WorkManagerTool.existsRunningTask(context, lifecycleOwner)
            .filterNotNull()
            .collect {
                val currentRoute = navController.currentBackStackEntry?.destination?.route ?: MergeScreenNavigationData.VideoSelectScreenData.screenName
                // 進捗画面にいない場合は飛ばす
                if (MergeScreenNavigationData.findScreenOrNull(currentRoute) != MergeScreenNavigationData.VideoMergeScreenData) {
                    navController.navigate(
                        route = MergeScreenNavigationData.VideoMergeScreenData.screenName,
                        navOptions = navOptions {
                            popUpTo(MergeScreenNavigationData.VideoSourceSelectScreenData.screenName) {
                                // 結果的にActivity終了へ
                                inclusive = true
                            }
                        }
                    )
                }
            }
    })

    Scaffold(
        topBar = {
            ConecoAppBar(
                title = stringResource(id = currentNavigationScreen!!.titleBarLabelResId),
                indicatorCount = MergeScreenNavigationData.VIDEO_MERGE_STEP,
                indicatorProgress = MergeScreenNavigationData.getProgress(currentNavigationScreen),
                onOpenSettingClick = { mainScreenNavigation.navigate(MainScreenNavigationData.SETTING.screenName) }
            )
        },
        content = {
            // 画面切り替え
            NavHost(navController = navController, startDestination = MergeScreenNavigationData.VideoSourceSelectScreenData.screenName) {
                composable(MergeScreenNavigationData.VideoSourceSelectScreenData.screenName) {
                    MergeVideoSourceSelectScreen(
                        navController = navController
                    )
                }
                composable(MergeScreenNavigationData.VideoSelectScreenData.screenName) {
                    MergeVideoSelectScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(MergeScreenNavigationData.VideoHlsConfigScreenData.screenName) {
                    MergeVideoHlsConfigScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(MergeScreenNavigationData.VideoSelectScreenData.screenName) {
                    MergeVideoSelectScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(MergeScreenNavigationData.VideoConfigScreenData.screenName) {
                    MergeConfigScreen(
                        mergeScreenViewModel = mergeScreenViewModel,
                        navController = navController
                    )
                }
                composable(MergeScreenNavigationData.VideoMergeScreenData.screenName) {
                    MergeTaskScreen()
                }
            }
        }
    )
}