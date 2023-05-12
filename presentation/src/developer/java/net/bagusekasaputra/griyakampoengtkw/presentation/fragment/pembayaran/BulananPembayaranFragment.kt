package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBulananPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.BulananPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class BulananPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentBulananPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBulananPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.pembayaranBulanansLive.observe(requireActivity()) {
            it?.also {
                setTablePembayaranBulanan(it)
                setTotalTunggakan(it)
            }
        }
    }

    private fun setTablePembayaranBulanan(pembayaranBulanans: List<PembayaranBulanan>) {
        val columnHeaderWidths = listOf(
            Pair(BulananPembayaranTableWrapper.BULAN, 250),
            Pair(BulananPembayaranTableWrapper.UANG_MASUK, 300),
            Pair(BulananPembayaranTableWrapper.JUMLAH_TUNGGAKAN, 300),
            Pair(BulananPembayaranTableWrapper.ALOKASI, 300),
            Pair(BulananPembayaranTableWrapper.STATUS, 250),
        )

        BulananPembayaranTableWrapper(binding.tablePembayaranBulanan, pembayaranBulanans)
            .setWidthColumnHeader(columnHeaderWidths)
            .createTable()
    }

    private fun setTotalTunggakan(pembayaranBulanans: List<PembayaranBulanan>) {
        val totalTunggakan = PembayaranBulanan.hitungSemuaTunggakan(pembayaranBulanans)
        val textTunggakan = if (totalTunggakan < 0) "-Rp. " else "Rp. " +
                NumberUtil.formatLongToString(totalTunggakan)

        binding.tvTotalTunggakan.text = textTunggakan
    }
}