package io.github.takusan23.coneco.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ライセンス画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen() {
    val licenseList = listOf(
        coroutine,
        serialization,
        okhttp,
        conecoCore,
    )
    Scaffold(topBar = { MediumTopAppBar(title = { Text(text = "ライセンス", fontSize = 25.sp) }) }) {
        LazyColumn {
            items(licenseList) { licenseData ->
                LicenseItem(licenseData = licenseData)
                Divider()
            }
        }
    }
}

/**
 * ライセンス一覧の各項目
 *
 * @param licenseData ライセンス情報
 * */
@Composable
private fun LicenseItem(licenseData: LicenseData) {
    Surface(color = Color.Transparent) {
        Column {
            Text(
                modifier = Modifier.padding(5.dp),
                text = licenseData.name,
                fontSize = 25.sp
            )
            Text(
                text = licenseData.license,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )
        }
    }
}

private val conecoCore = LicenseData("takusan23/conecocore, takusan23/conecohls", """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
       
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
""".trimIndent())

private val serialization = LicenseData("Kotlin/kotlinx.serialization", """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
""".trimIndent())

private val coroutine = LicenseData("Kotlin/kotlinx.coroutines", """
   Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
""".trimIndent())

private val okhttp = LicenseData("square/okhttp", """
   Copyright 2019 Square, Inc.
  
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
""".trimIndent())

/**
 * ライセンス情報データクラス
 * @param name 名前
 * @param license ライセンス
 * */
private data class LicenseData(
    val name: String,
    val license: String,
)