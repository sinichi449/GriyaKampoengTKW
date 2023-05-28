package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ActionPembayaranItemBottomSheetDialog(): BottomSheetDialogFragment() {

    private lateinit var dialogBinding: DialogActionsItemPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private var indexPembayaran: Int? = null

    // Need for acquiring max size foto pembayaran compression
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    // Need both of these to fill the register picker results
    private var currentKavling = ""
    private var currentTermin = ""

    private val fotoPembayaranPickerResultLauncher =
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
                        imageViewModel.addFotoPembayaran(
                            kavlingKode = currentKavling,
                            termin = currentTermin,
                            uri = uri,
                            onComplete = { msg ->
                                viewModel.needSyncPembayaran.value = true
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        ImagePicker.getError(intent),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    companion object {
        const val EXTRAS_INDEX_PEMBAYARAN_POSITION = "EXTRAS_INDEX_PEMBAYARAN_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        indexPembayaran = arguments?.getInt(EXTRAS_INDEX_PEMBAYARAN_POSITION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogBinding = DialogActionsItemPembayaranBinding.inflate(inflater, container, false)

        return dialogBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pembayaran = viewModel.fullPembayaransLive.value?.get(indexPembayaran!!)

        if (pembayaran != null) {
            currentKavling = viewModel.currentKavlingKode!!
            currentTermin = pembayaran.termin

            // Dialog title
            dialogBinding.tvKavlingTermin.text = "Kav. $currentKavling - $currentTermin"

            if (pembayaran.sudahIsiFotoPembayaran) {
                dialogBinding.cardAmbilKuitansi.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        with (dialogBinding.switchSudahAmbilKuitansi) {
                            isChecked = !isChecked
                        }
                    }
                }

                dialogBinding.switchSudahAmbilKuitansi.apply {
                    visibility = View.VISIBLE
                    isChecked = pembayaran.sudahAmbilKuitansi
                    setOnCheckedChangeListener { _, isChecked ->
                        val ambilKuitansi = AmbilKuitansi(
                            kavling = currentKavling,
                            termin = currentTermin,
                            sudahAmbil = isChecked,
                        )

                        viewModel.insertAmbilKuitansi(ambilKuitansi,
                            onProgress = {
                                this@ActionPembayaranItemBottomSheetDialog.isCancelable = false
                                isEnabled = false

                                visibility = View.GONE
                                dialogBinding.progressAmbilKuitansi.visibility = View.VISIBLE
                            },
                            onSuccess = {
                                this@ActionPembayaranItemBottomSheetDialog.isCancelable = true
                                isEnabled = true

                                visibility = View.VISIBLE
                                dialogBinding.progressAmbilKuitansi.visibility = View.GONE
                            },
                            onFailure = {
                                this@ActionPembayaranItemBottomSheetDialog.dismiss()

                                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }

                dialogBinding.cardTambahkanFotoPembayaran.visibility = View.GONE

                dialogBinding.cardLihatFotoPembayaran.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Lihat Foto", Toast.LENGTH_SHORT).show()
                    }
                }

                dialogBinding.cardUbahFotoPembayaran.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        fotoPembayaranPickerDialog()
                    }
                }

                dialogBinding.cardHapusFotoPembayaran.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Hapus Foto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                dialogBinding.cardAmbilKuitansi.visibility = View.GONE

                dialogBinding.cardTambahkanFotoPembayaran.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        fotoPembayaranPickerDialog()
                    }
                }

                dialogBinding.cardLihatFotoPembayaran.visibility = View.GONE

                dialogBinding.cardUbahFotoPembayaran.visibility = View.GONE

                dialogBinding.cardHapusFotoPembayaran.visibility = View.GONE
            }

            dialogBinding.cardUbahDataPembayaran.setOnClickListener {
                Toast.makeText(requireContext(), "Dalam perbaikan!", Toast.LENGTH_LONG).show()
            }

            dialogBinding.cardHapusDataPembayaran.setOnClickListener {
                Toast.makeText(requireContext(), "Hapus Data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Pembayaran is null!", Toast.LENGTH_LONG).show()
        }
    }

    private fun fotoPembayaranPickerDialog() {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_pembayaran", 256))
            .createIntent {
                fotoPembayaranPickerResultLauncher.launch(it)
            }
    }
}