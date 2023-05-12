package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBulananPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.BulananPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import kotlin.math.absoluteValue

@AndroidEntryPoint
class BulananPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentBulananPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            Pair(BulananPembayaranTableWrapper.TUNGGAKAN, 300),
            Pair(BulananPembayaranTableWrapper.ALOKASI, 300),
            Pair(BulananPembayaranTableWrapper.KELUNASAN, 250),
        )

        BulananPembayaranTableWrapper(binding.tablePembayaranBulanan, pembayaranBulanans)
            .setWidthColumnHeader(columnHeaderWidths)
            .createTable()
    }

    private fun setTotalTunggakan(pembayaranBulanans: List<PembayaranBulanan>) {
        val totalTunggakan = PembayaranBulanan.hitungSemuaTunggakan(pembayaranBulanans)
        val absoluteTunggakanStr = NumberUtil.formatLongToString(totalTunggakan.absoluteValue)

        val textBackgroundColor: Int
        val textTunggakan: String
        if (totalTunggakan <= 0) {
            textTunggakan = (if (totalTunggakan == 0L) "Rp" else "- Rp. ") + absoluteTunggakanStr
            textBackgroundColor = ContextCompat.getColor(requireContext(), R.color.pembayaran_bulanan_lunas)

            binding.tvTotalTunggakan.text = SpannableString(textTunggakan).apply {
                setSpan(StrikethroughSpan(), 0, textTunggakan.length, 0)
            }
        } else {
            textTunggakan = "Rp. $absoluteTunggakanStr"
            textBackgroundColor = ContextCompat.getColor(requireContext(), R.color.pembayaran_bulanan_belum_lunas_bg_total)

            binding.tvTotalTunggakan.text = textTunggakan
            binding.tvTotalTunggakan.setTextColor(ContextCompat.getColor(
                requireContext(), R.color.white
            ))
            binding.tvTotalTunggakan.typeface = Typeface.DEFAULT_BOLD
        }
        binding.layoutBackgroundPelunasan.setBackgroundColor(textBackgroundColor)
    }
}