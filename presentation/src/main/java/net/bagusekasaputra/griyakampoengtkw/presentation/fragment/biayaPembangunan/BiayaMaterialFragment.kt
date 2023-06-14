package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.filter.Filter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.timeMillisToSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaPembangunan.Companion.totalBiaya
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaMaterialBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionBiayaMaterialBottomSheetDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan.BiayaPembangunanFragment.Companion.PAGE_BIAYA_MATERIAL
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.SingleRowHeaderViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPembangunanViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.OnResultListener

@AndroidEntryPoint
class BiayaMaterialFragment : Fragment() {

    private lateinit var binding: FragmentBiayaMaterialBinding
    private var fabAction: FloatingActionButton? = null

    private val viewModel by activityViewModels<BiayaPembangunanViewModel>()

    private val tableDataProvider = object : TableViewDataProvider<BiayaMaterial> {
        override fun getColumnHeaders(data: Collection<BiayaMaterial>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Kavling"))
                add(ColumnHeader("Nama Item"))
                add(ColumnHeader("Tanggal"))
                add(ColumnHeader("Pcs"))
                add(ColumnHeader("Harga/Pcs"))
                add(ColumnHeader("Total Harga"))
                add(ColumnHeader("Keterangan"))
            }
        }

        override fun getRowHeaders(data: Collection<BiayaMaterial>): List<RowHeader> {
            val items = data.toList()
            return buildList {
                items.forEachIndexed { index, biayaMaterial ->
                    val nomor = index + 1
                    val buktiPembayaran = biayaMaterial.uriFoto
                    val keyId = biayaMaterial.keyId
                    val rowHeaderData = "${nomor}${SEPARATOR_ROW_DATA}" +
                            "${buktiPembayaran}${SEPARATOR_ROW_DATA}" + keyId

                    add(RowHeader(rowId = nomor.toString(), data = rowHeaderData))
                }
            }
        }

