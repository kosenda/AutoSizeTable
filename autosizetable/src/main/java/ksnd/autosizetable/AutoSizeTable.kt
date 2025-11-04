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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Display the table with the size of each item automatically adjusted.
 *
 * This composable creates a table that automatically sizes its cells based on content,
 * with support for fixed header rows and columns that remain visible during scrolling.
 *
 * @param content Items to display in the table. Each inner list represents a row of cells.
 * @param modifier Modifier for the table container.
 * @param fixedTopSize Number of rows to be fixed at the top (header rows). Default is 1.
 * @param fixedStartSize Number of columns to be fixed at the start (header columns). Default is 1.
 * @param outlineStroke Stroke style for cell borders. Default is 1dp width.
 * @param outlineColor Color of the cell borders. Default is Black.
 * @param horizontalScrollState ScrollState for horizontal scroll control.
 * @param verticalScrollState ScrollState for vertical scroll control.
 * @param backgroundColor Lambda function to determine background color for each cell based on row and column indices.
 * @param contentAlignment Lambda function to determine content alignment for each cell based on row and column indices.
 *
 * @sample
 * ```
 * AutoSizeTable(
 *     content = listOf(
 *         listOf({ Text("Header 1") }, { Text("Header 2") }),
 *         listOf({ Text("Cell 1") }, { Text("Cell 2") })
 *     ),
 *     fixedTopSize = 1,
 *     fixedStartSize = 0
 * )
 * ```
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSizeTable(
    content: List<List<@Composable () -> Unit>>,
    modifier: Modifier = Modifier,
    fixedTopSize: Int = 1,
    fixedStartSize: Int = 1,
    outlineStroke: Stroke = Stroke(width = 1.0f),
    outlineColor: Color = Color.Black,
    horizontalScrollState: ScrollState = rememberScrollState(),
    verticalScrollState: ScrollState = rememberScrollState(),
    backgroundColor: (rowIndex: Int, columnIndex: Int) -> Color = { _, _ -> Color.Unspecified },
    contentAlignment: (rowIndex: Int, columnIndex: Int) -> Alignment = { _, _ -> Alignment.Center },
) {
    // Validate input
    require(content.isNotEmpty()) { "Content must not be empty" }
    require(content.all { it.size == content.first().size }) { "All rows must have the same number of columns" }
    require(fixedTopSize >= 0) { "fixedTopSize must be non-negative" }
    require(fixedStartSize >= 0) { "fixedStartSize must be non-negative" }
    require(fixedTopSize <= content.size) { "fixedTopSize ($fixedTopSize) must not exceed the number of rows (${content.size})" }
    require(fixedStartSize <= content.first().size) {
        "fixedStartSize ($fixedStartSize) must not exceed the number of columns (${content.first().size})"
    }

    val isFixedTop = remember(fixedTopSize) { fixedTopSize > 0 }
    val isFixedStart = remember(fixedStartSize) { fixedStartSize > 0 }

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
                    content.take(fixedTopSize).forEachIndexed { rowIndex, rowList ->
                        Row {
                            rowList.take(fixedStartSize).forEachIndexed { columnIndex, item ->
                                TableCell(
                                    width = tableItemSize.columnWidthSize[columnIndex],
                                    height = tableItemSize.rowHeightSize[rowIndex],
                                    backgroundColor = backgroundColor(rowIndex, columnIndex),
                                    contentAlignment = contentAlignment(rowIndex, columnIndex),
                                    outlineColor = outlineColor,
                                    outlineStroke = outlineStroke,
                                    content = item,
                                )
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
                        content.take(fixedTopSize).forEachIndexed { rowIndex, rowList ->
                            Row {
                                rowList.takeLast(rowList.size - fixedStartSize).forEachIndexed { columnIndex, item ->
                                    TableCell(
                                        width = tableItemSize.columnWidthSize[columnIndex + fixedStartSize],
                                        height = tableItemSize.rowHeightSize[rowIndex],
                                        backgroundColor = backgroundColor(rowIndex, columnIndex + fixedStartSize),
                                        contentAlignment = contentAlignment(rowIndex, columnIndex + fixedStartSize),
                                        outlineColor = outlineColor,
                                        outlineStroke = outlineStroke,
                                        content = item,
                                    )
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
                    content.takeLast(content.size - fixedTopSize).forEachIndexed { rowIndex, rowList ->
                        Row {
                            rowList.take(fixedStartSize).forEachIndexed { columnIndex, item ->
                                TableCell(
                                    width = tableItemSize.columnWidthSize[columnIndex],
                                    height = tableItemSize.rowHeightSize[rowIndex + fixedTopSize],
                                    backgroundColor = backgroundColor(rowIndex + fixedTopSize, columnIndex),
                                    contentAlignment = contentAlignment(rowIndex + fixedTopSize, columnIndex),
                                    outlineColor = outlineColor,
                                    outlineStroke = outlineStroke,
                                    content = item,
                                )
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
                            Modifier.horizontalScroll(horizontalScrollState)
                        } else {
                            Modifier
                        },
                    ) {
                        content.takeLast(content.size - fixedTopSize).forEachIndexed { rowIndex, rowList ->
                            Row {
                                rowList.takeLast(content.first().size - fixedStartSize).forEachIndexed { columnIndex, item ->
                                    TableCell(
                                        width = tableItemSize.columnWidthSize[columnIndex + fixedStartSize],
                                        height = tableItemSize.rowHeightSize[rowIndex + fixedTopSize],
                                        backgroundColor = backgroundColor(rowIndex + fixedTopSize, columnIndex + fixedStartSize),
                                        contentAlignment = contentAlignment(rowIndex + fixedTopSize, columnIndex + fixedStartSize),
                                        outlineColor = outlineColor,
                                        outlineStroke = outlineStroke,
                                        content = item,
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

/**
 * Represents the size information for each column and row in the table.
 *
 * @param columnWidthSize Width of each column
 * @param rowHeightSize Height of each row
 */
private data class TableItemSize(
    val columnWidthSize: List<Dp>,
    val rowHeightSize: List<Dp>,
)

/**
 * A single cell in the table with consistent styling.
 *
 * @param width Width of the cell
 * @param height Height of the cell
 * @param backgroundColor Background color of the cell
 * @param contentAlignment Alignment of the content within the cell
 * @param outlineColor Color of the cell border
 * @param outlineStroke Stroke style for the cell border
 * @param content Content to display in the cell
 */
@Composable
private fun TableCell(
    width: Dp,
    height: Dp,
    backgroundColor: Color,
    contentAlignment: Alignment,
    outlineColor: Color,
    outlineStroke: Stroke,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .background(color = backgroundColor)
            .drawBehind {
                drawRect(
                    color = outlineColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(width = size.width, height = size.height),
                    style = outlineStroke,
                )
            },
        contentAlignment = contentAlignment,
    ) {
        content()
    }
}

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
        val rowCount = items.size
        val columnCount = items.first().size

        val maxHeightPerRow = MutableList(rowCount) { 0.dp }
        val maxWidthPerColumn = MutableList(columnCount) { 0.dp }

        val itemsMeasurable = items.mapIndexed { rowIndex, rowList ->
            List(rowList.size) { columnIndex ->
                subcompose("${rowIndex}_$columnIndex") {
                    items[rowIndex][columnIndex]()
                }.first().measure(Constraints())
            }
        }

        items.forEachIndexed { rowIndex, rowList ->
            rowList.forEachIndexed { columnIndex, _ ->
                val item = itemsMeasurable[rowIndex][columnIndex]
                val width = item.width.toDp()
                val height = item.height.toDp()
                maxWidthPerColumn[columnIndex] = maxOf(maxWidthPerColumn[columnIndex], width)
                maxHeightPerRow[rowIndex] = maxOf(maxHeightPerRow[rowIndex], height)
            }
        }

        val contentPlaceable = subcompose("content") {
            content(TableItemSize(maxWidthPerColumn, maxHeightPerRow))
        }.first().measure(constraints)

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}
