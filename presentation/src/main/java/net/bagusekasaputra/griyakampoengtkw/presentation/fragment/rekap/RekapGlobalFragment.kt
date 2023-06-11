package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.evrencoskun.tableview.sort.SortState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.misc.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapGlobalBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutWarningAndLoadingRekapBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DoubleRowHeaderConfigurator
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RekapGlobalColumnPosition
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RekapGlobalTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapGlobalFragment : Fragment() {

    private lateinit var binding: FragmentRekapGlobalBinding
    private val rekapViewModel: RekapViewModel by activityViewModels()

    private val dataProviderTableView = object : TableViewDataProvider<RekapGlobal> {
        override fun getColumnHeaders(data: Collection<RekapGlobal>): List<ColumnHeader> {
            return buildList {
                add(ColumnHeader("Nama"))
                add(ColumnHeader("Tgl. Pembelian"))
                add(ColumnHeader("Harga"))
                add(ColumnHeader("Uang Masuk"))
                add(ColumnHeader("Sisa Pembayaran"))
                add(ColumnHeader("Persentase"))
            }
        }

        override fun getRowHeaders(data: Collection<RekapGlobal>): List<RowHeader> {
            return buildList {
                data.forEachIndexed { index, rekapGlobal ->
                    val rowText = "${index + 1}${cornerSeparator}${rekapGlobal.noKavling}"

                    add(RowHeader(rekapGlobal.noKavling, rowText))
                }
            }
        }

        override fun getCellItems(data: Collection<RekapGlobal>): List<List<CellItem>> {
            return buildList {
                data.forEach {
                    val cells = mutableListOf<CellItem>()

                    cells.add(CellItem(it.noKavling, it.namaCostumer))
                    cells.add(CellItem(it.noKavling, it.tanggalPembelian?.toSlashedString() ?: "-"))
                    cells.add(CellItem(it.noKavling, it.parsedHarga))
                    cells.add(CellItem(it.noKavling, it.parsedJumlahUangMasuk))
                    cells.add(CellItem(it.noKavling, it.parsedSisaPembayaran))
                    cells.add(CellItem(it.noKavling, it.parsedPersentase))

                    add(cells)
                }
            }
        }
    }
    private var genericTableView: GenericTableView<RekapGlobal>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRekapGlobalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setupViewModelLegacy()
        binding.setupWithViewModel()

        binding.layoutWarningLoading.btnLihatRingkasan.setOnClickListener {
//            binding.layoutWarningLoading.layoutWarningRekap.visibility = View.GONE
//            binding.layoutWarningLoading.layoutLoadingRekap.visibility = View.VISIBLE
            binding.layoutWarningLoading.root.visibility = View.GONE
            binding.tableRekapGlobal.visibility = View.VISIBLE

//            rekapViewModel.getListRekapGlobal { failMsg ->
//                Toast.makeText(requireContext().applicationContext, failMsg, Toast.LENGTH_LONG).show()
//            }

            val snackBarLoading = Snackbar.make(binding.root, "Memuat data ...", Snackbar.LENGTH_INDEFINITE)
            snackBarLoading.setAction("Batal") {
                rekapViewModel.cancelFetchRekapGlobal()
            }

            rekapViewModel.fetchRekapGlobalOfKavlings(
                kavlingList = emptyList(),
                exclusionList = emptyList(),
                onLoading = {
                    snackBarLoading.show()
                },
                onCompleted = {
                    snackBarLoading.dismiss()

                    binding.tableRekapGlobal.setupTableRekapBesar(
                        rekapViewModel.rekapGlobalList.value
                    )
                },
                onFailed = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun FragmentRekapGlobalBinding.setupWithViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                rekapViewModel.rekapGlobalList
                    .onStart {
                        tableRekapGlobal.setupTableRekapBesar(emptyList())
                    }
                    .collect { value ->
                    if (value.isNotEmpty()) {
                        genericTableView?.updateData(value)
                    }
                }
            }
        }
    }

    private fun TableView.setupTableRekapBesar(rekapGlobalList: List<RekapGlobal>) {
        genericTableView = GenericTableView(this, rekapGlobalList)
            .useDoubleCorner(DoubleRowHeaderConfigurator("Kavling", cornerSeparator))
            .setWidthColumnHeaders(columnHeaderWidths)
            .setDataProvider(dataProviderTableView)

        genericTableView?.create()
    }


    @Deprecated("")
    private fun setupViewModelLegacy() {
        rekapViewModel.rekapGlobalProgress.observe(requireActivity()) {
            if (it != null) {
                binding.layoutWarningLoading.setProgress(it)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rekapViewModel.isRekapGlobalLoaded.collect { loaded ->
                    withContext(Dispatchers.Main) {
                        binding.layoutWarningLoading.root.visibility = if (loaded) View.GONE else View.VISIBLE
                        binding.tableRekapGlobal.visibility = if (loaded) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        rekapViewModel.listRekapGlobalLive.observe(requireActivity()) {
            it?.also { rekapGlobalList ->
                if (rekapGlobalList.isNotEmpty()) {
                    val columnHeaders = rekapViewModel.getColumnHeaderRekapTable()
                    val rowHeaders = rekapViewModel.getRowHeaderRekapTable()
                    val cellItems = rekapViewModel.getListCellsRekapTable()

                    setupRekapTableView(columnHeaders, rowHeaders, cellItems)
                }
            }
        }
    }

    @Deprecated("")
    private fun setupRekapTableView(
        columnHeaders: List<RgColumnHeader>,
        rowHeaders: List<RgRowHeader>,
        cellItems: List<List<RgCell>>,
    ) {
        val adapter = RekapGlobalTableViewAdapter {
            with(binding.tableRekapGlobal) {
                repeat(columnHeaders.size) {
                    sortColumn(it, SortState.UNSORTED)
                }

                setupRekapTableView(columnHeaders, rowHeaders, cellItems)

                selectionHandler.selectedColumnPosition = -1
                selectionHandler.selectedRowPosition = -1

                scrollToRowPosition(0)
            }
        }

        binding.tableRekapGlobal.setAdapter(adapter)

        adapter.setAllItems(columnHeaders, rowHeaders, cellItems)
        adapter.notifyDataSetChanged()

        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.NAMA, 400)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.TANGGAL_PEMBELIAN, 300)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.HARGA, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.JUMLAH_UANG_MASUK, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.SISA_PEMBAYARAN, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.PERSENTASE, 350)

        with(binding.tableRekapGlobal) {
            tableViewListener = object : ITableViewListener {
                override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

                }

                override fun onCellDoubleClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

                }

                override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

                }

                override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
                    val nextSortState = when (getSortingStatus(column)) {
                        SortState.UNSORTED -> SortState.ASCENDING
                        SortState.ASCENDING -> SortState.DESCENDING
                        SortState.DESCENDING -> SortState.UNSORTED
                    }
                    sortColumn(column, nextSortState)
                }

                override fun onColumnHeaderDoubleClicked(
                    columnHeaderView: RecyclerView.ViewHolder,
                    column: Int
                ) {

                }

                override fun onColumnHeaderLongPressed(
                    columnHeaderView: RecyclerView.ViewHolder,
                    column: Int
                ) {

                }

                override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {}

                override fun onRowHeaderDoubleClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

                }

                override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {}
            }
        }
    }

    private fun LayoutWarningAndLoadingRekapBinding.setProgress(progressState: ProgressState) {
        linearprogressReport.progress = progressState.percent
        tvLoadingReport.text = progressState.message
    }

    private companion object {
        const val COLUMN_NAMA = 0
        const val COLUMN_TANGGAL_PEMBELIAN = 1
        const val COLUMN_HARGA = 2
        const val COLUMN_JUMLAH_UANG_MASUK = 3
        const val COLUMN_SISA_PEMBAYARAN = 4
        const val COLUMN_PERSENTASE = 5

        const val cornerSeparator = "<>"

        private val columnHeaderWidths = buildList {
            add(Pair(COLUMN_NAMA, 400))
            add(Pair(COLUMN_TANGGAL_PEMBELIAN, 300))
            add(Pair(COLUMN_HARGA, 350))
            add(Pair(COLUMN_JUMLAH_UANG_MASUK, 350))
            add(Pair(COLUMN_SISA_PEMBAYARAN, 350))
            add(Pair(COLUMN_PERSENTASE, 350))
        }
    }

}