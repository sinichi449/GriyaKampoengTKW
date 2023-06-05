@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest
import javax.inject.Inject

@AndroidEntryPoint
class ActionPembayaranStandardBottomSheetDialog(): BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsItemPembayaranBinding
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

    private val ubahPembayaranRequestCode = 1001


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

                                viewModel.requestSync(PembayaranSyncRequest.TABEL_PEMBAYARAN)
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
        binding = DialogActionsItemPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pembayaran = viewModel.fullPembayaransLive.value?.get(indexPembayaran!!)!!

        currentKavling = viewModel.currentKavlingKode!!
        currentTermin = pembayaran.termin
        progressBarTambahFoto = binding.progressbarTambahkanFoto

        // Dialog title
        binding.tvKavlingTermin.text = "Kav. $currentKavling - $currentTermin"

        if (pembayaran.sudahIsiFotoPembayaran) {
            binding.cardAmbilKuitansi.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    with (binding.switchSudahAmbilKuitansi) {
                        isChecked = !isChecked
                    }
                }
            }

            binding.switchSudahAmbilKuitansi.apply {
                visibility = View.VISIBLE
                isChecked = pembayaran.sudahAmbilKuitansi
                setOnCheckedChangeListener { _, isChecked ->
                    val standardAmbilKuitansi = StandardAmbilKuitansi(
                        kavling = currentKavling,
                        mTermin = currentTermin,
                        mSudahAmbil = isChecked,
                    )

                    viewModel.insertAmbilKuitansi(standardAmbilKuitansi,
                        onProgress = {
                            this@ActionPembayaranStandardBottomSheetDialog.isCancelable = false
                            isEnabled = false

                            visibility = View.GONE
                            binding.progressAmbilKuitansi.visibility = View.VISIBLE
                        },
                        onSuccess = {
                            this@ActionPembayaranStandardBottomSheetDialog.isCancelable = true
                            isEnabled = true

                            visibility = View.VISIBLE
                            binding.progressAmbilKuitansi.visibility = View.GONE
                        },
                        onFailure = {
                            this@ActionPembayaranStandardBottomSheetDialog.dismiss()

                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }

            binding.cardTambahkanFotoPembayaran.visibility = View.GONE

            binding.cardLihatFotoPembayaran.apply {
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

            binding.cardHapusFotoPembayaran.apply {
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
                                    binding.progressbarHapusFoto.visibility = View.VISIBLE
                                },
                                onComplete = { msg ->
                                    dialog.dismiss()

                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                                    this@ActionPembayaranStandardBottomSheetDialog.dismiss()

                                    viewModel.requestSync(PembayaranSyncRequest.TABEL_PEMBAYARAN)
                                }
                            )
                        }
                        setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss()}
                    }.create()
                        .show()
                }
            }
        } else {
            binding.cardAmbilKuitansi.visibility = View.GONE

            binding.cardTambahkanFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    fotoPembayaranPickerDialog()
                }
            }

            binding.cardLihatFotoPembayaran.visibility = View.GONE

            binding.cardHapusFotoPembayaran.visibility = View.GONE
        }

        // Go to FormActivity when cardEditDataPembayaran clicked
        binding.cardEditDataPembayaran.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)

            intent.putExtra(FormActivity.EXTRAS_FORM_TYPE, FormActivity.FORM_PEMBAYARAN_KAVLING)
            intent.putExtra(FormActivity.EXTRAS_KAVLING, currentKavling)
            intent.putExtra(FormActivity.EXTRAS_TERMIN, currentTermin)

            Log.d("FORM_INPUT_PEMBAYARAN", "ActionPembayaranBottomSheetDialog: Request to navigate to FormActivity with extras Kavling: $currentKavling and Termin : $currentTermin")

            startActivityForResult(intent, ubahPembayaranRequestCode)
        }

        binding.cardHapusDataPembayaran.setOnClickListener {
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
                            binding.progressbarHapusData.visibility = View.VISIBLE
                            binding.progressbarHapusFoto.visibility = View.VISIBLE
                        },
                        onSuccess = {
                            Toast.makeText(requireContext(), "Berhasil menghapus pembayaran!", Toast.LENGTH_SHORT).show()
                            dialogHapus.dismiss()

                            this@ActionPembayaranStandardBottomSheetDialog.dismiss()

                            viewModel.requestSync(PembayaranSyncRequest.TABEL_PEMBAYARAN)
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                            this@ActionPembayaranStandardBottomSheetDialog.dismiss()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ubahPembayaranRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.getString(FormActivity.EXTRAS_SUCCESS_DATA)?.also {
                    dismiss()

                    viewModel.requestSync(PembayaranSyncRequest.TABEL_PEMBAYARAN)
                }
            } else {
                data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}