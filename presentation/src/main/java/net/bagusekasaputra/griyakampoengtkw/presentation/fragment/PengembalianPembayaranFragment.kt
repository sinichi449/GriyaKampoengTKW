package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.evrencoskun.tableview.TableView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.timeMillisToSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian.Companion.total
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPengembalianPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderConfigurator
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PengembalianViewModel

@AndroidEntryPoint
class PengembalianPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentPengembalianPembayaranBinding
    private var fabAction: FloatingActionButton? = null

    private val viewModel by viewModels<PengembalianViewModel>()

    private val tableDataProvider = object : TableViewDataProvider<Pengembalian> {
        override fun getColumnHeaders(data: Collection<Pengembalian>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Nama"))
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Jumlah Uang"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<Pengembalian>): List<RowHeader> {
            val pengembalianList = data.toList()
            return buildList {
                pengembalianList.forEachIndexed { index, item ->
                    val rowHeaderData = "${index + 1}${SEPARATOR_ROW_HEADER_DATA}${item.kavling}" +
                            "${SEPARATOR_ROW_HEADER_DATA}${item.uri}"

                    add(RowHeader(rowId = item.kavling, data = rowHeaderData))
                }
            }
        }

        override fun getCellItems(data: Collection<Pengembalian>): List<List<CellItem>> {
            val pengembalianList = data.toList()
            return buildList {
                pengembalianList.forEach { item ->
                    val cellId = item.kavling
                    val cellItems = listOf(
                        CellItem(cellId, item.namaCustomer),
                        CellItem(cellId, item.tanggal.time), // Tanggal in TimeMillis
                        CellItem(cellId, item.jumlah),
                        CellItem(cellId, item.keterangan),
                    )

                    add(cellItems)
                }
            }
        }

    }
    private var adapterTableView: GenericTableView<Pengembalian>? = null

    private companion object {
        const val CHANNEL_ID = "PENGEMBALIAN_PEMBAYARAN"

        const val COLUMN_NAMA = 0
        const val COLUMN_TANGGAL = 1
        const val COLUMN_JUMLAH_UANG = 2
        const val COLUMN_KETERANGAN = 3

        // separate No, Kavling, and availability of the image.
        const val SEPARATOR_ROW_HEADER_DATA = "<>"
        const val INDEX_NOMOR = 0
        const val INDEX_KAVLING = 1
        const val INDEX_SUDAH_ISI_FOTO = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPengembalianPembayaranBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_pengembalian_pembayaran)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            UiUtils.hideFabsOnVerticalScroll(scrollViewContent, fabAction)

            setupWithViewModel()

            fabAction?.setOnClickListener {
                // TODO
                Snackbar.make(binding.root, "Ini pengembalian pembayaran", Snackbar.LENGTH_SHORT).show()
            }

            swipeRefreshPengembalianPembayaran.setOnRefreshListener {
                sync()
            }
        }

        sync()
    }

    private fun FragmentPengembalianPembayaranBinding.setupWithViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pengembalianList
                    .onStart {
                        tableviewPengembalianPembayaran.setupTablePengembalian(emptyList())
                    }
                    .collect { items ->
                        Log.d("PENGEMBALIAN", "Items size is :$items")
                        if (!items.isNullOrEmpty()) {
                            adapterTableView?.updateData(items)
                            tvTotalPengembalian.setTotalPengembalian(items)
                        }
                }
            }
        }
    }

    private fun TableView.setupTablePengembalian(items: List<Pengembalian>) {
        val keteranganColumnWidth = buildList {
            add(Pair(COLUMN_KETERANGAN, 500))
        }
        adapterTableView = GenericTableView(this, items)
            .useDoubleCorner(DoubleRowHeaderConfigurator("Kavling", SEPARATOR_ROW_HEADER_DATA))
            .setDataProvider(tableDataProvider)
            .setWidthColumnHeaders(keteranganColumnWidth)
            .setOnColumnHeaderBinding { viewHolder, _, _ ->
                with(viewHolder) {
                    container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    tvColumnHeader.requestLayout()
                }
            }
            .setOnCellBinding { cellViewHolder, cellItem, column, _ ->
                with(cellViewHolder) {
                    val cellGravity: Int
                    val cellText: String
                    when (column) {
                        COLUMN_JUMLAH_UANG -> {
                            cellGravity = Gravity.CENTER

                            val jumlahUang = (cellItem?.data as Long?) ?: 0L
                            cellText = jumlahUang.numericToString()
                        }
                        COLUMN_TANGGAL -> {
                            cellGravity = Gravity.CENTER

                            val timeMillisTanggal = (cellItem?.data as Long?)
                            cellText = timeMillisTanggal?.timeMillisToSlashedString() ?: "-"
                        }
                        // Column NAMA and TANGGAL
                        else -> {
                            cellGravity = Gravity.START
                            cellText = cellItem?.data?.toString() ?: "-"
                        }
                    }

                    tvCell.gravity = cellGravity
                    tvCell.text = cellText

                    container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    tvCell.requestLayout()
                }
            }
            .setOnRowHeaderBinding { viewHolder, item, _ ->
                with(viewHolder as DoubleRowHeaderViewHolder) {
                    val rowHeaderData = item?.data?.split(SEPARATOR_ROW_HEADER_DATA)
                    val nomorText: String
                    val kavlingText: String
                    val fotoPengembalianUri: String
                    if (rowHeaderData.isNullOrEmpty()) {
                        nomorText = "0"
                        kavlingText = "-"
                        fotoPengembalianUri = ""
                    } else {
                        nomorText = rowHeaderData[INDEX_NOMOR]
                        kavlingText = rowHeaderData[INDEX_KAVLING]
                        fotoPengembalianUri = rowHeaderData[INDEX_SUDAH_ISI_FOTO]
                    }

                    tvNomor.text = nomorText
                    tvRowHeader.text = kavlingText

                    // set background colour to `R.color.table_selected_colour` when `Pengembalian.uri`
                    // is not empty, which means that there exists bukti foto.
                    if (fotoPengembalianUri.isNotEmpty()) {
                        bgColour = R.color.table_selected_colour
                    }
                }
            }

        adapterTableView?.create()
    }

    private fun TextView.setTotalPengembalian(items: List<Pengembalian>) {
        val total = items.total().numericToString()
        val totalWithRupiah = "Rp. $total"

        text = totalWithRupiah
    }

    private fun sync() {
        with(binding) {
            viewModel.getAllPengembalianList(
                onLoading = { swipeRefreshPengembalianPembayaran.isRefreshing = true },
                onCompleted = { swipeRefreshPengembalianPembayaran.isRefreshing = false },
                onFailed = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Terjadi kesalahan!",
                        text = it ?: "NULL",
                        channelId = CHANNEL_ID,
                        notificationId = 100,
                    )
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        fabAction?.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

        fabAction?.visibility = View.GONE
    }

}