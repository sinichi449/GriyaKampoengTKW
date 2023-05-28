package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
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

    // Need to define here to avoid uninitialized binding
    private var progressBarTambahFoto: ProgressBar? = null


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
                            onProgress = {
                                progressBarTambahFoto?.visibility = View.VISIBLE
                            },
                            onComplete = { msg ->
                                progressBarTambahFoto?.visibility = View.GONE

                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                                this.dismiss()
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

        val pembayaran = viewModel.fullPembayaransLive.value?.get(indexPembayaran!!)!!

        currentKavling = viewModel.currentKavlingKode!!
        currentTermin = pembayaran.termin
        progressBarTambahFoto = dialogBinding.progressbarTambahkanFoto

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
                    val imageTransport = imageViewModel.createImageTransport(
                        sendIntent = GriyaNodes.INTENT_FOTO_PEMBAYARAN,
                        content = mapOf(
                            Pair("kavlingKode", currentKavling),
                            Pair("termin", currentTermin),
                        ),
                    )

                    val fullImageIntent = Intent(requireContext(), FullImageActivity::class.java)
                    fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)
                    startActivity(fullImageIntent)
                }
            }

            dialogBinding.cardHapusFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    // Show delete confirmation
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Hapus Foto Pembayaran $currentTermin?")
                        setMessage("Apakah Anda yakin menghapus Foto Pembayaran pada termin $currentTermin?")
                        setPositiveButton("Ya") { dialog, _ ->
                            imageViewModel.deleteFotoPembayaran(
                                kavlingKode = currentKavling,
                                termin = currentTermin,
                                onProgress = {
                                    dialogBinding.progressbarHapusFoto.visibility = View.VISIBLE
                                },
                                onComplete = { msg ->
                                    dialog.dismiss()

                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                                    this@ActionPembayaranItemBottomSheetDialog.dismiss()
                                }
                            )
                        }
                        setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss()}
                    }.create()
                        .show()
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

            dialogBinding.cardHapusFotoPembayaran.visibility = View.GONE
        }

        dialogBinding.cardHapusDataPembayaran.setOnClickListener {
            // Show hapus Pembayaran confirmation.
            // Foto pembayaran will also deleted!
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pembayaran")
                .setMessage("Apakah Anda yakin menghapus pembayaran $currentTermin? " +
                        "Foto pembayaran juga akan terhapus!")
                .setPositiveButton("Ya") { dialogHapus, _ ->
                    dialogHapus.dismiss()

                    viewModel.deletePembayaran(
                        kavling = currentKavling,
                        pembayaran = pembayaran,
                        onProgress = {
                            dialogBinding.progressbarHapusData.visibility = View.VISIBLE
                            dialogBinding.progressbarHapusFoto.visibility = View.VISIBLE
                        },
                        onSuccess = {
                            Toast.makeText(requireContext(), "Berhasil menghapus pembayaran!", Toast.LENGTH_SHORT).show()
                            dialogHapus.dismiss()

                            this@ActionPembayaranItemBottomSheetDialog.dismiss()
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                            this@ActionPembayaranItemBottomSheetDialog.dismiss()
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

    private fun fotoPembayaranPickerDialog() {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_pembayaran", 256))
            .createIntent {
                fotoPembayaranPickerResultLauncher.launch(it)
            }
    }
}