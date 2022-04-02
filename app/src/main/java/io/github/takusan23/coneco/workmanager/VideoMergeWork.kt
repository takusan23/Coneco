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
import io.github.takusan23.coneco.tool.ExternalFileManager
import io.github.takusan23.coneco.tool.SerializationTool
import io.github.takusan23.conecocore.ConecoCore
import io.github.takusan23.conecocore.tool.VideoMergeStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

    /** 内部ストレージを扱いやすくするクラス */
    private val externalFileManager = ExternalFileManager(appContext)

    override suspend fun doWork(): Result {
        withContext(Dispatchers.Default) {
            try {
                // 長期間タスクですよ...
                setForeground(createForegroundInfo())
                // 結合を行う
                startMerge()
            } catch (e: Exception) {
                e.printStackTrace()
                // WorkManagerがキャンセルになると、CancellationException が投げられるのでキャッチする
                conecoCore?.stop()
            }
        }
        return Result.success()
    }

    /** 動画の結合を行う */
    private suspend fun startMerge() = withContext(Dispatchers.Default) {
        // 各データを取り出す
        val fileName = inputData.getString(RESULT_FILE_NAME)!!
        val mergeUriList = inputData.getStringArray(MERGE_URI_LIST_KEY)?.map { it.toUri() }!!
        val audioConfData = inputData.getString(AUDIO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<AudioConfData>(it) }
        val videoConfData = inputData.getString(VIDEO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<VideoConfData>(it) }
        // Uriだと扱えないので内部固有ストレージへコピーする
        val externalCopedFileList = mergeUriList.mapIndexed { index, uri ->
            externalFileManager.copyFileFromUri(uri, index.toString())
        }
        // 作ったライブラリを利用して合成する
        conecoCore = ConecoCore(externalCopedFileList, externalFileManager.tempResultFile, externalFileManager.tempFileFolder).apply {
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
        conecoCore!!.merge()
        // あとしまつ
        externalFileManager.createFileAndMoveVideoFile(fileName)
        externalFileManager.delete()
        scope.cancel()
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
        val channelId = "video_merge_task_notification"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName("動画を繋げる作業中通知")
        }.build()
        // 通知ちゃんねるがない場合は作成
        if (notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(appContext, channelId).apply {
            setContentTitle("動画を繋げています")
            setContentText("アプリで進捗を確認できます。")
            setSmallIcon(R.drawable.ic_outline_videocam_24)
            // プログレスバー
            if (progress > 0.0f) {
                setProgress(maxStep, progress, false)
            }
            // 再表示用PendingIntent
            setContentIntent(MainActivity.createMainActivityPendingIntent(applicationContext))
            // キャンセル用PendingIntent
            addAction(R.drawable.ic_outline_clear_24, "中止する", WorkManager.getInstance(applicationContext).createCancelPendingIntent(id))
        }.build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    companion object {

        /** WorkManagerに指定しておくタグ */
        const val WORKER_TAG = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_MERGE_WORK"

        /** つなげたファイルの名前 */
        const val RESULT_FILE_NAME = "io.github.takusan23.coneco.workmanager.VideoMergeWork.RESULT_FILE_NAME"

        /** 結合する動画のURI配列 */
        const val MERGE_URI_LIST_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.MERGE_URI_LIST_KEY"

        /** 音声の設定 */
        const val AUDIO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.AUDIO_CONF_DATA_KEY"

        /** 映像の設定 */
        const val VIDEO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_CONF_DATA_KEY"

        /** 通知ID */
        const val NOTIFICATION_ID = 2525

        /** 進行状態 */
        const val WORK_STATUS_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.WORK_STATUS_KEY"

    }
}