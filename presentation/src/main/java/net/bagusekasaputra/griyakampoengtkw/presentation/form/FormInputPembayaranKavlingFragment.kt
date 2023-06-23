package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.bulanListBahasaIndo
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertFormPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateFormPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormInputViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.util.Calendar
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputPembayaranKavlingBinding as FormBinding

@AndroidEntryPoint
class FormInputPembayaranKavlingFragment : Fragment() {

    private lateinit var binding: FormBinding
    private val pembayaranViewModel by activityViewModels<FormPembayaranViewModel>()
    private val formViewModel by activityViewModels<FormInputViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.also { bundle ->
            val parcelable: Parcelable? = BundleCompat.getParcelable(
                bundle, FormActivity.EXTRAS_PARCEL, Parcelable::class.java
            )
            when (parcelable) {
                is InsertFormPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.currentTermin = null

                    pembayaranViewModel.formIsEditMode = false
                }
                is UpdateFormPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.currentTermin = parcelable.termin

                    pembayaranViewModel.formIsEditMode = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formActivity = (requireActivity() as FormActivity)

        // Setup toolbar title and subtitle
        with(formActivity.getToolbar()) {
            if (pembayaranViewModel.formIsEditMode) {
                title = "Ubah Pembayaran"
                subtitle = "Kav. ${pembayaranViewModel.currentKavlingKode} - ${pembayaranViewModel.currentTermin}"
            } else {
                title = "Tambahkan Pembayaran"
                subtitle = "Kav. ${pembayaranViewModel.currentKavlingKode}"
            }
        }


        with(binding) {
            edtJumlahUangDibayar.addThousandTextListener()

            val datePickerHelper = DatePickerHelper(requireContext(), btnPilihTanggal, edtTanggal)

            if (pembayaranViewModel.formIsEditMode) {
                datePickerHelper.setupDateDefaultOrPick(false)

                pembayaranViewModel.getSinglePembayaran(
                    kavling = pembayaranViewModel.currentKavlingKode!!,
                    termin = pembayaranViewModel.currentTermin!!,
                )

                setupViewModelForEditMode()
            } else {
                datePickerHelper.setupDateDefaultOrPick(true)

                // Sync pembayaran, needed to get next sequence termin if not EditMode
                pembayaranViewModel.fetchPembayaranData(pembayaranViewModel.currentKavlingKode!!)

                setupViewModel()
            }

            formActivity.getFabDone().setOnClickListener {
                val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                    edtTermin, edtTanggal, edtJumlahUangDibayar
                )

                if (!isInvalidEdt) {
                    val jenisTermin = rgJenisPembayaran.getSelectedJenisTermin()
                    val urutanTermin = edtTermin.text.toString().toInt()
                    val tanggalPembayaran = edtTanggal.text.toString()
                    val jumlahUangDibayar = edtJumlahUangDibayar.text.toString()
                    val keteranganProgress = edtKeteranganProgress.text.toString().ifBlank { "-" }
                    val bulanInvoice = spinnerBulanAngsuran.selectedItemPosition + 1
                    val tahunInvoice = spinnerTahunAngsuran.selectedItem.toString().toInt()

                    val pembayaran = Pembayaran(
                        termin = "$jenisTermin $urutanTermin",
                        tanggal = tanggalPembayaran,
                        jumlahUangDibayar = jumlahUangDibayar,
                        keterangan = keteranganProgress,
                        timeMillis = System.currentTimeMillis(),
                        bulanAngsuran = BulanAngsuran(bulanInvoice, tahunInvoice)
                    )
                    if (pembayaranViewModel.formIsEditMode) {
                        val oldPembayaran = pembayaranViewModel.pembayaran.value.run {
                            (this as UiState.Success).data
                        }!!

                        pembayaranViewModel.updatePembayaran(
                            kavlingKode = pembayaranViewModel.currentKavlingKode!!,
                            oldPembayaran = oldPembayaran,
                            newPembayaran = pembayaran,
                        )
                    } else {
                        pembayaranViewModel.addPembayaran(
                            kavlingKode = pembayaranViewModel.currentKavlingKode!!,
                            pembayaran = pembayaran
                        )
                    }

                    disableForms(formActivity.getFabDone())
                } else {
                    Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        with(binding) {
            pembayaranViewModel.pembayaranList.observe(requireActivity()) {
                it?.also { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            onLoading(true)
                        }
                        is UiState.Success -> {
                            val pembayaranList = uiState.data ?: emptyList()

                            onLoading(false)

                            listenForJenisPembayaran(pembayaranList, edtTermin)

                            setupPembayaranOverdueInvoice(null)
                        }
                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    pembayaranViewModel.insertPembayaranOperation.collect {
                        it?.also { uiState ->
                            when (uiState) {
                                is UiState.Loading -> {
                                    Snackbar.make(root, "Memproses data pembayaran ...", Snackbar.LENGTH_INDEFINITE)
                                        .show()
                                }
                                is UiState.Success -> {
                                    FormUtil.sendResultAndExit(
                                        requireActivity(), Activity.RESULT_OK, null
                                    )
                                }
                                is UiState.Failure -> {
                                    val resultIntent = Intent()
                                    resultIntent.putExtra(FormActivity.EXTRAS_FAIL_MSG, uiState.failMsg)

                                    FormUtil.sendResultAndExit(
                                        requireActivity(), Activity.RESULT_CANCELED, resultIntent,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModelForEditMode() {
        with(binding) {
            pembayaranViewModel.pembayaran.observe(requireActivity()) {
                it?.also { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            onLoading(true)
                        }
                        is UiState.Success -> {
                            val pembayaran = uiState.data

                            if (pembayaran != null) {
                                onLoading(false)

                                rgJenisPembayaran.setCheckedJenisTermin(pembayaran.getJenisTermin())
                                disableAllJenisPembayaranRadioButtons()

                                edtTermin.setText(pembayaran.getUrutan().toString())
                                edtTermin.isEnabled = false

                                edtTanggal.setText(pembayaran.tanggal)

                                edtJumlahUangDibayar.setText(pembayaran.jumlahUangDibayar)

                                edtKeteranganProgress.setText(pembayaran.keterangan)

                                setupPembayaranOverdueInvoice(pembayaran.bulanAngsuran)
                            } else {
                                Toast.makeText(requireContext(), "Pembayaran tidak ditemukan!", Toast.LENGTH_LONG).show()
                            }
                        }
                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            pembayaranViewModel.ubahPembayaranOperation.observe(requireActivity()) {
                it?.also { uiState ->
                    val progressSnackbar = Snackbar.make(root, "Memproses perubahan data ..", Snackbar.LENGTH_INDEFINITE)
                    val formActivity = (requireActivity() as FormActivity)
                    val dataToSend = Intent()

                    when (uiState) {
                        is UiState.Loading -> {
                            progressSnackbar.show()
                        }
                        is UiState.Success -> {
                            dataToSend.putExtra(FormActivity.EXTRAS_SUCCESS_DATA, "Berhasil mengubah pembayaran ${pembayaranViewModel.currentTermin}!")

                            FormUtil.sendResultAndExit(formActivity, Activity.RESULT_OK, dataToSend)
                        }
                        is UiState.Failure -> {
                            dataToSend.putExtra(FormActivity.EXTRAS_FAIL_MSG, uiState.failMsg)

                            FormUtil.sendResultAndExit(formActivity, Activity.RESULT_CANCELED, dataToSend)
                        }
                    }
                }
            }
        }
    }

    private fun RadioGroup.setCheckedJenisTermin(jenisTermin: String) {
        with(binding) {
            val checkedTermin = when (jenisTermin) {
                Pembayaran.JenisTermin.ITJ.text -> rbItj.id
                Pembayaran.JenisTermin.DP.text -> rbDp.id
                else -> rbTermin.id
            }

            check(checkedTermin)
        }
    }

    private fun disableAllJenisPembayaranRadioButtons() {
        with(binding) {
            rbItj.isEnabled = false
            rbDp.isEnabled = false
            rbTermin.isEnabled = false
        }
    }

    private fun RadioGroup.getSelectedJenisTermin(): String {
        return with(binding) {
            when (checkedRadioButtonId) {
                rbItj.id -> Pembayaran.JenisTermin.ITJ.text
                rbDp.id -> Pembayaran.JenisTermin.DP.text
                rbTermin.id -> Pembayaran.JenisTermin.TERMIN.text
                else -> Pembayaran.JenisTermin.TERMIN.text
            }
        }
    }

    private fun listenForJenisPembayaran(
        pembayaranList: List<Pembayaran>,
        edtTermin: TextInputEditText,
    ) {
        with(binding) {
            rbItj.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisTermin.ITJ)
            }
            rbDp.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisTermin.DP)
            }
            rbTermin.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisTermin.TERMIN)
            }
        }

        // Listen for radio button jenis termin changes
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                formViewModel.pembayaranSelectedJenisTermin.collect {
                    withContext(Dispatchers.Main) {
                        if (it != null) {
                            val nextSequencePembayaran = Pembayaran.nextPembayaranSequence(
                                pembayaranList, it
                            )

                            edtTermin.isEnabled = true
                            edtTermin.setText(nextSequencePembayaran)
                        } else {
                            edtTermin.isEnabled = false
                        }
                    }
                }
            }
        }
    }

    private fun FormBinding.onLoading(
        isLoading: Boolean,
        loadingText: String? = null
    ) {
        layoutLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        layoutContent.visibility = if (isLoading) View.GONE else View.VISIBLE

        tvLoadingMessage.text = if (!loadingText.isNullOrEmpty())
            loadingText else "Memverifikasi data pembayaran ..."
    }

    /**
     * Prevent user from changing data from either radio button or edittext
     */
    private fun FormBinding.disableForms(fabAction: FloatingActionButton) {
        onLoading(true, "Memproses data ...")

        fabAction.hide()
    }

    private fun FormBinding.setupPembayaranOverdueInvoice(bulanAngsuran: BulanAngsuran?) {
        cardPembayaranOverdueInvoice.setOnClickListener {
            val pembayaranUntukBulanSekarang = formViewModel.pembayaranUntukBulanSekarang.value

            formViewModel.setPembayaranUntukBulanSekarang(!pembayaranUntukBulanSekarang)
        }

        checkBoxPembayaranUntukBulanSekarang.setOnCheckedChangeListener { _, isChecked ->
            formViewModel.setPembayaranUntukBulanSekarang(isChecked)
        }

        if (bulanAngsuran != null) {
            formViewModel.setPembayaranUntukBulanSekarang(false)

            spinnerBulanAngsuran.setupBulanAngsuran(bulanAngsuran.bulan)
            spinnerTahunAngsuran.setupTahunAngsuran(bulanAngsuran.tahun)
        } else {
            // if bulan angsuran not given,
            // set default checkBoxPembayaranUntukBulanSekarang to true
            formViewModel.setPembayaranUntukBulanSekarang(true)

            spinnerBulanAngsuran.setupBulanAngsuran()
            spinnerTahunAngsuran.setupTahunAngsuran()
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                formViewModel.pembayaranUntukBulanSekarang.collect { untukBulanSekarang ->
                    if (untukBulanSekarang) {
                        layoutPembayaranUntukBulanSekarang.visibility = View.VISIBLE
                        layoutPembayaranUntukCustom.visibility = View.GONE
                    } else {
                        layoutPembayaranUntukBulanSekarang.visibility = View.GONE
                        layoutPembayaranUntukCustom.visibility = View.VISIBLE
                    }

                    checkBoxPembayaranUntukBulanSekarang.isChecked = untukBulanSekarang
                }
            }
        }
    }

    private fun Spinner.setupBulanAngsuran(selectedBulan: Int? = null) {
        val bulanList = bulanListBahasaIndo()
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            bulanList
        )

        if (selectedBulan != null) {
            // Lists are starting from zero, while selectedBulan corresponds to actual month number.
            setSelection(selectedBulan - 1)
        } else {
            // Default to bulan sekarang
            val bulanSekarang = Calendar.getInstance()
                .get(Calendar.MONTH)

            setSelection(bulanSekarang)
        }
    }

    private fun Spinner.setupTahunAngsuran(selectedTahun: Int?  = null) {
        val tahunList = DateUtil.tahunListOf()
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            tahunList,
        )

        val spinnerPosition: Int
        if (selectedTahun != null) {
            spinnerPosition = tahunList.indexOf(selectedTahun.toString())

            setSelection(spinnerPosition)
        } else {
            // Default to tahun sekarang
            val tahunSekarang = Calendar.getInstance()
                .get(Calendar.YEAR)
            spinnerPosition = tahunList.indexOf(tahunSekarang.toString())

            setSelection(spinnerPosition)
        }
    }
}