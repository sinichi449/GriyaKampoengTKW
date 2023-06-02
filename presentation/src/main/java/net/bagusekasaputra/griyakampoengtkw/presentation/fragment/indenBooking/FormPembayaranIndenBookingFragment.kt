@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionCatatanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogEditHargaRumahIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormPembayaranIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionPembayaranIndenBookingBottomSheetDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class FormPembayaranIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormPembayaranIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    private val REQUEST_CODE_INPUT_NEW_PEMBAYARAN = 901

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormPembayaranIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnRefreshListener {
            refresh(Model.HARGA_RUMAH, Model.LIST_PEMBAYARAN, Model.CATATAN_PEMBAYARAN)

            // Until harga rumah ready
            binding.root.isRefreshing = false
        }

        // Setup fab add
        with((requireActivity() as DetailIndenBookingActivity).getFab()) {
            // Hide on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.scrollViewPembayaranIndenBooking, this)

            // Go to form input if harga rumah already set
            setOnClickListener {
                if (viewModel.hargaRumahIndenBooking.value != null) {
                    val intent = Intent(requireContext(), FormActivity::class.java).apply {
                        putExtra(
                            FormActivity.EXTRAS_FORM_TYPE,
                            FormActivity.FORM_PEMBAYARAN_INDEN_BOOKING
                        )
                        putExtra(FormActivity.EXTRAS_KEY_ID_INDEN_BOOKING, viewModel.currentKeyId)
                    }

                    startActivityForResult(intent, REQUEST_CODE_INPUT_NEW_PEMBAYARAN)
                } else {
                    Toast.makeText(requireContext(), "Entry pembayaran memerlukan Harga Rumah yang telah disetting!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Edit harga rumah
        binding.imgEditHargaRumah.setOnClickListener {
            dialogEditHargaRumah()
        }

        // Edit catatan pembayaran
        binding.cardCatatanPembayaran.setOnClickListener {
            val catatanPembayaran = viewModel.catatanPembayaran.value

            catatanPembayaranDialog(
                catatanPembayaran = catatanPembayaran,
                onInputValidAndSubmitRequested = { dialogInterface, dialogBinding, newCatatanPembayaran ->
                    dialogBinding.btnTambahkan.isEnabled = false

                    // Update Operation
                    if (catatanPembayaran != null) {
                        viewModel.updateCatatanPembayaran(newCatatanPembayaran,
                            onProgress = {
                                dialogBinding.btnTambahkan.text = "Mengupdate data ..."
                            },
                            onSuccess = {
                                dialogInterface.dismiss()

                                Snackbar.make(binding.root, "Berhasil mengubah catatan pembayaran!", Snackbar.LENGTH_SHORT)
                                    .show()

                                refresh(Model.CATATAN_PEMBAYARAN)
                            },
                            onFailure = {
                                dialogInterface.dismiss()

                                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            }
                        )

                    // Insert operation
                    } else {
                        viewModel.insertCatatanPembayaran(newCatatanPembayaran,
                            onProgress = {
                                dialogBinding.btnTambahkan.text = "Memproses data ..."
                            },
                            onSuccess = {
                                dialogInterface.dismiss()

                                Snackbar.make(binding.root, "Berhasil menambahkan catatan pembayaran!", Snackbar.LENGTH_SHORT)
                                    .show()

                                refresh(Model.CATATAN_PEMBAYARAN)
                            },
                            onFailure = {
                                dialogInterface.dismiss()

                                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                onDeleteRequested = { dialog, _ ->
                    // TODO
                    dialog.dismiss()

                    Toast.makeText(requireContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        refresh(Model.HARGA_RUMAH, Model.LIST_PEMBAYARAN, Model.CATATAN_PEMBAYARAN)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.hargaRumahIndenBooking.observe(requireActivity()) {
            it?.also { hargaRumah ->
                binding.tvHarga.text = NumberUtil.formatLongToString(hargaRumah.harga)

                binding.tvTambahanLuas.text = NumberUtil.formatLongToString(hargaRumah.tambahLuasan)

                binding.tvTotalHarga.text = NumberUtil.formatLongToString(hargaRumah.hargaDanTambahLuasan)
            }
        }

        viewModel.pembayaranListIndenBooking.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) {
                tablePembayaran(it)
            }
        }

        viewModel.catatanPembayaran.observe(requireActivity()) {
            binding.tvCatatan.text = it?.content ?: "Tidak ada catatan"
        }
    }

    private fun tablePembayaran(pembayarans: List<Pembayaran>) {
        val tableListener = object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                // Show keterangan in dialog
                if (column == FullPembayaranTableWrapper.KETERANGAN_PROGRESS) {
                    val pembayaran = pembayarans[row]
                    val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("${viewModel.namaCostumer} - ${pembayaran.termin}")
                        setMessage(pembayaran.keterangan)
                    }.create()

                    DialogUtil.additionalDialogSetting(requireContext(), dialog)

                    dialog.show()
                }
            }

            override fun onCellDoubleClicked(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onCellLongPressed(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onColumnHeaderClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderDoubleClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderLongPressed(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
                val actionDialog = ActionPembayaranIndenBookingBottomSheetDialog()
                val pembayaranTypeAndPositionBundle = bundleOf(
                    ActionPembayaranIndenBookingBottomSheetDialog.EXTRAS_INDEX_PEMBAYARAN_POSITION
                            to row,
                )
                actionDialog.arguments = pembayaranTypeAndPositionBundle

                actionDialog.show(childFragmentManager, null)
            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {

            }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

        }

        FullPembayaranTableWrapper(binding.tableFormPembayaran, pembayarans)
            .setTableListener(tableListener)
            .createTable()
    }

    private fun refresh(vararg what: Model) {
        val currentKeyId = viewModel.currentKeyId
        if ((currentKeyId != "NULL_ID") || (currentKeyId.isNotEmpty())) {
            what.forEach {
                if (it == Model.HARGA_RUMAH) {
                    viewModel.getHargaRumah(currentKeyId,
                        onProgress = {
                            binding.progressBarLoadingHargaRumah.visibility = View.VISIBLE
                            binding.imgEditHargaRumah.visibility = View.GONE
                        },
                        onComplete = {
                            binding.progressBarLoadingHargaRumah.visibility = View.GONE
                            binding.imgEditHargaRumah.visibility = View.VISIBLE
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                if (it == Model.LIST_PEMBAYARAN) {
                    viewModel.getAllPembayaran(currentKeyId,
                        onProgress = {
                            binding.layoutLoadingFormPembayaran.visibility = View.VISIBLE
                            binding.tableFormPembayaran.visibility = View.GONE
                        },
                        onComplete = {
                            binding.layoutLoadingFormPembayaran.visibility = View.GONE
                            binding.tableFormPembayaran.visibility = View.VISIBLE
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                if (it == Model.CATATAN_PEMBAYARAN) {
                    viewModel.getCatatanPembayaran(currentKeyId,
                        onProgress = {
                            binding.progressBarCatatanPembayaran.visibility = View.VISIBLE
                            binding.imgEditCatatan.visibility = View.GONE
                        },
                        onSuccess = {
                            binding.progressBarCatatanPembayaran.visibility = View.GONE
                            binding.imgEditCatatan.visibility = View.VISIBLE
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogEditHargaRumah() {
        val dialogBinding = DialogEditHargaRumahIndenBookingBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)
        dialogView.show()

        dialogBinding.edtHarga.apply {
            val harga = binding.tvHarga.text
            if (harga != "0")
                this.setText(harga)
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }
        dialogBinding.edtTambahLuasan.apply {
            val tambahanLuas = binding.tvTambahanLuas.text
            if (tambahanLuas != "0") this.setText(tambahanLuas)

            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtHarga)

            if (!isInvalidEdt) {
                val harga = dialogBinding.edtHarga.text.toString().let {
                    NumberUtil.formatStringToLong(it)
                }
                val tambahLuasan = dialogBinding.edtTambahLuasan.text.toString().let {
                    if (it.isNotEmpty()) NumberUtil.formatStringToLong(it)
                    else 0L
                }

                val hargaRumah = HargaRumahIndenBooking(
                    harga = harga,
                    tambahLuasan = tambahLuasan,
                    keyId = viewModel.currentKeyId,
                )
                viewModel.updateHargaRumah(
                    keyId = viewModel.currentKeyId,
                    newHargaRumah = hargaRumah,
                    onProgress = {
                        dialogBinding.btnTambahkan.startAnimation()
                    },
                    onSuccess = {
                        dialogBinding.btnTambahkan.revertAnimation()

                        dialogView.dismiss()

                        Snackbar.make(binding.root, "Berhasil mengubah harga rumah!", Snackbar.LENGTH_SHORT)
                            .show()

                        refresh(Model.HARGA_RUMAH)
                    },
                    onFailure = {
                        dialogBinding.btnTambahkan.revertAnimation()

                        dialogView.dismiss()

                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Input belum benar!", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun catatanPembayaranDialog(
        catatanPembayaran: IndenBookingCatatanPembayaran?,
        onInputValidAndSubmitRequested: (
            dialogInterface: DialogInterface,
            dialogView: DialogActionCatatanPembayaranBinding,
            newCatatanPembayaran: IndenBookingCatatanPembayaran,
        ) -> Unit,
        onDeleteRequested: (dialogInterface: DialogInterface, dialogView: DialogActionCatatanPembayaranBinding) -> Unit,
    ) {
        val dialogBinding = DialogActionCatatanPembayaranBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

        // if Edit Mode, ENABLE the Delete Button, set the text as the one before,and change the Dialog Title
        val editMode = catatanPembayaran != null

        with(dialogBinding) {
            if (editMode) {
                tvInfoTitleTambahCatatan.text = "Ubah Catatan"
                edtCatatan.setText(catatanPembayaran!!.content)
                btnTambahkan.text = "Ubah"
                btnHapusCatatan.visibility = View.VISIBLE

            } else {
                tvInfoTitleTambahCatatan.text = "Tambahkan Catatan"
                btnTambahkan.text = "Tambahkan"
                btnHapusCatatan.visibility = View.GONE
            }
        }

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtCatatan)

            if (!isInvalidEdt) {
                val content = dialogBinding.edtCatatan.text.toString()

                onInputValidAndSubmitRequested(dialogView, dialogBinding,
                    IndenBookingCatatanPembayaran(
                        keyId = viewModel.currentKeyId,
                        mContent = content,
                    )
                )
            } else {
                Toast.makeText(requireContext(), "Input masih belum valid!", Toast.LENGTH_LONG).show()
            }
        }

        dialogBinding.btnHapusCatatan.setOnClickListener {
            onDeleteRequested(dialogView, dialogBinding)
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.fragment.app.Fragment"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Input Pembayaran request handling
        if (requestCode == REQUEST_CODE_INPUT_NEW_PEMBAYARAN) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(binding.root, "Berhasil menambahkan pembayaran!", Snackbar.LENGTH_SHORT)
                    .show()

                refresh(Model.LIST_PEMBAYARAN)
            } else {
                data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private enum class Model {
        HARGA_RUMAH, LIST_PEMBAYARAN, CATATAN_PEMBAYARAN
    }
}