package io.github.takusan23.conecocore.data

import io.github.takusan23.conecocore.ConecoCore
import java.io.File

/**
 * [ConecoCore]へリクエストする際に渡すデータ
 *
 * @param videoFilePathList 繋げたい動画のファイルパスの配列
 * @param resultFile 繋げたファイルの保存先
 * @param tempFileFolder 一時保存先
 * */
data class ConecoRequestData(
    val videoFilePathList: List<String>,
    val resultFile: File,
    val tempFileFolder: File,
) : ConecoRequestInterface {

    /** 結合先ファイル */
    override val mergeResultFile: File
        get() = resultFile

    /** 一時保存先 */
    override val tempFolder: File
        get() = tempFileFolder

    /** 結合する動画 */
    override suspend fun getMergeVideoList() = videoFilePathList

    /** あとしまつ */
    override suspend fun release() {
        tempFileFolder.delete()
    }
}