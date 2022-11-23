package net.bagusekasaputra.griyakampoengtkw.presentation.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentReportBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TotalUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumRowHeaders

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private val detailViewModel: DetailViewModel by viewModels()

    private var isCollapsedDetailUangMasuk = true

    private val periodeReportList = listOf(
        "Minggu ini",
        "Bulan ini",
        "Tahun ini",
        "Custom"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        setupSpinnerPeriode()

        binding.swipeRefreshReport.setOnRefreshListener {
            syncData()
        }

        binding.cardTotalUangMasuk.setOnClickListener {
            if (isCollapsedDetailUangMasuk) {
                // Show
                TransitionManager.beginDelayedTransition(binding.layoutTotalUangMasuk, AutoTransition())
                binding.tableRekapTotalUangMasuk.visibility = View.VISIBLE

                isCollapsedDetailUangMasuk = false
            } else {
                // Hide
                TransitionManager.beginDelayedTransition(binding.layoutTotalUangMasuk, AutoTransition())
                binding.tableRekapTotalUangMasuk.visibility = View.GONE

                isCollapsedDetailUangMasuk = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        syncData()
    }

    private fun syncData() {
        detailViewModel.provideReportUangMasuk()
    }

    private fun setupViewModel() {
        detailViewModel.isFinishOperation.observe(requireActivity()) { finished ->
            finished?.let {
                binding.swipeRefreshReport.isRefreshing = it.not()
            }
        }

        detailViewModel.listReportTotalUangMasukLive.observe(requireActivity()) {
            if (it != null) {
                val overallTotalUangMasuk = "Rp. ${detailViewModel.getOverallTotalMasuk()}"
                binding.tvRekapTotalUangMasuk.text = overallTotalUangMasuk
            }

            val columnHeaders = detailViewModel.getTotalUangMasukColumnHeaders()
            val rowHeaders = detailViewModel.getTotalUangMasukRowHeaders()
            val cellItems = detailViewModel.getTotalUangMasukCellItems()

            setupTumTableView(columnHeaders, rowHeaders, cellItems)
        }
    }

    private fun setupSpinnerPeriode() {
        binding.spinnerPeriode.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            periodeReportList
        )
    }

    private fun setupTumTableView(
        columnHeaders: List<TumColumnHeaders>,
        rowHeaders: List<TumRowHeaders>,
        cellItems: List<List<TumCell>>,
    ) {
        val tumTableAdapter = TotalUangMasukTableViewAdapter()

        binding.tableRekapTotalUangMasuk.setAdapter(tumTableAdapter)

        tumTableAdapter.apply {
            setAllItems(columnHeaders, rowHeaders, cellItems)
            notifyDataSetChanged()
        }

        binding.tableRekapTotalUangMasuk.setColumnWidth(0, 300)
        binding.tableRekapTotalUangMasuk.setColumnWidth(1, 500)
    }


}