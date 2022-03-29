package io.github.takusan23.coneco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.takusan23.coneco.ui.screen.MainScreen
import io.github.takusan23.coneco.viewmodel.MergeScreenViewModel

class MainActivity : ComponentActivity() {

    /** ViewModel */
    private val mainScreenViewModel by viewModels<MergeScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(mainScreenViewModel)
        }

    }
}