package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateTambahLuasanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsTambahLuasanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest
import javax.inject.Inject

@AndroidEntryPoint
class ActionTambahLuasanPembayaranBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsTambahLuasanPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private var selectedIndex: Int? = null

    // Need both of these to be passed into Picker Register result
    private var currentKavling: String? = ""
    private var currentId: String? = ""
    private var sudahIsiFoto: Boolean = false

    private var progressBarTambahFoto: ProgressBar? = null

    private val ubahPembayaranRequestCode = 1002

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private val pickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val intent = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = intent?.data
                    if (uri != null) {
                        NotificationUtil.createNotification(
                            activity = requireActivity(),
                            title = "Upload Foto Pembayaran",
                            content = "Mohon tunggu sebentar ...",
                            finished = false,
                        )

                        imageViewModel.insertFotoTambahLuasan(
                            FotoTambahLuasan(
                                tambahLuasanId = currentId!!,
                                kavling = currentKavling!!,
                                uri = uri.toString()
                            )
                        )

                        imageViewModel.writeFotoTambahLuasanOperation.observe(requireActivity()) { k ->
                            k?.also { uiState ->
                                when (uiState) {
                                    is UiState.Loading -> {
                                        progressBarTambahFoto?.visibility = View.VISIBLE
                                    }
                                    is UiState.Success -> {
                                        progressBarTambahFoto?.visibility = View.GONE

                                        this.dismiss()

                                        viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
                                    }
                                    is UiState.Failure -> {
                                        progressBarTambahFoto?.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Uri is Empty!", Toast.LENGTH_SHORT).show()
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(intent), Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    companion object {
        const val EXTRAS_SELECTED_INDEX_POSITION = "EXTRAS_SELECTED_INDEX_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedIndex = arguments?.getInt(EXTRAS_SELECTED_INDEX_POSITION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogActionsTambahLuasanPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tambahLuasanUiState = viewModel.tambahanLuasPembayaran.value
        val data = if (tambahLuasanUiState is UiState.Success) {
           tambahLuasanUiState.data?.get(selectedIndex!!)!!
        } else {
            null
        }


        currentKavling = data?.kavling
        currentId = data?.id
        sudahIsiFoto = data?.fotoUri?.isNotEmpty() ?: false

        progressBarTambahFoto = binding.progressbarTambahkanFoto


        with(binding) {
            // Dialog Title
            tvPembayaranInfo.text = buildString {
                append(data?.tanggal).append(" - ").append(data?.jumlahUang?.numericToString())
            }
            tvPembayaranId.text = data?.id

            if (sudahIsiFoto) {
                cardTambahkanFotoPembayaran.visibility = View.GONE
                cardHapusFotoPembayaran.visibility = View.VISIBLE

                binding.cardLihatFotoPembayaran.setOnClickListener {
                    val imageTransport = imageViewModel.createImageTransport(
                        sendIntent = GriyaNodes.INTENT_FOTO_TAMBAH_LUASAN,
                        content = mapOf(
                            "kavling" to currentKavling,
                            "id" to currentId
                        )
                    )
                    val intent = Intent(requireContext(), FullImageActivity::class.java)
                    intent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)

                    startActivity(intent)
                }
            } else {
                cardLihatFotoPembayaran.visibility = View.GONE
                cardTambahkanFotoPembayaran.visibility = View.VISIBLE
                cardHapusFotoPembayaran.visibility = View.GONE

                cardTambahkanFotoPembayaran.setOnClickListener {
                    launchFotoPickerDialog()
                }
            }
        }

        binding.cardEditDataPembayaran.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)
            val parcel = UpdateTambahLuasanPembayaranParcel(
                kavling = currentKavling!!,
                id = currentId!!,
            )
            intent.putExtra(FormActivity.EXTRAS_PARCEL, parcel)
            startActivityForResult(intent, ubahPembayaranRequestCode)
        }

        binding.cardHapusDataPembayaran.setOnClickListener {
            // Foto Pembayaran also deleted, hazu datta...
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Tambah Luasan Data?")
                .setMessage("Apakah Anda yakin menghapus pembayaran $currentId? Foto Pembayaran juga akan terhapus!")
                .setPositiveButton("Ya") { dialogHapus, _ ->
                    dialogHapus.dismiss()

                    // Show loading icon
                    with(binding) {
                        progressbarHapusData.visibility = View.VISIBLE
                        progressbarHapusFoto.visibility = View.VISIBLE
                    }

                    viewModel.deleteTambahLuasanPembayaran(
                        kavling = currentKavling!!,
                        id = currentId!!,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Berhasil menghapus data!", Toast.LENGTH_SHORT).show()
                            dialogHapus.dismiss()

                            this@ActionTambahLuasanPembayaranBottomSheetDialog.dismiss()

                            viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            this@ActionTambahLuasanPembayaranBottomSheetDialog.dismiss()
                        }
                    )
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ubahPembayaranRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                dismiss()

                viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
            } else {
                data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun launchFotoPickerDialog() {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_pembayaran", 256))
            .createIntent {
                pickerResultLauncher.launch(it)
            }
    }
}