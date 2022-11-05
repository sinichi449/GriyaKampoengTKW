package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogTambahDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil

@AndroidEntryPoint
class DataDiriFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriBinding
    private val viewModel: DetailViewModel by viewModels()
    private var currentKavlingKode: String? = null
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

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

    override fun onResume() {
        super.onResume()

        syncDataDiri()
    }

    private fun syncDataDiri() {
        viewModel.getDataDiri(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }

        viewModel.dataDiriLive.observe(requireActivity()) { dataDiri ->
            binding.tvNama.text = dataDiri?.nama ?: "-"
            binding.tvJenisIdentitas.text = dataDiri?.jenisIdentitas ?: "KTP"
            binding.tvNoIdentitas.text = dataDiri?.noIdentitas ?: "-"
            binding.tvNegaraBekerja.text = dataDiri?.negaraBekerja ?: "Hongkong"
            binding.tvAlamatKerja.text = dataDiri?.alamatKerja ?: "-"
            binding.tvAlamatIndo.text = dataDiri?.alamatIndo ?: "-"
            binding.tvNoHp.text = dataDiri?.noHp ?: "-"
        }
    }

    private fun showAddDataDiriDialog() {
        val dialogBinding = DialogTambahDataDiriBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireActivity())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        setupSpinner(dialogBinding)

        // If data diri exists in viewModel, then assign to the EditTexts
        // as an Update Data Diri Operation.
        viewModel.dataDiriLive.value?.let { dataDiri ->
            dialogBinding.spinnerNegaraBekerja.setSelection(arrayAdapter.getPosition(dataDiri.negaraBekerja), true)
            dialogBinding.edtNamaCostumer.setText(dataDiri.nama)
            dialogBinding.edtNoIdentitas.setText(dataDiri.noIdentitas)
            dialogBinding.edtAlamatKerja.setText(dataDiri.alamatKerja)
            dialogBinding.edtAlamatIndo.setText(dataDiri.alamatIndo)
            dialogBinding.edtNoHandphone.setText(dataDiri.noHp)
            if (dataDiri.jenisIdentitas == "KTP") {
                dialogBinding.rbIdKtp.isChecked = true
                dialogBinding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            } else {
                dialogBinding.rbIdPassport.isChecked = true
                dialogBinding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            }
        }

        // listen for radio button
        dialogBinding.rbIdKtp.setOnClickListener {
            dialogBinding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        }
        dialogBinding.rbIdPassport.setOnClickListener {
            dialogBinding.edtNoIdentitas.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }

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
                    viewModel.addDataDiri(currentKavlingKode!!, dataDiri) { completeMsg ->
                        Toast.makeText(requireContext(), completeMsg, Toast.LENGTH_SHORT).show()
                        dialogView.dismiss()
                        syncDataDiri()
                    }
                }
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun showDeleteDataDiriDialog() {
        val dialogView = AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data Diri")
            .setMessage("Apakah Anda yakin akan menghapus Data Diri di kavling $currentKavlingKode?")
            .setPositiveButton("Ya") { dialog, _ ->
                 viewModel.deleteDataDiri(currentKavlingKode!!) { completeMsg ->
                     Toast.makeText(requireContext(), completeMsg, Toast.LENGTH_SHORT).show()
                     dialog.dismiss()
                     syncDataDiri()
                 }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogView.show()
    }

    private fun setupSpinner(dialogBinding: DialogTambahDataDiriBinding) {
        val negaraBekerjaList = ArrayList<String>().apply {
            add("Hongkong")
            add("Macau")
            add("Taiwan")
            add("Singapore")
            add("Malaysia")
            add("Arab Saudi")
            add("Abu Dhabi")
        }

        arrayAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, negaraBekerjaList
        )

        dialogBinding.spinnerNegaraBekerja.adapter = arrayAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_data_diri, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hapus_data_diri -> {
                showDeleteDataDiriDialog()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}