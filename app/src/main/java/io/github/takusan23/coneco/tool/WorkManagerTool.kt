package io.github.takusan23.coneco.tool

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.takusan23.coneco.workmanager.VideoMergeWork
import io.github.takusan23.conecocore.tool.VideoMergeStatus
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
     * 実行中タスクを終了させる
     *
     * @param context [Context]
     * */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(VideoMergeWork.WORKER_TAG)
    }

}