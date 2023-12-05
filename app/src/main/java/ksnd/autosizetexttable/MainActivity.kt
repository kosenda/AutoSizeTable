package ksnd.autosizetexttable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ksnd.autosizetexttable.ui.theme.AutoSizeTextTableTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorScheme = MaterialTheme.colorScheme
            val type = listOf(
                0 to 0,
                0 to 1,
                1 to 0,
                1 to 1,
                1 to 2,
                2 to 2,
            )
            var typeIndex by remember { mutableIntStateOf(0) }
            AutoSizeTextTableTheme(
                darkTheme = false,
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {// scrollStateを各列に設定していてスクロールした時の挙動が少しおかしくなるため、スクロール末尾のバネを無くしている
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null,
                    ) {
                        Column {
                            Button(
                                onClick = {
                                    typeIndex = (typeIndex + 1) % type.size
                                }
                            ) {
                                Text("切替")
                            }
                            Table(
                                modifier = Modifier.padding(all = 8.dp),
                                items = listOf(
                                    listOf("タイトル1", "タイトル2", "タイトル3", "タイトル4", "タイトル5", "タイトル6", "タイトル7", "タイトル8"),
                                    listOf("タイトル2", "111\n11\n1", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル3", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル4", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル5", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル6", "a", "あああ", "BBB", "a", "C", "C", "C"),
                                    listOf("タイトル7", "111", "あああ", "BBB", "CCC", "CCCCC", "CCCCCCCCCCCCC", "C"),
                                    listOf("タイトル8", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル9", "111", "あああ", "BBB", "CCC", "D", "C", "C"),
                                    listOf("タイトル10", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル11", "111", "あああ", "BBB", "CCC", "C", "C", "DDDDDDD"),
                                    listOf("タイトル12", "111", "あああ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル13", "あああああああ\naaa\naaa\na", "あaaa", "BBB", "CCC", "C", "C", "CCCCCCCCCCCCCCCCCc"),
                                    listOf("タイトル14", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル15", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル16", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル17", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル18", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル19", "あああああああ\naaa\naaa\naaa\n1aaa", "あ", "BBB", "CCC", "C", "C", "C"),
                                    listOf("タイトル20", "あああああああ\naaa\naaa\naaa\n1aaa", "あ", "BBB", "CCC", "C", "C", "C"),
                                ),
                                style = { _, _ -> TextStyle.Default.merge(fontSize = 18.sp) },
                                outlineColor = colorScheme.outline,
                                textColor = { _, _ -> colorScheme.onSurface },
                                backgroundColor = { columnId, rowId ->
                                    when {
                                        columnId in 0..<type[typeIndex].first -> {
                                            colorScheme.primaryContainer
                                        }
                                        rowId in 0..<type[typeIndex].second -> {
                                            colorScheme.tertiaryContainer
                                        }
                                        columnId % 2 == 0 -> colorScheme.surface
                                        else -> colorScheme.inverseOnSurface
                                    }
                                },
                                fixedTopSize = type[typeIndex].first,
                                fixedStartSize = type[typeIndex].second,
                            )
                        }
                    }
                }
            }
        }
    }
}
