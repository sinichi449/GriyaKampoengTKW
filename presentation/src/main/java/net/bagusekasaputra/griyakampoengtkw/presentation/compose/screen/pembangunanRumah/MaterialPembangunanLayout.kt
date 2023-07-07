package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.pembangunanRumah

import android.graphics.Typeface
import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutTableGenericSingleCornerBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.pembangunan.MaterialPembangunanUiModel
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.rowAndCellId

@Composable
fun MaterialPembangunanLayout(
    modifier: Modifier = Modifier,
    totalMaterial: Long,
    materialPembangunanTable: @Composable () -> Unit,
) {
    DataPembangunanRumahLayout(
        modifier = modifier,
        title = "Rincian Material",
        total = totalMaterial
    ) {
        materialPembangunanTable()
    }
}

@Composable
fun MaterialPembangunanTable(
    modifier: Modifier = Modifier,
    tableData: List<MaterialPembangunanUiModel>,
    selectedRow: Int = -1,
    onRowHeaderClicked: (row: Int) -> Unit = {},
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
                                cells.add(CellItem(cellId, item.qtyTotal))
                                cells.add(CellItem(cellId, item.hargaTotal))

                                add(cells)
                            }
                        } else {
                            val cells = mutableListOf<CellItem>()
                            val cellId = rowAndCellId(0)
                            val emptyData = MaterialPembangunanUiModel.empty()

                            cells.add(CellItem(cellId, emptyData.nama))
                            cells.add(CellItem(cellId, emptyData.qtyTotal))
                            cells.add(CellItem(cellId, emptyData.hargaTotal))

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

            val genericTableView = GenericTableView(this.tableView, tableData)
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
                .setOnClickedRowHeader { _, row ->
                    onRowHeaderClicked(row)
                }
                .setDataProvider(dataProvider)

            genericTableView.create()

            tableView.selectionHandler.selectedRowPosition = selectedRow
        }
    )
}

@Preview(showBackground = true, group = "sub-components")
@Composable
private fun MaterialPembangunanTablePreview() {
    var selectedRow by remember { mutableStateOf(-1) }

    MaterialPembangunanTable(
        tableData = PembangunanRumahPreviewParams.materialPembangunan(5),
        selectedRow = selectedRow,
        onRowHeaderClicked = {
            selectedRow = it
        },
    )
}

@Preview(showBackground = true, group = "components-fixed")
@Composable
private fun MaterialPembangunanLayoutPreview() {
    val data = PembangunanRumahPreviewParams.materialPembangunan(3)
    val total = data.sumOf { it.hargaTotal }
    var selectedRow by remember { mutableStateOf(-1) }
    var logMaterialDialog by remember { mutableStateOf(false) }

    MaterialPembangunanLayout(totalMaterial = total) {
        MaterialPembangunanTable(
            tableData = data,
            selectedRow = selectedRow,
            onRowHeaderClicked = {
                selectedRow = it
                logMaterialDialog = true
            }
        )
    }

    if (selectedRow >= 0) {
        PembelianLogDetailDialog(
            show = logMaterialDialog,
            title = data[selectedRow].nama,
            logs = data[selectedRow].logs,
            onDismiss = { logMaterialDialog = false }
        )
    }
}

@Preview(showBackground = true, group = "components-fixed")
@Composable
private fun MaterialPembangunanLayoutOnEmptyPreview() {
    val data = emptyList<MaterialPembangunanUiModel>()
    val total = 0L

    MaterialPembangunanLayout(totalMaterial = total) {
        MaterialPembangunanTable(tableData = data)
    }
}


