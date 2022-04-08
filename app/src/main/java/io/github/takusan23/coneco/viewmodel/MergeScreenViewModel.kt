package io.github.takusan23.coneco.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.takusan23.coneco.data.AudioConfData
import io.github.takusan23.coneco.data.SelectVideoItemData
import io.github.takusan23.coneco.data.VideoConfData
import io.github.takusan23.coneco.tool.FileNameGenerator
import io.github.takusan23.coneco.tool.GetVideoData
import io.github.takusan23.coneco.tool.SerializationTool
import io.github.takusan23.coneco.workmanager.VideoMergeWork
import io.github.takusan23.conecohls.data.ConecoRequestHlsData
import io.github.takusan23.conecohls.data.MultiVariantPlaylist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** 結合する作業一連 で利用するViewModel */
class MergeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _selectedVideoList = MutableStateFlow<List<SelectVideoItemData>>(emptyList())
    private val _audioMergeEditData = MutableStateFlow(AudioConfData())
    private val _videoMergeEditData = MutableStateFlow(VideoConfData())
    private val _resultFileName = MutableStateFlow("${FileNameGenerator.easterEgg() ?: ""}.mp4")
    private val _hlsMasterPlaylistUrl = MutableStateFlow("")
    private val _hlsPlaylistUrl = MutableStateFlow<String?>(null)
    private val _hlsQualityList = MutableStateFlow<List<MultiVariantPlaylist>?>(null)

    /** 選択した動画をFlowで返す */
    val selectedVideoList = _selectedVideoList as StateFlow<List<SelectVideoItemData>>

    /** 音声の設定 */
    val audioMergeEditData = _audioMergeEditData as StateFlow<AudioConfData>

    /** 映像の設定 */
    val videoMergeEditData = _videoMergeEditData as StateFlow<VideoConfData>

    /** 保存先ファイル名 */
    val resultFileName = _resultFileName as StateFlow<String>

    /** HLSの場合 マスタープレイリスト(m3u8)のURL */
    val hlsMasterPlaylistUrl = _hlsMasterPlaylistUrl as StateFlow<String>

    /** HLSの場合 動画のURLが書いてあるプレイリストのURL */
    val hlsPlaylistUrl = _hlsPlaylistUrl as StateFlow<String?>

    /** HLSの場合 マスタープレイリストの中の画質一覧を返す */
    val hlsQualityList = _hlsQualityList as StateFlow<List<MultiVariantPlaylist>?>

    /**
     * 動画選択から戻ってきた際に、動画を追加する
     *
     * @param videoList 動画Uriの配列
     * */
    fun selectVideo(videoList: List<Uri>) {
        viewModelScope.launch {
            _selectedVideoList.value += videoList.map { GetVideoData.getVideoData(context, it) }
        }
    }

    /**
     * 指定した[Uri]の動画を削除する
     *
     * @param uri 削除する
     * */
    fun deleteVideo(uri: Uri) {
        _selectedVideoList.value = _selectedVideoList.value.filter { it.uri != uri }
    }

    /**
     * [AudioConfData]をセットする
     *
     * @param audioConfData [AudioConfData]
     * */
    fun updateAudioMergeEditData(audioConfData: AudioConfData) {
        _audioMergeEditData.value = audioConfData
    }

    /**
     * [VideoConfData]をセットする
     *
     * @param videoConfData [VideoConfData]
     * */
    fun updateVideoMergeEditData(videoConfData: VideoConfData) {
        _videoMergeEditData.value = videoConfData
    }

    /**
     * 保存するファイル名をセット
     *
     * @param fileName ファイル名
     * */
    fun setResultFileName(fileName: String) {
        _resultFileName.value = fileName
    }

    /**
     * HLSのプレイリストURLをセットする
     *
     * @param m3u8Url m3u8のURL
     * */
    fun setHlsPlaylistUrl(m3u8Url: String) {
        _hlsMasterPlaylistUrl.value = m3u8Url
    }

    /**
     * HLSがマスタープレイリストだった場合にどの画質のURLにするか
     *
     * @param hlsPlaylistUrl 各プレイリストのURL
     * */
    fun setHlsQualityPlaylistUrl(hlsPlaylistUrl: String) {
        _hlsPlaylistUrl.value = hlsPlaylistUrl
    }

    /**
     * HLSのプレイリストがマスタープレイリストだった場合に画質一覧をFlowで返す。
     *
     * ただのプレイリストだった場合は空を返す。
     * */
    fun getHlsMasterPlaylistQualityList() {
        viewModelScope.launch {
            val masterPlaylist = ConecoRequestHlsData.getMasterPlaylist(hlsMasterPlaylistUrl.value)
            _hlsQualityList.value = ConecoRequestHlsData.getMasterPlaylist(hlsMasterPlaylistUrl.value) ?: emptyList()
            // マスタープレイリストじゃない場合はプレイリストとして登録する
            if (masterPlaylist == null) {
                _hlsPlaylistUrl.value = hlsMasterPlaylistUrl.value
            }
        }
    }

    /**
     * 動画の結合を始める。進捗がほしいのでWorkManagerを利用した。
     * */
    fun startMerge() {
        val videoMergeWork = OneTimeWorkRequestBuilder<VideoMergeWork>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(VideoMergeWork.WORKER_TAG)
            .setInputData(workDataOf(
                // 結合する動画のURI配列。空っぽならnull
                VideoMergeWork.MERGE_URI_LIST_KEY to selectedVideoList.value.ifEmpty { null },
                // 結合する動画のHLSプレイリストURL。ない場合は URI配列 のほうが使われる
                VideoMergeWork.MERGE_HLS_PLAYLIST_URL_KEY to hlsPlaylistUrl.value,
                // 保存先
                VideoMergeWork.RESULT_FILE_NAME to resultFileName.value,
                // 音声設定
                VideoMergeWork.AUDIO_CONF_DATA_KEY to SerializationTool.convertString(audioMergeEditData.value),
                // 映像設定
                VideoMergeWork.VIDEO_CONF_DATA_KEY to SerializationTool.convertString(videoMergeEditData.value),
            ))
            .build()
        WorkManager.getInstance(context).enqueue(videoMergeWork)
    }

}