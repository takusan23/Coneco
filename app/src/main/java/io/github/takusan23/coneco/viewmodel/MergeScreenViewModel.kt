package io.github.takusan23.coneco.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.coneco.data.AudioMergeEditData
import io.github.takusan23.coneco.data.SelectVideoItemData
import io.github.takusan23.coneco.data.VideoMergeEditData
import io.github.takusan23.coneco.tool.FileTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** 結合する作業一連 で利用するViewModel */
class MergeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _selectedVideoList = MutableStateFlow<List<SelectVideoItemData>>(emptyList())
    private val _audioMergeEditData = MutableStateFlow(AudioMergeEditData())
    private val _videoMergeEditData = MutableStateFlow(VideoMergeEditData())
    private val _resultFileUri = MutableStateFlow<Uri?>(null)

    /** 選択した動画をFlowで返す */
    val selectedVideoList = _selectedVideoList as StateFlow<List<SelectVideoItemData>>

    /** 音声の設定 */
    val audioMergeEditData = _audioMergeEditData as StateFlow<AudioMergeEditData>

    /** 映像の設定 */
    val videoMergeEditData = _videoMergeEditData as StateFlow<VideoMergeEditData>

    /** 保存先Uri */
    val resultFileUri = _resultFileUri as StateFlow<Uri?>

    /**
     * 動画選択から戻ってきた際に、動画を追加する
     *
     * @param videoList 動画Uriの配列
     * */
    fun selectVideo(videoList: List<Uri>) {
        viewModelScope.launch {
            _selectedVideoList.value += videoList.map { FileTool.getVideoData(context, it) }
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
     * [AudioMergeEditData]をセットする
     *
     * @param audioMergeEditData [AudioMergeEditData]
     * */
    fun updateAudioMergeEditData(audioMergeEditData: AudioMergeEditData) {
        _audioMergeEditData.value = audioMergeEditData
    }

    /**
     * [VideoMergeEditData]をセットする
     *
     * @param videoMergeEditData [VideoMergeEditData]
     * */
    fun updateVideoMergeEditData(videoMergeEditData: VideoMergeEditData) {
        _videoMergeEditData.value = videoMergeEditData
    }

    /**
     * 保存先Uriをセットする
     *
     * @param uri [Uri]
     * */
    fun setResultUri(uri: Uri) {
        _resultFileUri.value = uri
    }

}