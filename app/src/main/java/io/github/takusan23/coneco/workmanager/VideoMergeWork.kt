package io.github.takusan23.coneco.workmanager

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.github.takusan23.coneco.R
import io.github.takusan23.coneco.data.AudioConfData
import io.github.takusan23.coneco.data.VideoConfData
import io.github.takusan23.coneco.tool.ExternalFileManager
import io.github.takusan23.coneco.tool.SerializationTool
import io.github.takusan23.conecocore.ConecoCore

/**
 * 動画を結合する仕事をWorkManagerにやらせる。
 *
 * 多分くっそ長いので、長期間実行ワーカーとして登録する。
 * */
class VideoMergeWork(private val appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val notificationManager = NotificationManagerCompat.from(appContext)

    /** 内部ストレージを扱いやすくするクラス */
    private val externalFileManager = ExternalFileManager(appContext)

    override suspend fun doWork(): Result {
        // 長期間タスクですよ...
        setForeground(createForegroundInfo())
        // 結合を行う
        startMerge()
        return Result.success()
    }

    /** 動画の結合を行う */
    private suspend fun startMerge() {
        // 各データを取り出す
        val resultPath = inputData.getString(RESULT_FILE_KEY)?.toUri()!!
        val mergeUriList = inputData.getStringArray(MERGE_URI_LIST_KEY)?.map { it.toUri() }!!
        val audioConfData = inputData.getString(AUDIO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<AudioConfData>(it) }
        val videoConfData = inputData.getString(VIDEO_CONF_DATA_KEY)!!.let { SerializationTool.convertDataClass<VideoConfData>(it) }
        // Uriだと扱えないので内部固有ストレージへコピーする
        val externalCopedFileList = mergeUriList.mapIndexed { index, uri ->
            externalFileManager.copyFileFromUri(uri, index.toString())
        }
        // 作ったライブラリを利用して合成する
        val conecoCore = ConecoCore(externalCopedFileList, externalFileManager.tempResultFile, externalFileManager.tempFileFolder).apply {
            configureAudioFormat(audioConfData.bitRate)
            configureVideoFormat(
                videoConfData.bitRate,
                videoConfData.frameRate,
            )
        }
        // 結合開始
        conecoCore.merge()
        // あとしまつ
        externalFileManager.moveResultFileToUri(resultPath)
        externalFileManager.delete()
        println("終了！！！！！！！！！！")
    }

    private fun createForegroundInfo(): ForegroundInfo {
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
        }.build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    companion object {

        /** WorkManagerに指定しておくタグ */
        const val WORKER_TAG = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_MERGE_WORK"

        /** 保存先Uri */
        const val RESULT_FILE_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.RESULT_FILE_URI"

        /** 結合する動画のURI配列 */
        const val MERGE_URI_LIST_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.MERGE_URI_LIST_KEY"

        /** 音声の設定 */
        const val AUDIO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.AUDIO_CONF_DATA_KEY"

        /** 映像の設定 */
        const val VIDEO_CONF_DATA_KEY = "io.github.takusan23.coneco.workmanager.VideoMergeWork.VIDEO_CONF_DATA_KEY"

        /** 通知ID */
        const val NOTIFICATION_ID = 2525

    }
}