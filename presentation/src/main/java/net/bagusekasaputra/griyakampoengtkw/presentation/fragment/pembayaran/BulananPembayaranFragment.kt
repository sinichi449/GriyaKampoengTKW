package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.evrencoskun.tableview.TableView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan.Kelunasan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBulananPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.SingleRowHeaderViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.util.Calendar
import kotlin.math.absoluteValue

@AndroidEntryPoint
class BulananPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentBulananPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    private companion object {
        const val COLUMN_INVOICE = 0
        const val COLUMN_UANG_MASUK = 1
        const val COLUMN_TUNGGAKAN = 2
        const val COLUMN_ALOKASI = 3
        const val COLUMN_KELUNASAN = 4

        const val KELUNASAN_SEPARATOR = "<>"

        val columnHeaderWidths = listOf(
            Pair(COLUMN_INVOICE, 250),
            Pair(COLUMN_UANG_MASUK, 300),
            Pair(COLUMN_TUNGGAKAN, 300),
            Pair(COLUMN_ALOKASI, 300),
            Pair(COLUMN_KELUNASAN, 250),
        )
    }

    private val tableDataProvider = object : TableViewDataProvider<PembayaranBulanan> {
        override fun getColumnHeaders(data: Collection<PembayaranBulanan>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Invoice"))
                add(ColumnHeader("Uang Masuk"))
                add(ColumnHeader("Tunggakan"))
                add(ColumnHeader("Alokasi"))
                add(ColumnHeader("Kelunasan"))
            }
        }

        override fun getRowHeaders(data: Collection<PembayaranBulanan>): List<RowHeader> {
            val list = data.toList()
            return buildList {
                repeat(data.size) { index ->
                    val nomor = index.plus(1).toString()
                    val kelunasan = list[index].kelunasan.str
                    val rowHeaderData = "${nomor}${KELUNASAN_SEPARATOR}${kelunasan}"

                    add(RowHeader(rowId = index.toString(), data = rowHeaderData))
                }
            }
        }

        override fun getCellItems(data: Collection<PembayaranBulanan>): List<List<CellItem>> {
            return buildList {
                data.forEachIndexed { index, item ->
                    val cell = mutableListOf<CellItem>()

                    cell.add(CellItem(cellId = index.toString(), item.bulanTahunDate.time))
                    cell.add(CellItem(cellId = index.toString(), item.uangMasuk))
                    cell.add(CellItem(cellId = index.toString(), item.tunggakan))
                    cell.add(CellItem(cellId = index.toString(), item.alokasi))
                    cell.add(CellItem(cellId = index.toString(), item.kelunasan.str))

                    add(cell)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBulananPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.pembayaranBulanansLive.observe(requireActivity()) {
            it?.also {
                binding.tablePembayaranBulanan.setupTablePembayaranBulanan(it)
                setTotalTunggakan(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun TableView.setupTablePembayaranBulanan(pembayaranBulanans: List<PembayaranBulanan>) {
        val chAndCornerBackground = R.color.purple_500

        GenericTableView(this, pembayaranBulanans)
            .setWidthColumnHeaders(columnHeaderWidths)
            .setDataProvider(tableDataProvider)
            .setOnColumnHeaderBinding { viewHolder, _, _ ->
                viewHolder.columnHeaderBackgroundColour = chAndCornerBackground
                viewHolder.columnHeaderTextColour = R.color.white
            }
            .setOnRowHeaderBinding { rowHeaderViewHolder, item, _ ->
                val viewHolder = rowHeaderViewHolder as SingleRowHeaderViewHolder
                with(viewHolder) {
                    val nomorDanKelunasan = item?.data?.split(KELUNASAN_SEPARATOR)
                    val nomor = nomorDanKelunasan?.get(0)
                    val kelunasan = nomorDanKelunasan?.get(1)

                    bgColour = when(kelunasan) {
                        Kelunasan.LUNAS.str -> R.color.pembayaran_bulanan_lunas
                        Kelunasan.KURANG.str -> R.color.pembayaran_bulanan_belum_lunas
                        else -> R.color.pembayaran_bulanan_nil
                    }

                    tvRowHeader.text = nomor
                }
            }
            .setOnCornerViewBinding { view ->
                view.setBackgroundColor(ContextCompat.getColor(
                    requireContext(), chAndCornerBackground
                ))
            }
            .setOnCellBinding { cellViewHolder, cellItem, col, row ->
                with(cellViewHolder) {
                    val fontFamily: Typeface
                    val cellBackgroundColour: Int
                    val calendar = Calendar.getInstance()
                    var isBold = false

                    when (col) {
                        COLUMN_INVOICE -> {
                            fontFamily = Typeface.DEFAULT
                            cellBackgroundColour = R.color.white

                            val bulanAndTahunTimemillis = cellItem?.data as Long
                            calendar.timeInMillis = bulanAndTahunTimemillis

                            val bulan = calendar.get(Calendar.MONTH) + 1
                            val tahun = calendar.get(Calendar.YEAR)
                            val bulanAngsuran = BulanAngsuran(bulan, tahun)

                            tvCell.text = bulanAngsuran.bulanAndTahun
                        }
                        COLUMN_UANG_MASUK -> {
                            fontFamily = Typeface.SERIF

                            val uangMasuk = cellItem?.data as Long
                            cellBackgroundColour = if (uangMasuk <= 0L)
                                R.color.pembayaran_bulanan_belum_lunas else R.color.white

                            tvCell.text = uangMasuk.numericToString()
                        }
                        COLUMN_TUNGGAKAN -> {
                            fontFamily = Typeface.SERIF

                            val tunggakan = cellItem?.data as Long
                            cellBackgroundColour = if (tunggakan < 0L) android.R.color.darker_gray
                            else if (tunggakan == 0L) R.color.white
                            else R.color.pembayaran_bulanan_belum_lunas

                            tvCell.text = tunggakan.numericToString()
                        }
                        COLUMN_ALOKASI -> {
                            fontFamily = Typeface.SERIF

                            val alokasi = cellItem?.data as Long
                            cellBackgroundColour = if (alokasi <= 0)
                                android.R.color.darker_gray else R.color.white

                            tvCell.text = alokasi.numericToString()
                        }
                        COLUMN_KELUNASAN -> {
                            fontFamily = Typeface.MONOSPACE
                            isBold = true

                            val kelunasan = cellItem?.data as String
                            cellBackgroundColour = when (kelunasan) {
                                Kelunasan.LUNAS.str -> R.color.pembayaran_bulanan_lunas
                                Kelunasan.KURANG.str -> R.color.pembayaran_bulanan_belum_lunas
                                else -> R.color.pembayaran_bulanan_nil
                            }
                        }
                        else -> {
                            fontFamily = Typeface.DEFAULT
                            cellBackgroundColour = R.color.white
                            isBold = false
                        }
                    }

                    tvCell.typeface = TypefaceCompat.create(
                        requireContext(),
                        fontFamily,
                        if (isBold) Typeface.BOLD else Typeface.NORMAL
                    )
                    bgColour = cellBackgroundColour
                }
            }
            .create()
    }

    private fun setTotalTunggakan(pembayaranBulanans: List<PembayaranBulanan>) {
        val totalTunggakan = PembayaranBulanan.hitungSemuaTunggakan(pembayaranBulanans)
        val absoluteTunggakanStr = NumberUtil.formatLongToString(totalTunggakan.absoluteValue)

        val textBackgroundColor: Int
        val textTunggakan: String
        if (totalTunggakan <= 0) {
            textTunggakan = (if (totalTunggakan == 0L) "Rp" else "- Rp. ") + absoluteTunggakanStr
            textBackgroundColor = ContextCompat.getColor(requireContext(), R.color.pembayaran_bulanan_lunas)

            binding.tvTotalTunggakan.text = SpannableString(textTunggakan).apply {
                setSpan(StrikethroughSpan(), 0, textTunggakan.length, 0)
            }
        } else {
            textTunggakan = "Rp. $absoluteTunggakanStr"
            textBackgroundColor = ContextCompat.getColor(requireContext(), R.color.pembayaran_bulanan_belum_lunas_bg_total)

            binding.tvTotalTunggakan.text = textTunggakan
            binding.tvTotalTunggakan.setTextColor(ContextCompat.getColor(
                requireContext(), R.color.white
            ))
            binding.tvTotalTunggakan.typeface = Typeface.DEFAULT_BOLD
        }
        binding.layoutBackgroundPelunasan.setBackgroundColor(textBackgroundColor)
    }
}