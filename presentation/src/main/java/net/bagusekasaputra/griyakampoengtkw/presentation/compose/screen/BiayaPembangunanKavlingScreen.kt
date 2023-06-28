package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan.Companion.totalBiaya
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutGenericTableviewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembangunanKavlingViewModel
import java.util.Date

@Composable
fun BiayaPembangunanKavlingScreen(
    modifier: Modifier = Modifier,
    pembangunanViewModel: PembangunanKavlingViewModel,
    onTableMaterialPembangunanCellClicked: (column: Int, row: Int) -> Unit,
) {
    val materialList by pembangunanViewModel.materialList.collectAsState()

    BiayaPembangunanKavlingScreen(
        modifier = modifier,
        materialPembangunan = materialList,
        onTableMaterialPembangunanCellClicked = onTableMaterialPembangunanCellClicked,
    )
}

@Composable
fun BiayaPembangunanKavlingScreen(
    modifier: Modifier = Modifier,
    materialPembangunan: List<MaterialPembangunan> = emptyList(),
    onTableMaterialPembangunanCellClicked: (column: Int, row: Int) -> Unit = {_,_->},
    showTable: Boolean = true,
) {
    var isExpandedCardPembangunan by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        CardProgressPembangunan(
            expanded = isExpandedCardPembangunan,
            onClick = {
                isExpandedCardPembangunan = !isExpandedCardPembangunan
            }
        )
        Spacer(modifier = Modifier.height(36.dp))
        MaterialPembangunanTable(
            materialPembangunan = materialPembangunan,
            onTableCellClicked = onTableMaterialPembangunanCellClicked,
            showTable = showTable,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardProgressPembangunan(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Informasi Pembangunan", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp
                        else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            RowItemProgressPembangunan(title = "Luas", text = "36 m2")
            RowItemProgressPembangunan(title = "Progress", text = "24,00 %")
            RowItemProgressPembangunan(title = "Harga Borong", text = "Rp. 1.000.000,00")
            RowItemProgressPembangunan(title = "Retensi", text = "3%")
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            if (expanded) {
                RowItemProgressPembangunan(title = "Addendum", text = "-")
                RowItemProgressPembangunan(title = "Kontrak", text = "Rp. 36.000.000,00")
                RowItemProgressPembangunan(title = "Kontrak + Addendum", text = "Rp. 36.000.000,00")
            }
            if (expanded) {
                RowItemProgressPembangunan(title = "Dana Terserap Progress", text = "Rp. 9.024.000,00")
                RowItemProgressPembangunan(title = "Dana Terserap Lainnya", text = "-")
            }
            RowItemProgressPembangunan(title = "Bisa Diserap", text = "Rp. 8.753.280,00")
        }
    }
}

