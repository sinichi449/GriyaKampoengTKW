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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.SisaPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.SisaPembayaran.Companion.hitungTotal
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailSisaPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavlingRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdWithKavling_TableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailSisaPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentRekapDetailSisaPembayaranBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapDetailSisaPembayaranBinding.inflate(inflater, container, false)

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
                binding.tableviewRekapSisaPembayaran.setAllItems(rekapBesarDetail.listSisaPembayaran)

                val total = rekapBesarDetail.listSisaPembayaran.hitungTotal()
                binding.tvTotalRekap.text = "Rp. ${NumberUtil.formatLongToString(total)}"

                (requireActivity() as RekapBesarDetailActivity)
                    .setToolbarTitle(RekapType.SisaPembayaran, total)
            }
        }
    }

    private fun TableView.setAllItems(listSisaPembayaran: List<SisaPembayaran>) {
        val adapter = RbdWithKavling_TableViewAdapter(onCellTextCreated = { columnPosition, cellTextView ->
            when (columnPosition) {
                0 -> cellTextView.gravity = Gravity.START // Nama
                else -> cellTextView.gravity = Gravity.CENTER
            }
        })
        setAdapter(adapter)

        val columnHeader = listOf(
            RbdColumnHeader("Nama"),
            RbdColumnHeader("Sisa Pembayaran"),
            RbdColumnHeader("Uang Masuk"),
            RbdColumnHeader("Harga Kavling"),
            RbdColumnHeader("Tambah Luasan"),
            RbdColumnHeader("(%)"),
        )
        val rowHeaders = mutableListOf<RbdWithKavlingRowHeader>().run {
            listSisaPembayaran.forEachIndexed { index, sisaPembayaran ->
                add(RbdWithKavlingRowHeader(index.plus(1).toString(), sisaPembayaran.kavling))
            }

            this
        }
        val cellILists = mutableListOf<List<RbdCell>>().run {
            listSisaPembayaran.forEach { sisaPembayaran ->
                val cells = mutableListOf<RbdCell>()

                cells.add(RbdCell(sisaPembayaran.namaCostumer))
                cells.add(RbdCell(sisaPembayaran.getParsedSisaBelumDibayar()))
                cells.add(RbdCell(sisaPembayaran.getParsedTotalUangMasuk()))
                cells.add(RbdCell(sisaPembayaran.hargaKavling.harga))
                cells.add(RbdCell(sisaPembayaran.hargaKavling.tambahanLuas))
                cells.add(RbdCell(sisaPembayaran.getPresentase().toString()))

                add(cells)
            }
            this
        }

        adapter.setAllItems(columnHeader, rowHeaders, cellILists)

        setColumnWidth(0, 350) // Nama Costumer
        setColumnWidth(1, 350) // Sisa Pembayaran
        setColumnWidth(2, 350) // Uang Masuk
        setColumnWidth(3, 350) // Harga Kavling
        setColumnWidth(4, 350) // Tambah Luasan
        setColumnWidth(5, 250) // Persentase
    }

    private fun setupFabScroll() {
        /**
         * Temporarily disabled due to costumer's request
         */
//        val dataLamaIncluded = viewModel.rekapDetailTransportLive.value?.includeDataLama
//
//        if (dataLamaIncluded == true) {
//            (requireActivity() as RekapBesarDetailActivity)
//                .setFabScrollingBehavior(
//                    scrollView = binding.root,
//                    upwardView = binding.tvInfoDataBaru,
//                    downwardView = binding.tvInfoDataLama,
//                )
//        }
    }
}