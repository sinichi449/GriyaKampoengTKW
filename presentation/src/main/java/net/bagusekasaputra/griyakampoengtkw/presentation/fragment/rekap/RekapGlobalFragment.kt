package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapGlobalBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.*
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapGlobalFragment : Fragment() {

    private lateinit var binding: FragmentRekapGlobalBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapGlobalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }


    private fun setupViewModel() {
        viewModel.listRekapGlobalLive.observe(requireActivity()) {
            val columnHeaders = viewModel.getColumnHeaderRekapTable()
            val rowHeaders = viewModel.getRowHeaderRekapTable()
            val cellItems = viewModel.getListCellsRekapTable()

            setupRekapTableView(columnHeaders, rowHeaders, cellItems)
        }
    }

    private fun setupRekapTableView(
        columnHeaders: List<RgColumnHeader>,
        rowHeaders: List<RgRowHeader>,
        cellItems: List<List<RgCell>>,
    ) {
        val adapter = RekapGlobalTableViewAdapter()

        binding.tableRekapGlobal.setAdapter(adapter)

        adapter.setAllItems(columnHeaders, rowHeaders, cellItems)

        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.NAMA, 400)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.TANGGAL_PEMBELIAN, 300)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.HARGA, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.JUMLAH_UANG_MASUK, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.SISA_PEMBAYARAN, 350)
        binding.tableRekapGlobal.setColumnWidth(RekapGlobalColumnPosition.PERSENTASE, 350)
    }
}