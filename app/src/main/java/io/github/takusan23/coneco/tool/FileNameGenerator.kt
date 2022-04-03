package io.github.takusan23.coneco.tool

import java.text.SimpleDateFormat
import java.util.*

/** ファイル名生成器 */
object FileNameGenerator {

    private val simpleDateFormat = SimpleDateFormat("M/d", Locale.JAPAN)

    /**
     * スポーツの日とかは毎年変動するので書いてない（そもそもスポーツ嫌いだから書かないけど）
     * */
    private val specialHolidayList = listOf(
        "1/1" to "元旦",
        "2/11" to "建国記念の日",
        "2/23" to "天皇誕生日",
        "3/21" to "春分の日",
        "4/1" to "エイプリルフール",
        "4/29" to "昭和の日",
        "5/3" to "憲法記念日",
        "5/4" to "みどりの日",
        "5/5" to "こどもの日",
        "8/11" to "山の日",
        "9/23" to "秋分の日",
        "11/3" to "文化の日",
        "12/24" to "クリスマスイブ",
        "12/25" to "クリスマス"
    )

    /**
     * オマケ機能
     *
     * @return 祝日じゃなければnull
     * */
    fun easterEgg(): String? {
        val nowCalendar = Calendar.getInstance()
        return specialHolidayList.firstOrNull { (date, _) ->
            val (month, day) = parseDate(date)
            month == (nowCalendar[Calendar.MONTH] + 1) && day == nowCalendar[Calendar.DAY_OF_MONTH]
        }?.second
    }

    /** 8/11 を 8 to 11 に変換する */
    private fun parseDate(date: String): Pair<Int, Int> {
        val date = simpleDateFormat.parse(date)!!
        val calendar = Calendar.getInstance().apply { time = date }
        return calendar[Calendar.MONTH] + 1 to calendar[Calendar.DAY_OF_MONTH]
    }

}