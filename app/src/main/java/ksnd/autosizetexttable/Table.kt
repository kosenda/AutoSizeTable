package ksnd.autosizetexttable

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Table(
    modifier: Modifier = Modifier,
    items: List<List<String>>,
    style: TextStyle,
    numberOfTopFixes: Int = 1,
    numberOfStartFixes: Int = 1,
) {
    val horizontalOnDraw: DrawScope.() -> Unit = {
        drawRect(
            color = Color.Black,
            topLeft = Offset(0f, 0f),
            size = Size(width = size.width, height = size.height),
            style = Stroke(width = 3f),
        )
    }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    val fixedTop by remember(numberOfTopFixes) { derivedStateOf { numberOfTopFixes > 0 } }
    val fixedStart by remember(numberOfStartFixes) { derivedStateOf { numberOfStartFixes > 0 } }

    AutoSizeTextTable(
        modifier = modifier,
        items = items,
        style = style,
    ) { tableItemSize ->
        Column(
            modifier = Modifier
                .then(
                    if (fixedTop.not() && fixedStart.not()) {
                        Modifier
                            .horizontalScroll(horizontalScrollState)
                            .verticalScroll(verticalScrollState)
                    } else {
                        Modifier
                    }
                ),
        ) {
            Row {
                Column(
                    modifier = Modifier.background(Color.Red),
                ) {
                    items.take(numberOfTopFixes).forEachIndexed { columnId, columnList ->
                        Row {
                            columnList.take(numberOfStartFixes).forEachIndexed { rowId, item ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            width = tableItemSize.columnWidthSize[rowId],
                                            height = tableItemSize.rowHeightSize[columnId],
                                        )
                                        .drawBehind(onDraw = horizontalOnDraw),
                                    contentAlignment = Alignment.TopStart,
                                ) {
                                    Text(
                                        text = item,
                                        style = style,
                                    )
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .then(
                            if (fixedTop) {
                                Modifier.horizontalScroll(horizontalScrollState)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    items.take(numberOfTopFixes).forEachIndexed { columnId, columnList ->
                        Row {
                            columnList.takeLast(columnList.size - numberOfStartFixes)
                                .forEachIndexed { rowId, item ->
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = tableItemSize.columnWidthSize[rowId + numberOfStartFixes],
                                                height = tableItemSize.rowHeightSize[columnId],
                                            )
                                            .drawBehind(onDraw = horizontalOnDraw),
                                        contentAlignment = Alignment.TopStart,
                                    ) {
                                        Text(
                                            text = item,
                                            style = style,
                                        )
                                    }
                                }
                        }
                    }
                }
            }

            Row {
                Column(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .then(
                            if (fixedStart) {
                                Modifier.verticalScroll(verticalScrollState)
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    items.takeLast(items.size - numberOfTopFixes)
                        .forEachIndexed { columnId, columnList ->
                            Row {
                                columnList.take(numberOfStartFixes).forEachIndexed { rowId, item ->
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = tableItemSize.columnWidthSize[rowId],
                                                height = tableItemSize.rowHeightSize[columnId + numberOfTopFixes],
                                            )
                                            .drawBehind(onDraw = horizontalOnDraw),
                                        contentAlignment = Alignment.TopStart,
                                    ) {
                                        Text(
                                            text = item,
                                            style = style,
                                        )
                                    }
                                }
                            }
                        }
                }
                Column(
                    modifier = Modifier.then(
                        if (fixedTop || fixedStart) {
                            Modifier
                                .verticalScroll(verticalScrollState)
                                .horizontalScroll(horizontalScrollState)
                        } else {
                            Modifier
                        }
                    )
                ) {
                    items.takeLast(items.size - numberOfTopFixes)
                        .forEachIndexed { columnId, columnList ->
                            Row {
                                columnList.takeLast(items.first().size - numberOfStartFixes)
                                    .forEachIndexed { rowId, item ->
                                        Box(
                                            modifier = Modifier
                                                .size(
                                                    width = tableItemSize.columnWidthSize[rowId + numberOfStartFixes],
                                                    height = tableItemSize.rowHeightSize[columnId + numberOfTopFixes],
                                                )
                                                .drawBehind(onDraw = horizontalOnDraw),
                                            contentAlignment = Alignment.TopStart,
                                        ) {
                                            Text(
                                                text = item,
                                                style = style,
                                            )
                                        }
                                    }
                            }
                        }
                }
            }
        }
    }
}

data class TableItemSize(
    val columnWidthSize: List<Dp>,
    val rowHeightSize: List<Dp>,
)

@Composable
fun AutoSizeTextTable(
    modifier: Modifier = Modifier,
    items: List<List<String>>,
    style: TextStyle,
    content: @Composable (TableItemSize) -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val heightSize = MutableList(items.size) { 0.dp }
        val widthSize = MutableList(items.first().size) { 0.dp }

        val itemsMeasurable = items.mapIndexed { columnId, columnList ->
            List(columnList.size) { rowId ->
                subcompose("${columnId}_${rowId}") {
                    Text(
                        text = items[columnId][rowId],
                        style = style,
                    )
                }.first().measure(Constraints())
            }
        }
        items.forEachIndexed { columnId, columnList ->
            columnList.forEachIndexed { rowId, _ ->
                val item = itemsMeasurable[columnId][rowId]
                val width = item.width.toDp()
                val height = item.height.toDp()
                widthSize[rowId] = maxOf(widthSize[rowId], width)
                heightSize[columnId] = maxOf(heightSize[columnId], height)
            }
        }

        val contentPlaceable = subcompose("content") {
            content(TableItemSize(widthSize, heightSize))
        }[0].measure(constraints)

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}

@Preview
@Composable
fun PreviewTable() {
    Table(
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
            listOf("タイトル13", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル14", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル15", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル16", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル17", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル18", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル19", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
            listOf("タイトル20", "あああああああ\naaa\naaa\na", "あ", "BBB", "CCC", "C", "C", "C"),
        ),
        style = LocalTextStyle.current,
    )
}