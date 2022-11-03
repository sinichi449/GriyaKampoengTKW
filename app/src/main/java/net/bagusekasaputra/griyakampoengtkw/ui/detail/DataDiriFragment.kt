package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogTambahDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil

@AndroidEntryPoint
class DataDiriFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriBinding
    private val viewModel: DetailViewModel by viewModels()
    private var currentKavlingKode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataDiriBinding.inflate(inflater, container, false)

        arguments?.getString("kavling_kode").let { args ->
            args?.let {
                currentKavlingKode = it
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        syncDataDiri()

        binding.fabTambahDataDiri.setOnClickListener {
            showAddDataDiriDialog()
        }

        binding.swipeRefreshDataDiri.setOnRefreshListener {
            syncDataDiri()
        }

        viewModel.isFinishOperation.observe(requireActivity()) {
            it?.let { finish ->
                binding.swipeRefreshDataDiri.isRefreshing = !finish
            }
        }
    }

    private fun syncDataDiri() {
        if (currentKavlingKode != null) {
            viewModel.getDataDiri(currentKavlingKode!!)

            viewModel.operationResult.observe(requireActivity()) { operation ->
                operation?.let {
                    it.message?.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }

                viewModel.dataDiriLive.value?.let { dataDiri ->
                    binding.tvNama.text = dataDiri.nama
                    binding.tvJenisIdentitas.text = dataDiri.jenisIdentitas
                    binding.tvNoIdentitas.text = dataDiri.noIdentitas
                    binding.tvNegaraBekerja.text = dataDiri.negaraBekerja
                    binding.tvAlamatKerja.text = dataDiri.alamatKerja
                    binding.tvAlamatIndo.text = dataDiri.alamatIndo
                    binding.tvNoHp.text = dataDiri.noHp
                }
            }
        }
    }

    private fun showAddDataDiriDialog() {
        val dialogBinding = DialogTambahDataDiriBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireActivity())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            dialogBinding.btnTambahkan.text = "Menyimpan data ..."
            dialogBinding.btnTambahkan.isEnabled = false

            // check not null edt
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtNamaCostumer,
                dialogBinding.edtNoIdentitas, dialogBinding.edtAlamatKerja,
                dialogBinding.edtAlamatIndo, dialogBinding.edtNoHandphone)

            // get text from edt
            if (!isInvalidEdt) {
                val namaCostumer = dialogBinding.edtNamaCostumer.text.toString()
                val jenisIdentitas = if (dialogBinding.rbIdPassport.isChecked) "Passport" else "KTP"
                val noIdentitas = dialogBinding.edtNoIdentitas.text.toString()
                val negaraBekerja = dialogBinding.spinnerNegaraBekerja.selectedItem.toString()
                val alamatKerja = dialogBinding.edtAlamatKerja.text.toString()
                val alamatIndo = dialogBinding.edtAlamatIndo.text.toString()
                val noHandphone = dialogBinding.edtNoHandphone.text.toString()

                val dataDiri = DataDiri(
                    namaCostumer, jenisIdentitas, noIdentitas, negaraBekerja, alamatKerja,
                    alamatIndo, noHandphone)

                // upload data via viewModel
                if (currentKavlingKode == null) {
                    Toast.makeText(requireContext(),
                        "Ada masalah dengan kavling, mohon hubungi developer: Null Kavling",
                        Toast.LENGTH_LONG).show()
                } else {
                    viewModel.addDataDiri(currentKavlingKode!!, dataDiri)

                    // observe operation in viewModel
                    // if operation finished, show the result
                    viewModel.operationResult.observe(requireActivity()) { operation ->
                        operation?.let {
                            Toast.makeText(requireContext(), it.message?: "Null", Toast.LENGTH_SHORT).show()

                            dialogView.dismiss()
                        }
                    }
                }
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }

        setupSpinner(dialogBinding)
    }

    private fun setupSpinner(dialogBinding: DialogTambahDataDiriBinding) {
        val negaraBekerjaList = ArrayList<String>()
        negaraBekerjaList.add("Hongkong")
        negaraBekerjaList.add("Macau")
        negaraBekerjaList.add("Taiwan")
        negaraBekerjaList.add("Singapore")
        negaraBekerjaList.add("Malaysia")
        negaraBekerjaList.add("Arab Saudi")
        negaraBekerjaList.add("Abu Dhabi")

        val arrayAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, negaraBekerjaList
        )

        dialogBinding.spinnerNegaraBekerja.adapter = arrayAdapter
        // TODO: Spinner onclick
    }

}