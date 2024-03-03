package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateTambahLuasanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsTambahLuasanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

class ActionTambahLuasanPembayaranBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsTambahLuasanPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
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

        binding.cardEditDataPembayaran.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)
            val parcel = UpdateTambahLuasanPembayaranParcel(
                kavling = currentKavling!!,
                id = currentId!!,
            )
            intent.putExtra(FormActivity.EXTRAS_PARCEL, parcel)
            startActivityForResult(intent, ubahPembayaranRequestCode)
        }
    }

}