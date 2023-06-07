package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.detail

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.evrencoskun.tableview.TableView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailUangMasukBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavlingRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavling_TableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailUangMasukFragment : Fragment() {

    private lateinit var binding: FragmentRekapDetailUangMasukBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapDetailUangMasukBinding.inflate(inflater, container, false)

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
                binding.tableviewRekapUangMasuk.setAllItems(rekapBesarDetail.mapListPembayaranRekapBaru)

                val totalDataBaru = rekapBesarDetail.getTotalUangMasukPembayaran(RekapBesarDetail.DATA_BARU)
                val rupiahTotalUangMasukRekapBaru = "Rp. ${NumberUtil.formatLongToString(totalDataBaru)}"
                binding.tvTotalRekap.text = rupiahTotalUangMasukRekapBaru

                val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama
                if (dataLamaIncluded == true) {
                    binding.layoutDataLama.visibility = View.VISIBLE
                    binding.tvInfoDataBaru.visibility = View.VISIBLE

                    binding.tableviewRekapUangMasukDataLama.setAllItems(rekapBesarDetail.mapListPembayaranRekapLama)

                    val totalDataLama = rekapBesarDetail.getTotalUangMasukPembayaran(RekapBesarDetail.DATA_LAMA)
                    val rupiahTotalUangMasukRekapLama = "Rp. ${NumberUtil.formatLongToString(totalDataLama)}"
                    binding.tvTotalRekapDataLama.text = rupiahTotalUangMasukRekapLama

                    setTotalUangMasukToToolbarTitle(totalDataBaru, totalDataLama)
                } else {
                    binding.layoutDataLama.visibility = View.GONE
                    binding.tvInfoDataBaru.visibility = View.GONE

                    setTotalUangMasukToToolbarTitle(totalDataBaru, 0L)
                }
            }
        }
    }

    private fun TableView.setAllItems(mapListPembayaranWithNamaCostumer: Map<String, List<PembayaranWithNamaCostumer>?>) {
        val adapter = RbdWithKavling_TableViewAdapter(onCellTextCreated = { position, tvCell ->
            if (position == 0) {
                tvCell.gravity = Gravity.START
            } else {
                tvCell.gravity = Gravity.CENTER
            }
        })
        setAdapter(adapter)

        val columnHeader = listOf(
            RbdColumnHeader("Nama Costumer"),
            RbdColumnHeader("Tanggal"),
            RbdColumnHeader("Jenis Pembayaran"),
            RbdColumnHeader("Jumlah Pembayaran"),
            RbdColumnHeader("Invoice")
        )
        val rowHeaders = mutableListOf<RbdWithKavlingRowHeader>().run {
            var index = 1
            mapListPembayaranWithNamaCostumer.keys.forEach { kavling ->
                mapListPembayaranWithNamaCostumer[kavling]?.forEach {
                    add(RbdWithKavlingRowHeader(index.toString(), kavling))

                    index++
                }
            }

            this
        }
        val cellLists = mutableListOf<List<RbdCell>>().run {
            mapListPembayaranWithNamaCostumer.keys.forEach { kavling ->
                mapListPembayaranWithNamaCostumer[kavling]?.forEach { pembayaranWithNamaCostumer ->
                    val cells = mutableListOf<RbdCell>()
                    val pembayaran = pembayaranWithNamaCostumer.pembayaran
                    cells.add(RbdCell(pembayaranWithNamaCostumer.namaCostumer))
                    cells.add(RbdCell(pembayaran.tanggal))
                    cells.add(RbdCell(pembayaran.termin))
                    cells.add(RbdCell(pembayaran.jumlahUangDibayar))
                    cells.add(RbdCell(pembayaran.bulanAngsuran.bulanAndTahun))

                    if (cells.isNotEmpty()) {
                        add(cells)
                    }
                }
            }

            this
        }
        adapter.setAllItems(columnHeader, rowHeaders, cellLists)

        setColumnWidth(0, 350) // Nama Costumer
        setColumnWidth(1, 300) // Tanggal
        setColumnWidth(2, 300) // Jenis Pembayaran
        setColumnWidth(3, 350) // Jumlah Pembayaran
        setColumnWidth(4, 250) // Invoice
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
     * Set RekapBesarDetailActivity's Toolbar's Title -> Total Uang Masuk
     * @param totalDataLama
     * @param totalDataBaru
     */
    private fun setTotalUangMasukToToolbarTitle(totalDataBaru: Long, totalDataLama: Long) {
        val totalAll = totalDataBaru + totalDataLama
        (requireActivity() as RekapBesarDetailActivity)
            .setToolbarTitle(RekapType.UangMasuk, totalAll)
    }
}