        override fun getCellItems(data: Collection<BiayaMaterial>): List<List<CellItem>> {
            val items = data.toList()
            return buildList {
                items.forEachIndexed { index, biayaMaterial ->
                    val cellItems = mutableListOf<CellItem>()
                    val cellId = index.plus(1).toString()

                    cellItems.add(CellItem(cellId, biayaMaterial.kavling))
                    cellItems.add(CellItem(cellId, biayaMaterial.namaItem))
                    cellItems.add(CellItem(cellId, biayaMaterial.tanggal.time))
                    cellItems.add(CellItem(cellId, biayaMaterial.pcs))
                    cellItems.add(CellItem(cellId, biayaMaterial.hargaPerItem))
                    cellItems.add(CellItem(cellId, biayaMaterial.totalBiayaMaterial))
                    cellItems.add(CellItem(cellId, biayaMaterial.keterangan))

                    add(cellItems)
                }
            }
        }

    }
    private var biayaMaterialTable: GenericTableView<BiayaMaterial>? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "BiayaMaterialFragment"
        const val ON_FETCH_FAILED_CODE = 1301

        const val COLUMN_KAVLING = 0
        const val COLUMN_NAMA_ITEM = 1
        const val COLUMN_TANGGAL = 2
        const val COLUMN_PCS = 3
        const val COLUMN_HARGA_PER_ITEM = 4
        const val COLUMN_TOTAL_HARGA = 5
        const val COLUMN_KETERANGAN = 6

        const val SEPARATOR_ROW_DATA = "<>"
        const val INDEX_NOMOR = 0
        const val INDEX_BUKTI_PEMBAYARAN = 1
        const val INDEX_KEY_ID = 2

        const val EXTRAS_ROW_POSITION = "EXTRAS_ROW_POSITION"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBiayaMaterialBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_biaya_material)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAction?.setOnClickListener {
            Snackbar.make(binding.root, "Tambah Biaya Material", Snackbar.LENGTH_SHORT)
                .show()
        }

        with(binding) {
            setupWithViewModel()

            swipeRefreshBiayaMaterial.setOnRefreshListener { sync() }

            UiUtils.hideFabsOnVerticalScroll(scrollViewContent, fabAction)
        }

        sync()
    }

    private fun FragmentBiayaMaterialBinding.setupWithViewModel() {
        // If `currentViewPagerPage` is this fragment, set `fabAction visibility to visible`
        // and vice versa if not
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentViewPagerPage.collect { currentPage ->
                    if (currentPage == PAGE_BIAYA_MATERIAL) {
                        fabAction?.show()
                    } else {
                        fabAction?.hide()
                    }
                }
            }
        }

        // Listen for `List<BiayaMaterial>`
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.biayaMaterialList
                    .onStart {
                        tableviewBiayaMaterial.setupBiayaMaterialTable(viewModel.biayaMaterialList.value)
                    }
                    .collect { items ->
                        if (items.isNotEmpty()) {
                            biayaMaterialTable?.updateData(items)
                        }
                    }
            }
        }
    }

    private fun TableView.setupBiayaMaterialTable(
        items: List<BiayaMaterial>,
        onItemClicked: (row: Int) -> Unit = {},
    ) {
        val columnHeaderWidths = buildList {
            add(Pair(COLUMN_KAVLING, 200))
            add(Pair(COLUMN_PCS, 150))
            add(Pair(COLUMN_NAMA_ITEM, 500))
            add(Pair(COLUMN_KETERANGAN, 500))
        }
        biayaMaterialTable = GenericTableView(this, items)
            .setDataProvider(tableDataProvider)
            .setWidthColumnHeaders(columnHeaderWidths)
            .setOnCellBinding { cellViewHolder, cellItem, column, _ ->
                val cellText: String
                val cellGravity: Int
                when (column) {
                    COLUMN_HARGA_PER_ITEM, COLUMN_TOTAL_HARGA -> {
                        val harga = (cellItem?.data as Long?) ?: 0L

                        cellText = harga.numericToString()
                        cellGravity = Gravity.CENTER
                    }
                    COLUMN_NAMA_ITEM, COLUMN_KETERANGAN -> {
                        cellText = (cellItem?.data as String?) ?: "-"
                        cellGravity = Gravity.START
                    }
                    COLUMN_TANGGAL -> {
                        val timeMillis = cellItem?.data as Long?

                        cellText = timeMillis?.timeMillisToSlashedString() ?: "-"
                        cellGravity = Gravity.CENTER
                    }
                    else -> {
                        cellText = (cellItem?.data?.toString()) ?: "-"
                        cellGravity = Gravity.CENTER
                    }
                }

                with(cellViewHolder) {
                    tvCell.text = cellText
                    tvCell.gravity = cellGravity

                    container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    tvCell.requestLayout()
                }
            }
            .setOnRowHeaderBinding { viewHolder, item, _ ->
                val rowHeaderData = item?.data?.split(SEPARATOR_ROW_DATA)
                val backgroundColour: Int
                val rowText: String
                if (!rowHeaderData.isNullOrEmpty()) {
                    rowText = rowHeaderData[INDEX_NOMOR]

                    backgroundColour = if (rowHeaderData[INDEX_BUKTI_PEMBAYARAN].isNotEmpty())
                        R.color.table_selected_colour else R.color.table_unselected_colour
                } else {
                    backgroundColour = R.color.table_unselected_colour
                    rowText = "-"
                }
                with(viewHolder as SingleRowHeaderViewHolder) {
                    bgColour = backgroundColour
                    tvRowHeader.text = rowText
                }
            }
            .setOnColumnHeaderBinding { viewHolder, _, _ ->
                with(viewHolder) {
                    container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    tvColumnHeader.requestLayout()
                }
            }
            .setOnClickedRowHeader { _, row ->
                onItemClicked(row)
            }

        biayaMaterialTable?.create()
    }

    private fun Spinner.setupKavlingFilter(
        kavlingList: List<String>,
        filterTableView: Filter,
    ) {
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            kavlingList,
        )

        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // No filter
                    filterTableView.set(COLUMN_KAVLING, "")
                } else {
                    val requestedKavlingToFilter = kavlingList[position]
                    filterTableView.set(COLUMN_KAVLING, requestedKavlingToFilter)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun MaterialTextView.setTotalBiaya(items: List<BiayaMaterial>) {
        val totalParsed = items.totalBiaya().numericToString()
        val withRupiah = "Rp. $totalParsed"

        text = withRupiah
    }

    private fun onBiayaMaterialClickListener(rowPosition: Int) {
        val biayaMaterialActionDialog = ActionBiayaMaterialBottomSheetDialog()
        biayaMaterialActionDialog.arguments = bundleOf(
            EXTRAS_ROW_POSITION to rowPosition
        )

        biayaMaterialActionDialog.show(childFragmentManager, null)
    }

    private fun sync() {
        with(binding) {
            viewModel.getAllBiayaMaterial(object : OnResultListener {
                override fun onLoading() {
                    swipeRefreshBiayaMaterial.isRefreshing = true
                }

                override fun onCompleted() {
                    swipeRefreshBiayaMaterial.isRefreshing = false

                    // Add "Semua" filterable item
                    val kavlingList = buildList {
                        add("Semua")
                        addAll(viewModel.kavlingKodeList.value)
                    }
                    spinnerKavling.setupKavlingFilter(
                        kavlingList = kavlingList,
                        filterTableView = Filter(tableviewBiayaMaterial),
                    )

                    val biayaMaterialList = viewModel.biayaMaterialList.value
                    tableviewBiayaMaterial.setupBiayaMaterialTable(
                        items = biayaMaterialList,
                        onItemClicked = { rowPosition ->
                            onBiayaMaterialClickListener(rowPosition)
                        }
                    )
                    tvTotalBiaya.setTotalBiaya(biayaMaterialList)
                }

                override fun onFailure(failMsg: String?) {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Memuat data Biaya Material gagal!",
                        text = failMsg ?: "Kesalahan tidak dikenali terjadi!",
                        channelId = NOTIFICATION_CHANNEL_ID,
                        notificationId = ON_FETCH_FAILED_CODE,
                    )
                }
            })
        }
    }
}