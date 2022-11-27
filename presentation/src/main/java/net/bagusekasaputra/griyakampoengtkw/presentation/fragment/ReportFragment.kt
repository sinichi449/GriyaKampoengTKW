package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
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
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ReportViewModel

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private val reportViewModel: ReportViewModel by viewModels()

    private var collapsedRekap = false
    private var collapsedTabel = true

    private val periodeReportList = listOf(
        "Pilih Periode",
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
            reportViewModel.reportKavlingRefreshed.value = false

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
                val semuaPeriode = 1
                val mingguIni = 2
                val bulanIni = 3
                val tahunIni = 4

                when (spinnerPosition) {
                    semuaPeriode -> {
                        reportViewModel.getRekapSemuaPeriode()
                    }
                    mingguIni -> {
                        reportViewModel.getRekapMingguIni()
                    }
                    bulanIni -> {
                        reportViewModel.getRekapBulanIni()
                    }
                    tahunIni -> {
                        reportViewModel.getRekapTahunIni()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnLihatRingkasan.setOnClickListener {
            binding.btnLihatRingkasan.visibility = View.GONE
            binding.progressBarReport.visibility = View.VISIBLE
            binding.tvInfoLoadingReport.visibility = View.VISIBLE

            syncData()
        }
    }


    private fun syncData() {
        reportViewModel.getAllReportKavling {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupViewModel() {
        reportViewModel.isFinishOperation.observe(requireActivity()) { finished ->
            if (finished != null) {
                binding.swipeRefreshReport.isRefreshing = finished.not()
            }
        }

        reportViewModel.isFinishedFetchingReport.observe(requireActivity()) { fetched ->
            if (fetched != null) {
                if (fetched) {
                    binding.layoutLoadingReport.visibility = View.GONE
                    binding.nestedScrollReport.visibility = View.VISIBLE

                    val snackbarCompletion = Snackbar.make(binding.root, "Memuat data berhasil. Silakan pilih periode.", Snackbar.LENGTH_LONG)
                    snackbarCompletion.setAction("OK") {
                        snackbarCompletion.dismiss()
                    }

                    snackbarCompletion.show()
                } else {
                    binding.layoutLoadingReport.visibility = View.VISIBLE
                    binding.nestedScrollReport.visibility = View.GONE
                }
            }
        }

        reportViewModel.rangeTanggalLive.observe(requireActivity()) {
            if (it != null) {
                binding.tvRangePeriode.text = it
            }
        }

        reportViewModel.listReportTotalUangMasukLive.observe(requireActivity()) {
            if (it != null) {
                binding.tvRekapBesar.text = reportViewModel.getOverallTotalCuan()
                binding.tvRekapTotalUangMasuk.text = reportViewModel.getOverallTotalMasuk()
                binding.tvTotalPengeluaran.text = reportViewModel.getOverallTotalPengeluaran()
            } else {
                binding.tvRekapBesar.text = "-"
                binding.tvRekapTotalUangMasuk.text = "-"
                binding.tvTotalPengeluaran.text = "-"
            }

            val columnHeaders = reportViewModel.getTotalUangMasukColumnHeaders()
            val rowHeaders = reportViewModel.getTotalUangMasukRowHeaders()
            val cellItems = reportViewModel.getTotalUangMasukCellItems()

            setupTumTableView(columnHeaders, rowHeaders, cellItems)
        }
    }

    private fun setupSpinnerPeriode() {
        binding.spinnerPeriode.adapter = ArrayAdapter(
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
        reportViewModel.isFinishOperation.value = false

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.layoutFullTabel.visibility = View.VISIBLE

                binding.imgArrowBukaTabel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24))
                collapsedTabel = false
                binding.tvInfoBukaTabel.text = "Tutup Tabel"

                reportViewModel.isFinishOperation.postValue(true)
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