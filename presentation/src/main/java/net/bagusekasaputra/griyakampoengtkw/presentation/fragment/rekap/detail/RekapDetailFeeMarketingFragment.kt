package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.evrencoskun.tableview.TableView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailFeeMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavlingRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavling_TableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailFeeMarketingFragment : Fragment() {

    private lateinit var binding: FragmentRekapDetailFeeMarketingBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapDetailFeeMarketingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFabScroll()
    }

    private fun setupViewModel() {
        viewModel.rekapBesarDetailLive.observe(requireActivity()) {
            it?.also { rekapBesarDetail ->
                binding.tableviewRekapFeeMarketing.setAllItems(rekapBesarDetail.mapFeeMarketingRekapBaru)

                val totalDataBaru = rekapBesarDetail.getTotalFeeMarketing(RekapBesarDetail.DATA_BARU)

                val rupiahTotalFeeMarketingRekapBaru = "Rp. ${NumberUtil.formatLongToString(totalDataBaru)}"
                binding.tvTotalRekap.text = rupiahTotalFeeMarketingRekapBaru

                val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama
                if (dataLamaIncluded == true) {
                    binding.layoutDataLama.visibility = View.VISIBLE
                    binding.tvInfoDataBaru.visibility = View.VISIBLE

                    binding.tableviewRekapFeeMarketingDataLama.setAllItems(rekapBesarDetail.mapFeeMarketingRekapLama)

                    val totalDataLama = rekapBesarDetail.getTotalFeeMarketing(RekapBesarDetail.DATA_LAMA)
                    val rupiahTotalFeeMarketingRekapLama = "Rp. ${NumberUtil.formatLongToString(totalDataLama)}"
                    binding.tvTotalRekapDataLama.text = rupiahTotalFeeMarketingRekapLama

                    setTotalFeeMarketingToToolbarTitle(totalDataBaru, totalDataLama)
                } else {
                    binding.layoutDataLama.visibility = View.GONE
                    binding.tvInfoDataBaru.visibility = View.GONE

                    setTotalFeeMarketingToToolbarTitle(totalDataBaru, 0L)
                }
            }
        }
    }

    private fun TableView.setAllItems(mapFeeMarketing: Map<String, FeeMarketing?>) {
        val adapter = RbdWithKavling_TableViewAdapter()
        setAdapter(adapter)

        val columnHeaders = listOf(
            RbdColumnHeader("Nama Marketer"),
            RbdColumnHeader("Tanggal Penerimaan"),
            RbdColumnHeader("Jumlah Uang"),
        )
        val rowHeaders = mutableListOf<RbdWithKavlingRowHeader>().run {
            var index = 1
            mapFeeMarketing.keys.forEach { kavling ->
                if (mapFeeMarketing[kavling] != null) {
                    add(RbdWithKavlingRowHeader(index.toString(), kavling))

                    index++
                }
            }

            this
        }
        val cellLists = mutableListOf<List<RbdCell>>().run {
            val cells = mutableListOf<RbdCell>()
            mapFeeMarketing.keys.forEach { kavling ->
                mapFeeMarketing[kavling]?.also { feeMarketing ->
                    cells.add(RbdCell(feeMarketing.namaMarketer))
                    cells.add(RbdCell(feeMarketing.tanggalPenerimaan))
                    cells.add(RbdCell(feeMarketing.biayaMarketer))

                    add(cells)
                }
            }

            this
        }
        adapter.setAllItems(columnHeaders, rowHeaders, cellLists)

        setColumnWidth(0, 400) // Nama Marketer
        setColumnWidth(1, 300) // Tanggal Penerimaan
        setColumnWidth(2, 350) // Jumlah Uang
    }

    private fun setupFabScroll() {
        val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama

        if (dataLamaIncluded == true) {
            (requireActivity() as RekapBesarDetailActivity)
                .setFabScrollingBehavior(
                    scrollView = binding.root,
                    upwardView = binding.tvInfoDataBaru,
                    downwardView = binding.tvInfoDataLama,
                )
        }
    }

    /**
     * Set RekapBesarDetailActivity's Toolbar's Title -> Total Fee Marketing
     * @param totalDataLama
     * @param totalDataBaru
     */
    private fun setTotalFeeMarketingToToolbarTitle(totalDataBaru: Long, totalDataLama: Long) {
        val totalAll = totalDataBaru + totalDataLama
        (requireActivity() as RekapBesarDetailActivity)
            .setToolbarTitle(RekapType.FeeMarketing, totalAll)
    }
}