package ksnd.autosizetable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ksnd.autosizetable.ui.theme.AutoSizeTableTheme

private const val NUM_OF_ITEMS_IN_EACH_COLUMN = 20
private const val NUM_OF_ITEMS_IN_EACH_ROW = 30
private const val MAX_NUM_OF_MAIL_ICONS = 5

@Composable
fun SampleScreen() {
    val colorScheme = MaterialTheme.colorScheme
    var typeIndex by remember { mutableIntStateOf(0) }
    var numMailRowIcons by remember { mutableIntStateOf(1) }
    var numMailColumnIcons by remember { mutableIntStateOf(1) }

    // fixedTopSize to fixedStartSize (Row to Column)
    val type = listOf(
        1 to 1,
        1 to 0,
        0 to 1,
        0 to 0,
        1 to 2,
        2 to 2,
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Button(
                    onClick = { typeIndex = (typeIndex + 1) % type.size },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                ) {
                    Text("Switch fixed size")
                }

                Button(
                    onClick = { numMailRowIcons = (numMailRowIcons) % MAX_NUM_OF_MAIL_ICONS + 1 },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                ) {
                    Text("Switch num of row mail icons")
                }

                Button(
                    onClick = { numMailColumnIcons = (numMailColumnIcons) % MAX_NUM_OF_MAIL_ICONS + 1 },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                ) {
                    Text("Switch num of column mail icons")
                }
            }

            AutoSizeTable(
                modifier = Modifier.padding(all = 8.dp),
                outlineColor = colorScheme.outline,
                content = List(NUM_OF_ITEMS_IN_EACH_ROW) { rowIndex ->
                    List(NUM_OF_ITEMS_IN_EACH_COLUMN) { columnIndex ->
                        {
                            if (columnIndex == 0 && rowIndex % 2 == 1) {
                                Column {
                                    repeat(numMailColumnIcons) {
                                        Row {
                                            repeat(numMailRowIcons) {
                                                Icon(
                                                    imageVector = Icons.Default.MailOutline,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .size(48.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "column: $columnIndex\nrow: $rowIndex",
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                        }
                    }
                },
                backgroundColor = { rowIndex, columnIndex ->
                    when {
                        rowIndex in 0..<type[typeIndex].first -> colorScheme.primaryContainer
                        columnIndex in 0..<type[typeIndex].second -> colorScheme.tertiaryContainer
                        rowIndex % 2 == 0 -> colorScheme.surface
                        else -> colorScheme.inverseOnSurface
                    }
                },
                contentAlignment = { _, _ -> Alignment.Center },
                fixedTopSize = type[typeIndex].first,
                fixedStartSize = type[typeIndex].second,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSampleScreen() {
    AutoSizeTableTheme {
        SampleScreen()
    }
}
