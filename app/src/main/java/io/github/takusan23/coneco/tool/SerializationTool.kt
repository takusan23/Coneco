package io.github.takusan23.coneco.tool

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** kotlinx serialization を利用してデータクラスを文字列にする */
object SerializationTool {

    val json = Json {
        // JSONのキーが全部揃ってなくてもパース
        ignoreUnknownKeys = true
        // data class の省略時の値を使うように
        encodeDefaults = true
    }

    /** データクラスを文字列にする */
    inline fun <reified T> convertString(dataClass: T): String {
        return json.encodeToString(dataClass)
    }

    /** 文字列からデータクラスを作る */
    inline fun <reified T> convertDataClass(serialize: String): T {
        return json.decodeFromString<T>(serialize)
    }

}