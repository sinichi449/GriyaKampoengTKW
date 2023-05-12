package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFullPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class FullPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFullPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.baselinePembayaranLive.observe(requireActivity()) {
            it?.also { baseline -> setSisaWaktuAngsuran(baseline) }
        }

        viewModel.fullPembayaransLive.observe(requireActivity()) {
            it?.also { pembayarans ->
                setTablePembayaran(pembayarans)

                binding.tvSisaBlmTerbayar.text = StringBuilder().run {
                    append("Rp. ")
                    append(Pembayaran.getSisaBelumTerbayar(pembayarans))
                    toString()
                }
            }
        }
    }

    private fun setSisaWaktuAngsuran(baselinePembayaran: BaselinePembayaran) {
        val sortedPembayarans = viewModel.fullPembayaransLive.value
        if (!sortedPembayarans.isNullOrEmpty()) {
            val sisaBulan = baselinePembayaran.hitungSisaBulanAngsuran(sortedPembayarans)
            binding.tvSisaWaktuAngsuran.text = StringBuilder().run {
                append(sisaBulan)
                append(" Bulan")
                toString()
            }
        }
    }

    private fun setTablePembayaran(pembayarans: List<Pembayaran>) {
        val widthColumnHeaders = listOf(
            Pair(FullPembayaranTableWrapper.TANGGAL, 250),
            Pair(FullPembayaranTableWrapper.UANG_DIBAYAR, 350),
            Pair(FullPembayaranTableWrapper.TOTAL, 350),
            Pair(FullPembayaranTableWrapper.PERSENTASE, 250),
            Pair(FullPembayaranTableWrapper.KETERANGAN_PROGRESS, 500),
        )
        val listener = object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                if (column == FullPembayaranTableWrapper.KETERANGAN_PROGRESS) {
                    viewModel.fullPembayaransLive.value?.also {
                        val pembayaran = it[row]
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("${viewModel.currentKavlingKode} - ${pembayaran.termin}")
                            .setMessage(pembayaran.keterangan)
                            .create()
                            .show()
                    }
                }
            }

            override fun onCellDoubleClicked(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onCellLongPressed(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onColumnHeaderClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderDoubleClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderLongPressed(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {

            }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }
        }

        FullPembayaranTableWrapper(binding.tableFormPembayaran, pembayarans)
            .setTableListener(listener)
            .setWidthColumnHeader(widthColumnHeaders)
            .createTable()
    }
}