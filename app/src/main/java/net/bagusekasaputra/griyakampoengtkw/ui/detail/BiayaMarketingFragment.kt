package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogActionsBiayaMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogPilihJenisBiayaBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentBiayaMarketingBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.ui.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.ui.detail.adapter.JenisBiayaMarketingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.Cell
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.MyTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.RowHeader
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil

@AndroidEntryPoint
class BiayaMarketingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaMarketingBinding
    private var currentKavlingKode: String? = null
    private var areAllFabsVisible: Boolean = false

    private val viewModel: DetailViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

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

        binding.fabEditBiayaMarketing.setOnClickListener {
            showJenisPembayaranSelectionDialog()
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

        viewModel.listBiayaMarketingLive.observe(requireActivity()) { listBiayaMarketing ->
            if (listBiayaMarketing != null) {
                setupTableView()
                setupHeaderText()
            } else {
                binding.tvTotalBiaya.text = "0"
                setupTableView()
            }
        }


    }

    /**
     * The total biaya marketing and Cuan texts
     */
    private fun setupHeaderText() {
        binding.tvTotalBiaya.text = NumberUtil.formatLongToString(
            viewModel.getTotalBiayaMarketing()
        )
        binding.tvCuan.text = NumberUtil.formatLongToString(
            viewModel.getCuanBiayaMarketing()
        )
    }

    private fun setupTableView() {
        binding.tableviewBiayaMarketing.invalidate()

        val adapter = MyTableViewAdapter()

        binding.tableviewBiayaMarketing.setAdapter(adapter)

        val columnHeaders = viewModel.getBiayaMarketingColumnHeaders().map { ColumnHeader(it) }
        val rowHeaders = viewModel.getBiayaMarketingRowHeaders().map { RowHeader(it) }
        val cellItems = viewModel.getBiayaMarketingCellItems().map { firstOrder ->
            firstOrder.map { Cell(it) }
        }

        adapter.setAllItems(columnHeaders, rowHeaders, cellItems)
        adapter.notifyDataSetChanged()
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
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."
                dialogBinding.btnTambahkan.isEnabled = false

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

    private fun showJenisPembayaranSelectionDialog() {
        val listBiayaMarketing = viewModel.listBiayaMarketingLive.value

        if (listBiayaMarketing != null) {
            val dialogBinding = DialogPilihJenisBiayaBinding.inflate(layoutInflater)
            val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
                setView(dialogBinding.root)
            }.create()

            DialogUtil.additionalDialogSetting(requireContext(), dialogView)
            dialogView.show()

            dialogBinding.btnBatal.setOnClickListener {
                dialogView.dismiss()
            }

            setupJenisBiayaRecyclerView(
                listBiayaMarketing = listBiayaMarketing,
                jenisBiayaDialog = dialogView,
                recyclerJenisBiaya = dialogBinding.recyclerJenisBiaya,
            )
        } else {
            Snackbar.make(binding.root, "Daftar biaya marketing masih kosong", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupJenisBiayaRecyclerView(
        listBiayaMarketing: List<BiayaMarketing>,
        jenisBiayaDialog: AlertDialog,
        recyclerJenisBiaya: RecyclerView,
    ) {
        val jenisBiayaList = ArrayList<String>()

        listBiayaMarketing.forEach { jenisBiayaList.add(it.jenisBiaya) }

        val adapter = JenisBiayaMarketingRecyclerAdapter(jenisBiayaList) {
            jenisBiayaDialog.dismiss()
            showEditBiayaMarketingDialog(listBiayaMarketing[it])
        }
        recyclerJenisBiaya.adapter = adapter
        recyclerJenisBiaya.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showEditBiayaMarketingDialog(biayaMarketing: BiayaMarketing) {
        val dialogBinding = DialogActionsBiayaMarketingBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

        // Setup edit layout
        dialogBinding.tvInfoTitleTambahBiayaMarketing.text = "Ubah Biaya Marketing"
        dialogBinding.edtJenisBiaya.setText(biayaMarketing.jenisBiaya)
        // Harga in this case isn't formatted into a comma separated value as expected
        // So I will transform here.
        dialogBinding.edtHarga.setText(NumberUtil.formatLongToString(biayaMarketing.harga.toLong()))
        dialogBinding.btnTambahkan.text = "Ubah Data"
        // Set hapus button visible
        dialogBinding.btnHapusBiayaMarketing.visibility = View.VISIBLE
        // I almost forgot to add textwatcher for comma separated value
        dialogBinding.edtHarga.apply {
            val harga = this.text.toString()

            if (harga != "0")
                this.setText(harga)

            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        dialogView.show()


        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtJenisBiaya,
                dialogBinding.edtHarga,
            )

            if (!isInvalidEdt) {
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."
                dialogBinding.btnTambahkan.isEnabled = false

                val newJenisBiaya = dialogBinding.edtJenisBiaya.text.toString()
                val newHarga = dialogBinding.edtHarga.text.toString()

                viewModel.editBiayaMarketing(
                    oldBiayaMarketing = biayaMarketing,
                    kavlingKode = currentKavlingKode!!,
                    newJenisHarga = newJenisBiaya,
                    newHarga = newHarga,
                    onComplete = { msg ->
                        dialogView.dismiss()
                        syncData()

                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    },
                )
            }
        }

        dialogBinding.btnHapusBiayaMarketing.setOnClickListener {
            dialogView.dismiss()

            val hapusAlert = MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Hapus ${biayaMarketing.jenisBiaya}?")
                setMessage("Apakah Anda yakin menghapus biaya marketing ini?")
                setPositiveButton("Ya") { hapusDialog, _ ->
                    viewModel.deleteBiayaMarketing(
                        kavlingKode = currentKavlingKode!!,
                        biayaMarketing = biayaMarketing,
                        onComplete = { msg ->
                            syncData()
                            hapusDialog.dismiss()
                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
                setNegativeButton("Tidak") { hapusDialog, _ ->
                    hapusDialog.dismiss()
                }
            }.create()

            hapusAlert.show()
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun showHapusSemuaBiayaMarketingDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Hapus Semua Biaya Marketing?")
            setMessage("Apakah Anda yakin menghapus semua biaya marketing?")
            setPositiveButton("Ya") { hapusSemuaDialog, _ ->
                viewModel.deleteAllBiayaMarketing(
                    kavlingKode = currentKavlingKode!!,
                    onComplete = { msg ->
                        syncData()
                        hapusSemuaDialog.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                )
            }
            setNegativeButton("Tidak") { hapusSemuaDialog, _ ->
                hapusSemuaDialog.dismiss()
            }
        }.create()
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_biaya_marketing, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hapus_semua_biaya_marketing -> {
                showHapusSemuaBiayaMarketingDialog()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


