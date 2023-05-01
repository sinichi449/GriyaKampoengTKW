package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapGlobalBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutWarningAndLoadingRekapBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RekapGlobalColumnPosition
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RekapGlobalTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
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

        binding.layoutWarningLoading.btnLihatRingkasan.setOnClickListener {
            binding.layoutWarningLoading.layoutWarningRekap.visibility = View.GONE
            binding.layoutWarningLoading.layoutLoadingRekap.visibility = View.VISIBLE

            viewModel.getListRekapGlobal { failMsg ->
                Toast.makeText(requireContext().applicationContext, failMsg, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setupViewModel() {
        viewModel.rekapGlobalProgress.observe(requireActivity()) {
            if (it != null) {
                binding.layoutWarningLoading.setProgress(it)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRekapGlobalLoaded.collect { loaded ->
                    withContext(Dispatchers.Main) {
                        binding.layoutWarningLoading.root.visibility = if (loaded) View.GONE else View.VISIBLE
                        binding.tableRekapGlobal.visibility = if (loaded) View.VISIBLE else View.GONE
                    }
                }
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

    private fun LayoutWarningAndLoadingRekapBinding.setProgress(progressState: ProgressState) {
        linearprogressReport.progress = progressState.percent
        tvLoadingReport.text = progressState.message
    }
}