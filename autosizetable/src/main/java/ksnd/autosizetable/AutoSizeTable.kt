package ksnd.autosizetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Display the table with the size of each item automatically adjusted.
 *
 * @param modifier Modifier for table
 * @param fixedTopSize Number of rows to be fixed at the top
 * @param fixedStartSize Number of columns to be fixed at the start
 * @param strokeWidth Width of the outline stroke in pixels
 * @param outlineColor Color of the outline
 * @param horizontalScrollState ScrollState for horizontal scroll
 * @param verticalScrollState ScrollState for vertical scroll
 * @param backgroundColor Background color of each item
 * @param contentAlignment Alignment of each item
 * @param content Items to display in the table
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSizeTable(
    modifier: Modifier = Modifier,
    fixedTopSize: Int = 1,
    fixedStartSize: Int = 1,
    strokeWidth: Float = 5.0f,
    outlineColor: Color = Color.Black,
    horizontalScrollState: ScrollState = rememberScrollState(),
    verticalScrollState: ScrollState = rememberScrollState(),
    backgroundColor: (columnId: Int, rowId: Int) -> Color = { _, _ -> Color.Unspecified },
    contentAlignment: (columnId: Int, rowId: Int) -> Alignment = { _, _ -> Alignment.TopStart },
    content: List<List<@Composable () -> Unit>>,
) {
    val isFixedTop by remember(fixedTopSize) { derivedStateOf { fixedTopSize > 0 } }
    val isFixedStart by remember(fixedStartSize) { derivedStateOf { fixedStartSize > 0 } }

    val outlineOnDraw: DrawScope.() -> Unit = {
        drawRect(
            color = outlineColor,
            topLeft = Offset(0f, 0f),
            size = Size(width = size.width, height = size.height),
            style = Stroke(width = strokeWidth),
        )
    }

    MeasureTable(
        modifier = modifier,
        items = content,
    ) { tableItemSize ->
        Column(
            modifier = if (isFixedTop.not() && isFixedStart.not()) {
                Modifier
                    .horizontalScroll(horizontalScrollState)
                    .verticalScroll(verticalScrollState)
            } else {
                Modifier
            },
        ) {
            // Fixed top part
            Row {

                // Fixed top and left part
                Column {
                    content.take(fixedTopSize).forEachIndexed { columnId, columnList ->
                        Row {
                            columnList.take(fixedStartSize).forEachIndexed { rowId, item ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            width = tableItemSize.columnWidthSize[rowId],
                                            height = tableItemSize.rowHeightSize[columnId],
                                        )
                                        .background(color = backgroundColor(columnId, rowId))
                                        .drawBehind(onDraw = outlineOnDraw),
                                    contentAlignment = contentAlignment(columnId, rowId),
                                ) {
                                    item()
                                }
                            }
                        }
                    }
                }

                // Fixed top part
                CompositionLocalProvider(
                    // Disable horizontal OverScrollEffect
                    // because it cannot be common OverScrollEffect and results in strange behavior.
                    LocalOverscrollConfiguration provides null,
                ) {
                    Column(
                        modifier = if (isFixedTop) {
                            Modifier.horizontalScroll(horizontalScrollState)
                        } else {
                            Modifier
                        },
                    ) {
                        content.take(fixedTopSize).forEachIndexed { columnId, columnList ->
                            Row {
                                columnList.takeLast(columnList.size - fixedStartSize).forEachIndexed { rowId, item ->
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = tableItemSize.columnWidthSize[rowId + fixedStartSize],
                                                height = tableItemSize.rowHeightSize[columnId],
                                            )
                                            .background(
                                                color = backgroundColor(columnId, rowId + fixedStartSize),
                                            )
                                            .drawBehind(onDraw = outlineOnDraw),
                                        contentAlignment = contentAlignment(columnId, rowId + fixedStartSize),
                                    ) {
                                        item()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Unfixed top part
            Row(
                modifier = if (isFixedStart.not() && isFixedTop.not()) {
                    Modifier
                } else {
                    Modifier.verticalScroll(verticalScrollState)
                },
            ) {

                // Fixed left part
                Column {
                    content.takeLast(content.size - fixedTopSize)
                        .forEachIndexed { columnId, columnList ->
                            Row {
                                columnList.take(fixedStartSize).forEachIndexed { rowId, item ->
                                    Box(
                                        modifier =
                                        Modifier
                                            .size(
                                                width = tableItemSize.columnWidthSize[rowId],
                                                height = tableItemSize.rowHeightSize[columnId + fixedTopSize],
                                            )
                                            .background(
                                                color = backgroundColor(columnId + fixedTopSize, rowId),
                                            )
                                            .drawBehind(onDraw = outlineOnDraw),
                                        contentAlignment = contentAlignment(columnId + fixedTopSize, rowId),
                                    ) {
                                        item()
                                    }
                                }
                            }
                        }
                }

                // Unfixed part
                CompositionLocalProvider(
                    // Disable horizontal OverScrollEffect
                    // because it cannot be common OverScrollEffect and results in strange behavior.
                    LocalOverscrollConfiguration provides null,
                ) {
                    Column(
                        modifier = if (isFixedTop || isFixedStart) {
                            Modifier
                                .horizontalScroll(horizontalScrollState)
                        } else {
                            Modifier
                        },
                    ) {
                        content.takeLast(content.size - fixedTopSize).forEachIndexed { columnId, columnList ->
                            Row {
                                columnList.takeLast(content.first().size - fixedStartSize).forEachIndexed { rowId, item ->
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = tableItemSize.columnWidthSize[rowId + fixedStartSize],
                                                height = tableItemSize.rowHeightSize[columnId + fixedTopSize],
                                            )
                                            .background(
                                                color = backgroundColor(
                                                    columnId + fixedTopSize,
                                                    rowId + fixedStartSize,
                                                ),
                                            )
                                            .drawBehind(onDraw = outlineOnDraw),
                                        contentAlignment = contentAlignment(columnId + fixedTopSize, rowId + fixedStartSize),
                                    ) {
                                        item()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * @param columnWidthSize Width of each column
 * @param rowHeightSize Height of each row
 */
private data class TableItemSize(
    val columnWidthSize: List<Dp>,
    val rowHeightSize: List<Dp>,
)

/**
 * Measure the size of each item in the table and display it.
 *
 * @param modifier Modifier for table
 * @param items List of items to be displayed in the table
 * @param content Content to be displayed in the table
 */
@Composable
private fun MeasureTable(
    modifier: Modifier = Modifier,
    items: List<List<@Composable () -> Unit>>,
    content: @Composable (TableItemSize) -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val heightSizes = MutableList(items.size) { 0.dp }
        val widthSizes = MutableList(items.first().size) { 0.dp }

        val itemsMeasurable = items.mapIndexed { columnId, columnList ->
            List(columnList.size) { rowId ->
                subcompose("${columnId}_$rowId") {
                    items[columnId][rowId]()
                }.first().measure(Constraints())
            }
        }

        items.forEachIndexed { columnId, columnList ->
            columnList.forEachIndexed { rowId, _ ->
                val item = itemsMeasurable[columnId][rowId]
                val width = item.width.toDp()
                val height = item.height.toDp()
                widthSizes[rowId] = maxOf(widthSizes[rowId], width)
                heightSizes[columnId] = maxOf(heightSizes[columnId], height)
            }
        }

        val contentPlaceable = subcompose("content") {
            content(TableItemSize(widthSizes, heightSizes))
        }.first().measure(constraints)

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}
