package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapUserBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class RekapUserFragment : Fragment() {

    private lateinit var binding: FragmentRekapUserBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapUserBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            with(binding) {
                viewModel.bulanRekapUser = edtBulan.text?.toString()?.toInt() ?: 1
                viewModel.tahunRekapUser = edtTahun.text?.toString()?.toInt() ?: 2022

                layoutPilihBulanAngsuran.visibility = View.GONE
                layoutLoading.visibility = View.VISIBLE

                val bulanAngsuran = BulanAngsuran(viewModel.bulanRekapUser, viewModel.tahunRekapUser)
                viewModel.getUserPaymentStatusWithInvoice(
                    bulanAngsuran = bulanAngsuran,
                    onSuccess = {
                        layoutLoading.visibility = View.GONE
                        layoutContent.visibility = View.VISIBLE
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.userPaymentStatus.observe(requireActivity()) { i ->
            i?.also { dataList ->
                val sudahBayar = dataList[0]
                val belumBayar = dataList[1]

                val text = buildString {
                    append("${viewModel.bulanRekapUser}/${viewModel.tahunRekapUser}")
                    append("\n\n")
                    append("Sudah Bayar List").append("\n")
                    append(sudahBayar).append("\n\n")
                    append("Belum Bayar List").append("\n")
                    append(belumBayar).append("\n\n")
                }
                binding.tvContent.text = text
            }
        }
    }

}