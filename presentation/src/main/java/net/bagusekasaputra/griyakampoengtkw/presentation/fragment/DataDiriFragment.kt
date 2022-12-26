package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogTambahDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.*
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DataDiriFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriBinding
//    private val viewModel: DetailViewModel by viewModels()
    private val viewModel: DetailViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private var currentKavlingKode: String? = null
    private lateinit var arrayAdapter: ArrayAdapter<String>

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private val negaraBekerjaList = ArrayList<String>().apply {
        add("Hongkong")
        add("Macau")
        add("Taiwan")
        add("Jepang")
        add("Singapore")
        add("Malaysia")
        add("Arab Saudi")
        add("Abu Dhabi")
        add("Bangladesh")
    }

    private fun createImagePickerResultLauncher(onResultOk: (uri: Uri?) -> Unit): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    onResultOk(data?.data)
                    Log.d("DEBUG_ME", "ImagePicker(): Pick image in ${data?.data} success!")
                }
                ImagePicker.RESULT_ERROR -> {
//                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    Log.d("DEBUG_ME", "Error image picker: ${ImagePicker.getError(data)}")
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val startProfileImageForResult = createImagePickerResultLauncher { uri ->
        NotificationUtil.createNotification(
            activity = requireActivity(),
            title = "Sedang mengupload gambar",
            content = "Mohon tunggu sebentar",
            finished = false,
        )
        imageViewModel.addImageDataDiri(currentKavlingKode!!, uri!!) {
            Log.d("DEBUG_ME", "DataDiriFragment->startProfileImageForResult(): $it")
        }
    }

    private val startSPRImageForResult = createImagePickerResultLauncher { uri ->
        imageViewModel.addSprImage(currentKavlingKode!!, uri!!,
            onComplete = { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show() },
            onFailure = { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        )
    }

    private var offlineMode = false


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

        // Create an external directory for cache
        File(requireContext().getExternalFilesDir(null), "data_diri_images").let {
            if (it.exists().not()) it.mkdir()
        }
        File(requireContext().getExternalFilesDir(null), "spr_images").let {
            if (it.exists().not()) it.mkdir()
        }

        offlineMode = viewModel.offlineMode

        setupExtendedFloatingButton()

        binding.swipeRefreshDataDiri.setOnRefreshListener {
            // When user invokes refresh, we need to update the "xRefreshed" value in viewModel
            // to be FALSE.
            viewModel.dataDiriRefreshed.value = false
            syncDataDiri()
        }

        binding.imgProfile.setOnClickListener {
            // Set full picture
            Intent(requireContext(), FullImageActivity::class.java).let { intent ->
                imageViewModel.imageDataDiriLive.value.let { img ->
                    if (img == null) {
                        Toast.makeText(requireContext(), "Foto masih kosong!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val imageTransport = imageViewModel.createImageTransport(
                            sendIntent = GriyaNodes.INTENT_DATA_DIRI,
                            content = mapOf<String, String>(
                                Pair("kavlingKode", currentKavlingKode!!)
                            )
                        )
                        intent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)
                        startActivity(intent)
                    }
                }
            }
        }

        setupViewModel()

        binding.fabTambahDataDiri.setOnClickListener {
            if (offlineMode)
                showDialogOnOfflineMode()
            else
                showAddDataDiriDialog()
        }

        binding.fabTambahFoto.setOnClickListener { showImagePickerDataDiri(startProfileImageForResult) }

        // Even when I already set the visibility of FabAction into View.GONE,
        // to prevent the user from writing the data on offline mode, it's probably still
        // showing when I scroll the screen.
        // So, I put the conditional here for the scroll operation.
        if (offlineMode.not())
        // Hide fabs on scroll
            UiUtils.hideExtendedFabOnVerticalScroll(
                nestedScrollView = binding.scrollViewImageviewAndCard,
                extendedFabs = binding.fabActions,
            )
    }

    override fun onResume() {
        super.onResume()

        syncDataDiri()
    }

    private fun syncDataDiri() {
        viewModel.getDataDiri(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }

        imageViewModel.getImageDataDiri(currentKavlingKode!!) { }
    }

    private fun setLayoutImageDataDiriLoading(isLoading: Boolean) {
        binding.layoutImageProfile?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.layoutLoadingImage?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvLoadingText?.text = "Memuat gambar ..."
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) {
            it?.let { finish ->
                binding.swipeRefreshDataDiri.isRefreshing = !finish
            }
        }

        imageViewModel.imageDataDiriLive.observe(requireActivity()) { imageDataDiri ->
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

        imageViewModel.isFinishLoadingImage.observe(requireActivity()) { finished ->
            finished?.let {
                setLayoutImageDataDiriLoading(it.not())
            }
        }

        imageViewModel.isFinishAddImage.observe(requireActivity()) { finished ->
            finished?.let {
                if (it) {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Selesai mengupload gambar",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        finished = true,
                    )
                    imageViewModel.getImageDataDiri(viewModel.currentKavlingKode.value!!) { failMsg ->
                        Log.d("DEBUG_ME", "DataDiriFragment->finishAddImage(): $failMsg")
                    }
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
    }

    private fun showAddDataDiriDialog() {
        val dialogBinding = DialogTambahDataDiriBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireActivity())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

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
        val dialogView = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Data Diri")
            .setMessage("Apakah Anda yakin akan menghapus Data Diri di kavling $currentKavlingKode?")
            .setPositiveButton("Ya")  { dialog, _ ->
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

    private fun showDeleteImageFotoDataDiriDialog() {
        val dialogView = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Foto")
            .setMessage("Apakah Anda yakin akan menghapus foto?")
            .setPositiveButton("Ya") { _, _ ->
                NotificationUtil.createNotification(
                    activity = requireActivity(),
                    title = "Sedang menghapus foto data diri",
                    content = "...",
                    finished = false,
                )
                imageViewModel.deleteImageDataDiri { completeMsg ->
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Berhasil menghapus foto data diri!",
                        content = "",
                        finished = true,
                    )
                    ResourcesCompat.getDrawable(resources, R.drawable.avatar_1, null).let {
                        binding.imgProfile.setImageDrawable(it)
                    }
                    Toast.makeText(requireContext(), completeMsg, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .create()

        dialogView.show()
    }

    private fun lihatFotoSpr() {
        val imageTransport = imageViewModel.createImageTransport(
            sendIntent = GriyaNodes.INTENT_FOTO_SPR,
            content = mapOf(
                Pair("kavlingKode", currentKavlingKode!!)
            )
        )

        val intent = Intent(requireContext(), FullImageActivity::class.java)
        intent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)

        startActivity(intent)
    }

    /**
     * Ada dua ImagePicker, bedanya pada settingan max size foto.
     */
    private fun showImagePickerDataDiri(launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_data_diri", 256))
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }

    private fun showImagePickerSPR(launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_spr", 256))
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }

    private fun setupSpinner(dialogBinding: DialogTambahDataDiriBinding) {
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
                showDeleteImageFotoDataDiriDialog()
                true
            }
            R.id.hapus_data_diri -> {
                if (offlineMode)
                    showDialogOnOfflineMode()
                else
                    showDeleteDataDiriDialog()

                true
            }
            R.id.lihat_foto_spr -> {
                lihatFotoSpr()

                true
            }
            R.id.tambahkan_spr -> {
                showImagePickerSPR(startSPRImageForResult)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This AlertDialog will appear when the user trying to interact with
     * operations interface when the offlineMode is enabled in the Pengaturan.
     */
    private fun showDialogOnOfflineMode() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nonaktifkan Mode Offline")
            .setMessage("Pada mode offline, Anda tidak dapat melakukan operasi penambahan atau penghapusan data. Untuk melakukan operasi ini, silakan nonaktifkan Mode Offline pada layar Pengaturan.")
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

}