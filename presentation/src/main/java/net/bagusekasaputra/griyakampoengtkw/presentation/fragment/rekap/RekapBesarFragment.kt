package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.RekapDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapPengeluaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapUangMasukBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogPickCustomPeriodeBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapBesarBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
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

        binding.spinnerPeriode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val onFailure = { failMsg: String ->
                    Toast.makeText(requireContext().applicationContext, failMsg, Toast.LENGTH_LONG).show()
                }

                when (position) {
                    0 -> {}
                    1 -> viewModel.getRekapBesar(periode = PeriodeRekap.SEMUA, onFailure = onFailure)
                    2 -> viewModel.getRekapBesar(periode = PeriodeRekap.MINGGU_INI, onFailure = onFailure)
                    3 -> viewModel.getRekapBesar(periode = PeriodeRekap.BULAN_INI, onFailure = onFailure)
                    4 -> viewModel.getRekapBesar(periode = PeriodeRekap.TAHUN_INI, onFailure = onFailure)
                    5 -> showCustomPeriodePickerDialog()
                    else -> Toast.makeText(requireContext().applicationContext, "Spinner Position unreconizable!!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }

        }

        binding.cardPemasukan.layoutTotalUangMasuk.setOnClickListener {
            navigateToRekapDetail(RekapType.UangMasuk)
        }
    }

    private fun navigateToRekapDetail(rekapType: RekapType) {
        val intent = Intent(requireContext(), RekapDetailActivity::class.java)
        intent.putExtra("INTENT_REKAP_TYPE", rekapType.name)
        intent.putExtra("INTENT_REKAP_DATE_RANGE", viewModel.rangeTanggal.value ?: "-")
        requireActivity().startActivity(intent)
    }

    private fun showCustomPeriodePickerDialog() {
        val dialogBinding = DialogPickCustomPeriodeBinding.inflate(layoutInflater)

        val dialogView = MaterialAlertDialogBuilder(
            requireContext(),
            net.bagusekasaputra.griyakampoengtkw.presentation.R.style.AlertDialogTheme
        ).apply {
            setView(dialogBinding.root)
        }.create()

        dialogView.show()

        DatePickerHelper(
            ctx = requireContext(),
            triggerButton = dialogBinding.btnPilihStartTanggal,
            targetEdt = dialogBinding.edtStartTanggal,
        ).setupDateDefaultOrPick(false)

        DatePickerHelper(
            ctx = requireContext(),
            triggerButton = dialogBinding.btnPilihEndTanggal,
            targetEdt = dialogBinding.edtEndTanggal,
        ).setupDateDefaultOrPick(true)

        dialogBinding.btnLanjutkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtStartTanggal,
                dialogBinding.edtEndTanggal,
            )

            if (!isInvalidEdt) {
                val startDate = dialogBinding.edtStartTanggal.text.toString()
                    .toDate()
                val endDate = dialogBinding.edtEndTanggal.text.toString()
                    .toDate()

                viewModel.getRekapBesar(
                    periode = PeriodeRekap.CUSTOM,
                    startDate = startDate,
                    endDate = endDate,
                    onFailure = {
                        Toast.makeText(requireContext().applicationContext, it, Toast.LENGTH_LONG)
                            .show()
                    }
                )

                dialogView.dismiss()
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun setupViewModel() {
        val progressDialog = createProgressDialog()

        viewModel.rekapBesarProgress.observe(requireActivity()) {
            if (it != null) {
                progressDialog.progress = it.percent
                progressDialog.setMessage(it.message)
            }
        }

        viewModel.isRekapBesarLoaded.observe(requireActivity()) {
            if (it != null) {
                if (it) {
                    progressDialog.dismiss()

                    binding.scrollviewRekapBesar.alpha = 1.0f
                } else  {
                    progressDialog.show()

                    binding.scrollviewRekapBesar.alpha = 0.1f
                }
            }
        }

        viewModel.rangeTanggal.observe(requireActivity()) {
            if (it != null) {
                binding.tvRangePeriode.text = it
            }
        }

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

    private fun createProgressDialog(): ProgressDialog {
        val progressDialog = ProgressDialog(
            requireContext(),
            net.bagusekasaputra.griyakampoengtkw.presentation.R.style.AlertDialogTheme
        )
        progressDialog.apply {
            this.setTitle("Sedang merekap")
            this.setCancelable(false)
            this.max = 100
        }

        return progressDialog
    }

}