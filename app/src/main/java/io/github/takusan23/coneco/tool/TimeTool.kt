package io.github.takusan23.coneco.tool

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object TimeTool {

    private val SimpleDateFormat by lazy { SimpleDateFormat("mm:ss.SSS", Locale.getDefault()) }

    /**
     * ミリ秒を分:秒.ミリ秒に変換する
     *
     * @param ms ミリ秒
     * */
    fun millSecToFormat(ms: Long) = DateUtils.formatElapsedTime(ms / 1000L)

}