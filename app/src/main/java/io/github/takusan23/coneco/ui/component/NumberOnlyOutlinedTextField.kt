package io.github.takusan23.coneco.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * 数字のみのOutlinedTextField
 *
 * @param modifier [Modifier]
 * @param label ラベル
 * @param initialNum 数値
 * @param onNumberChange 変更したら呼ばれる
 * */
@Composable
fun NumberOnlyOutlinedTextField(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    initialNum: Int,
    onNumberChange: (Int) -> Unit,
) {
    val numberText = remember { mutableStateOf(initialNum.toString()) }
    OutlinedTextField(
        modifier = modifier,
        value = numberText.value,
        label = label,
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = {
            numberText.value = it
            onNumberChange(numberText.value.toIntOrNull() ?: 0)
        }
    )
}

/**
 * 数字のみのOutlinedTextField
 *
 * 初期値の指定が不要。Nullが返ってくる可能性があります。
 *
 * @param modifier [Modifier]
 * @param label ラベル
 * @param onNumberChange 変更したら呼ばれる
 * */
@Composable
fun NullableNumberOnlyOutlinedTextField(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    initialNum: Int? = null,
    onNumberChange: (Int?) -> Unit,
) {
    val numberText = remember { mutableStateOf(initialNum?.toString() ?: "") }
    OutlinedTextField(
        modifier = modifier,
        value = numberText.value,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = {
            numberText.value = it
            onNumberChange(numberText.value.toIntOrNull())
        }
    )
}