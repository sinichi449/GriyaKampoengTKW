package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapPengeluaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapUangMasukBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapBesarBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapBesarFragment : Fragment() {

    private lateinit var binding: FragmentRekapBesarBinding
    private val viewModel: RekapViewModel by activityViewModels()

    private val periodeRekapList = listOf(
        "Pilih Periode",
        "Semua",
        "Minggu ini",
        "Bulan ini",
        "Tahun ini",
        "Custom"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapBesarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        setupSpinnerPeriode()
    }

    private fun setupViewModel() {
        viewModel.rekapBesarLive.observe(requireActivity()) {
            if (it != null) {
                val sisaUang = "Rp ${it.parsedSisaUang}"
                binding.tvSisaUang.text = sisaUang

                binding.cardPemasukan.setAllItems(
                    totalUangMasuk = it.totalUangMasuk,
                    sisaBelumBayar = it.totalSisaBelumBayar,
                )

                binding.cardPengeluaran.setAllItems(
                    feeMarketing = it.totalFeeMarketing,
                    biayaMarketing = it.totalBiayaMarketing,
                    biayaLain = it.totalBiayaLain,
                )
            }
        }
    }

    private fun setupSpinnerPeriode() {
        binding.spinnerPeriode.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            periodeRekapList
        )
    }

    private fun CardRekapUangMasukBinding.setAllItems(
        totalUangMasuk: Long,
        sisaBelumBayar: Long,
    ) {
        this.apply {
            tvTotalUangMasuk.text = NumberUtil.formatLongToString(totalUangMasuk)
            tvSisaBelumBayar.text = NumberUtil.formatLongToString(sisaBelumBayar)
        }
    }

    private fun CardRekapPengeluaranBinding.setAllItems(
        feeMarketing: Long,
        biayaMarketing: Long,
        biayaLain: Long,
    ) {
        this.apply {
            tvFeeMarketing.text = NumberUtil.formatLongToString(feeMarketing)
            tvBiayaMarketing.text = NumberUtil.formatLongToString(biayaMarketing)
            tvBiayaLain.text = NumberUtil.formatLongToString(biayaLain)
        }
    }

}