package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputPembayaranIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class FormInputPembayaranIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputPembayaranIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    private var currentKeyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentKeyId = arguments?.getString(FormActivity.EXTRAS_KEY_ID_INDEN_BOOKING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputPembayaranIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar title
        (requireActivity() as FormActivity).setFormTitle("Pembayaran (Inden Booking)")

        // Syncing pembayaran inden booking, needed for get next sequence termin
        syncPembayaranIndenBooking()

        with(binding) {
            // Add thousand comma separator to edtJumlahUangDibayar
            edtJumlahUangDibayar.addTextChangedListener(
                ThousandSeparatorTextWatcher(edtJumlahUangDibayar)
            )
            
            // Date picker
            DatePickerHelper(requireContext(), btnPilihTanggal, edtTanggal)
                .setupDateDefaultOrPick(true)
        }
        
        
        // Setup fab
        with((requireActivity() as FormActivity).getFabDone()) {
            // Hide fab on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.root, this)

            setOnClickListener {
                val isInvalidEdt = binding.run { 
                    InputUtil.isNullOrEmptyEditTexts(
                        edtTermin, edtTanggal, edtJumlahUangDibayar, 
                    )   
                }
                if (!isInvalidEdt) {
                    val termin = binding.edtTermin.text.toString().let { urutanTermin ->
                        val jenisTermin = if (binding.rbItj.isChecked) "ITJ"
                            else if (binding.rbDp.isChecked) "DP"
                            else if (binding.rbTermin.isChecked) "Termin"
                            else "NULL" // This is ridiculuously error :v

                        "$jenisTermin $urutanTermin"
                    }
                    val tanggal = binding.edtTanggal.text.toString()
                    val jumlahUangDibayar = binding.edtJumlahUangDibayar.text.toString()
                    val keteranganProgress = binding.edtKeteranganProgress.text.toString().let {
                        it.ifEmpty { "-" }
                    }

                    val pembayaran = Pembayaran(
                        termin = termin,
                        tanggal = tanggal,
                        jumlahUangDibayar = jumlahUangDibayar,
                        keterangan = keteranganProgress,
                        timeMillis = System.currentTimeMillis(),
                    )
                    val loadingSnackbar = Snackbar.make(binding.root, "Memproses data pembayaran, tunggu sebentar ...", Snackbar.LENGTH_INDEFINITE)
                    viewModel.insertPembayaran(
                        keyId = currentKeyId!!,
                        pembayaran = pembayaran,
                        onProgress = {
                            loadingSnackbar.show()
                        },
                        onSuccess = {
                            loadingSnackbar.dismiss()

                            FormUtil.sendResultAndExit(requireActivity(),
                                Activity.RESULT_OK, null
                            )
                        },
                        onFailure = {
                            loadingSnackbar.dismiss()

                            val failData = Intent()
                            failData.putExtra(FormActivity.EXTRAS_FAIL_MSG, it)
                            FormUtil.sendResultAndExit(requireActivity(),
                                Activity.RESULT_CANCELED, failData
                            )
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.pembayaranListIndenBooking.observe(requireActivity()) { pembayaranList ->
            with(binding) {
                arrayOf(rbItj, rbDp, rbTermin).forEach { terminRadioButton ->
                    terminRadioButton.setOnClickListener {
                        edtTermin.isEnabled = true
                        tilTermin.isEnabled = true

                        val jenisTermin = when(it.id) {
                            rbItj.id -> Pembayaran.JenisTermin.ITJ
                            rbDp.id -> Pembayaran.JenisTermin.DP
                            else -> Pembayaran.JenisTermin.TERMIN
                        }
                        val nextSequence = Pembayaran.nextPembayaranSequence(pembayaranList, jenisTermin)
                        edtTermin.setText(nextSequence)
                    }
                }
            }
        }
    }

    private fun syncPembayaranIndenBooking() {
        val progressSnackbar = Snackbar.make(binding.root, "Mendapatkan data pembayaran ...", Snackbar.LENGTH_INDEFINITE)
        viewModel.getAllPembayaran(
            keyId = currentKeyId!!,
            onProgress = {
                progressSnackbar.show()
                binding.root.alpha = 0.25f
            },
            onComplete = {
                progressSnackbar.dismiss()
                binding.root.alpha = 1f
            },
            onFailure = {
                progressSnackbar.dismiss()
                binding.root.alpha = 1f

                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }
}