package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.pembangunanRumah

import android.graphics.Typeface
import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutTableGenericSingleCornerBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.MaterialPembangunanUiModel
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider

@Composable
fun MaterialPembangunanLayout(
    modifier: Modifier = Modifier,
    data: List<MaterialPembangunanUiModel>,
    total: Long,
) {
    DataPembangunanRumahLayout(
        modifier = modifier,
        title = "Rincian Material",
        total = total
    ) {
        MaterialPembangunanTable(tableData = data)
    }
}

@Composable
private fun MaterialPembangunanTable(
    modifier: Modifier = Modifier,
    tableData: List<MaterialPembangunanUiModel>,
) {
    AndroidViewBinding(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        factory = { inflater, parent, attachToParent ->
            val binding = LayoutTableGenericSingleCornerBinding.inflate(inflater, parent, attachToParent)

            binding
        },
        update = {
            val rowAndCellId = { index: Int ->
                index.toString()
            }
            val columns = object {
                val material = 0
                val qty = 1
                val totalHarga = 2
            }
            val dataProvider = object : TableViewDataProvider<MaterialPembangunanUiModel> {
                override fun getColumnHeaders(data: Collection<MaterialPembangunanUiModel>): List<ColumnHeader> {
                    return buildList {
                        add(ColumnHeader("Material"))
                        add(ColumnHeader("Qty"))
                        add(ColumnHeader("Total Harga"))
                    }
                }

                override fun getRowHeaders(data: Collection<MaterialPembangunanUiModel>): List<RowHeader> {
                    return buildList {
                        if (data.isNotEmpty()) {
                            repeat(data.size) { index ->
                                val rowId = rowAndCellId(index)
                                val rowData = index.plus(1).toString()

                                add(RowHeader(rowId, rowData))
                            }
                        } else {
                            val rowId = rowAndCellId(0)

                            add(RowHeader(rowId, "0"))
                        }
                    }
                }

                override fun getCellItems(data: Collection<MaterialPembangunanUiModel>): List<List<CellItem>> {
                    val items = data.toList()
                    return buildList {
                        if (items.isNotEmpty()) {
                            items.forEachIndexed { index, item ->
                                val cellId = rowAndCellId(index)
                                val cells = mutableListOf<CellItem>()

                                cells.add(CellItem(cellId, item.nama))
                                cells.add(CellItem(cellId, item.qty))
                                cells.add(CellItem(cellId, item.totalHarga))

                                add(cells)
                            }
                        } else {
                            val cells = mutableListOf<CellItem>()
                            val cellId = rowAndCellId(0)
                            val emptyData = MaterialPembangunanUiModel.empty()

                            cells.add(CellItem(cellId, emptyData.nama))
                            cells.add(CellItem(cellId, emptyData.qty))
                            cells.add(CellItem(cellId, emptyData.totalHarga))

                            add(cells)
                        }
                    }
                }
            }
            val columnHeaderWidths = buildList {
                add(columns.material to 500)
                add(columns.qty to 250)
                add(columns.totalHarga to 300)
            }

            GenericTableView(this.tableView, tableData)
                .setWidthColumnHeaders(columnHeaderWidths)
                .setOnCellBinding { viewHolder, cellItem, column, row ->
                    viewHolder.apply {
                        when (column) {
                            columns.material -> {
                                tvCell.gravity = Gravity.START
                            }
                            columns.qty -> {
                                val quantity = cellItem?.data?.toString() ?: "0.0"

                                tvCell.text = if (quantity == "0.0") "-" else quantity
                            }
                            columns.totalHarga -> {
                                val totalHarga = (cellItem?.data as Long?)?.numericToString() ?: "0"

                                tvCell.text = if (totalHarga == "0") "-" else totalHarga
                                tvCell.typeface = Typeface.MONOSPACE
                                tvCell.gravity = Gravity.END
                            }
                            else -> {}
                        }
                    }
                }
                .setDataProvider(dataProvider)
                .create()
        }
    )
}

@Preview(showBackground = true, group = "sub-components")
@Composable
private fun MaterialPembangunanTablePreview() {
    MaterialPembangunanTable(
        tableData = PembangunanRumahPreviewParams.materialPembangunan(5)
    )
}

@Preview(showBackground = true, group = "components")
@Composable
private fun MaterialPembangunanLayoutPreview() {
    val data = PembangunanRumahPreviewParams.materialPembangunan(3)
    val total = data.sumOf { it.totalHarga }

    MaterialPembangunanLayout(
        data = data,
        total = total
    )
}

@Preview(showBackground = true, group = "components")
@Composable
private fun MaterialPembangunanLayoutOnEmptyPreview() {
    val data = emptyList<MaterialPembangunanUiModel>()
    val total = 0L

    MaterialPembangunanLayout(
        data = data,
        total = total
    )
}


