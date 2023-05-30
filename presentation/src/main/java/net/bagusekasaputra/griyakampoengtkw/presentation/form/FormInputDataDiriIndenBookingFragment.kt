package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputDataDiriIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.Consts
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class FormInputDataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputDataDiriIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()
    private var keyId: String? = null
    private var isEditMode = false

    // Needed to setup edit mode
    private lateinit var spinnerNegaraBekerjaAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keyId = arguments?.getString(FormActivity.EXTRAS_KEY_ID_DATA_DIRI_INDEN_BOOKING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputDataDiriIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set toolbar title
        (requireActivity() as FormActivity).setFormTitle("Data Diri (Inden Booking)")

        // Set negara bekerja spinner
        spinnerNegaraBekerjaAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Consts.negaraBekerjaList,
        )
        binding.spinnerNegaraBekerja.adapter = spinnerNegaraBekerjaAdapter

        // Set appropriate input type for each Jenis Identitas
        binding.rbIdKtp.setOnClickListener {
            binding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        }
        binding.rbIdPassport.setOnClickListener {
            binding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }

        // Setup edit mode
        if (!keyId.isNullOrEmpty()) {
            isEditMode = true
            setupEditMode(keyId!!)
        }

        // On done click
        (requireActivity() as FormActivity).getFabDone().setOnClickListener {
            val isInvalidInput = InputUtil.isNullOrEmptyEditTexts(
                binding.edtNamaCostumer,
                binding.edtNoIdentitas,
                binding.edtAlamatKerja,
                binding.edtAlamatIndo,
                binding.edtNoHandphone,
            )

            if (!isInvalidInput) {
                val namaCostumer = binding.edtNamaCostumer.text.toString()
                val jenisIdentitas = if (binding.rbIdKtp.isChecked) "KTP" else "Passport"
                val noIdentitas = binding.edtNoIdentitas.text.toString()
                val negaraBekerja = binding.spinnerNegaraBekerja.selectedItem.toString()
                val alamatKerja = binding.edtAlamatKerja.text.toString()
                val alamatIndo = binding.edtAlamatIndo.text.toString()
                val noHp = binding.edtNoHandphone.text.toString()

                val dataDiri = DataDiri(
                    nama = namaCostumer,
                    jenisIdentitas = jenisIdentitas,
                    noIdentitas = noIdentitas,
                    negaraBekerja = negaraBekerja,
                    alamatKerja = alamatKerja,
                    alamatIndo = alamatIndo,
                    noHp = noHp,
                )

                if (isEditMode) {
                    viewModel.updateDataDiri(keyId!!, dataDiri,
                        onProgress = {
                            // TODO
                        },
                        onComplete = {
                            sendResultAndExit(Activity.RESULT_OK, null)
                        },
                        onFailure = {
                            val failureData = Intent()
                            failureData.putExtra(FormActivity.EXTRAS_FAIL_MSG, it)

                            sendResultAndExit(Activity.RESULT_CANCELED, failureData)
                        }
                    )
                } else {
                    viewModel.insertDataDiri(
                        dataDiri,
                        onProgress = {
                            // TODO
                        },
                        onComplete = { generatedKeyId ->
                            val dataToSend = Intent()
                            dataToSend.putExtra(FormActivity.EXTRAS_SUCCESS_DATA, generatedKeyId)

                            sendResultAndExit(Activity.RESULT_OK, dataToSend)
                        },
                        onFailure = {
                            val dataToSend = Intent()
                            dataToSend.putExtra(FormActivity.EXTRAS_FAIL_MSG, it)

                            sendResultAndExit(Activity.RESULT_CANCELED, dataToSend)
                        }
                    )
                }
            }
        }
    }

    private fun setupEditMode(dataDiriKeyId: String) {
        val snackBarLoading = Snackbar.make(binding.root, "Memuat data diri ...", Snackbar.LENGTH_INDEFINITE)

        viewModel.getDataDiri(
            dataDiriKeyId,
            onProgress = {
                snackBarLoading.show()
            },
            onComplete = {
                snackBarLoading.dismiss()
            },
            onFailure = {
                val dataToSend = Intent()
                dataToSend.putExtra(FormActivity.EXTRAS_FAIL_MSG, it)

                sendResultAndExit(Activity.RESULT_CANCELED, dataToSend)
            }
        )

        viewModel.dataDiriIndenBooking.observe(requireActivity()) {
            it?.also { dataDiri ->
                with(binding) {
                    edtNamaCostumer.setText(dataDiri.nama)
                    edtNoIdentitas.setText(dataDiri.noIdentitas)
                    edtAlamatKerja.setText(dataDiri.alamatKerja)
                    edtAlamatIndo.setText(dataDiri.alamatIndo)
                    edtNoHandphone.setText(dataDiri.noHp)

                    if (dataDiri.jenisIdentitas == "KTP") {
                        rbIdKtp.isChecked = true
                    } else {
                        rbIdPassport.isChecked = true
                    }

                    spinnerNegaraBekerja.setSelection(
                        spinnerNegaraBekerjaAdapter.getPosition(dataDiri.negaraBekerja), true
                    )
                }
            }
        }
    }

    private fun sendResultAndExit(resultCode: Int, dataToSend: Intent?) {
        with(requireActivity()) {
            setResult(resultCode, dataToSend)
            finish()
        }
    }

}