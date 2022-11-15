package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogActionsBiayaMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentBiayaMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.ui.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.Cell
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.MyTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.RowHeader
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil

@AndroidEntryPoint
class BiayaMarketingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaMarketingBinding
    private var currentKavlingKode: String? = null
    private var areAllFabsVisible: Boolean = false

    private val viewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBiayaMarketingBinding.inflate(inflater, container, false)

        arguments?.getString("kavling_kode").let { args ->
            args?.let {
                currentKavlingKode = it
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        setupExtendedFab()

        // Setup swipe refresh layout
        binding.root.setOnRefreshListener { syncData() }

        binding.fabTambahBiayaMarketing.setOnClickListener {
            showTambahBiayaMarketingDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        syncData()
    }

    private fun syncData() {
        viewModel.getAllBiayaMarketing(
            kavlingKode = currentKavlingKode!!,
            onFailure = { failMsg ->
                Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) { finish ->
            binding.root.isRefreshing = !finish
        }

        viewModel.listBiayaMarketingLive.observe(requireActivity()) {
            if (it != null) {
                setupTableView()
            }
        }
    }

    private fun setupTableView() {
        val adapter = MyTableViewAdapter()
        val columnHeaders = viewModel.getBiayaMarketingColumnHeaders().map { ColumnHeader(it) }
        val rowHeaders = viewModel.getBiayaMarketingRowHeaders().map { RowHeader(it) }
        val cellItems = viewModel.getBiayaMarketingCellItems().map { firstOrder ->
            firstOrder.map { Cell(it) }
        }

        binding.tableviewBiayaMarketing.setAdapter(adapter)

        adapter.setAllItems(columnHeaders, rowHeaders, cellItems)
    }

    private fun setupExtendedFab() {
        binding.fabTambahBiayaMarketing.visibility = View.GONE
        binding.fabEditBiayaMarketing.visibility = View.GONE
        binding.fabActionsBiayaMarketing.shrink()

        binding.fabActionsBiayaMarketing.setOnClickListener {
            if (areAllFabsVisible) {
                binding.fabActionsBiayaMarketing.shrink()
                hideFabs()

                areAllFabsVisible = false
            } else {
                binding.fabActionsBiayaMarketing.extend()
                showFabs()

                areAllFabsVisible = true
            }
        }
    }

    private fun hideFabs() {
        binding.fabTambahBiayaMarketing.hide()
        binding.fabEditBiayaMarketing.hide()
    }

    private fun showFabs() {
        binding.fabTambahBiayaMarketing.show()
        binding.fabEditBiayaMarketing.show()
    }

    private fun showTambahBiayaMarketingDialog() {
        val dialogBinding = DialogActionsBiayaMarketingBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

        dialogBinding.edtHarga.apply {
            val harga = this.text.toString()

            if (harga != "0")
                this.setText(harga)

            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }


        dialogView.show()



        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtJenisBiaya, dialogBinding.edtHarga)

            if (!isInvalidEdt) {
                val jenisBiaya = dialogBinding.edtJenisBiaya.text.toString()
                val harga = dialogBinding.edtHarga.text.toString()

                viewModel.addBiayaMarketing(
                    kavlingKode = currentKavlingKode!!,
                    jenisBiaya = jenisBiaya,
                    harga = harga,
                    onComplete = { msg ->
                        dialogView.dismiss()
                        syncData()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }
}