package io.github.takusan23.coneco

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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

    companion object {

        /**
         * [MainActivity]を指すPendingIntentを作る
         *
         * @param context [Context]
         * */
        fun createMainActivityPendingIntent(context: Context): PendingIntent {
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getActivity(context, 1, Intent(context, MainActivity::class.java), flag)
        }

    }
}