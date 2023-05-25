package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormPembayaranIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
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

        sync()

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.hargaRumahIndenBooking.observe(requireActivity()) {
            it?.also { hargaRumah ->
                binding.tvHarga.text = StringBuilder()
                    .append("Rp. ")
                    .append(NumberUtil.formatLongToString(hargaRumah.harga))

                binding.tvTambahanLuas.text = StringBuilder()
                    .append("Rp. ")
                    .append(NumberUtil.formatLongToString(hargaRumah.tambahLuasan))

                binding.tvTotalHarga.text = StringBuilder()
                    .append("Rp. ")
                    .append(NumberUtil.formatLongToString(hargaRumah.hargaDanTambahLuasan))
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

}