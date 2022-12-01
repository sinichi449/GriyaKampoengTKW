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
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TotalUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumRowHeaders
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

        binding.swipeRefreshRekap.setOnRefreshListener {
            hideWarning()

            sync()
        }

        binding.btnLihatRingkasan.setOnClickListener {
            hideWarning()

            sync()
        }
    }

    private fun sync() {
        viewModel.getAllRekapGlobal {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.isFinishedOperation.observe(requireActivity()) { finished ->
            if (finished != null) {
                binding.swipeRefreshRekap.isRefreshing = finished.not()
            }
        }

        viewModel.isLoadingRekapDone.observe(requireActivity()) { finished ->
            if (finished != null) {
                if (finished) onCompletedView() else onLoadingView()
            }
        }

        viewModel.progressState.observe(requireActivity()) { progressState ->
            if (progressState != null) {
                binding.linearprogressReport.progress = progressState.percent
                binding.tvInfoLoadingReport.text = progressState.message
            }
        }

        viewModel.listRekapGlobalLive.observe(requireActivity()) {
            val listColumnHeaders = viewModel.getColumnHeaderRekapTable()
            val listRowHeaders = viewModel.getRowHeaderRekapTable()
            val listCellItems = viewModel.getListCellsRekapTable()

            setupRekapTableView(listColumnHeaders, listRowHeaders, listCellItems)
        }
    }

    private fun setupRekapTableView(
        columnHeaders: List<TumColumnHeaders>,
        rowHeaders: List<TumRowHeaders>,
        cellItems: List<List<TumCell>>,
    ) {
        val tumTableAdapter = TotalUangMasukTableViewAdapter()

        binding.tableRekapGlobal.setAdapter(tumTableAdapter)

        tumTableAdapter.apply {
            setAllItems(columnHeaders, rowHeaders, cellItems)
            notifyDataSetChanged()
        }

        binding.tableRekapGlobal.setColumnWidth(2, 250) // Harga

    }

    private fun onLoadingView() {
        binding.linearprogressReport.visibility = View.VISIBLE
        binding.tvInfoLoadingReport.visibility = View.VISIBLE

        binding.layoutRekapGlobal.visibility = View.GONE
    }

    private fun onCompletedView() {
        binding.linearprogressReport.visibility = View.GONE
        binding.tvInfoLoadingReport.visibility = View.GONE

        binding.layoutRekapGlobal.visibility = View.VISIBLE
    }

    private fun hideWarning() {
        binding.tvInfoWarningLihatRingkasan.visibility = View.GONE
        binding.btnLihatRingkasan.visibility = View.GONE
    }

}