package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class ActionPembayaranItemBottomSheetDialog(
    private val pembayaran: Pembayaran,
    private val onSyncRequest: () -> Unit,
): BottomSheetDialogFragment() {

    private lateinit var dialogBinding: DialogActionsItemPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogBinding = DialogActionsItemPembayaranBinding.inflate(inflater, container, false)

        return dialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogBinding.switchSudahAmbilKuitansi.visibility = View.VISIBLE
        dialogBinding.switchSudahAmbilKuitansi.isChecked = pembayaran.sudahAmbilKuitansi
        dialogBinding.switchSudahAmbilKuitansi.setOnCheckedChangeListener { _, isChecked ->
            val ambilKuitansi = AmbilKuitansi(
                kavling = viewModel.currentKavlingKode!!,
                termin = pembayaran.termin,
                sudahAmbil = isChecked,
            )

            viewModel.insertAmbilKuitansi(ambilKuitansi,
                onProgress = {
                    this.isCancelable = false
                    dialogBinding.switchSudahAmbilKuitansi.isEnabled = false

                    dialogBinding.switchSudahAmbilKuitansi.visibility = View.GONE
                    dialogBinding.progressAmbilKuitansi.visibility = View.VISIBLE
                },
                onSuccess = {
                    this.isCancelable = true
                    dialogBinding.switchSudahAmbilKuitansi.isEnabled = true

                    dialogBinding.switchSudahAmbilKuitansi.visibility = View.VISIBLE
                    dialogBinding.progressAmbilKuitansi.visibility = View.GONE
                },
                onFailure = {
                    this.dismiss()

                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

}