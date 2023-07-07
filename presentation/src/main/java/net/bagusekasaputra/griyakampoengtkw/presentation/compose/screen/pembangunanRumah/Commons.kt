package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.pembangunanRumah

import android.graphics.Typeface
import android.view.Gravity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.timeMillisToSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutTableGenericSingleCornerBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.pembangunan.MaterialPembangunanUiModel
import net.bagusekasaputra.griyakampoengtkw.presentation.model.pembangunan.PembelianLog
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.rowAndCellId
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

@Composable
fun DataPembangunanRumahLayout(
    modifier: Modifier = Modifier,
    title: String,
    total: Long,
    tableView: @Composable () -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        TitleAndTotal(title = title, total = total)
        Spacer(modifier = Modifier.height(16.dp))
        tableView()
    }
}

@Composable
fun TitleAndTotal(
    modifier: Modifier = Modifier,
    title: String,
    total: Long,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = if (total == 0L) "Tidak ada data" else "Rp. ${total.numericToString()}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PembelianLogDetailDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    logs: List<PembelianLog>,
    onDismiss: () -> Unit
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
                shape = RoundedCornerShape(16.dp)
            ) {
                PembelianLogLayout(
                    title = title,
                    logs = logs,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PembelianLogLayout(
    modifier: Modifier = Modifier,
    title: String,
    logs: List<PembelianLog>,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium,)

        Spacer(modifier = Modifier.height(24.dp))
        AndroidViewBinding(
            modifier = Modifier.fillMaxWidth(),
            factory = { inflater, parent, attachToParent ->
                LayoutTableGenericSingleCornerBinding.inflate(inflater, parent, attachToParent)
            },
            update = {
                val columns = object {
                    val tanggal = 0
                    val supplier = 1
                    val qty = 2
                    val harga = 3
                }
                val columnHeaderWidths = listOf(
                    columns.tanggal to 250,
                    columns.supplier to 350,
                    columns.qty to 250,
                    columns.harga to 300,
                )
                val dataProvider = object : TableViewDataProvider<PembelianLog> {
                    override fun getColumnHeaders(data: Collection<PembelianLog>): List<ColumnHeader> {
                        return buildList {
                            add(ColumnHeader("Tanggal"))
                            add(ColumnHeader("Supplier"))
                            add(ColumnHeader("Qty"))
                            add(ColumnHeader("Harga"))
                        }
                    }

                    override fun getRowHeaders(data: Collection<PembelianLog>): List<RowHeader> {
                        return buildList {
                            repeat(data.size) {
                                add(RowHeader(
                                    rowId = rowAndCellId(it),
                                    data = it.plus(1).toString(),
                                ))
                            }
                        }
                    }

                    override fun getCellItems(data: Collection<PembelianLog>): List<List<CellItem>> {
                        val items = data.toList()
                        return buildList {
                            items.forEachIndexed { index, pembelianLog ->
                                val cellId = rowAndCellId(index)
                                val cells = listOf(
                                    CellItem(cellId, pembelianLog.tanggal.time),
                                    CellItem(cellId, pembelianLog.supplier),
                                    CellItem(cellId, pembelianLog.orderQty),
                                    CellItem(cellId, pembelianLog.harga),
                                )

                                add(cells)
                            }
                        }
                    }

                }

                GenericTableView(tableView, logs)
                    .setWidthColumnHeaders(columnHeaderWidths)
                    .setDataProvider(dataProvider)
                    .setOnCellBinding { cellViewHolder, cellItem, col, row ->
                        cellViewHolder.apply {
                            when (col) {
                                columns.tanggal -> {
                                    val tanggal = (cellItem?.data as Long?)?.timeMillisToSlashedString()
                                        ?: "-"

                                    tvCell.text = tanggal
                                }
                                columns.harga -> {
                                    val harga = (cellItem?.data as Long?)?.numericToString()
                                        ?: "-"

                                    tvCell.text = harga
                                    tvCell.gravity = Gravity.END
                                    tvCell.typeface = Typeface.MONOSPACE
                                }
                                else -> {}
                            }
                        }
                    }
                    .create()
            }
        )
    }
}

@Preview(showBackground = true, group = "sub-components")
@Composable
private fun TitleAndTotalPreview() {
    TitleAndTotal(
        title = "Biaya Material",
        total = 28_335_000L,
    )
}

@Preview(showBackground = true, group = "components")
@Composable
private fun PembelianMaterialLogLayoutPreview() {
    PembelianLogLayout(
        title = "Semen Imasco",
        logs = PembangunanRumahPreviewParams.pembelianLog(5),
    )
}

object PembangunanRumahPreviewParams {

    fun materialPembangunan(size: Int): List<MaterialPembangunanUiModel> {
        return buildList {
            repeat(size) {
                val logSize = Random.nextInt(from = 1, until = 10)

                add(MaterialPembangunanUiModel(
                    nama = "Material $it",
                    satuan = "unit",
                    logs = pembelianLog(logSize)
                ))
            }
        }
    }

    fun pembelianLog(size: Int): List<PembelianLog> {
        return buildList {
            repeat(size) {
                val qty = randomQty().toDouble()
                val harga = randomHarga()

                add(PembelianLog(
                    tanggal = randomTanggal(),
                    supplier = "Supplier $it",
                    orderQty = qty,
                    arrivedQty = qty,
                    harga = harga,
                    terbayar = harga,
                ))
            }
        }
    }

    private fun randomQty() = Random.nextInt(from = 1, until = 100)

    private fun randomHarga() = Random.nextLong(from = 1, until = 1000) * 1000L

    private fun randomTanggal(): Date {
        val start = "01/01/2020".toDate().time
        val end = "01/07/2023".toDate().time
        val randomTimeMillis = Random.nextLong(from = start, until = end)

        return Calendar.getInstance().apply {
            timeInMillis = randomTimeMillis
        }.time
    }
}