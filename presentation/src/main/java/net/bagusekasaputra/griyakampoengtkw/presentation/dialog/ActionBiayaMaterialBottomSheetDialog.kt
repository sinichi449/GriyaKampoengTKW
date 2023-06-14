package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsBiayaMaterialBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan.BiayaMaterialFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPembangunanViewModel

@AndroidEntryPoint
class ActionBiayaMaterialBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsBiayaMaterialBinding

    private val viewModel by activityViewModels<BiayaPembangunanViewModel>()
    private var indexItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        indexItem = arguments?.getInt(BiayaMaterialFragment.EXTRAS_ROW_POSITION, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogActionsBiayaMaterialBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val biayaMaterial = viewModel.biayaMaterialList.value[indexItem]

        with(binding) {
            populateHeaderInformation(biayaMaterial.keyId, biayaMaterial.namaItem)

            // Data Operation
            cardEditDataPembayaran.setOnClickListener {
                // TODO
            }
            cardHapusDataPembayaran.setOnClickListener {
                // TODO
            }

            // Foto Operation
            val tambahkanFotoVisibility: Int
            val lihatFotoVisibility: Int
            val hapusFotoVisibility: Int
            if (biayaMaterial.uriFoto.isEmpty()) {
                tambahkanFotoVisibility = View.VISIBLE
                lihatFotoVisibility = View.GONE
                hapusFotoVisibility = View.GONE
            } else {
                tambahkanFotoVisibility = View.GONE
                lihatFotoVisibility = View.VISIBLE
                hapusFotoVisibility = View.VISIBLE
            }

            cardTambahkanFotoPembayaran.apply {
                visibility = tambahkanFotoVisibility
                setOnClickListener {
                    // TODO
                }
            }
            cardLihatFotoPembayaran.apply {
                visibility = lihatFotoVisibility
                setOnClickListener {
                    // TODO
                }
            }
            cardHapusFotoPembayaran.apply {
                visibility = hapusFotoVisibility
                setOnClickListener {
                    // TODO
                }
            }
        }
    }

    private fun DialogActionsBiayaMaterialBinding.populateHeaderInformation(keyId: String, namaItem: String) {
        tvKeyId.text = keyId
        tvNamaItem.text = namaItem
    }

}