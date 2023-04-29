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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.RekapBesarDetailActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapDetailBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail.RbdTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailBiayaLainFragment : Fragment() {

    private lateinit var binding: FragmentRekapDetailBiayaLainBinding
    private val viewModel: RekapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapDetailBiayaLainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.rekapBesarDetailLive.observe(requireActivity()) {
            it?.also { rekapBesarDetail ->
                val listBiayaLain = rekapBesarDetail.listBiayaLain
                if (listBiayaLain != null) {
                    binding.tableviewRekapBiayaLain.setAllItems(listBiayaLain)

                    val total = BiayaLain.hitungTotalBiayaLain(listBiayaLain)
                    binding.tvTotalRekap.text = "Rp. ${NumberUtil.formatLongToString(total)}"
                    (requireActivity() as RekapBesarDetailActivity)
                        .setToolbarTitle(RekapType.BiayaLain, total)
                }
            }
        }
    }

    private fun TableView.setAllItems(listBiayalain: List<BiayaLain>) {
        val adapter = RbdTableViewAdapter(onCellTextCreated = { columnPosition, cellTextView ->
            when (columnPosition) {
                0 -> cellTextView.gravity = Gravity.START
                else -> cellTextView.gravity = Gravity.CENTER
            }
        })
        setAdapter(adapter)

        val columnHeader = listOf(
            RbdColumnHeader("Jenis Biaya"),
            RbdColumnHeader("Harga"),
            RbdColumnHeader("Tanggal"),
        )
        val rowHeaders = mutableListOf<RbdRowHeader>().run {
            listBiayalain.forEachIndexed { index, _ ->
                add(RbdRowHeader(index.plus(1).toString()))
            }

            this
        }
        val cellLists = mutableListOf<List<RbdCell>>().run {
            listBiayalain.forEach { biayaLain ->
                val cells = mutableListOf<RbdCell>()
                cells.add(RbdCell(biayaLain.jenisBiaya))
                cells.add(RbdCell(biayaLain.parsedHarga))
                cells.add(RbdCell(biayaLain.tanggal))

                add(cells)
            }

            this
        }
        adapter.setAllItems(columnHeader, rowHeaders, cellLists)

        setColumnWidth(0, 500) // Jenis Biaya
        setColumnWidth(1, 300) // Harga
        setColumnWidth(2, 300) // Tanggal
    }
}