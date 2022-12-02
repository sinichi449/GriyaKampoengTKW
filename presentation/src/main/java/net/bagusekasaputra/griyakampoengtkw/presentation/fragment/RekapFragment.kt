package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.*
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapFragment : Fragment() {

    private lateinit var binding: FragmentRekapBinding
    private val viewModel by viewModels<RekapViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        binding.swipeRefreshRekap.isEnabled = false

        binding.layoutWarningAndLoadingRekap.btnLihatRingkasan.setOnClickListener {
            onLoadingView()

            sync()
        }
    }

    private fun onLoadingView() {
        binding.layoutWarningAndLoadingRekap.layoutWarningRekap.visibility = View.GONE
        binding.layoutWarningAndLoadingRekap.layoutLoadingRekap.visibility = View.VISIBLE

        binding.tableRekapGlobal.visibility = View.GONE
    }

    private fun onCompletedView() {
        binding.layoutWarningAndLoadingRekap.root.visibility = View.GONE

        binding.tableRekapGlobal.visibility = View.VISIBLE
    }

    private fun sync() {
        viewModel.getAllRekapGlobal {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.progressState.observe(requireActivity()) {
            if (it != null) {
                binding.layoutWarningAndLoadingRekap.linearprogressReport.progress = it.percent
                binding.layoutWarningAndLoadingRekap.tvLoadingReport.text = it.message
            }
        }

        viewModel.isLoadingRekapDone.observe(requireActivity()) { done ->
            if (done != null) {
                if (done) onCompletedView()
            }
        }

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