package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFullPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ActionPembayaranStandardBottomSheetDialogLegacy
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class FullPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFullPembayaranBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.baselineAndFullPembayaran.observe(requireActivity()) {
            it?.also { baselineAndPembayaran ->
                val baseline = baselineAndPembayaran.first
                val pembayarans = baselineAndPembayaran.second

                if (pembayarans != null) {
                    setTablePembayaran(pembayarans)

                    if (baseline != null) {
                        setSisaWaktuAngsuran(baseline)
                        setupFullScreen(viewModel.isFullScreenTable, baseline, pembayarans)
                    }

                    binding.tvSisaBlmTerbayar.text = StringBuilder().run {
                        append("Rp. ")
                        append(Pembayaran.getSisaBelumTerbayar(pembayarans))
                        toString()
                    }
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
            ) {}

            override fun onCellLongPressed(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {}

            override fun onColumnHeaderClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {}

            override fun onColumnHeaderDoubleClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {}

            override fun onColumnHeaderLongPressed(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {}

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
                val actionDialog = ActionPembayaranStandardBottomSheetDialogLegacy()
                val positionBundle = bundleOf(
                    ActionPembayaranStandardBottomSheetDialogLegacy.EXTRAS_INDEX_PEMBAYARAN_POSITION
                            to row,
                )
                actionDialog.arguments = positionBundle

                actionDialog.show(childFragmentManager, null)

//                val pembayaranList = viewModel.fullPembayaransLive.value
//                if (pembayaranList.isNullOrEmpty()) {
//                    Snackbar.make(binding.root, "Pembayaran Masih Kosong!", Snackbar.LENGTH_SHORT)
//                        .show()
//                } else {
//                    val pembayaran = pembayaranList[row]
//                    val actionDialog = ActionPembayaranBottomSheetDialog()
//                    actionDialog.arguments = ActionPembayaranBottomSheetDialog.PembayaranPercelable
//                        .createPembayaranBundle(viewModel.currentKavlingKode!!, pembayaran)
//
//                    actionDialog.show(childFragmentManager, null)
//                }
            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {}

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {}
        }

        FullPembayaranTableWrapper(binding.tableFormPembayaran, pembayarans)
            .setTableListener(listener)
            .createTable(lifecycleScope)
    }

    private fun setupFullScreen(
        fullScreen: Boolean,
        baseline: BaselinePembayaran,
        pembayarans: List<Pembayaran>
    ) {
        if (fullScreen) {
            binding.tvInfoSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvInfoSisaBlmTerbayar.visibility = View.VISIBLE
            binding.tvSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvSisaBlmTerbayar.visibility = View.VISIBLE

            binding.tvInfoBlmDibayarBulanIni.visibility = View.VISIBLE
            binding.tvBlmDibayarBulanIni.visibility = View.VISIBLE


            binding.tvBlmDibayarBulanIni.text = pembayarans.run {
                val blmDibayarBulanIni = baseline.hitungSisaBlmBayarBulanIni(this)

                "Rp. ${NumberUtil.formatLongToString(blmDibayarBulanIni)}"
            }
        } else {
            binding.tvInfoSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvInfoSisaBlmTerbayar.visibility = View.VISIBLE
            binding.tvSisaWaktuAngsuran.visibility = View.VISIBLE
            binding.tvSisaBlmTerbayar.visibility = View.VISIBLE

            binding.tvInfoBlmDibayarBulanIni.visibility = View.GONE
            binding.tvBlmDibayarBulanIni.visibility = View.GONE
        }
    }
}