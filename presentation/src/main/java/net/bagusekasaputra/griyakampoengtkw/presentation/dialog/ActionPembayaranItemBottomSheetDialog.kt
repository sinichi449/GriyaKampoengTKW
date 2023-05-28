package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class ActionPembayaranItemBottomSheetDialog(): BottomSheetDialogFragment() {

    private lateinit var dialogBinding: DialogActionsItemPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()
    private var indexPembayaran: Int? = null

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
            val kavling = viewModel.currentKavlingKode!!
            val termin = pembayaran.termin

            // Dialog title
            dialogBinding.tvKavlingTermin.text = "Kav. $kavling - $termin"

            // Only allow modify switch sudah ambil kuitansi if sudah isi pembayaran
            with(dialogBinding.switchSudahAmbilKuitansi) {
                isEnabled = pembayaran.sudahIsiFotoPembayaran
                visibility = View.VISIBLE
                isChecked = pembayaran.sudahAmbilKuitansi
                setOnCheckedChangeListener { _, isChecked ->
                    val ambilKuitansi = AmbilKuitansi(
                        kavling = kavling,
                        termin = termin,
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
        } else {
            Toast.makeText(requireContext(), "Pembayaran is null!", Toast.LENGTH_LONG).show()
        }
    }

}