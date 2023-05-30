package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogEditHargaRumahIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormPembayaranIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class FormPembayaranIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormPembayaranIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormPembayaranIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnRefreshListener {
            sync()

            // Until harga rumah ready
            binding.root.isRefreshing = false
        }

        // Hide fab on scroll
        with((requireActivity() as DetailIndenBookingActivity).getFab()) {
            // Hide on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.scrollViewPembayaranIndenBooking, this)
        }

        binding.imgEditHargaRumah.setOnClickListener {
            dialogEditHargaRumah()
        }

        sync()

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
            it?.also { pembayarans: List<Pembayaran> ->
                FullPembayaranTableWrapper(binding.tableFormPembayaran, pembayarans)
                    .createTable()
            }
        }
    }

    private fun sync() {
        val currentKeyId = viewModel.currentKeyId
        if ((currentKeyId != "NULL_ID") || (currentKeyId.isNotEmpty())) {
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

                        sync()
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

}