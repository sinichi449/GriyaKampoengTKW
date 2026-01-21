package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToLong
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFullPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionPembayaranStandardBottomSheetDialogLegacy
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionTambahLuasanPembayaranBottomSheetDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderConfigurator
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.util.Date

@AndroidEntryPoint
class FullPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFullPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    companion object {
        const val COLUMN_INVOICE = 0
        const val COLUMN_TANGGAL = 1
        const val COLUMN_UANG_DIBAYAR = 2
        const val COLUMN_TOTAL = 3
        const val COLUMN_PERSENTASE = 4
        const val COLUMN_KETERANGAN_PROGRESS = 5

        const val ROW_SEPARATOR = "<>"
        const val ROW_NOMOR = 0
        const val ROW_TERMIN = 1
        const val ROW_STATUS_FOTO_PEMBAYARAN = 2
        const val ROW_STATUS_AMBIL_KUITANSI = 3

        data class RowHeaderData(
            val nomor: Int,
            val termin: String,
            val sudahIsiFoto: Boolean,
            val sudahAmbilKuitansi: Boolean,
        ) {
            fun asString(separator: String) = buildString {
                append(nomor).append(separator)
                append(termin).append(separator)
                append(sudahIsiFoto).append(separator)
                append(sudahAmbilKuitansi).append(separator)
            }

            companion object {
                fun fromString(str: String, separator: String): RowHeaderData {
                    val split = str.split(separator)

                    return RowHeaderData(
                        nomor = split[ROW_NOMOR].toInt(),
                        termin = split[ROW_TERMIN],
                        sudahIsiFoto = split[ROW_STATUS_FOTO_PEMBAYARAN].toBoolean(),
                        sudahAmbilKuitansi = split[ROW_STATUS_AMBIL_KUITANSI].toBoolean()
                    )
                }
            }
        }
    }

    private val tableDataProvider = object : TableViewDataProvider<Pembayaran> {
        fun getRowAndCellId(index: Int): String {
            return (index + 1).toString()
        }

        override fun getColumnHeaders(data: Collection<Pembayaran>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Invoice"))
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Uang Dibayar"))
                add(ColumnHeader("Total"))
                add(ColumnHeader("Persentase"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<Pembayaran>): List<RowHeader> {
            val pembayaranList = data.toMutableList()

            return buildList {
                pembayaranList.forEachIndexed { index, pembayaran ->
                    val rowId = getRowAndCellId(index)
                    val rowHeaderData = RowHeaderData(
                        nomor = rowId.toInt(),
                        termin = pembayaran.termin,
                        sudahIsiFoto = pembayaran.sudahIsiFotoPembayaran,
                        sudahAmbilKuitansi = pembayaran.sudahAmbilKuitansi,
                    )

                    add(RowHeader(rowId = rowId, data = rowHeaderData.asString(ROW_SEPARATOR)))
                }
            }
        }

        override fun getCellItems(data: Collection<Pembayaran>): List<List<CellItem>> {
            val pembayaranList = data.toMutableList()
            return buildList {
                pembayaranList.forEachIndexed { index, pembayaran ->
                    val cellId = getRowAndCellId(index)
                    val cell = mutableListOf<CellItem>()

                    cell.add(CellItem(cellId = cellId, data = pembayaran.bulanAngsuran.date))
                    cell.add(CellItem(cellId = cellId, data = pembayaran.tanggal.toDate()))
                    cell.add(CellItem(cellId = cellId, data = pembayaran.jumlahUangDibayar.numericToLong()))
                    cell.add(CellItem(cellId = cellId, data = pembayaran.totalUangMasuk.numericToLong()))
                    cell.add(CellItem(cellId = cellId, data = pembayaran.presentase))
                    cell.add(CellItem(cellId = cellId, data = pembayaran.keterangan))

                    add(cell)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.baselineAndFullPembayaran.observe(requireActivity()) {
            it?.also { baselineAndPembayaran ->
                val baseline = baselineAndPembayaran.first
                val pembayarans = baselineAndPembayaran.second

                if (pembayarans != null) {
                    setTablePembayaran(pembayarans)

                    if (baseline != null) {
                        setSisaWaktuAngsuran(baseline)
                        setupFullScreen(viewModel.isFullScreenTable, baseline, pembayarans)
                    }

                    binding.tvSisaBlmTerbayar.text = StringBuilder().run {
                        append("Rp. ")
                        append(Pembayaran.getSisaBelumTerbayar(pembayarans))
                        toString()
                    }
                }
            }
        }

        viewModel.tambahanLuasPembayaran.observe(requireActivity()) { k ->
            binding.tableviewTambahanLuasan.also {
                if (k != null) {
                    it.root.visibility = View.VISIBLE

                    when (k) {
                        is UiState.Loading -> {
                            it.tvInfoLoadingTambahanLuasan.text = "Sedang memuat ..."
                        }

                        is UiState.Failure -> {
                            it.tvInfoLoadingTambahanLuasan.text = "Terjadi kesalahan!"
                        }

                        is UiState.Success -> {
                            it.tvInfoLoadingTambahanLuasan.visibility = View.GONE
                            if (!k.data.isNullOrEmpty()) {
                                // override k.data with dummy list if
                                it.tableviewTambahanLuasan.visibility = View.VISIBLE
                                setupTableTambahanLuasan(k.data)

                                val total = k.data.sumOf { total -> total.jumlahUang }
                                    .numericToString()
                                it.tvTotalTambahLuasan.visibility = View.VISIBLE
                                it.tvTotalTambahLuasan.text = total
                            } else {
                                it.root.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    it.root.visibility = View.GONE
                }
            }
        }
    }

    private fun setSisaWaktuAngsuran(baselinePembayaran: BaselinePembayaran) {
        val sortedPembayarans = viewModel.fullPembayaransLive.value
        if (!sortedPembayarans.isNullOrEmpty()) {
            val sisaBulan = baselinePembayaran.hitungSisaBulanAngsuran(sortedPembayarans)
            binding.tvSisaWaktuAngsuran.text = StringBuilder().run {
                append(sisaBulan)
                append(" Bulan")
                toString()
            }
        }
    }

    private fun setTablePembayaran(pembayarans: List<Pembayaran>) {
        val resources = binding.root.resources
        val orientation = resources.configuration.orientation

        // 1. Define the list variable
        val widthColumnHeaders: List<Pair<Int, Int>>

        // 2. Switch logic based on orientation
        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            // --- LANDSCAPE: DYNAMIC CALCULATION ---

            // Get screen width
            val screenWidth = resources.displayMetrics.widthPixels

            // Get Row Header width from dimensions
            val rowHeaderWidth = resources.getDimensionPixelSize(
                R.dimen.table_generic_double_row_header_width
            )

            // Calculate scrollable area
            val availableWidth = screenWidth - rowHeaderWidth

            // Apply percentages (Total ~100%)
            widthColumnHeaders = listOf(
                Pair(COLUMN_INVOICE, (availableWidth * 0.125).toInt()),
                Pair(COLUMN_TANGGAL, (availableWidth * 0.125).toInt()),
                Pair(COLUMN_UANG_DIBAYAR, (availableWidth * 0.175).toInt()),
                Pair(COLUMN_TOTAL, (availableWidth * 0.175).toInt()),
                Pair(COLUMN_PERSENTASE, (availableWidth * 0.150).toInt()),
                Pair(COLUMN_KETERANGAN_PROGRESS, (availableWidth * 0.245).toInt())
            )

        } else {
            // --- PORTRAIT: MANUAL / HARDCODED (SCROLLABLE) ---
            // Keeps your original values so user can scroll horizontally
            widthColumnHeaders = listOf(
                Pair(COLUMN_INVOICE, 250),
                Pair(COLUMN_TANGGAL, 250),
                Pair(COLUMN_UANG_DIBAYAR, 350),
                Pair(COLUMN_TOTAL, 350),
                Pair(COLUMN_PERSENTASE, 300),
                Pair(COLUMN_KETERANGAN_PROGRESS, 500),
            )
        }

        GenericTableView(binding.tableFormPembayaran, pembayarans)
            .setDataProvider(tableDataProvider)
            .setOnRowHeaderBinding { viewHolder, item, row ->
                val rowHeaderData = item?.data?.let {
                    RowHeaderData.fromString(it, ROW_SEPARATOR)
                }
                if (rowHeaderData != null) {
                    with(viewHolder as DoubleRowHeaderViewHolder) {
                        bgColour = if (rowHeaderData.sudahIsiFoto) {
                            R.color.table_selected_colour
                        } else {
                            R.color.table_unselected_colour
                        }

                        val termin = buildString {
                            append(rowHeaderData.termin)
                            if (rowHeaderData.sudahAmbilKuitansi) {
                                append(" {*}")
                            }
                        }
                        tvRowHeader.text = termin
                    }
                }
            }
            .setOnCellBinding { cellViewHolder, cellItem, column, _ ->
                with(cellViewHolder) {
                    when (column) {
                        COLUMN_INVOICE -> {
                            val invoice = (cellItem?.data as Date?)?.let {
                                BulanAngsuran.fromDate(it).bulanAndTahun
                            } ?: "NULL"

                            tvCell.text = invoice
                        }
                        COLUMN_TANGGAL -> {
                            val tanggalPembayaran = (cellItem?.data as Date?)?.toSlashedString() ?: "NULL"

                            tvCell.text = tanggalPembayaran
                        }
                        COLUMN_UANG_DIBAYAR, COLUMN_TOTAL -> {
                            val uang = (cellItem?.data as Long?)?.numericToString() ?: "0"

                            tvCell.text = uang
                        }
                        COLUMN_PERSENTASE -> {
                            val persentase = (cellItem?.data as Double?)?.run {
                                "${this}%"
                            } ?: "0%"

                            tvCell.text = persentase
                        }
                        COLUMN_KETERANGAN_PROGRESS -> {
                            tvCell.gravity = Gravity.START
                        }
                    }
                }
            }
            .setWidthColumnHeaders(widthColumnHeaders)
            .useDoubleCorner(DoubleRowHeaderConfigurator(
                cornerViewTitle = "Termin",
                cornerTextSeparator = ROW_SEPARATOR
            ))
            .setOnClickedRowHeader { _, row ->
                val actionPembayaran = ActionPembayaranStandardBottomSheetDialogLegacy()
                val bundle = bundleOf(
                    ActionPembayaranStandardBottomSheetDialogLegacy.EXTRAS_INDEX_PEMBAYARAN_POSITION to row,
                )
                actionPembayaran.arguments = bundle
                actionPembayaran.show(childFragmentManager, null)

            }
            .create()
    }

    private fun setupTableTambahanLuasan(pembayaranTambahLuasanList: List<PembayaranTambahLuasan>) {
        if (pembayaranTambahLuasanList.isNotEmpty()) {
            val Columns = object {
                val TANGGAL = 0
                val JUMLAH_UANG = 1
                val KETERANGAN = 2
            }
            val widthColumnHeaders = listOf(
                Pair(Columns.TANGGAL, if (viewModel.isFullScreenTable) 350 else 250),
                Pair(Columns.JUMLAH_UANG, if (viewModel.isFullScreenTable) 450 else 350),
                Pair(Columns.KETERANGAN, if (viewModel.isFullScreenTable) 600 else 500),
            )
            val rowSeparator = "<>"

            GenericTableView(binding.tableviewTambahanLuasan.tableviewTambahanLuasan, pembayaranTambahLuasanList)
                .setDataProvider(object : TableViewDataProvider<PembayaranTambahLuasan> {
                    override fun getColumnHeaders(data: Collection<PembayaranTambahLuasan>): List<ColumnHeader> {
                        return buildList {
                            add(ColumnHeader("Tanggal"))
                            add(ColumnHeader("Jumlah Uang"))
                            add(ColumnHeader("Keterangan"))
                        }
                    }

                    override fun getRowHeaders(data: Collection<PembayaranTambahLuasan>): List<RowHeader> {
                        val list = data.toMutableList()
                        return buildList {
                            list.forEachIndexed { index, item ->
                                val rowId = index.plus(1).toString()
                                val rowData = TambahanLuasanRowData(
                                    nomor = index.plus(1),
                                    sudahIsiFoto = item.fotoUri != "",
                                    sudahAmbilKuitansi = item.sudahAmbilKuitansi,
                                )

                                add(RowHeader(rowId, rowData.asString(TambahanLuasanRowData.DEFAULT_SEPARATOR)))
                            }
                        }
                    }

                    override fun getCellItems(data: Collection<PembayaranTambahLuasan>): List<List<CellItem>> {
                        val list = data.toMutableList()
                        return buildList {
                            list.forEachIndexed { index, item ->
                                val cellId = index.plus(1).toString()
                                val cell = mutableListOf<CellItem>()

                                cell.add(CellItem(cellId, item.tanggal))
                                cell.add(CellItem(cellId, item.jumlahUang.numericToString()))
                                cell.add(CellItem(cellId, item.keterangan))

                                add(cell)
                            }
                        }
                    }

                })
                .setWidthColumnHeaders(widthColumnHeaders)
                .setOnRowHeaderBinding { viewHolder, item, row ->
                    val data = item?.data?.let {
                        TambahanLuasanRowData.fromString(it, TambahanLuasanRowData.DEFAULT_SEPARATOR)
                    }

                    if (data != null) {
                        Log.d("DEBUG_ME_PRO", "Data ${data.nomor} => ${data.sudahIsiFoto}")
                        viewHolder.setRowHeaderBgColour(
                            if (data.sudahIsiFoto) R.color.table_selected_colour
                            else R.color.table_unselected_colour)
                    }

                    viewHolder.setRowHeaderText(data?.nomor?.toString() ?: "0")
                }
                .setOnCellBinding { cellViewHolder, cellItem, col, row ->
                    if (col == Columns.KETERANGAN) {
                        cellViewHolder.tvCell.gravity = Gravity.START
                    } else {
                        cellViewHolder.tvCell.gravity = Gravity.CENTER
                    }
                }
                .setOnClickedRowHeader { rowHeaderView, row ->
                    val actionDialog = ActionTambahLuasanPembayaranBottomSheetDialog()
                    val bundle = bundleOf(
                        ActionTambahLuasanPembayaranBottomSheetDialog.EXTRAS_SELECTED_INDEX_POSITION to row
                    )
                    actionDialog.arguments = bundle
                    actionDialog.show(childFragmentManager, null)
                }
                .create()
        }
    }

    private fun setupFullScreen(
        fullScreen: Boolean,
        baseline: BaselinePembayaran,
        pembayarans: List<Pembayaran>
    ) {
        if (fullScreen) {
            // Hide Sisa Waktu Angsuran on Fullscreen Mode
            binding.tvInfoSisaWaktuAngsuran.visibility = View.GONE
            binding.tvSisaWaktuAngsuran.visibility = View.GONE

            binding.tvInfoSisaBlmTerbayar.visibility = View.VISIBLE
            binding.tvSisaBlmTerbayar.visibility = View.VISIBLE

            binding.tvInfoBlmDibayarBulanIni.visibility = View.VISIBLE
            binding.tvBlmDibayarBulanIni.visibility = View.VISIBLE


            binding.tvBlmDibayarBulanIni.text = pembayarans.run {
                val blmDibayarBulanIni = baseline.hitungSisaBlmBayarBulanIni(this)

                "Rp. ${NumberUtil.formatLongToString(blmDibayarBulanIni)}"
            }
        } else {
            binding.tvInfoSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvInfoSisaBlmTerbayar.visibility = View.VISIBLE
            binding.tvSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvSisaBlmTerbayar.visibility = View.VISIBLE

            binding.tvInfoBlmDibayarBulanIni.visibility = View.GONE
            binding.tvBlmDibayarBulanIni.visibility = View.GONE
        }
    }
}