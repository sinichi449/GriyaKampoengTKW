package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogTambahDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil

@AndroidEntryPoint
class DataDiriFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriBinding
    private val viewModel: DetailViewModel by viewModels()
    private var currentKavlingKode: String? = null
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private val startProfileImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                viewModel.addImageDataDiri(currentKavlingKode!!, data?.data!!) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

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

        setupExtendedFloatingButton()

        binding.swipeRefreshDataDiri.isEnabled = false
//        binding.swipeRefreshDataDiri.setOnRefreshListener {
//            syncDataDiri()
//        }

        binding.imgProfile.setOnClickListener {
            Intent(requireContext(), FullImageFotoDataDiriActivity::class.java).let { intent ->
                viewModel.imageDataDiriLive.value.let { img ->
                    if (img == null) {
                        Toast.makeText(requireContext(), "Foto masih kosong!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        intent.putExtra(GriyaNodes.INTENT_BITMAP, img.bitmap)
                        startActivity(intent)
                    }
                }
            }
        }

        setupViewModel()
    }

    override fun onResume() {
        super.onResume()

        syncDataDiri()
    }



    private fun syncDataDiri() {
        viewModel.getDataDiri(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }

        viewModel.getImageDataDiri(currentKavlingKode!!) { }
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) {
            it?.let { finish ->
                binding.swipeRefreshDataDiri.isRefreshing = !finish
            }
        }

        viewModel.imageDataDiriLive.observe(requireActivity()) { imageDataDiri ->
            imageDataDiri?.let {
                binding.imgProfile.setImageBitmap(imageDataDiri.bitmap)
            }
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

        viewModel.isFinishAddImage.observe(requireActivity()) { finished ->
            finished?.let {
                if (it) viewModel.getImageDataDiri(currentKavlingKode!!) { failMsg ->
                    Toast.makeText(requireContext(), failMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupExtendedFloatingButton() {
        binding.fabActions.shrink()
        binding.fabTambahDataDiri.visibility = View.GONE
        binding.fabTambahFoto.visibility = View.GONE

        var isAllFabVisible = false
        binding.fabActions.setOnClickListener {
            if (isAllFabVisible) {
                binding.fabActions.shrink()
                binding.fabTambahDataDiri.hide()
                binding.fabTambahFoto.hide()

                isAllFabVisible = false

            } else {
                binding.fabActions.extend()
                binding.fabTambahDataDiri.show()
                binding.fabTambahFoto.show()

                isAllFabVisible = true
            }
        }

        binding.fabTambahDataDiri.setOnClickListener {
            showAddDataDiriDialog()
        }

        binding.fabTambahFoto.setOnClickListener { showImagePicker() }
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
                 viewModel.deleteImageDataDiri(currentKavlingKode!!) { completeMsg ->
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

    private fun showImagePicker() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .createIntent { intent ->
                startProfileImageForResult.launch(intent)
            }
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
            R.id.hapus_foto -> {
                viewModel.deleteImageDataDiri { completeMsg ->
                    ResourcesCompat.getDrawable(resources, R.drawable.avatar_1, null).let {
                        binding.imgProfile.setImageDrawable(it)
                    }
                    Toast.makeText(requireContext(), completeMsg, Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.hapus_data_diri -> {
                showDeleteDataDiriDialog()
                true
            }
            R.id.refresh -> {
                syncDataDiri()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}