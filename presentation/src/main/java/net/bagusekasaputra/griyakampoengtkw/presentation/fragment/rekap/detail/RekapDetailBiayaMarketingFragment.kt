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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailBiayaMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavlingRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavling_TableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailBiayaMarketingFragment : Fragment() {

    private lateinit var binding: FragmentRekapDetailBiayaMarketingBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapDetailBiayaMarketingBinding.inflate(inflater, container, false)

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
                binding.tableviewRekapBiayaMarketing.setAllItems(rekapBesarDetail.mapListBiayaMarketingRekapBaru)

                val totalDataBaru = rekapBesarDetail.getTotalBiayaMarketing(RekapBesarDetail.DATA_BARU)
                val rupiahTotalBiayaMarketingBaru = "Rp. ${NumberUtil.formatLongToString(totalDataBaru)}"
                binding.tvTotalRekap.text = rupiahTotalBiayaMarketingBaru

                val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama
                if (dataLamaIncluded == true) {
                    binding.layoutDataLama.visibility = View.VISIBLE
                    binding.tvInfoDataBaru.visibility = View.VISIBLE

                    binding.tableviewRekapBiayaMarketingDataLama.setAllItems(rekapBesarDetail.mapListBiayaMarketingRekapLama)

                    val totalDataLama = rekapBesarDetail.getTotalBiayaMarketing(RekapBesarDetail.DATA_LAMA)
                    val rupiahTotalBiayaMarketingLama = "Rp. ${NumberUtil.formatLongToString(totalDataLama)}"
                    binding.tvTotalRekapDataLama.text = rupiahTotalBiayaMarketingLama

                    setTotalBiayaMarketingToToolbarTitle(totalDataBaru, totalDataLama)
                } else {
                    binding.layoutDataLama.visibility = View.GONE
                    binding.tvInfoDataBaru.visibility = View.GONE

                    setTotalBiayaMarketingToToolbarTitle(totalDataBaru, 0L)
                }
            }
        }
    }

    private fun TableView.setAllItems(mapListBiayaMarketing: Map<String, List<BiayaMarketing>?>) {
        val adapter = RbdWithKavling_TableViewAdapter()
        setAdapter(adapter)

        val columnHeaders = listOf(
            RbdColumnHeader("Jenis Biaya"),
            RbdColumnHeader("Tanggal"),
            RbdColumnHeader("Harga"),
        )
        val rowHeaders = mutableListOf<RbdWithKavlingRowHeader>().run {
            var index = 1
            mapListBiayaMarketing.keys.forEach { kavling ->
                mapListBiayaMarketing[kavling]?.forEach {
                    add(RbdWithKavlingRowHeader(index.toString(), kavling))

                    index++
                }
            }
            this
        }
        val cellLists = mutableListOf<List<RbdCell>>().run {
            mapListBiayaMarketing.keys.forEach { kavling ->
                mapListBiayaMarketing[kavling]?.forEach { biayaMarketing ->
                    val cells = mutableListOf<RbdCell>()

                    cells.add(RbdCell(biayaMarketing.jenisBiaya))
                    cells.add(RbdCell(biayaMarketing.tanggal))
                    cells.add(RbdCell(biayaMarketing.harga))

                    add(cells)
                }
            }

            this
        }
        adapter.setAllItems(columnHeaders, rowHeaders, cellLists)

        setColumnWidth(0, 500) // Jenis Biaya
        setColumnWidth(1, 300) // Tanggal
        setColumnWidth(2, 350) // Harga
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
     * Set RekapBesarDetailActivity's Toolbar's Title -> Total Biaya Marketing
     * @param totalDataLama
     * @param totalDataBaru
     */
    private fun setTotalBiayaMarketingToToolbarTitle(totalDataBaru: Long, totalDataLama: Long) {
        val totalAll = totalDataBaru + totalDataLama
        (requireActivity() as RekapBesarDetailActivity)
            .setToolbarTitle(RekapType.UangMasuk, totalAll)
    }
}