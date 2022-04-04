package io.github.takusan23.coneco.tool

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.takusan23.coneco.workmanager.VideoMergeWork
import io.github.takusan23.conecocore.data.VideoMergeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** WorkManager 関係のユーティリティクラス */
object WorkManagerTool {

    /**
     * 実行中タスクがあるかどうかを返す
     *
     * @param context [Context]
     * @param owner LifecycleOwner
     * @return ない場合はnull
     * */
    fun existsRunningTask(context: Context, owner: LifecycleOwner): StateFlow<WorkInfo?> {
        val runningTask = MutableStateFlow<WorkInfo?>(null)
        WorkManager.getInstance(context).getWorkInfosByTagLiveData(VideoMergeWork.WORKER_TAG).observe(owner) {
            runningTask.value = it.firstOrNull { it.state == WorkInfo.State.RUNNING }
        }
        return runningTask
    }

    /**
     * 実行中タスクの進捗のFlowを返す
     *
     * @param context [Context]
     * @param owner LifecycleOwner
     * @return ない場合は[VideoMergeStatus.NO_TASK]
     * */
    fun collectMergeState(context: Context, owner: LifecycleOwner): StateFlow<VideoMergeStatus> {
        val state = MutableStateFlow(VideoMergeStatus.NO_TASK)
        WorkManager.getInstance(context).getWorkInfosByTagLiveData(VideoMergeWork.WORKER_TAG).observe(owner) { list ->
            state.value = list.firstOrNull { it.state == WorkInfo.State.RUNNING }?.progress?.getString(VideoMergeWork.WORK_STATUS_KEY)?.let {
                VideoMergeStatus.findFromName(it)
            } ?: VideoMergeStatus.NO_TASK
        }
        return state
    }

    /**
     * 結合にかかった時間を取得する
     *
     * @param context [Context]
     * @param owner LifecycleOwner
     * @return 終わるまで-1
     * */
    fun collectMergeTotalTime(context: Context, owner: LifecycleOwner): StateFlow<Long> {
        val state = MutableStateFlow(-1L)
        WorkManager.getInstance(context).getWorkInfosByTagLiveData(VideoMergeWork.WORKER_TAG).observe(owner) { list ->
            state.value = list.firstOrNull { it.state.isFinished }?.outputData?.getLong(VideoMergeWork.WORK_TOTAL_MERGE_TIME, -1L) ?: -1L
        }
        return state
    }

    /**
     * 実行中タスクを終了させる
     *
     * @param context [Context]
     * */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(VideoMergeWork.WORKER_TAG)
    }

}