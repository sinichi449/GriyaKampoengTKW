package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToLong
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertTambahanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFullPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionPembayaranStandardBottomSheetDialogLegacy
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderConfigurator
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest
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

    private val tambahanDataProvider = object : TableViewDataProvider<TambahanPembayaran> {
        override fun getColumnHeaders(data: Collection<TambahanPembayaran>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Jumlah"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<TambahanPembayaran>): List<RowHeader> {
            val tambahanList = data.toMutableList()
            return buildList {
                tambahanList.forEachIndexed { index, item ->
                    val rowId = (index + 1).toString()
                    val rowData = buildString {
                        append(rowId + ROW_SEPARATOR)
                        append(item.kategori.kode + ROW_SEPARATOR)
                        append(item.sudahIsiFoto.toString() + ROW_SEPARATOR)
                    }
                    add(RowHeader(rowId, rowData))
                }
            }
        }

        override fun getCellItems(data: Collection<TambahanPembayaran>): List<List<CellItem>> {
            val tambahanList = data.toMutableList()
            return buildList {
                tambahanList.forEachIndexed { index, item ->
                    val cellId = (index + 1).toString()
                    val cell = mutableListOf<CellItem>()
                    cell.add(CellItem(cellId, item.tanggal))
                    cell.add(CellItem(cellId, item.jumlahUang))
                    cell.add(CellItem(cellId, item.keterangan))

                    add(cell)
                }
            }
        }

    }

    private val REQUEST_INSERT_TAMBAHAN_PEMBAYARAN = 118


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

        binding.layoutTambahanHeader.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)
            val insertTambahanPembayaranParcel = InsertTambahanPembayaranParcel(viewModel.currentKavlingKode!!)
            intent.putExtra(FormActivity.EXTRAS_PARCEL, insertTambahanPembayaranParcel)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_INSERT_TAMBAHAN_PEMBAYARAN)
        }
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

        viewModel.tambahanPembayarans.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) setTableTambahan(it)
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
        val widthColumnHeaders = listOf(
            Pair(COLUMN_INVOICE, 250),
            Pair(COLUMN_TANGGAL, 250),
            Pair(COLUMN_UANG_DIBAYAR, 350),
            Pair(COLUMN_TOTAL, 350),
            Pair(COLUMN_PERSENTASE, 300),
            Pair(COLUMN_KETERANGAN_PROGRESS, 500),
        )

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

    private fun setTableTambahan(tambahan: List<TambahanPembayaran>) {
        GenericTableView(binding.tableTambahanPembayaran, tambahan)
            .setDataProvider(tambahanDataProvider)
            .setOnRowHeaderBinding { viewHolder, item, row ->
                if (item != null) {
                    val split = item.data.split(ROW_SEPARATOR)
                    val kategori = split[1]
                    val sudahIsiFoto = split[2].toBoolean()
                    with(viewHolder as DoubleRowHeaderViewHolder) {
                        tvRowHeader.text = kategori
                        bgColour = if (sudahIsiFoto) {
                            R.color.table_selected_colour
                        } else {
                            R.color.table_unselected_colour
                        }
                    }
                }
            }
            .setOnCellBinding { cellViewHolder, cellItem, col, row ->
                with(cellViewHolder) {
                    when (col) {
                        0 -> {
                            tvCell.text = (cellItem?.data as Date?)?.toSlashedString() ?: "-"
                        }
                        1 -> {
                            tvCell.text = (cellItem?.data as Long?)?.numericToString()
                        }
                        else -> {
                            tvCell.text = (cellItem?.data as String?)?.toString() ?: "-"
                            tvCell.gravity = Gravity.START
                        }
                    }
                }
            }
            .setWidthColumnHeaders(buildList {
                add(0 to 250)
                add(1 to 350)
                add(2 to 500)
            })
            .useDoubleCorner(DoubleRowHeaderConfigurator("Kategori", ROW_SEPARATOR))
            .setOnClickedRowHeader { rowHeaderView, row ->
                // TODO: Lihat foto pembayaran tambahan
            }
            .create()
    }

    private fun setupFullScreen(
        fullScreen: Boolean,
        baseline: BaselinePembayaran,
        pembayarans: List<Pembayaran>
    ) {
        if (fullScreen) {
            binding.tvInfoSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvInfoSisaBlmTerbayar.visibility = View.VISIBLE
            binding.tvSisaWaktuAngsuran.visibility = View.VISIBLE
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

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.fragment.app.Fragment"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Snackbar.make(binding.root, "Berhasil menambahkan Tambahan Pembayaran !", Snackbar.LENGTH_SHORT)
                .show()

            viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
        } else {
            data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}