@Composable
fun RowItemProgressPembangunan(
    modifier: Modifier = Modifier,
    title: String,
    text: String
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        val (titleRef, textRef) = createRefs()

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(textRef.start)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(textRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                start.linkTo(titleRef.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
fun MaterialPembangunanTable(
    modifier: Modifier = Modifier,
    materialPembangunan: List<MaterialPembangunan>,
    onTableCellClicked: (column: Int, row: Int) -> Unit = {_,_->},
    showTable: Boolean = true,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        Text(text = "Material Pembangunan", style = MaterialTheme.typography.titleLarge)
        Text(
            text = "Rp. ${materialPembangunan.totalBiaya().numericToString()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (showTable) {
            TableViewMaterialPembangunan(
                materialPembangunan = materialPembangunan,
                onCellClick = onTableCellClicked
            )
        }
    }
}

@Composable
fun TableViewMaterialPembangunan(
    materialPembangunan: List<MaterialPembangunan>,
    onCellClick: (column: Int, row: Int) -> Unit = {_,_ ->},
) {
    val dataProvider = object : TableViewDataProvider<MaterialPembangunan> {
        override fun getColumnHeaders(data: Collection<MaterialPembangunan>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Material"))
                add(ColumnHeader("Qty"))
                add(ColumnHeader("Biaya"))
                add(ColumnHeader("Kelunasan"))
                add(ColumnHeader("Kedatangan"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<MaterialPembangunan>): List<RowHeader> {
            return buildList {
                repeat(data.size) {
                    add(RowHeader(rowId = it.toString(), data = it.plus(1).toString()))
                }
            }
        }

        override fun getCellItems(data: Collection<MaterialPembangunan>): List<List<CellItem>> {
            return buildList {
                data.forEachIndexed { index, item ->
                    val cellId = index.toString()
                    val cell = mutableListOf<CellItem>()

                    cell.add(CellItem(cellId, item.tanggal))
                    cell.add(CellItem(cellId, item.namaMaterial))
                    cell.add(CellItem(cellId, "${item.qty} ${item.satuan}"))
                    cell.add(CellItem(cellId, item.hargaTotal))
                    cell.add(CellItem(cellId, item.kelunasan))
                    cell.add(CellItem(cellId, item.kedatangan))
                    cell.add(CellItem(cellId, item.keterangan))

                    add(cell)
                }
            }
        }
    }

    AndroidViewBinding(
        modifier = Modifier.fillMaxWidth(),
        factory = { inflater, parent, attachToParent ->
            val binding = LayoutGenericTableviewBinding.inflate(inflater, parent, attachToParent)

            GenericTableView(binding.tableView, materialPembangunan)
                .setDataProvider(dataProvider)
                .setOnCellBinding { cellViewHolder, cellItem, col, _ ->
                    with(cellViewHolder) {
                        when(col) {
                            TableMaterialPembangunan.INDEX_TANGGAL -> {
                                tvCell.text = (cellItem?.data as Date?)?.toSlashedString() ?: "01/01/1979"
                            }
                            TableMaterialPembangunan.INDEX_BIAYA -> {
                                val totalBiaya  = (cellItem?.data as Long?)?.numericToString() ?: "0"
                                tvCell.text = "Rp. $totalBiaya"
                            }
                            TableMaterialPembangunan.INDEX_KEDATANGAN -> {
                                val kedatangan = (cellItem?.data as MaterialPembangunan.Kedatangan?)
                                tvCell.text = when (kedatangan) {
                                    is MaterialPembangunan.Kedatangan.Datang -> "DATANG"
                                    is MaterialPembangunan.Kedatangan.Belum -> "BELUM"
                                    is MaterialPembangunan.Kedatangan.Partial -> "PARTIAL"
                                    else -> "N/A"
                                }
                            }
                            TableMaterialPembangunan.INDEX_KELUNASAN -> {
                                val kelunasan = (cellItem?.data as MaterialPembangunan.Kelunasan?)
                                tvCell.text = when(kelunasan) {
                                    is MaterialPembangunan.Kelunasan.Lunas -> "LUNAS"
                                    is MaterialPembangunan.Kelunasan.Belum -> "BELUM"
                                    is MaterialPembangunan.Kelunasan.Partial -> "PARTIAL"
                                    else -> "N/A"
                                }
                            }
                        }
                    }
                }
                .setOnClickedCellItem { _, column, row ->
                    onCellClick(column, row)
                }
                .create()

            binding
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, group = "layout")
@Composable
fun BiayaPembangunanKavlingScreenPreview(
    @PreviewParameter(BiayaMaterialPembangunanParamProvider::class, 1)
    materialList: List<MaterialPembangunan>,
) {
    GriyaKampoengTkwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BiayaPembangunanKavlingScreen(
                materialPembangunan = materialList,
                showTable = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, group = "components")
@Composable
fun TabelBiayaMaterialPreview(
    @PreviewParameter(BiayaMaterialPembangunanParamProvider::class, 1)
    materialPembangunan: List<MaterialPembangunan>,
) {
    GriyaKampoengTkwTheme {
        MaterialPembangunanTable(
            materialPembangunan = materialPembangunan,
            modifier = Modifier.padding(16.dp),
            showTable = false,
        )
    }
}

@Preview(showBackground = true, group = "components")
@Composable
fun CardProgressPembangunanPreview() {
    GriyaKampoengTkwTheme {
        var expanded by remember {
            mutableStateOf(false)
        }
        CardProgressPembangunan(
            modifier = Modifier.padding(16.dp),
            expanded = expanded,
            onClick = {
                expanded = !expanded
            }
        )
    }
}

private class BiayaMaterialPembangunanParamProvider: PreviewParameterProvider<List<MaterialPembangunan>> {
    override val values: Sequence<List<MaterialPembangunan>>
        get() = sequenceOf(
            listOf(
                MaterialPembangunan(
                    untukKavling = "D1",
                    namaMaterial = "Besi SNI 10mm",
                    tanggal = "28/06/2023".toDate(),
                    qty = 40.0,
                    satuan = "ljr",
                    hargaTotal = 1_320_000L,
                    kedatangan = MaterialPembangunan.Kedatangan.Datang(40.0),
                ),
                MaterialPembangunan(
                    untukKavling = "D1",
                    namaMaterial = "Pasir Cor",
                    tanggal = "28/06/2023".toDate(),
                    qty = 2.0,
                    satuan = "rit",
                    hargaTotal = 3_550_000L,
                    kedatangan = MaterialPembangunan.Kedatangan.Datang(2.0),
                ),
                MaterialPembangunan(
                    untukKavling = "D1",
                    namaMaterial = "Batu Bata",
                    tanggal = "28/06/2023".toDate(),
                    qty = 6000.0,
                    satuan = "biji",
                    hargaTotal = 2_345_000L,
                    kedatangan = MaterialPembangunan.Kedatangan.Datang(6000.0),
                ),
            )
        )
}

object TableMaterialPembangunan {
    const val INDEX_TANGGAL = 0
    const val INDEX_NAMA_MATERIAL = 1
    const val INDEX_QTY = 2
    const val INDEX_BIAYA = 3
    const val INDEX_KELUNASAN = 4
    const val INDEX_KEDATANGAN = 5
    const val INDEX_KETERANGAN = 6
}
