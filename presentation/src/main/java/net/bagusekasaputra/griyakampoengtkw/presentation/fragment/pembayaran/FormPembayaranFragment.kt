package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertFormPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.PembayaranTabelFullActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.StatusPembayaranLayoutHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.TabelPembayaranNavHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.*
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.FormBaselinePembayaranDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.CellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.ColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.GenericTableView
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.RowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.TableViewDataProvider
import net.bagusekasaputra.griyakampoengtkw.presentation.util.*
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil.additionalDialogSetting
import net.bagusekasaputra.griyakampoengtkw.presentation.util.exporter.ExporterWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel.TablePembayaranType
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembayaranSyncRequest
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    companion object {
//        private const val WRITE_CSV_PERMISSION_REQUEST_CODE = 250
    }

    private lateinit var binding: FragmentFormPembayaranBinding

    private val viewModel: DetailViewModel by activityViewModels()
    private val pembayaranViewModel: FormPembayaranViewModel by activityViewModels()

    private val requestTambahFormPembayaran = 901
    private var currentKavlingKode: String? = null
    private var layoutStatusPembayaran: StatusPembayaranLayoutHelper? = null

    private var offlineMode = false

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private val startStorageRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val orientation = requireActivity().resources.configuration.orientation
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            onFullScreenLandscapeMode()
//        }

        arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)?.let {
            currentKavlingKode = it
            // Current Kavling Kode
            pembayaranViewModel.currentKavlingKode = it
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFormPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        pembayaranViewModel.requestSync(*PembayaranSyncRequest.ALL)

        // Disable write operation interfaces on offline mode such as
        // edit HargaKavling and CatatanPembayaran, and disable Fabs.
        offlineMode = viewModel.offlineMode
        val dataMode = viewModel.dataMode
        if (offlineMode || dataMode == DataMode.DATA_LAMA)
            onOfflineState()

        binding.layoutStatusPembayaran?.also {
            layoutStatusPembayaran = StatusPembayaranLayoutHelper(it)
        }

        binding.imgEdit?.setOnClickListener {
            showEditHargaDialog()
        }

        binding.swipeRefreshFormPembayaran.setOnRefreshListener {
            // When user invokes refresh, we need to update the "xRefreshed" value in viewModel
            // to be FALSE.
            viewModel.formPembayaranRefreshed.value = false

            pembayaranViewModel.requestSync(*PembayaranSyncRequest.ALL)
        }

        binding.cardCatatanPembayaran?.setOnClickListener {
            val currentCatatan = binding.tvCatatan?.text.toString()
            val tidakAdaCatatan = requireContext().getString(R.string.tidak_ada_catatan)

            // If tidak ada catatan, then open the add catatan, which means we need to disable
            // delete button
            if (currentCatatan == tidakAdaCatatan) {
                showActionsCatatanDialog(editMode = false)
            } else {
                showActionsCatatanDialog(editMode = true)
            }
        }

        binding.layoutTitleAngsuranBulanan?.setOnClickListener {
            val hargaKavling = viewModel.hargaKavlingLive.value

            if (hargaKavling != null) {
                val baselinePembayaran = pembayaranViewModel.baselinePembayaranLive.value
                val tanggalPembelian = pembayaranViewModel.fullPembayaransLive.value?.let {
                    Pembayaran.getTanggalPembelian(it)
                }

                FormBaselinePembayaranDialog(
                    currentKavlingKode!!, hargaKavling,
                    baselinePembayaran, tanggalPembelian,
                ).show(childFragmentManager, null)
            } else {
                Snackbar.make(binding.root, "Harga Kavling masih kosong!", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Even when I already set the visibility of FabAction into View.GONE,
        // to prevent the user from writing the data on offline mode, it's still
        // showing when I scroll the screen.
        // So, I put the conditional here for the scroll operation.
        if (offlineMode.not())
            // Hide fabs on scroll
            UiUtils.hideFabsOnVerticalScroll(
                nestedScrollView = binding.nestedScrollMain,
                fab = binding.fabActions,
            )

        startStorageRequest.launch(Array(2) {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                Manifest.permission.READ_EXTERNAL_STORAGE
            })


        // Prevent showing Sisa Belum dibayar bulan ini
        pembayaranViewModel.isFullScreenTable = false

        binding.btnFullscreen?.setOnClickListener {
            val intent = Intent(requireContext(), PembayaranTabelFullActivity::class.java)
            intent.putExtra(
                PembayaranTabelFullActivity.EXTRAS_KAVLING_KODE,
                pembayaranViewModel.currentKavlingKode!!
            )

            requireContext().startActivity(intent)
        }

        val tabelHelper = TabelPembayaranNavHelper(
            lifecycleOwner = requireActivity(),
            fragmentManager = childFragmentManager,
            pembayaranViewModel = pembayaranViewModel,
            containerId = R.id.navHostFragment_pembayaran,
            triggerViews = arrayOf(
                binding.btnLihatPembayaranBulanan,
                binding.fabLihatPembayaranBulanan,
            )
        )
        tabelHelper.listener = object : TabelPembayaranNavHelper.TabelPembayaranListener {
            override fun onTabelChanged(tableType: TablePembayaranType) {
                binding.btnLihatPembayaranBulanan?.text = when (tableType) {
                    TablePembayaranType.PEMBAYARAN_BULANAN -> "Semua"
                    TablePembayaranType.FORM_PEMBAYARAN -> "Per Bulan"
                }
            }
        }


        binding.fabActions?.setOnClickListener {
            val totalHargaKavling = viewModel.hargaKavlingLive.value?.hargaDanTambahLuasan ?: 0L
            val isEmptyHargaKavling =  totalHargaKavling <= 0L

            if (isEmptyHargaKavling) {
                Toast.makeText(requireContext(), "Harga kavling masih kosong", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Go to FormActivity
                val intent = Intent(requireContext(), FormActivity::class.java)
                val insertFormPembayaranParcel = InsertFormPembayaranParcel(
                    tipePembayaran = Pembayaran.PEMBAYARAN_KAVLING,
                    kavling = pembayaranViewModel.currentKavlingKode!!,
                )

                intent.putExtra(FormActivity.EXTRAS_PARCEL, insertFormPembayaranParcel)
                @Suppress("DEPRECATION")
                startActivityForResult(intent, requestTambahFormPembayaran)
            }
        }
    }

    private fun setupViewModel() {
        pembayaranViewModel.syncRequests.observe(requireActivity()) {
            it?.onEach { requestCode ->
                when (requestCode) {
                    PembayaranSyncRequest.HARGA_KAVLING -> {
                        viewModel.getHargaKavling(currentKavlingKode!!) { failMsg ->
                            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
                        }
                    }

                    PembayaranSyncRequest.BASELINE_PEMBAYARAN, PembayaranSyncRequest.TABEL_PEMBAYARAN -> {
                        pembayaranViewModel.getListPembayaranBulanan(currentKavlingKode!!,
                            onLoading = {
                                onLoadingFormPembayaran(false)
                            },
                            onSuccess = {
                                onLoadingFormPembayaran(true)
                            },
                            onFailure = { failMsg ->
                                Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    PembayaranSyncRequest.CATATAN_PEMBAYARAN -> {
                        pembayaranViewModel.getCatatanPembayaran(currentKavlingKode!!) { failMsg ->
                            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    PembayaranSyncRequest.STATUS_PEMBAYARAN -> {
                        pembayaranViewModel.getStatusPembayaran(
                            currentKavlingKode!!,
                            onLoading = {
                                layoutStatusPembayaran?.onLoading()
                            },
                            onFailure = { failMsg ->
                                layoutStatusPembayaran?.onFailure()

                                Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
                            },
                        )
                    }

                    PembayaranSyncRequest.TAMBAHAN_PEMBAYARAN -> {
                        pembayaranViewModel.getAllTambahanLuasPembayaran(currentKavlingKode!!)
                    }
                }
            }
        }

        viewModel.isFinishOperation.observe(requireActivity()) { finish ->
            finish?.let {
                binding.swipeRefreshFormPembayaran.isRefreshing = !it
            }
        }

        pembayaranViewModel.statusPembayaranLive.observe(requireActivity()) {
            it?.also { statusPembayaran ->
                layoutStatusPembayaran?.onSuccess(statusPembayaran)
            }
        }

        viewModel.hargaKavlingLive.observe(requireActivity()) { hargaKavling ->
            if (hargaKavling == null) {
                viewModel.getHargaKavling(currentKavlingKode!!) { failMsg ->
                    Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
                }
            } else {
                binding.tvHarga?.text = hargaKavling.harga
                binding.tvTambahanLuas?.text = hargaKavling.tambahanLuas

                (NumberUtil.formatStringToLong(hargaKavling.harga) + NumberUtil.formatStringToLong(hargaKavling.tambahanLuas)).let {
                    binding.tvTotalHarga?.text = NumberUtil.formatLongToString(it)
                }
            }
        }

        pembayaranViewModel.catatanPembayaranLive.observe(requireActivity()) { catatanPembayaran ->
            if (catatanPembayaran != null) {
                binding.tvCatatan?.text = catatanPembayaran.content
            } else {
                binding.tvCatatan?.text = requireContext().getString(R.string.tidak_ada_catatan)
            }
        }

        pembayaranViewModel.fullPembayaransLive.observe(requireActivity()) {j ->
            j?.also { listPembayaran ->
                pembayaranViewModel.baselinePembayaranLive.observe(requireActivity()) { k ->
                    k?.also { baselinePembayaran ->
                        setAngsuranBulananDetail(listPembayaran, baselinePembayaran)
                    }
                }
            }
        }

        pembayaranViewModel.tambahanLuasPembayaran.observe(requireActivity()) { k ->
            if (k != null) {
                binding.layoutTabelTambahanLuasan?.visibility = View.VISIBLE

                when (k) {
                    is UiState.Loading -> {
                        binding.tvInfoLoadingTambahanLuasan?.text = "Sedang memuat ..."
                    }
                    is UiState.Failure -> {
                        binding.tvInfoLoadingTambahanLuasan?.text = "Terjadi kesalahan!"
                    }
                    is UiState.Success -> {
                        binding.tvInfoLoadingTambahanLuasan?.visibility = View.GONE
                        if (!k.data.isNullOrEmpty()) {
                            // override k.data with dummy list if
                            binding.tableviewTambahanLuasan?.visibility = View.VISIBLE
                            setupTableTambahanLuasan(k.data)

                            val total = k.data.sumOf { it.jumlahUang }
                                .numericToString()
                            binding.tvTotalTambahLuasan?.visibility = View.VISIBLE
                            binding.tvTotalTambahLuasan?.text = total
                        }
                    }
                }
            } else {
                binding.layoutTabelTambahanLuasan?.visibility = View.GONE
            }
        }
    }

    private fun onLoadingFormPembayaran(finished: Boolean) {
        binding.layoutLoadingFormPembayaran.visibility = if (finished) View.GONE else View.VISIBLE
        binding.navHostFragmentPembayaran.visibility = if (finished) View.VISIBLE else View.GONE

        binding.btnLihatPembayaranBulanan?.visibility = if (finished) View.VISIBLE else View.GONE
        binding.fabLihatPembayaranBulanan?.visibility = if (finished) View.VISIBLE else View.GONE

        binding.btnFullscreen?.visibility = if (finished) View.VISIBLE else View.GONE
    }

    private fun setAngsuranBulananDetail(listPembayaran: List<Pembayaran>, baselinePembayaran: BaselinePembayaran) {
        binding.tvMinimalAngsuran?.text = "Rp. ${baselinePembayaran.parsedJumlahUang}"
        binding.tvMaksimalTanggalBayar?.text = baselinePembayaran.tanggalPembayaranMaks.toString()

        // Disabling sisa belum bayar if entry hanya berisi ITJ
        if (listPembayaran.last().termin == "ITJ 1") {
            binding.tvSisaBelumBayarBulanIni?.visibility = View.GONE
            binding.tvInfoSisaBelumBayarBulanIni?.visibility = View.GONE
        } else {
            binding.tvSisaBelumBayarBulanIni?.visibility = View.VISIBLE
            binding.tvInfoSisaBelumBayarBulanIni?.visibility = View.VISIBLE

            try {
                binding.tvSisaBelumBayarBulanIni?.text = listPembayaran.run {
                    val sisaBelumBayar = baselinePembayaran.hitungSisaBlmBayarBulanIni(this)

                    "Rp. ${NumberUtil.formatLongToString(sisaBelumBayar)}"
                }
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(requireContext(), "Terjadi kesalahan : ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEditHargaDialog() {
        val dialogBinding = DialogEditHargaBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        additionalDialogSetting(requireContext(), dialogView)
        dialogView.show()

        dialogBinding.edtHarga.apply {
            val harga = binding.tvHarga?.text
            if (harga != "0")
                this.setText(harga)
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }
        dialogBinding.edtTambahLuasan.apply {
            val tambahanLuas = binding.tvTambahanLuas?.text
            if (tambahanLuas != "0") this.setText(tambahanLuas)

            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            dialogBinding.btnTambahkan.isEnabled = false
            dialogBinding.btnTambahkan.text = "Menyimpan data ..."

            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtHarga)

            if (!isInvalidEdt) {
                val harga = dialogBinding.edtHarga.text.toString()
                var hargaKavling = HargaKavling(currentKavlingKode!!, harga, "0")

                dialogBinding.edtTambahLuasan.text.let {
                    if (!it.isNullOrBlank()) {
                        val tambahLuasan = it.toString()
                        hargaKavling = HargaKavling(currentKavlingKode!!, harga, tambahLuasan)
                    }
                }

                viewModel.addHargaKavling(hargaKavling) { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                    pembayaranViewModel.requestSync(
                        PembayaranSyncRequest.HARGA_KAVLING,
                        PembayaranSyncRequest.TABEL_PEMBAYARAN,
                    )

                    dialogView.dismiss()
                }

            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun setupTableTambahanLuasan(pembayaranTambahLuasanList: List<PembayaranTambahLuasan>) {
        if (pembayaranTambahLuasanList.isNotEmpty() && binding.tableviewTambahanLuasan != null) {
            val Columns = object {
                val TANGGAL = 0
                val JUMLAH_UANG = 1
                val KETERANGAN = 2
            }
            val widthColumnHeaders = listOf(
                Pair(Columns.TANGGAL, 250),
                Pair(Columns.JUMLAH_UANG, 350),
                Pair(Columns.KETERANGAN, 500),
            )

            GenericTableView(binding.tableviewTambahanLuasan!!, pembayaranTambahLuasanList)
                .setDataProvider(object : TableViewDataProvider<PembayaranTambahLuasan> {
                    override fun getColumnHeaders(data: Collection<PembayaranTambahLuasan>): List<ColumnHeader> {
                        return buildList {
                            add(ColumnHeader("Tanggal"))
                            add(ColumnHeader("Jumlah Uang"))
                            add(ColumnHeader("Keterangan"))
                        }
                    }

                    override fun getRowHeaders(data: Collection<PembayaranTambahLuasan>): List<RowHeader> {
                        return buildList {
                            (1 .. data.size).forEach {
                                val result = it.toString()
                                add(RowHeader(result, result))
                            }
                        }
                    }

                    override fun getCellItems(data: Collection<PembayaranTambahLuasan>): List<List<CellItem>> {
                        val list = data.toMutableList()
                        return buildList {
                            list.forEachIndexed { index, item ->
                                val cellId = index.plus(1).toString()
                                val cell = mutableListOf<CellItem>()

                                cell.add(CellItem(cellId, item.tanggal))
                                cell.add(CellItem(cellId, item.jumlahUang.numericToString()))
                                cell.add(CellItem(cellId, item.keterangan))

                                add(cell)
                            }
                        }
                    }

                })
                .setWidthColumnHeaders(widthColumnHeaders)
                .setOnCellBinding { cellViewHolder, cellItem, col, row ->
                    if (col == Columns.KETERANGAN) {
                        cellViewHolder.tvCell.gravity = Gravity.START
                    } else {
                        cellViewHolder.tvCell.gravity = Gravity.CENTER
                    }
                }
                .create()
        }
    }

    private fun showActionsCatatanDialog(editMode: Boolean) {
        val dialogBinding = DialogActionCatatanPembayaranBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        additionalDialogSetting(requireContext(), dialogView)

        // if Edit Mode, ENABLE the Delete Button, set the text as the one before,and change the Dialog Title
        if (editMode) {
            dialogBinding.tvInfoTitleTambahCatatan.text = "Ubah Catatan"
            dialogBinding.edtCatatan.setText(binding.tvCatatan?.text)
            dialogBinding.btnHapusCatatan.visibility = View.VISIBLE
        }

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtCatatan)

            if (!isInvalidEdt) {
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."
                dialogBinding.btnTambahkan.isEnabled = false

                val catatan = dialogBinding.edtCatatan.text.toString()

                pembayaranViewModel.addCatatanPembayaran(
                    kavlingKode = currentKavlingKode!!,
                    catatan = catatan,
                    onComplete = { msg ->
                        pembayaranViewModel.requestSync(PembayaranSyncRequest.CATATAN_PEMBAYARAN)

                        dialogView.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialogBinding.btnHapusCatatan.setOnClickListener {
            pembayaranViewModel.deleteCatatanPembayaran(
                kavlingKode = currentKavlingKode!!,
                onComplete = { msg ->
                    pembayaranViewModel.requestSync(PembayaranSyncRequest.CATATAN_PEMBAYARAN)

                    dialogView.dismiss()
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("@Suppress(\"DEPRECATION\") super.onActivityResult(requestCode, resultCode, data)", "androidx.fragment.app.Fragment"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestTambahFormPembayaran) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(binding.root, "Berhasil menambahkan pembayaran !", Snackbar.LENGTH_SHORT)
                    .show()

                pembayaranViewModel.requestSync(PembayaranSyncRequest.TABEL_PEMBAYARAN)
            } else {
                data?.extras?.getString(FormActivity.EXTRAS_FAIL_MSG)?.also {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pembayaran, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hapus_semua_pembayaran -> {
                if (offlineMode) {
                    showDialogOnOfflineMode()
                } else {
                    // Show confirmation to delete all Pembayaran.
                    // This is also showing an alert that this action will delete the Foto Pembayaran too.
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hapus Semua Pembayaran")
                        .setMessage("Apakah Anda yakin ingin menghapus semua pembayaran di kavling $currentKavlingKode?")
                        .setPositiveButton("Ya") { dialogPembayaran, _ ->
                            dialogPembayaran.dismiss()

                            // Show the confirmation to delete all Foto Pembayaran
//                            MaterialAlertDialogBuilder(requireContext())
//                                .setTitle("Hapus Semua Foto Pembayaran?")
//                                .setMessage("Perhatian! Menghapus seluruh Pembayaran juga akan menghapus seluruh Foto Pembayaran yang tersimpan. Apakah Anda yakin?")
//                                .setPositiveButton("Ya") { dialogFoto, _ ->
//                                    dialogFoto.dismiss()
//
//                                    viewModel.deleteAllPembayaran(
//                                        kavlingKode = currentKavlingKode!!,
//                                        onComplete = { msg ->
//                                            dialogFoto.dismiss()
//                                            syncPembayaran()
//                                            Toast.makeText(
//                                                requireContext(),
//                                                msg,
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                    )
//                                    viewModel.deleteAllPembayaran(currentKavlingKode!!) { msg ->
//
//                                    }
//                                }
//                                .setNegativeButton("Tidak") { dialogFoto, _ ->
//                                    dialogFoto.dismiss()
//                                }
//                                .create()
//                                .show()

                            MaterialAlertDialogBuilder(requireContext()).apply {
                                setTitle("Fitur Dinonaktifkan")
                                setMessage("Mohon maaf, akses untuk menghapus seluruh pembayaran tidak diizinkan. Hubungi developer untuk informasi lebih lanjut")
                            }.create()
                                .show()
                        }
                        .setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }

                true
            }
            R.id.tambahkan_foto -> {
                // Tambahkan foto will ask for confirmation to overwrite the
                // existing Foto Pembayaran if it already Exists.
                Toast.makeText(requireContext(), "Dipindahkan!", Toast.LENGTH_SHORT).show()

                true
            }
            R.id.ubah_foto -> {
                Toast.makeText(requireContext(), "Dipindahkan!", Toast.LENGTH_SHORT).show()

                true
            }
            R.id.lihat_foto -> {
                Toast.makeText(requireContext(), "Dipindahkan!", Toast.LENGTH_SHORT).show()

                true
            }
            R.id.hapus_foto -> {
                Toast.makeText(requireContext(), "Dipindahkan!", Toast.LENGTH_SHORT).show()

                true
            }
            R.id.export_excel -> {
                exportExcel()
                true
            }
            R.id.share_ui_pembayaran -> {
                shareUiPembayaran()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun onRadioButtonJenisPembayaranClick(dialogBinding: DialogAddFormPembayaranBinding) {
        val pembayaranList = pembayaranViewModel.fullPembayaransLive.value

        dialogBinding.rbItj.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                Pembayaran.nextPembayaranSequence(pembayaranList, Pembayaran.JenisTermin.ITJ)
            )
//            dialogBinding.tilTermin.hint = "Masukkan urutan ITJ"
        }
        dialogBinding.rbDp.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                Pembayaran.nextPembayaranSequence(pembayaranList, Pembayaran.JenisTermin.DP)
            )
//            dialogBinding.tilTermin.hint = "Masukkan urutan DP"
        }
        dialogBinding.rbTermin.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                Pembayaran.nextPembayaranSequence(pembayaranList, Pembayaran.JenisTermin.TERMIN)
            )

            // Set automatic Jumlah Uang Dibayar for Termin type
            val jumlahUangDibayar = pembayaranViewModel.getTerminJumlahUangDibayar()
            if (jumlahUangDibayar != null) {
                dialogBinding.edtJumlahUangDibayar.setText(jumlahUangDibayar)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun imgVisibilityOnClick(dialogView: AlertDialog, dialogBinding: DialogAddFormPembayaranBinding) {
        dialogBinding.imgVisibility.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                dialogBinding.root.alpha = 0.0f
                dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                dialogBinding.root.alpha = 1.0f
                dialogView.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.background_rounded_dialog))
            }

            true
        }
    }

    private fun exportExcel() {
        val kavlingKode = pembayaranViewModel.currentKavlingKode!!
        val dataDiri = viewModel.dataDiriLive.value ?: DataDiri("N/A", "KTP", "0000", "N/A", "N/A", "N/A", "N/A")
        val hargaKavling = viewModel.hargaKavlingLive.value ?: HargaKavling(kavlingKode, "0", "0")
        val pembayarans = pembayaranViewModel.fullPembayaransLive.value ?: emptyList()

        ExporterWrapper.exportPembayaran(requireContext(),
            kavlingKode, dataDiri, hargaKavling, pembayarans
        )
    }

    private fun shareUiPembayaran() {
        // TODO
    }

    private fun onOfflineState() {
        // Disable edit Harga icon
        binding.imgEdit?.visibility = View.GONE
        // Disable edit Catatan icon
        binding.imgEditCatatan?.visibility = View.GONE
        // Hide FABS
        binding.fabActions?.visibility = View.GONE
    }

    /**
     * This AlertDialog will appear when the user trying to interact with
     * operations interface when the offlineMode is enabled in the Pengaturan.
     */
    private fun showDialogOnOfflineMode() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nonaktifkan Mode Offline")
            .setMessage("Pada mode offline, Anda tidak dapat melakukan operasi penambahan atau penghapusan data. Untuk melakukan operasi ini, silakan nonaktifkan Mode Offline pada layar Pengaturan.")
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}