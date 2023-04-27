package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.UnmigratedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapPengeluaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.CardRekapUangMasukBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogPickCustomPeriodeBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapBesarBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel
import javax.inject.Inject

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
    @Inject
    lateinit var sharedPrefs: SharedPreferences

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


        // Only show checkbox include data lama Rekap on Data Lama Mode
        val isDataLamaMode = sharedPrefs.getString("dataLamaPath", null) != null
        if (isDataLamaMode) {
            binding.checkboxIncludeDataLama?.visibility = View.VISIBLE
            binding.checkboxIncludeDataLama?.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    showIncludeKavlingDataLamaDialog()
                } else {
                    viewModel.setListDataLamaRekapBesarIncluded(emptyList())
                }
            }
        } else {
            binding.checkboxIncludeDataLama?.visibility = View.GONE
        }

        binding.spinnerPeriode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val onFailure = { failMsg: String ->
                    Toast.makeText(requireContext().applicationContext, failMsg, Toast.LENGTH_LONG).show()
                }

                when (position) {
                    0 -> {}
                    1 -> viewModel.getRekapBesarOverview(periode = PeriodeRekap.SEMUA, onFailure = onFailure)
                    2 -> viewModel.getRekapBesarOverview(periode = PeriodeRekap.MINGGU_INI, onFailure = onFailure)
                    3 -> viewModel.getRekapBesarOverview(periode = PeriodeRekap.BULAN_INI, onFailure = onFailure)
                    4 -> viewModel.getRekapBesarOverview(periode = PeriodeRekap.TAHUN_INI, onFailure = onFailure)
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

        binding.cardPemasukan.layoutSisaBelumBayar.setOnClickListener {
            navigateToRekapDetail(RekapType.SisaPembayaran)
        }

        binding.cardPengeluaran.layoutFeeMarketing.setOnClickListener {
            navigateToRekapDetail(RekapType.FeeMarketing)
        }

        binding.cardPengeluaran.layoutBiayaMarketing.setOnClickListener {
            navigateToRekapDetail(RekapType.BiayaMarketing)
        }

        binding.cardPengeluaran.layoutBiayaLain.setOnClickListener {
            navigateToRekapDetail(RekapType.BiayaLain)
        }
    }

    private fun navigateToRekapDetail(rekapType: RekapType) {
        viewModel.setRekapTypeDetailTransport(rekapType)

        val intent = Intent(requireContext(), RekapBesarDetailActivity::class.java).apply {
            putExtra(RekapBesarDetailActivity.EXTRAS_REKAP_DETAIL_TRANSPORT, viewModel.rekapDetailTransportLive.value)
        }

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

                viewModel.getRekapBesarOverview(
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
            it?.also { progressMessage ->
                progressDialog.setMessage(progressMessage)
            }
        }
        viewModel.isRekapBesarOverviewLoaded.observe(requireActivity()) {
            if (it != null) {
                if (it) {
                    progressDialog.dismiss()

                    binding.scrollviewRekapBesar.alpha = 1.0f

                    // Release observer on progress done

                } else  {
                    progressDialog.show()

                    binding.scrollviewRekapBesar.alpha = 0.1f
                }
            }
        }
        viewModel.rekapDetailTransportLive.observe(requireActivity()) {
            if (it != null) {
                binding.tvRangePeriode.text = it.getRangeTanggal()
            }
        }

        viewModel.rekapBesarOverviewLive.observe(requireActivity()) {
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

        viewModel.listKavlingDataLamaRekapBesarIncluded.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) {
                binding.tvIncludedKavlingDataLama?.visibility = View.VISIBLE
                binding.tvIncludedKavlingDataLama?.text = it.run {
                    val text = StringBuilder()

                    this.forEachIndexed { index, str ->
                        if (index == lastIndex) {
                            text.append(str)
                        } else {
                            text.append("$str, ")
                        }
                    }

                    text.toString()
                }
            } else {
                binding.tvIncludedKavlingDataLama?.visibility = View.GONE
            }
        }
    }

    private fun setupSpinnerPeriode() {
        binding.spinnerPeriode.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
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
            setTitle("Sedang merekap")
            setCancelable(false)
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                viewModel.gettingRekapBesarJob?.cancel()

                dialog.dismiss()
            }
        }

        return progressDialog
    }

    private fun showIncludeKavlingDataLamaDialog() {
        fun showDialog(listUnmigratedKavling: List<UnmigratedKavling>) {
            val listKavlingDataLama = mutableListOf<String>().apply {
                listUnmigratedKavling.forEach {
                    add("${it.kavlingKode} (${it.namaCostumer})")
                }
            }.toTypedArray()
            val checkedKavling = mutableListOf<Boolean>().apply {
                repeat(listKavlingDataLama.size) {
                    add(true)
                }
            }
                .toBooleanArray()

            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("List Kavling Not-Migrated")
                setCancelable(false)
                setMultiChoiceItems(listKavlingDataLama, checkedKavling) { _, which, checked ->
                    checkedKavling[which] = checked
                }
                setPositiveButton("OK") { dialog, _ ->
                    val listCheckedKavlingDataLama = mutableListOf<String>().apply {
                        checkedKavling.forEachIndexed { index, isChecked ->
                            if (isChecked) {
                                add(listUnmigratedKavling[index].kavlingKode)
                            }
                        }
                    }

                    viewModel.setListDataLamaRekapBesarIncluded(listCheckedKavlingDataLama)

                    dialog.dismiss()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    viewModel.setListDataLamaRekapBesarIncluded(emptyList())

                    dialog.dismiss()
                }
                setOnDismissListener {
                    val listCheckedKavlingDataLama = viewModel.listKavlingDataLamaRekapBesarIncluded.value

                    if (listCheckedKavlingDataLama.isNullOrEmpty()) {
                        uncheckIncludeDataLamaCheckbox()
                        Toast.makeText(requireContext(), "None are selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
        }

        val progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Tunggu sebentar...")
            setMessage("Mendapatkan data Kavling not-migrated")
            setCancelable(false)
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                viewModel.kavlingLamaRekapBesarJob?.cancel()

                dialog.dismiss()
            }
        }

        viewModel.getListKavlingDataLama(
            onProgress = {
                progressDialog.show()
            },
            onSuccess = {
                progressDialog.dismiss()

                if (!it.isNullOrEmpty()) {
                    showDialog(it)
                }
            },
            onFailure = {
                progressDialog.dismiss()

                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun uncheckIncludeDataLamaCheckbox() {
        binding.checkboxIncludeDataLama?.isChecked = false
    }
}