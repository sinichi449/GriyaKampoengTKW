package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentReportBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TotalUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumRowHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DetailViewModel

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private val detailViewModel: DetailViewModel by viewModels()

    private var collapsedRekap = false
    private var collapsedTabel = true

    private val periodeReportList = listOf(
        "Semua",
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

        // Rekap besar card when clicked either expand/dismiss
        binding.layoutRekapBesar.setOnClickListener {
            if (collapsedRekap) {
                // Show
                showExpandableRekap()
                collapsedRekap = false
            } else {
                // Hide
                hideExpandableRekap()
                collapsedRekap = true
            }
        }

        // Table card when clicked either expand/dismiss
        binding.layoutBukaTabel.setOnClickListener {
            if (collapsedTabel) {
                // Show
                showExpandableTumCard()
            } else {
                // Hide
                hideExpandableTumCard()
            }
        }

        // On sembunyikan table card click
        binding.btnTableTotalUangMasukSembunyikan.setOnClickListener {
            hideExpandableTumCard()
        }

        binding.spinnerPeriode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, spinnerPosition: Int, p3: Long) {
                val semuaPeriode = 0
                val mingguIni = 1
                val bulanIni = 2
                val tahunIni = 3

                when (spinnerPosition) {
//                    semuaPeriode -> { detailViewModel.getRekapSemuaPeriode() }
//                    mingguIni -> { detailViewModel.getRekapMingguIni() }
//                    bulanIni -> { detailViewModel.getRekapBulanIni() }
//                    tahunIni -> { detailViewModel.getRekapTahunIni() }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }



    override fun onResume() {
        super.onResume()

        syncData()
    }

    private fun syncData() {

    }

    private fun setupViewModel() {
        detailViewModel.isFinishOperation.observe(requireActivity()) { finished ->
            finished?.let {
                binding.swipeRefreshReport.isRefreshing = it.not()
            }
        }

        detailViewModel.listReportTotalUangMasukLive.observe(requireActivity()) {
            if (it != null) {
                binding.tvRekapBesar.text = detailViewModel.getOverallTotalCuan()
                binding.tvRekapTotalUangMasuk.text = detailViewModel.getOverallTotalMasuk()
                binding.tvTotalPengeluaran.text = detailViewModel.getOverallTotalPengeluaran()
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

        (0..3).forEach {
            binding.tableRekapTotalUangMasuk.setColumnWidth(it, 350)
        }
    }


    /**
     * Expandable UI elements
     */
    private fun showExpandableRekap() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        binding.cardRekap.visibility = View.VISIBLE
        binding.imgArrowBukaRekapBesar.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24))
    }

    private fun hideExpandableRekap() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        binding.cardRekap.visibility = View.GONE
        binding.imgArrowBukaRekapBesar.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_right_24))
    }

    private fun showExpandableTumCard() {
        detailViewModel.isFinishOperation.value = false

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.layoutFullTabel.visibility = View.VISIBLE

                binding.imgArrowBukaTabel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24))
                collapsedTabel = false
                binding.tvInfoBukaTabel.text = "Tutup Tabel"

                detailViewModel.isFinishOperation.postValue(true)
            }
        }
    }

    private fun hideExpandableTumCard() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                TransitionManager.beginDelayedTransition(binding.layoutExpandableTabel, AutoTransition())
                binding.imgArrowBukaTabel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_right_24))
                binding.layoutFullTabel.visibility = View.GONE

                collapsedTabel = true

                binding.tvInfoBukaTabel.text = "Buka Tabel"
            }
        }
    }

}