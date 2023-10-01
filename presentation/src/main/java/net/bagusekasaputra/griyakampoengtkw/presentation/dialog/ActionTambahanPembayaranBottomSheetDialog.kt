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
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateTambahanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemTambahanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest
import javax.inject.Inject

@AndroidEntryPoint
class ActionTambahanPembayaranBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsItemTambahanPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private var indexPembayaran: Int? = null

    private val REQUEST_UBAH_TAMBAHAN_PEMBAYARAN = 119

    // Needed to acquire max size compression
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    // Need to define here to avoid uninitialized binding
    private var progressBarTambahFoto: ProgressBar? = null

    private val fotoTambahanPembayaranPickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val intent = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = intent?.data
                    if (uri != null) {
                        NotificationUtil.createNotification(
                            activity = requireActivity(),
                            title = "Upload Foto Tambahan Pembayaran",
                            content = "Mohon tunggu sebentar ...",
                            finished = false,
                        )

                        val tambahanPembayaran = viewModel.tambahanPembayarans.value?.get(indexPembayaran!!)!!
                        imageViewModel.addFotoTambahanPembayaran(
                            kavling = viewModel.currentKavlingKode!!,
                            id = tambahanPembayaran.id,
                            uri = uri.toString(),
                            onProgress = {
                                progressBarTambahFoto?.visibility = View.VISIBLE
                            },
                            onComplete = { msg ->
                                progressBarTambahFoto?.visibility = View.GONE

                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                                this.dismiss()

                                viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
                            }
                        )
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        ImagePicker.getError(intent),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    companion object {
        const val EXTRAS_INDEX_TABLE_POSITION = "EXTRAS_INDEX_TABLE_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        indexPembayaran = arguments?.getInt(EXTRAS_INDEX_TABLE_POSITION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogActionsItemTambahanPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tambahanPembayaran = viewModel.tambahanPembayarans.value?.get(indexPembayaran!!)!!

        progressBarTambahFoto = binding.progressbarTambahkanFoto

        // Dialog Title
        binding.tvTitle.text = buildString {
            append(tambahanPembayaran.kategori.toString() + " - ")
            append(tambahanPembayaran.keterangan)
        }

        if (tambahanPembayaran.sudahIsiFoto) {
            binding.cardTambahkanFotoPembayaran.visibility = View.GONE
            binding.cardLihatFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    // TODO: Lihat foto
                }
            }

            binding.cardHapusFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    // Show delete confirmation
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Hapus Foto?")
                        setMessage("Apakah Anda yakin menghapus Foto Pembayaran ini?")
                        setPositiveButton("Ya") { dialog, _ ->
                            // TODO: Hapus Foto
                        }
                        setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss()}
                    }.create()
                        .show()
                }
            }
        } else {
            binding.cardTambahkanFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    ImagePicker
                        .with(this@ActionTambahanPembayaranBottomSheetDialog)
                        .crop()
                        .compress(sharedPrefs.getInt("max_size_foto_pembayaran", 256))
                        .createIntent {  intent ->
                            fotoTambahanPembayaranPickerResultLauncher.launch(intent)
                        }
                }
            }
            binding.cardLihatFotoPembayaran.visibility = View.GONE
            binding.cardHapusFotoPembayaran.visibility = View.GONE
        }

        // Go to FormActivity when cardEditDataPembayaran clicked
        binding.cardEditDataPembayaran.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)
            val ubahDataParcel = UpdateTambahanPembayaranParcel(
                kavling = viewModel.currentKavlingKode!!,
                id = tambahanPembayaran.id
            )
            intent.putExtra(FormActivity.EXTRAS_PARCEL, ubahDataParcel)

            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_UBAH_TAMBAHAN_PEMBAYARAN)
        }

        binding.cardHapusDataPembayaran.setOnClickListener {
            // Show hapus Pembayaran confirmation.
            // Foto pembayaran will also deleted!
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pembayaran?")
                .setMessage("Apakah Anda yakin menghapus pembayaran ini? " +
                        "Foto pembayaran juga akan terhapus!")
                .setPositiveButton("Ya") { dialogHapus, _ ->
                    dialogHapus.dismiss()

                    viewModel.deleteTambahanPembayaran(
                        kavling = viewModel.currentKavlingKode!!,
                        id = tambahanPembayaran.id,
                        onSuccess = {
                            dismiss()

                            viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
                        },
                        onFailure = { msg ->
                            dismiss()

                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        }
                    )
                    // TODO: Hapus foto
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_UBAH_TAMBAHAN_PEMBAYARAN) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.getString(FormActivity.EXTRAS_SUCCESS_DATA)?.also {
                    dismiss()

                    viewModel.requestSync(PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN)
                }
            } else {
                data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}