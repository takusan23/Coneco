package io.github.takusan23.coneco.workmanager

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.*
import io.github.takusan23.coneco.MainActivity
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.data.AudioConfData
import io.github.takusan23.coneco.data.VideoConfData
import io.github.takusan23.coneco.tool.SerializationTool
import io.github.takusan23.conecocore.ConecoCore
import io.github.takusan23.conecocore.data.ConecoRequestUriData
import io.github.takusan23.conecocore.data.VideoMergeStatus
import io.github.takusan23.conecohls.data.ConecoRequestHlsData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

/**
 * 動画を結合する仕事をWorkManagerにやらせる。
 *
 * 多分くっそ長いので、長期間実行ワーカーとして登録する。
 *
 * 長時間ワーカーは中でForegroundServiceを動かしてるっぽい。
 * */
class VideoMergeWork(private val appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val scope = CoroutineScope(Job())
    private val notificationManager = NotificationManagerCompat.from(appContext)

    /** 動画を繋げるライブラリ */
    private var conecoCore: ConecoCore? = null

    override suspend fun doWork(): Result {
        try {
            // 長期間タスクですよ...
            setForeground(createForegroundInfo())
            // 結合を行う
            val mergeTime = withContext(Dispatchers.Default) { startMerge() }
            // 結合にかかった時間を渡して成功
            return Result.success(workDataOf(WORK_TOTAL_MERGE_TIME to mergeTime))
        } catch (e: Exception) {
            e.printStackTrace()
            // WorkManagerがキャンセルになると、CancellationException が投げられるのでキャッチする
            conecoCore?.stop()
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    /**
     * 動画の結合を行う
     *
     * @return 結合にかかった時間
     * */
    private suspend fun startMerge() = withContext(Dispatchers.Default) {
        // Uriの配列かプレイリストのURLどっちかがnullじゃない
        val mergeUriList = inputData.getStringArray(MERGE_URI_LIST_KEY)?.map { it.toUri() }
        val hlsPlaylistUrl = inputData.getString(MERGE_HLS_PLAYLIST_URL_KEY)
        // 各データを取り出す
        val fileName = inputData.getString(RESULT_FILE_NAME)!!
        val audioConfData = inputData.getString(AUDIO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<AudioConfData>(it) }
        val videoConfData = inputData.getString(VIDEO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<VideoConfData>(it) }
        val tempFolder = File(appContext.getExternalFilesDir(null), TEMP_FILE_FOLDER)
        // ライブラリ側でUriとFileの差分を吸収するように
        val requestData = if (mergeUriList != null) {
            // Uri配列
            ConecoRequestUriData(
                context = appContext,
                videoUriList = mergeUriList,
                folderName = MEDIA_STORE_FOLDER_NAME,
                resultFileName = fileName,
                tempFileFolder = tempFolder
            )
        } else {
            // HLSプレイリストURL
            ConecoRequestHlsData(
                context = appContext,
                m3u8PlaylistUrl = hlsPlaylistUrl!!,
                resultFileName = fileName,
                tempFileFolder = tempFolder
            )
        }
        // 作ったライブラリを利用して合成する
        conecoCore = ConecoCore(requestData).apply {
            configureAudioFormat(audioConfData.bitRate)
            configureVideoFormat(
                videoConfData.bitRate,
                videoConfData.frameRate,
                videoConfData.isRequireOpenGl,
                videoConfData.videoWidth,
                videoConfData.videoHeight
            )
        }
        // いまの進捗を大まかに取得できるようにした
        conecoCore!!.status.onEach {
            // WorkManagerに進捗を共有する機能がある
            setProgress(workDataOf(WORK_STATUS_KEY to it.name))
            // 通知のプログレスバーも更新。APIがFloat返すのでIntにする
            setForeground(createForegroundInfo(10, (VideoMergeStatus.progress(it) * 10).toInt()))
        }.launchIn(scope)
        // 結合開始
        val mergeTime = conecoCore!!.merge()
        scope.cancel()
        return@withContext mergeTime
    }

    /**
     * Foreground Service で実行させるための情報
     *
     * @param maxStep プログレスバー最大値
     * @param progress プログレスバーの位置
     * */
    private fun createForegroundInfo(
        maxStep: Int = 0,
        progress: Int = 0,
    ): ForegroundInfo {
        val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(appContext.getString(R.string.notification_video_merge_channel_name))
        }.build()
        // 通知ちゃんねるがない場合は作成
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(appContext.getString(R.string.notification_video_merge_title))
            setContentText(appContext.getString(R.string.notification_video_merge_subtitle))
            setSmallIcon(R.drawable.ic_outline_videocam_24)
            // プログレスバー
            if (progress > 0.0f) {
                setProgress(maxStep, progress, false)
            }
            // 再表示用PendingIntent
            setContentIntent(MainActivity.createMainActivityPendingIntent(applicationContext))
            // キャンセル用PendingIntent
            addAction(R.drawable.ic_outline_clear_24, appContext.getString(R.string.merge_video_merge_force_stop), WorkManager.getInstance(applicationContext).createCancelPendingIntent(id))
        }.build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    companion object {

        /** 通知チャンネルのID */
        const val NOTIFICATION_CHANNEL_ID = "video_merge_task_notification"

        /** WorkManagerに指定しておくタグ */
        const val WORKER_TAG = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_MERGE_WORK"

        /** つなげたファイルの名前 */
        const val RESULT_FILE_NAME = "io.github.takusan23.coneco.workmanager.VideoMergeWork.RESULT_FILE_NAME"

        /** 繋げる動画のURI配列 */
        const val MERGE_URI_LIST_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.MERGE_URI_LIST_KEY"

        /**
         * 繋げる動画がHLSで来ている場合はプレイリストファイルのURL。
         * [MERGE_URI_LIST_KEY]とこっちどっちか利用できます。(排他仕様)
         */
        const val MERGE_HLS_PLAYLIST_URL_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.MERGE_HLS_PLAYLIST_URL_KEY"

        /** 音声の設定 */
        const val AUDIO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.AUDIO_CONF_DATA_KEY"

        /** 映像の設定 */
        const val VIDEO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_CONF_DATA_KEY"

        /** 通知ID */
        const val NOTIFICATION_ID = 2525

        /** 進行状態 */
        const val WORK_STATUS_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.WORK_STATUS_KEY"

        /** 結合にかかった時間 */
        const val WORK_TOTAL_MERGE_TIME = "io.github.takusan23.coneco.workmanager.VideoMergeWork.WORK_TOTAL_MERGE_TIME"

        /** 一時保存先 */
        const val TEMP_FILE_FOLDER = "temp_file_folder"

        /** MediaStoreの動画保存フォルダ名 */
        const val MEDIA_STORE_FOLDER_NAME = "Coneco"

        /**
         * 保存先パスを返す
         *
         * 注意：これはユーザーに提示するためだけに利用されるパス、JavaのFileAPIでは利用できない
         * */
        val resultMovieSaveFolder: String
            get() = ConecoRequestUriData.resultFileFolder(MEDIA_STORE_FOLDER_NAME)


    }
}