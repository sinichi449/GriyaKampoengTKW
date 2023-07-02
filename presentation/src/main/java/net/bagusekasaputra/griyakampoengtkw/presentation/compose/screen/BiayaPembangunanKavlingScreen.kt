package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen

import android.app.DatePickerDialog
import android.content.Context
import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.padWithZero
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.InformasiPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan.Companion.totalBiaya
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja.Companion.totalDibayarkan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.FormDivider
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.FormTextField
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutGenericSingleTableviewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import java.util.Calendar
import java.util.Date

/**
 * Biaya Pembangunan Main Screen
 */
@Composable
fun BiayaPembangunanKavlingScreen(
    modifier: Modifier = Modifier,
    informasiPembangunan: InformasiPembangunan = InformasiPembangunan.EMPTY("D1"),
    upahPekerjaTableView: @Composable () -> Unit,
    materialPembangunanTableView: @Composable () -> Unit,
) {
    var isExpandedCardPembangunan by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        InformasiPembangunanKavling(
            expanded = isExpandedCardPembangunan,
            informasiPembangunan = informasiPembangunan,
            onClick = {
                isExpandedCardPembangunan = !isExpandedCardPembangunan
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        materialPembangunanTableView()
        Spacer(modifier = Modifier.height(32.dp))
        upahPekerjaTableView()
    }
}

/**
 * Dialog Input
 */

@Composable
fun MaterialPembangunanForms(
    modifier: Modifier = Modifier,
    kavling: String = "D1",
    material: MaterialPembangunan? = null,
    onSubmit: (editMode: Boolean, material: MaterialPembangunan) -> Unit = {_, _ ->},
    onDeleteRequest: (keyId: String?) -> Unit = {},
) {
    val isEditMode = material != null
    var showDatePicker by remember { mutableStateOf(false) }

    var namaMaterial by remember {
        mutableStateOf(if (isEditMode) material!!.namaMaterial else "")
    }
    var tanggal by remember {
        mutableStateOf(if (isEditMode) {
            material!!.tanggal.toSlashedString()
        } else {
            val calendar = Calendar.getInstance()
            val tahun = calendar.get(Calendar.YEAR)
            val bulan = calendar.get(Calendar.MONTH).plus(1)
                .padWithZero()
            val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
                .padWithZero()

            "${tanggal}/${bulan}/${tahun}"
        })
    }
    var orderQty by remember {
        mutableStateOf(if (isEditMode) material!!.qty.toString() else "" )
    }
    var satuan by remember {
        mutableStateOf(if (isEditMode) material!!.satuan else "" )
    }
    var hargaTotal by remember {
        mutableStateOf(if (isEditMode) material!!.hargaTotal.toString() else "" )
    }
    var terbayar by remember {
        mutableStateOf(if (isEditMode) material!!.kelunasan.terbayar.toString() else "" )
    }
    var arrivedQty by remember {
        mutableStateOf(if (isEditMode) material!!.kedatangan.qty.toString() else "" )
    }
    var keterangan by remember {
        mutableStateOf(if (isEditMode) material!!.keterangan else "" )
    }

    var enableSubmit by remember { mutableStateOf(true) }
    var enableDelete by remember { mutableStateOf(true) }
    var submitText by remember { mutableStateOf(if (isEditMode) "Ubah" else "Tambahkan") }

    fun onProgress() {
        enableSubmit = false
        enableDelete = false
        submitText = "Memproses data ..."
    }

    if (showDatePicker) {
        DatePickerDialogView(
            ctx = LocalContext.current,
            onDateSet = { year, month, day ->
                val dayPadded = day.toString().padStart(2, '0')
                val monthPadded = month.toString().padStart(2, '0')

                tanggal = "${dayPadded}/${monthPadded}/${year}"
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Column(modifier = modifier) {
        // Title
        Text(
            text = if (isEditMode) "Edit Material" else "Tambahkan Material",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Form Fields
        Column {
            FormTextField(
                title = "Nama Material",
                value = namaMaterial,
                onValueChange = { namaMaterial = it },
                onTextCleared = { namaMaterial = "" },
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Date Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FormTextField(
                    title = "Tanggal",
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    onTextCleared = { tanggal = "" },
                    modifier = Modifier.weight(0.65f),
                )
                Spacer(modifier = Modifier.weight(0.05f))
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(0.3f),
                ) {
                    Text(text = "Pilih")
                }
            }

            FormDivider(modifier = Modifier.height(8.dp))

            FormTextField(
                title = "Order Qty",
                value = orderQty,
                onValueChange = { orderQty = it },
                onTextCleared = { orderQty = "" },
                keyboardType = KeyboardType.Decimal,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FormTextField(
                title = "Arrived Qty",
                value = arrivedQty,
                onValueChange = { arrivedQty = it },
                onTextCleared = { arrivedQty = "" },
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(8.dp))
            FormTextField(
                title = "Satuan",
                value = satuan,
                onValueChange = { satuan = it },
                onTextCleared = { satuan = "" },
            )

            FormDivider(modifier = Modifier.height(8.dp))

            FormTextField(
                title = "Harga Total",
                value = hargaTotal,
                onValueChange = { hargaTotal = it },
                onTextCleared = { hargaTotal = "" },
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(8.dp))
            FormTextField(
                title = "Terbayar",
                value = terbayar,
                onValueChange = { terbayar = it },
                onTextCleared = { terbayar = "" },
                keyboardType = KeyboardType.Number
            )

            FormDivider(modifier = Modifier.height(8.dp))

            FormTextField(
                title = "Keterangan",
                value = keterangan,
                onValueChange = { keterangan = it },
                onTextCleared = { keterangan = "" },
                singleLine = false,
            )
        }
        // Submit Button
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val materialPembangunan = materialPembangunanFormsInstance(kavling, namaMaterial, tanggal, orderQty,
                    satuan, hargaTotal, terbayar, arrivedQty, keterangan)

                onSubmit(isEditMode, materialPembangunan)

                onProgress()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (enableSubmit) {
                Text(text = submitText)
            } else {
                TextProgress(text = submitText)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Delete Button
        Button(
            onClick = {
                onDeleteRequest(material?.keyId)

                onProgress()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        ) {
            Text(text = "Hapus")
        }
    }
}

/**
 * Informasi Pembangunan
 */
@Composable
private fun InformasiPembangunanKavling(
    modifier: Modifier = Modifier,
    informasiPembangunan: InformasiPembangunan = InformasiPembangunan.EMPTY("D1"),
    expanded: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .clickable {
                onClick()
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Informasi Pembangunan", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp
                else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        informasiPembangunan.also {
            RowItemInformasiPembangunan(title = "Luas", text = "${it.luas} m2")
            RowItemInformasiPembangunan(title = "Progress", text = "${it.progress}%")
            RowItemInformasiPembangunan(title = "Harga Borong", text = "Rp. ${it.hargaBorong.numericToString()}")
            RowItemInformasiPembangunan(title = "Retensi", text = "${it.persentaseRetensi}%")

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RowItemInformasiPembangunan(
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
private fun AddendumPembangunanItems(
    modifier: Modifier = Modifier,
    addendum: List<InformasiPembangunan.Addendum> = emptyList(),
) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        items(addendum) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (titleRef, contentRef) = createRefs()

                Text(
                    text = it.keterangan,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(contentRef.start)
                        width = Dimension.fillToConstraints
                    }
                )
                Text(
                    text = "Rp. ${it.jumlahUang.numericToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.constrainAs(contentRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        start.linkTo(titleRef.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }
        }
    }
}

/**
 * Upah Pekerja Table And Header
 */
@Composable
fun UpahPekerjaTable(
    modifier: Modifier = Modifier,
    upahPekerja: List<UpahPekerja> = emptyList(),
    showTable: Boolean = true,
    onRowHeaderClick: (row: Int) -> Unit = {},
    onCellClicked: (column: Int, row: Int) -> Unit = {_,_->},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        TableTitlePembangunanKavling(
            modifier = Modifier.fillMaxWidth(),
            title = "Upah Pekerja",
            sumData = upahPekerja.totalDibayarkan(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (showTable) {
            TableViewUpahPekerja(
                upahPekerja = upahPekerja,
                onRowHeaderClick = onRowHeaderClick,
                onCellClicked = onCellClicked
            )
        }
    }
}

@Composable
private fun TableViewUpahPekerja(
    upahPekerja: List<UpahPekerja>,
    onRowHeaderClick: (row: Int) -> Unit,
    onCellClicked: (column: Int, row: Int) -> Unit,
) {
    val dataProvider = object : TableViewDataProvider<UpahPekerja> {
        override fun getColumnHeaders(data: Collection<UpahPekerja>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Mandor"))
                add(ColumnHeader("Minggu Ke"))
                add(ColumnHeader("Dibayarkan"))
                add(ColumnHeader("Progress"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<UpahPekerja>): List<RowHeader> {
            return buildList {
                data.forEachIndexed { index, item ->
                    add(TableUpahPekerja.getRowHeaderData(index, item.kuitansiUri))
                }
            }
        }

        override fun getCellItems(data: Collection<UpahPekerja>): List<List<CellItem>> {
            return buildList {
                data.forEachIndexed { index, item ->
                    val cellId = index.toString()
                    val cell = mutableListOf<CellItem>()

                    cell.add(CellItem(cellId, item.tanggalDibayarkan))
                    cell.add(CellItem(cellId, item.mandor))
                    cell.add(CellItem(cellId, item.mingguKe))
                    cell.add(CellItem(cellId, item.jumlahDibayarkan))
                    cell.add(CellItem(cellId, item.progress))
                    cell.add(CellItem(cellId, item.keterangan))

                    add(cell)
                }
            }
        }

    }

    AndroidViewBinding(
        modifier = Modifier.fillMaxWidth(),
        factory = { inflater, parent, attachToParent ->
            LayoutGenericSingleTableviewBinding.inflate(inflater, parent, attachToParent)
        },
        update = {
            GenericTableView(tableView, upahPekerja)
                .setDataProvider(dataProvider)
                .setWidthColumnHeaders(TableUpahPekerja.columnHeaderWidths)
                .setOnRowHeaderBinding { viewHolder, item, _ ->
                    with(viewHolder) {
                        item?.also {
                            val nomor = TableUpahPekerja.getNomor(it.data)
                            val sudahIsiKuitansi = TableUpahPekerja.getKuitansiUri(it.rowId).isNotEmpty()

                            if (sudahIsiKuitansi) setRowHeaderBgColour(R.color.table_selected_colour)
                            getTextView().text = nomor
                        }
                    }
                }
                .setOnCellBinding { cellViewHolder, cellItem, col, _ ->
                    with(cellViewHolder) {
                        when (col) {
                            TableUpahPekerja.INDEX_TANGGAL -> {
                                val tanggal = (cellItem?.data as Date?)?.toSlashedString() ?: "-"
                                tvCell.text = tanggal
                            }
                            TableUpahPekerja.INDEX_MANDOR -> {
                                tvCell.gravity = Gravity.START
                            }
                            TableUpahPekerja.INDEX_DIBAYARKAN -> {
                                val jumlahUang = (cellItem?.data as Long?)?.numericToString() ?: "-"
                                tvCell.text = jumlahUang
                            }
                            TableUpahPekerja.INDEX_PROGRESS -> {
                                val progress = (cellItem?.data as Double?) ?: 0.0
                                tvCell.text = "${progress}%"
                            }
                            TableUpahPekerja.INDEX_KETERANGAN -> {
                                tvCell.gravity = Gravity.START
                            }
                        }
                    }
                }
                .setOnClickedCellItem { _, column, row ->
                    onCellClicked(column, row)
                }
                .setOnClickedRowHeader { _, row ->
                    onRowHeaderClick(row)
                }
                .create()
        }
    )
}

/**
 * Material Pembangunan Table And Header
 */
@Composable
fun MaterialPembangunanTable(
    modifier: Modifier = Modifier,
    materialPembangunan: List<MaterialPembangunan>,
    selectedRow: Int = -1,
    onTableCellClicked: (column: Int, row: Int) -> Unit = {_,_->},
    onTableRowClicked: (row: Int) -> Unit = {},
    showTable: Boolean = true,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        TableTitlePembangunanKavling(
            modifier = Modifier.fillMaxWidth(),
            title = "Material Pembangunan",
            sumData = materialPembangunan.totalBiaya(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (showTable) {
            TableViewMaterialPembangunan(
                materialPembangunan = materialPembangunan,
                selectedRow = selectedRow,
                onCellClick = onTableCellClicked,
                onRowHeaderClick = onTableRowClicked,
            )
        }
    }
}

@Composable
private fun TableViewMaterialPembangunan(
    materialPembangunan: List<MaterialPembangunan>,
    selectedRow: Int = -1,
    onRowHeaderClick: (row: Int) -> Unit = {},
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
            LayoutGenericSingleTableviewBinding.inflate(inflater, parent, attachToParent)
        },
        update = {
            GenericTableView(tableView, materialPembangunan)
                .setDataProvider(dataProvider)
                .setWidthColumnHeaders(TableMaterialPembangunan.columnHeaderWidths)
                .setOnCellBinding { cellViewHolder, cellItem, col, _ ->
                    with(cellViewHolder) {
                        when(col) {
                            TableMaterialPembangunan.INDEX_TANGGAL -> {
                                tvCell.text = (cellItem?.data as Date?)?.toSlashedString() ?: "01/01/1979"
                            }
                            TableMaterialPembangunan.INDEX_NAMA_MATERIAL -> {
                                tvCell.gravity = Gravity.START
                            }
                            TableMaterialPembangunan.INDEX_BIAYA -> {
                                val totalBiaya  = (cellItem?.data as Long?)?.numericToString() ?: "0"
                                tvCell.text = totalBiaya
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
                            TableMaterialPembangunan.INDEX_KETERANGAN -> {
                                tvCell.gravity = Gravity.START
                            }
                        }
                    }
                }
                .setOnClickedCellItem { _, column, row ->
                    onCellClick(column, row)
                }
                .setOnClickedRowHeader { _, row ->
                    onRowHeaderClick(row)
                }
                .create()

            tableView.selectionHandler.selectedRowPosition = selectedRow
        }
    )
}

/**
 * Common Components
 */
@Composable
private fun TableTitlePembangunanKavling(
    modifier: Modifier = Modifier,
    title: String = "Lorem Ipsum",
    sumData: Long = 0L
) {
    Column(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = "Rp. ${sumData.numericToString()}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TextProgress(
    modifier: Modifier = Modifier,
    text: String = "Memproses data ..."
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}


/**
 * Previews
 */
@Preview(showBackground = true, showSystemUi = true, group = "layout")
@Composable
private fun BiayaPembangunanKavlingScreenPreview(
    @PreviewParameter(MaterialPembangunanParamProvider::class, 1)
    materialList: List<MaterialPembangunan>,
) {
    val upahPekerja = UpahPekerjaParamProvider().values.toList()[0]

    GriyaKampoengTkwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BiayaPembangunanKavlingScreen(
                modifier = Modifier.padding(16.dp),
                upahPekerjaTableView = {},
                materialPembangunanTableView = {},
            )
        }
    }
}

@Preview(showBackground = true, group = "components")
@Composable
private fun UpahPekerjaTablePreview(
    @PreviewParameter(UpahPekerjaParamProvider::class, 1)
    upahPekerja: List<UpahPekerja>,
) {
    UpahPekerjaTable(
        modifier = Modifier.padding(16.dp),
        upahPekerja = upahPekerja,
        showTable = false,
    )
}

@Preview(showBackground = true, group = "components")
@Composable
private fun TabelBiayaMaterialPreview(
    @PreviewParameter(MaterialPembangunanParamProvider::class, 1)
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
private fun CardInformasiPembangunanPreview() {
    GriyaKampoengTkwTheme {
        var expanded by remember {
            mutableStateOf(true)
        }
        InformasiPembangunanKavling(
            modifier = Modifier.padding(16.dp),
            expanded = expanded,
            onClick = {
                expanded = !expanded
            }
        )
    }
}

@Preview(showBackground = true, group = "components")
@Composable
private fun AddendumPembangunanItemsPreview(
    @PreviewParameter(AddendumPembangunanParameterProvider::class, 1)
    addendum: List<InformasiPembangunan.Addendum>
) {
    AddendumPembangunanItems(addendum = addendum)
}

@Preview(showBackground = true, group = "isolated")
@Composable
private fun MaterialPembangunanFormsPreview() {
    MaterialPembangunanForms()
}

@Preview(showBackground = true, group = "isolated")
@Composable
private fun TextProgressPreview() {
    TextProgress()
}

private class AddendumPembangunanParameterProvider: PreviewParameterProvider<List<InformasiPembangunan.Addendum>> {
    override val values: Sequence<List<InformasiPembangunan.Addendum>>
        get() = sequenceOf(
            listOf(
                InformasiPembangunan.Addendum(1_200_000L, "Septick Tank"),
                InformasiPembangunan.Addendum(600_000L, "Urug-urug"),
                InformasiPembangunan.Addendum(1_000_000, "Pasturisasi Taman"),
            )
        )

}

private class MaterialPembangunanParamProvider: PreviewParameterProvider<List<MaterialPembangunan>> {
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

private class UpahPekerjaParamProvider: PreviewParameterProvider<List<UpahPekerja>> {
    override val values: Sequence<List<UpahPekerja>>
        get() = sequenceOf(
            listOf(
                UpahPekerja(
                    untukKavling = "D1",
                    tanggalDibayarkan = "24/06/2023".toDate(),
                    mingguKe = 1,
                    mandor = "Pak Sugeng",
                    progress = 16.0,
                    jumlahDibayarkan = 3_000_000L,
                ),
                UpahPekerja(
                    untukKavling = "D1",
                    tanggalDibayarkan = "01/07/2023".toDate(),
                    mingguKe = 2,
                    mandor = "Pak Pandri",
                    progress = 20.74,
                    jumlahDibayarkan = 790_000L,
                ),
                UpahPekerja(
                    untukKavling = "D1",
                    tanggalDibayarkan = "08/07/2023".toDate(),
                    mingguKe = 3,
                    mandor = "Pak Blablabla",
                    progress = 24.0,
                    jumlahDibayarkan = 4_250_000L,
                    kuitansiUri = "https://www.google.com"
                ),
            )
        )

}

private fun DatePickerDialogView(
    ctx: Context,
    onDateSet: (tahun: Int, bulan: Int, tanggal: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        onDateSet(year, month + 1, dayOfMonth)
    }

    val datePicker = DatePickerDialog(
        ctx, R.style.DatePicker, listener,
        currentYear, currentMonth, currentDay
    )
    datePicker.setOnDismissListener{ onDismiss() }

    datePicker.show()
}

private fun materialPembangunanFormsInstance(
    kavling: String,
    namaMaterial: String,
    tanggal: String,
    orderQty: String,
    satuan: String,
    hargaTotal: String,
    terbayar: String,
    arrivedQty: String,
    keterangan: String,
): MaterialPembangunan {
    val jumlahTerbayar = terbayar.ifEmpty { "0" }.toLong()
    val mHargaTotal = hargaTotal.ifEmpty { "0" }.toLong()
    val mArrivedQty = arrivedQty.ifEmpty { "0.0" }.toDouble()
    val mOrderQty = orderQty.ifEmpty { "0.0" }.toDouble()

    return MaterialPembangunan(
        untukKavling = kavling,
        namaMaterial = namaMaterial.ifEmpty { "NULL" },
        tanggal = tanggal.ifEmpty { "01/01/1970" }.toDate(),
        qty = orderQty.ifEmpty { "0.0" }.toDouble(),
        satuan = satuan,
        hargaTotal = mHargaTotal,
        kelunasan = MaterialPembangunan.getKelunasan(
            jumlahTerbayar = jumlahTerbayar,
            totalHarga = mHargaTotal,
        ),
        kedatangan = MaterialPembangunan.getKedatangan(
            datangQty = mArrivedQty,
            orderQty = mOrderQty,
        ),
        keterangan = keterangan.ifEmpty { "-" }
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

    val columnHeaderWidths = buildList {
        add(Pair(INDEX_TANGGAL, 250))
        add(Pair(INDEX_NAMA_MATERIAL, 350))
        add(Pair(INDEX_QTY, 250))
        add(Pair(INDEX_BIAYA, 350))
        add(Pair(INDEX_KELUNASAN, 250))
        add(Pair(INDEX_KEDATANGAN, 275))
        add(Pair(INDEX_KETERANGAN, 500))
    }
}

object TableUpahPekerja {
    const val INDEX_TANGGAL = 0
    const val INDEX_MANDOR = 1
    const val INDEX_MINGGU_KE = 2
    const val INDEX_DIBAYARKAN = 3
    const val INDEX_PROGRESS = 4
    const val INDEX_KETERANGAN = 5

    const val ROW_SEPARATOR = "<>"

    val columnHeaderWidths = buildList {
        add(Pair(INDEX_TANGGAL, 250))
        add(Pair(INDEX_MANDOR, 350))
        add(Pair(INDEX_MINGGU_KE, 200))
        add(Pair(INDEX_DIBAYARKAN, 350))
        add(Pair(INDEX_PROGRESS, 250))
        add(Pair(INDEX_KETERANGAN, 500))
    }

    fun getRowHeaderData(index: Int, kuitansiUri: String): RowHeader {
        return RowHeader(
            rowId = index.toString(),
            data = buildString {
                append(index.plus(1).toString())
                append(ROW_SEPARATOR)
                append(kuitansiUri)
            }
        )
    }

    fun getNomor(rowHeaderData: String): String {
        return splitRowHeader(rowHeaderData)[0]
    }
    fun getKuitansiUri(rowHeaderData: String): String {
        val split = splitRowHeader(rowHeaderData)
        return if (split.size > 1) {
            split[1]
        } else {
            ""
        }
    }

    private fun splitRowHeader(rowHeaderData: String): List<String> {
        return rowHeaderData.split(ROW_SEPARATOR)
    }
}
