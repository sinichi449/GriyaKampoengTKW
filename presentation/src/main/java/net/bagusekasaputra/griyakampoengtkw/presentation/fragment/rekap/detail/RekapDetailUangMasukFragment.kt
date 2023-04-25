package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailUangMasukBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RekapUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumRowHeader
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
    }

    private fun setupViewModel() {
        viewModel.rekapBesarDetailLive.observe(requireActivity()) {
            it?.also { rekapBesarDetail ->
                setupTableViewDataBaru(rekapBesarDetail.mapListPembayaranRekapBaru)
                val rupiahTotalUangMasukRekapBaru = "Rp. ${NumberUtil.formatLongToString(rekapBesarDetail.getTotalUangMasukRekapBaru())}"
                binding.tvTotalRekap.text = rupiahTotalUangMasukRekapBaru


                val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama
                if (dataLamaIncluded == true) {
                    binding.layoutDataLama.visibility = View.VISIBLE
                    binding.tvInfoDataBaru.visibility = View.VISIBLE
                    setupTableViewDataLama(rekapBesarDetail.mapListPembayaranRekapLama)

                    val rupiahTotalUangMasukRekapLama = "Rp. ${NumberUtil.formatLongToString(rekapBesarDetail.getTotalUangMasukRekapLama())}"
                    binding.tvTotalRekapDataLama.text = rupiahTotalUangMasukRekapLama
                } else {
                    binding.layoutDataLama.visibility = View.GONE
                    binding.tvInfoDataBaru.visibility = View.GONE
                }
            }
        }
    }

    private fun setupTableViewDataBaru(mapListPembayaranBaru: Map<String, List<PembayaranWithNamaCostumer>?>) {
        val adapter = RekapUangMasukTableViewAdapter()
        binding.tableviewRekapUangMasuk.setAdapter(adapter)

        val columnHeader = listOf(
            RumColumnHeader("Nama Costumer"),
            RumColumnHeader("Tanggal"),
            RumColumnHeader("Jenis Pembayaran"),
            RumColumnHeader("Jumlah Pembayaran"),
        )
        val rowHeaders = mutableListOf<RumRowHeader>().apply {
            var index = 1
            mapListPembayaranBaru.keys.forEach { kavling ->
                mapListPembayaranBaru[kavling]?.forEach {
                    add(RumRowHeader(index.toString(), kavling))

                    index++
                }
            }
        }
        val cellLists = mutableListOf<List<RumCell>>().apply {
            mapListPembayaranBaru.keys.forEach { kavling ->
                mapListPembayaranBaru[kavling]?.forEach { pembayaranWithNamaCostumer ->
                    val cells = mutableListOf<RumCell>()
                    val pembayaran = pembayaranWithNamaCostumer.pembayaran
                    cells.add(RumCell(pembayaranWithNamaCostumer.namaCostumer))
                    cells.add(RumCell(pembayaran.tanggal))
                    cells.add(RumCell(pembayaran.termin))
                    cells.add(RumCell(pembayaran.jumlahUangDibayar))

                    if (cells.isNotEmpty()) {
                        add(cells)
                    }
                }
            }
        }


        adapter.setAllItems(columnHeader, rowHeaders, cellLists)
        binding.tableviewRekapUangMasuk.apply {
            setColumnWidth(0, 350) // Nama Costumer
            setColumnWidth(1, 300) // Tanggal
            setColumnWidth(2, 300) // Jenis Pembayaran
            setColumnWidth(3, 350) // Jumlah Pembayaran
        }

        adapter.notifyDataSetChanged()
    }

    private fun setupTableViewDataLama(mapListPembayaranLama: Map<String, List<PembayaranWithNamaCostumer>?>) {
        val adapter = RekapUangMasukTableViewAdapter()
        binding.tableviewRekapUangMasukDataLama.setAdapter(adapter)

        val columnHeader = listOf(
            RumColumnHeader("Nama Costumer"),
            RumColumnHeader("Tanggal"),
            RumColumnHeader("Jenis Pembayaran"),
            RumColumnHeader("Jumlah Pembayaran"),
        )
        val rowHeaders = mutableListOf<RumRowHeader>().apply {
            var index = 1
            mapListPembayaranLama.keys.forEach { kavling ->
                mapListPembayaranLama[kavling]?.forEach {
                    add(RumRowHeader(index.toString(), kavling))

                    index++
                }
            }
        }
        val cellLists = mutableListOf<List<RumCell>>().apply {
            mapListPembayaranLama.keys.forEach { kavling ->
                mapListPembayaranLama[kavling]?.forEach { pembayaranWithNamaCostumer ->
                    val cells = mutableListOf<RumCell>()
                    val pembayaran = pembayaranWithNamaCostumer.pembayaran
                    cells.add(RumCell(pembayaranWithNamaCostumer.namaCostumer))
                    cells.add(RumCell(pembayaran.tanggal))
                    cells.add(RumCell(pembayaran.termin))
                    cells.add(RumCell(pembayaran.jumlahUangDibayar))

                    if (cells.isNotEmpty()) {
                        add(cells)
                    }
                }
            }
        }


        adapter.setAllItems(columnHeader, rowHeaders, cellLists)
        binding.tableviewRekapUangMasuk.apply {
            setColumnWidth(0, 350) // Nama Costumer
            setColumnWidth(1, 300) // Tanggal
            setColumnWidth(2, 300) // Jenis Pembayaran
            setColumnWidth(3, 350) // Jumlah Pembayaran
        }

        adapter.notifyDataSetChanged()
    }
}