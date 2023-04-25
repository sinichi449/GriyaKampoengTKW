package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
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
            }
        }
    }

    private fun setupTableViewDataBaru(mapListPembayaranBaru: Map<String, List<Pembayaran>?>) {
        val adapter = RekapUangMasukTableViewAdapter()
        binding.tableviewRekapUangMasuk.apply {
            setAdapter(adapter)
        }

        val columnHeader = listOf(
            RumColumnHeader("Tanggal"),
            // TODO: Nama Costumer
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
                mapListPembayaranBaru[kavling]?.forEach { pembayaran ->
                    val cells = mutableListOf<RumCell>()

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
            setColumnWidth(0, 300) // Tanggal
            setColumnWidth(1, 300) // Jenis Pembayaran
            setColumnWidth(2, 350) // Jumlah Pembayaran
        }

        adapter.notifyDataSetChanged()
    }
}