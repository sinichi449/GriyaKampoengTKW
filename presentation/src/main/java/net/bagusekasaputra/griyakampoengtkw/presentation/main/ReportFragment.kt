package net.bagusekasaputra.griyakampoengtkw.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentReportBinding

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private var isCollapsedDetailPengeluaran = true

    private val periodeReportList = listOf(
        "Minggu ini",
        "Bulan ini",
        "Tahun ini",
        "Custom"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinnerPeriode()

        binding.layoutDetailPengeluaran.setOnClickListener {
            if (isCollapsedDetailPengeluaran) {
                // Show
                TransitionManager.beginDelayedTransition(binding.cardDaftarPengeluaran, AutoTransition())
                binding.cardDaftarPengeluaran.visibility = View.VISIBLE
                binding.imgExpandDetailPengeluaran.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
                )

                isCollapsedDetailPengeluaran = false
            } else {
                // Hide
                TransitionManager.beginDelayedTransition(binding.cardDaftarPengeluaran, AutoTransition())
                binding.cardDaftarPengeluaran.visibility = View.GONE
                binding.imgExpandDetailPengeluaran.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_right_24)
                )

                isCollapsedDetailPengeluaran = true
            }
        }
    }

    private fun setupSpinnerPeriode() {
        binding.spinnerPeriode.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            periodeReportList
        )
    }
}