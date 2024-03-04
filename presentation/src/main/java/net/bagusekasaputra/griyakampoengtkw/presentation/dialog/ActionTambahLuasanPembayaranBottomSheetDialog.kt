package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateTambahLuasanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsTambahLuasanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest

class ActionTambahLuasanPembayaranBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsTambahLuasanPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private var selectedIndex: Int? = null

    // Need both of these to be passed into Picker Register result
    private var currentKavling: String? = ""
    private var currentId: String? = ""

    private val ubahPembayaranRequestCode = 1002

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

        with(binding) {
            // Dialog Title
            tvPembayaranInfo.text = buildString {
                append(data?.tanggal).append(" - ").append(data?.jumlahUang?.numericToString())
            }
            tvPembayaranId.text = data?.id
        }

        // TODO: Add conditional statement whether foto tambah luasan is exist
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

}