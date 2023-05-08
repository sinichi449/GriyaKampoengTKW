package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.TerminRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.*
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.PembayaranCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.PembayaranColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.PembayaranRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.PembayaranTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.util.*
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil.additionalDialogSetting
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    companion object {
        private const val WRITE_CSV_PERMISSION_REQUEST_CODE = 250
    }

    private lateinit var binding: FragmentFormPembayaranBinding
    private val viewModel: DetailViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private val pembayaranViewModel: FormPembayaranViewModel by activityViewModels()

    private var currentKavlingKode: String? = null
    private var isAllFabsVisible = false

    private var offlineMode = false

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private val startForFotoPembayaranResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val intent = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = intent?.data

                    uri?.let {
                        NotificationUtil.createNotification(
                            activity = requireActivity(),
                            title = "Upload Foto Pembayaran",
                            content = "Mohon tunggu sebentar ...",
                            finished = false,
                        )
                        imageViewModel.addFotoPembayaran(
                            kavlingKode = currentKavlingKode!!,
                            uri = it,
                            onComplete = { msg ->
                                syncPembayaran()
                                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(intent), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private val startStorageRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val orientation = requireActivity().resources.configuration.orientation
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            onFullScreenLandscapeMode()
//        }

        arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)?.let {
            currentKavlingKode = it
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

        File(requireContext().getExternalFilesDir(null), "foto_pembayaran_images").let {
            if (it.exists().not()) it.mkdir()
        }

        setupExtendedFloatingButton()

        setupViewModel()

        // Disable write operation interfaces on offline mode such as
        // edit HargaKavling and CatatanPembayaran, and disable Fabs.
        offlineMode = viewModel.offlineMode
        val dataMode = viewModel.dataMode
        if (offlineMode || dataMode == DataMode.DATA_LAMA)
            onOfflineState()

        binding.imgEdit?.setOnClickListener {
            showEditHargaDialog()
        }

        binding.swipeRefreshFormPembayaran.setOnRefreshListener {
            // When user invokes refresh, we need to update the "xRefreshed" value in viewModel
            // to be FALSE.
            viewModel.formPembayaranRefreshed.value = false
            syncPembayaran()
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

        binding.layoutUangBaselineAngsuran?.setOnClickListener {
            showSetBaselinePembayaranDialog()
        }

        // Even when I already set the visibility of FabAction into View.GONE,
        // to prevent the user from writing the data on offline mode, it's still
        // showing when I scroll the screen.
        // So, I put the conditional here for the scroll operation.
        if (offlineMode.not())
            // Hide fabs on scroll
            UiUtils.hideExtendedFabOnVerticalScroll(
                nestedScrollView = binding.nestedScrollMain,
                extendedFabs = binding.fabActions,
            )

        startStorageRequest.launch(
            Array<String>(2) {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )

        binding.fabAddPembayaranData?.setOnClickListener {
            showAddFormPembayaranDialog()
            hideFabs()
        }

        binding.fabEditData?.setOnClickListener {
            showTerminSelectionButtonsDialog()
            hideFabs()
        }
    }

    override fun onResume() {
        super.onResume()

        syncPembayaran()
    }

    private fun syncPembayaran() {
        viewModel.getHargaKavling(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }
        viewModel.getAllPembayaran(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }
        viewModel.getCatatanPembayaran(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_SHORT).show()
        }
        pembayaranViewModel.getBaselinePembayaran(
            kavling = currentKavlingKode!!,
            onLoading = {
                binding.progressBarBaselineAngsuran?.visibility = View.VISIBLE
                binding.layoutUangBaselineAngsuran?.visibility = View.GONE
            },
            onComplete = {
                binding.progressBarBaselineAngsuran?.visibility = View.GONE
                binding.layoutUangBaselineAngsuran?.visibility = View.VISIBLE
            },
            onFailure = { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() },
        )
    }

    private fun onLoadingFormPembayaran(finished: Boolean) {
        binding.tableFormPembayaran.visibility = if (finished) View.VISIBLE else View.GONE
        binding.layoutLoadingFormPembayaran?.visibility = if (finished) View.GONE else View.VISIBLE
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) { finish ->
            finish?.let {
                binding.swipeRefreshFormPembayaran.isRefreshing = !it
            }
        }

        viewModel.formPembayaranRefreshed.observe(requireActivity()) { refreshed ->
            if (refreshed != null) {
                val isFinish = refreshed == true
                onLoadingFormPembayaran(isFinish)
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

        pembayaranViewModel.baselinePembayaranLive.observe(requireActivity()) {
            if (it != null) {
                binding.tvBaselineAngsuranBulanan?.text = "Rp. ${it.parsedJumlahUang}"
            } else {
                binding.tvBaselineAngsuranBulanan?.text = "-"
            }
        }

        viewModel.listPembayaranLive.observe(requireActivity()) { listPembayaran ->
            if ((listPembayaran != null) and (listPembayaran?.isNotEmpty() == true)) {
                binding.tvSisaBlmTerbayar?.text = StringBuilder().run {
                    append("Rp. ")
                    append(listPembayaran?.last()?.sisaBelumTerbayar)
                    toString()
                }
            } else {
                clearPembayaranField()
            }

            // I think this will immune to the null value, since I set default values
            //  to Column and Row Headers, and the Cell Items.
            val pembayaranColumnHeaders = viewModel.getPembayaranTableColumnHeaders()
            val pembayaranRowHeaders = viewModel.getPembayaranTableRowHeaders()
            val pembayaranCellItems = viewModel.getPembayaranTableCellItems()

            populateTableView(pembayaranColumnHeaders, pembayaranRowHeaders, pembayaranCellItems)
        }

        viewModel.catatanPembayaranLive.observe(requireActivity()) { catatanPembayaran ->
            if (catatanPembayaran != null) {
                binding.tvCatatan?.text = catatanPembayaran.content
            } else {
                binding.tvCatatan?.text = requireContext().getString(R.string.tidak_ada_catatan)
            }
        }
    }

    private fun setupExtendedFloatingButton() {
        binding.fabAddPembayaranData?.visibility = View.GONE
        binding.fabEditData?.visibility = View.GONE
//        binding.tvInfoAddPembayaranData?.visibility = View.GONE
//        binding.tvInfoEditData?.visibility = View.GONE

        binding.fabActions?.shrink()

        binding.fabActions?.setOnClickListener {
            if (!isAllFabsVisible) {
                showFabs()
            } else {
                hideFabs()
            }
        }
    }

    private fun showAddFormPembayaranDialog() {
        // check harga kavling available
        val hargaKavling = binding.tvHarga?.text.toString().let {
            NumberUtil.formatStringToLong(it)
        }

        if (hargaKavling <= 0L) {
            Toast.makeText(requireContext(), "Harga kavling masih kosong", Toast.LENGTH_SHORT)
                .show()
        // available? move on!
        } else {
            // inflating
            val dialogBinding = DialogAddFormPembayaranBinding.inflate(layoutInflater)
            val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
                setView(dialogBinding.root)
                setCancelable(false)
            }.create()

            // Set Action for TextInputLayout everytime Radio Button Clicked
            // including auto add Jenis Pembayaran Sequence.
            onRadioButtonJenisPembayaranClick(dialogBinding)

            // setting layout
            dialogBinding.edtJumlahUangDibayar.apply {
                addTextChangedListener(ThousandSeparatorTextWatcher(this))
            }
            additionalDialogSetting(requireContext(), dialogView)

            // setup datepicker
            DatePickerHelper(
                ctx = requireContext(),
                triggerButton = dialogBinding.btnPilihTanggal,
                targetEdt = dialogBinding.edtTanggal,
            ).setupDateDefaultOrPick(true)

            dialogView.show()

            // click listeners
            dialogBinding.btnBatal.setOnClickListener {
                dialogView.dismiss()
            }

            dialogBinding.btnTambahkan.setOnClickListener {
                // on view click
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."
                dialogBinding.btnTambahkan.isEnabled = false

                val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                    dialogBinding.edtTermin, dialogBinding.edtTanggal, dialogBinding.edtJumlahUangDibayar
                )

                if (!isInvalidEdt) {
                    // getting data from edt
                    val termin = dialogBinding.edtTermin.text.toString().let { urutanTermin ->
                        if (dialogBinding.rbItj.isChecked) "ITJ $urutanTermin"
                        else if (dialogBinding.rbDp.isChecked) "DP $urutanTermin"
                        else if (dialogBinding.rbTermin.isChecked) "Termin $urutanTermin"
                        else "Termin 999" // This is ridiculuously error :v
                    }
                    val tanggal = dialogBinding.edtTanggal.text.toString()
                    val jumlahUangDibayar = dialogBinding.edtJumlahUangDibayar.text.toString()
                    val keteranganProgress = dialogBinding.edtKeteranganProgress.text.let {
                        if (it.isNullOrBlank())  "-"
                        else it.toString()
                    }
                    val pembayaran = Pembayaran(
                        termin = termin,
                        tanggal = tanggal,
                        jumlahUangDibayar = jumlahUangDibayar,
                        keterangan = keteranganProgress,
                        timeMillis = System.currentTimeMillis(),
                    )

                    // call view model
                    viewModel.addPembayaran(currentKavlingKode!!, hargaKavling, pembayaran) { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        dialogView.dismiss()
                        syncPembayaran()
                    }

                }
            }

            // on eye icon click
            imgVisibilityOnClick(dialogView, dialogBinding)
        }
    }

    private fun showEditPembayaranDialog(pembayaran: Pembayaran) {
        // inflate
        val dialogBinding = DialogAddFormPembayaranBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }.create()

        // setting layout
        dialogBinding.edtJumlahUangDibayar.apply {
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }
        dialogBinding.tilTermin.isEnabled = true
        dialogBinding.edtTermin.isEnabled = true
        dialogBinding.btnHapus.visibility = View.VISIBLE
        dialogBinding.btnTambahkan.text = "Simpan Perubahan"

        // setup datepicker
        DatePickerHelper(
            ctx = requireContext(),
            triggerButton = dialogBinding.btnPilihTanggal,
            targetEdt = dialogBinding.edtTanggal,
        ).setupDateDefaultOrPick(true)

        additionalDialogSetting(requireContext(), dialogView)
        dialogView.show()


        // misc
        fun getJenisPembayaranAndUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf(
                Pair("jenis", terminDanUrutan[0]),
                Pair("urutan", terminDanUrutan[1]),
            )
        }

        // populate fields with available pembayaran data
        dialogBinding.tvTitle.text = "Ubah Form"
        dialogBinding.apply {
            val mapTermin = getJenisPembayaranAndUrutan(pembayaran.termin)
            val jenisPembayaran = mapTermin["jenis"]!!

            when (jenisPembayaran) {
                "ITJ" -> rbItj.isChecked = true
                "DP" -> rbDp.isChecked = true
                "Termin" -> rbTermin.isChecked = true
            }
        } // which RadioButton is clicked
        dialogBinding.edtTermin.apply {
            val mapTermin = getJenisPembayaranAndUrutan(pembayaran.termin)
            setText(mapTermin["urutan"])
        }
        dialogBinding.edtTanggal.setText(pembayaran.tanggal)
        dialogBinding.edtJumlahUangDibayar.setText(pembayaran.jumlahUangDibayar.toString())
        dialogBinding.edtKeteranganProgress.setText(pembayaran.keterangan)

        fun getPembayaranFromEdt(): Pembayaran? {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtTermin, dialogBinding.edtTanggal, dialogBinding.edtJumlahUangDibayar)

            return if (!isInvalidEdt) {
                Pembayaran(
                    termin = dialogBinding.edtTermin.text.toString().let { urutanTermin ->
                        if (dialogBinding.rbItj.isChecked) "ITJ $urutanTermin"
                        else if (dialogBinding.rbDp.isChecked) "DP ${urutanTermin}"
                        else if (dialogBinding.rbTermin.isChecked) "Termin ${urutanTermin}"
                        else "Termin 999" // this is ridiciously wrong
                    },
                    tanggal = dialogBinding.edtTanggal.text.toString(),
                    jumlahUangDibayar = dialogBinding.edtJumlahUangDibayar.text.toString(),
                    totalUangMasuk = pembayaran.totalUangMasuk,
                    presentase = pembayaran.presentase,
                    keterangan = dialogBinding.edtKeteranganProgress.text.toString(),
                    timeMillis = System.currentTimeMillis(),
                )
            } else {
                null
            }
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            // onclick view
            dialogBinding.btnTambahkan.text = "Menyimpan data ..."
            dialogBinding.btnTambahkan.isEnabled = false
            dialogBinding.btnHapus.isEnabled = false

            getPembayaranFromEdt()?.let { newPembayaran ->
                viewModel.updatePembayaran(currentKavlingKode!!, pembayaran, newPembayaran) { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    dialogView.dismiss()
                    syncPembayaran()
                }
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }

        dialogBinding.btnHapus.setOnClickListener {
            dialogBinding.btnTambahkan.text = "Menghapus data ..."
            dialogBinding.btnTambahkan.isEnabled = false
            dialogBinding.btnHapus.isEnabled = false

            // Anonymous function to delete Pembayaran, which will be executed in both
            // positive or negative response to Delete Foto Pembayaran Dialog below.
            val deletePembayaran = { kavlingKode: String, termin: String ->
                // Deleting Pembayaran
                viewModel.deletePembayaranByTermin(
                    kavlingKode = kavlingKode,
                    termin = termin,
                    onComplete = { msg ->
                        syncPembayaran()
                        dialogView.dismiss()
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            val kavlingKode = currentKavlingKode!!
            val termin = pembayaran.termin
            val sudahIsiFotoPembayaran = pembayaran.sudahIsiFotoPembayaran

            // Show hapus Pembayaran confirmation.
            // This will also shows a confirmation to delete the Foto Pembayaran,
            // if "Pembayaran.sudahIsiFotoPembayaran == true".
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pembayaran")
                .setMessage("Apakah Anda yakin menghapus pembayaran $termin?")
                .setPositiveButton("Ya") { dialogHapus, _ ->
                    dialogHapus.dismiss()

                    if (sudahIsiFotoPembayaran) {
                        // Show the confirmation to delete the Foto Pembayaran
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Hapus Foto Pembayaran?")
                            .setMessage("Apakah Anda juga ingin menghapus Foto Pembayaran $termin?")
                            .setPositiveButton("Ya") { dialogFoto, _ ->
                                dialogFoto.dismiss()

                                // Deleting Foto Pembayaran
                                imageViewModel.deleteFotoPembayaran(
                                    kavlingKode = kavlingKode,
                                    termin = termin,
                                    onComplete = {
                                        // TODO: What might be here?
                                    }
                                )

                                // Deleting Pembayaran
                                deletePembayaran(kavlingKode, termin)

                            }
                            .setNegativeButton("Tidak") { dialogFoto, _ ->
                                dialogFoto.dismiss()

                                deletePembayaran(kavlingKode, termin)
                            }
                            .create()
                            .show()
                    } else {
                        deletePembayaran(kavlingKode, termin)
                    }

                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        imgVisibilityOnClick(dialogView, dialogBinding)
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
                    syncPembayaran()
                    dialogView.dismiss()
                }

            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    /**
     * This is clearing the pembayaran field: TableLayout, TvSisaBelumBayar,
     * TvTambahanLuas, and TvTotalHarga.
     */
    private fun clearPembayaranField() {
        binding.tvSisaBlmTerbayar?.text = "0"
        binding.tvTambahanLuas?.text = "0"
        binding.tvTotalHarga?.text = "0"
    }

    private fun populateTableView(
        columnHeaders: List<PembayaranColumnHeader>,
        rowHeaders: List<PembayaranRowHeader>,
        cellLists: List<List<PembayaranCell>>,
    ) {
        val pembayaranTableViewAdapter = PembayaranTableViewAdapter()

        binding.tableFormPembayaran.apply {
            setAdapter(pembayaranTableViewAdapter)
        }

        pembayaranTableViewAdapter.setAllItems(columnHeaders, rowHeaders, cellLists)
        pembayaranTableViewAdapter.notifyDataSetChanged()
    }

    private fun showSetBaselinePembayaranDialog() {
        val hargaKavling = viewModel.hargaKavlingLive.value

        if (hargaKavling != null) {
            val dialogBinding = DialogBaselineAngsuranPerBulanBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
                setCancelable(false)
                setView(dialogBinding.root)
            }.create()

            DialogUtil.additionalDialogSetting(requireContext(), dialog)

            dialogBinding.edtInfoHargaKavling.setText("Rp. ${NumberUtil.formatLongToString(hargaKavling.hargaLong)}")
            dialogBinding.spinnerTimeframeAngsuran.apply {
                val listOpsiTimeframe = listOf("Tahun", "Bulan")
                adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, listOpsiTimeframe
                )
            }
            dialogBinding.btnHitung.setOnClickListener {
                val timeFrame = dialogBinding.edtOpsiTimeframeAngsuran.text.toString().toInt()
                val opsiTimeFrame = dialogBinding.spinnerTimeframeAngsuran.selectedItem.toString()
                val biayaAngsuranPerBulan = try {
                    pembayaranViewModel.hitungAngsuranPerBulan(
                        hargaKavling,
                        timeFrame,
                        opsiTimeFrame
                    )
                } catch (e: Exception) {
                    e.printStackTrace()

                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()

                    0.0
                }

                dialogBinding.tvPerhitungan.text = StringBuilder().run {
                    append("${NumberUtil.formatLongToString(hargaKavling.hargaLong)} ")
                    append("/ ${if (opsiTimeFrame == "Tahun") "$timeFrame Tahun (${timeFrame * 12} Bulan) " else "$timeFrame Bulan"} ")
                    append("= Rp. ${NumberUtil.formatDoubleToString(biayaAngsuranPerBulan)}")

                    toString()
                }

                dialogBinding.edtUangAngsuranPerBulan.setText(biayaAngsuranPerBulan.let {
                    DecimalFormat("#", DecimalFormatSymbols(Locale.US))
                        .format(it)
                    // Prevent 1E77 or alike (exponents)
                })
            }
            dialogBinding.edtUangAngsuranPerBulan.addTextChangedListener {
                it?.toString()?.also { string ->
                    if (string.isNotEmpty()) {
                        val text = "= Rp. ${NumberUtil.formatDoubleToString(string.toDouble())}"

                        dialogBinding.tvInfoParsedUangAngsuranRupiah.text = text
                    } else {
                        dialogBinding.tvInfoParsedUangAngsuranRupiah.text = "Rp. 0"
                    }
                }
            }

            dialogBinding.btnTambahkan.setOnClickListener {
                val isInvalidInput = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtUangAngsuranPerBulan)

                if (!isInvalidInput) {
                    val biayaAngsuran = dialogBinding.edtUangAngsuranPerBulan.text?.toString()?.toLong() ?: 0L

                    pembayaranViewModel.insertBaselinePembayaran(
                        kavling = currentKavlingKode!!,
                        jumlahUang = biayaAngsuran,
                        onLoading = {
                            dialogBinding.btnTambahkan.text = "Menyimpan ..."
                            dialogBinding.btnTambahkan.isEnabled = false
                        },
                        onComplete = {
                            Toast.makeText(requireContext(), "Berhasil mengubah Angsuran Bulanan: Rp ${NumberUtil.formatLongToString(biayaAngsuran)}", Toast.LENGTH_SHORT)
                                .show()

                            dialog.dismiss()
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                            dialogBinding.btnTambahkan.isEnabled = true
                            dialogBinding.btnTambahkan.text = "Tambahkan"
                        }
                    )
                }
            }
            dialogBinding.btnBatal.setOnClickListener {
                // TODO: Cancel tambahkanJob
                dialog.dismiss()
            }

            dialog.show()
        } else {
            Snackbar.make(binding.root, "Harga Kavling masih kosong!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showTerminSelectionButtonsDialog() {
        val hargaKavling = binding.tvHarga?.text.toString().let {
            NumberUtil.formatStringToLong(it)
        }

        if (hargaKavling <= 0L) {
            Toast.makeText(requireContext(), "Harga kavling masih kosong", Toast.LENGTH_SHORT)
                .show()
        } else {
            val dialogBinding = DialogPilihTerminBinding.inflate(layoutInflater)
            val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
                setView(dialogBinding.root)
            }.create()

            additionalDialogSetting(requireContext(), dialogView)
            dialogView.show()

            dialogBinding.btnBatal.setOnClickListener {
                dialogView.dismiss()
            }
            
            viewModel.listPembayaranLive.value?.let {
                setupTerminRecyclerView(it, dialogView, dialogBinding.recyclerTermin)   
            }
        }
    }


    private fun setupTerminRecyclerView(
        listPembayaran: List<Pembayaran>,
        terminDialog: AlertDialog,
        recyclerTermin: RecyclerView,
    ) {
        val termins = ArrayList<String>()
        
        listPembayaran.forEach { termins.add(it.termin) }

        val adapter = TerminRecyclerAdapter(termins) {
            terminDialog.dismiss()
            showEditPembayaranDialog(listPembayaran[it])
        }
        recyclerTermin.adapter = adapter
        recyclerTermin.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showActionsCatatanDialog(editMode: Boolean) {
        val dialogBinding = DialogActionCatatanPembayaranBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

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

                viewModel.addCatatanPembayaran(
                    kavlingKode = currentKavlingKode!!,
                    catatan = catatan,
                    onComplete = { msg ->
                        syncPembayaran()
                        dialogView.dismiss()
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }

        dialogBinding.btnHapusCatatan.setOnClickListener {
            viewModel.deleteCatatanPembayaran(
                kavlingKode = currentKavlingKode!!,
                onComplete = { msg ->
                    syncPembayaran()
                    dialogView.dismiss()
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun showImagePickerDialog() {
        ImagePicker.with(this)
            .crop()
            .compress(sharedPrefs.getInt("max_size_foto_pembayaran", 256))
            .createIntent {
                startForFotoPembayaranResult.launch(it)
            }
    }

    private fun showFotoPembayaranSelectionDialog(
        dialogTitle: String,
        mode: OperasiFotoPembayaran,
        onTerminClick: (selectedTermin: String) -> Unit,
    ) {
        val listTerminPembayaran = when (mode) {
            OperasiFotoPembayaran.TAMBAH -> viewModel.getBelumIsiFotoTerminPembayaran()
            OperasiFotoPembayaran.UBAH -> viewModel.getAllArrayTerminPembayaran()
            OperasiFotoPembayaran.HAPUS -> viewModel.getSudahIsiFotoTerminPembayaran()
            OperasiFotoPembayaran.LIHAT -> viewModel.getSudahIsiFotoTerminPembayaran()
        }

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(dialogTitle)
            setItems(listTerminPembayaran) { dialog, selectionPosition ->
                val selectedTermin = listTerminPembayaran[selectionPosition]

                // Updated currentTermin here
                updateSelectedTermin(selectedTermin)

                dialog.dismiss()

                onTerminClick(selectedTermin)
            }
        }.create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pembayaran, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

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
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Hapus Semua Foto Pembayaran?")
                                .setMessage("Perhatian! Menghapus seluruh Pembayaran juga akan menghapus seluruh Foto Pembayaran yang tersimpan. Apakah Anda yakin?")
                                .setPositiveButton("Ya") { dialogFoto, _ ->
                                    dialogFoto.dismiss()

                                    viewModel.deleteAllPembayaran(
                                        kavlingKode = currentKavlingKode!!,
                                        onComplete = { msg ->
                                            dialogFoto.dismiss()
                                            syncPembayaran()
                                            Toast.makeText(
                                                requireContext(),
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                    viewModel.deleteAllPembayaran(currentKavlingKode!!) { msg ->

                                    }
                                }
                                .setNegativeButton("Tidak") { dialogFoto, _ ->
                                    dialogFoto.dismiss()
                                }
                                .create()
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
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Tambah Foto Pembayaran",
                    mode = OperasiFotoPembayaran.TAMBAH,
                    onTerminClick = {
                        showImagePickerDialog()
                    }
                )

                true
            }
            R.id.ubah_foto -> {
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Ubah Foto Pembayaran",
                    mode = OperasiFotoPembayaran.UBAH,
                    onTerminClick = {
                        showImagePickerDialog()
                    }
                )

                true
            }
            R.id.lihat_foto -> {
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Lihat Foto Pembayaran",
                    mode = OperasiFotoPembayaran.LIHAT,
                    onTerminClick = { selectedTermin ->
                        val imageTransport = imageViewModel.createImageTransport(
                            sendIntent = GriyaNodes.INTENT_FOTO_PEMBAYARAN,
                            content = mapOf<String, String>(
                                Pair("kavlingKode", currentKavlingKode!!),
                                Pair("termin", selectedTermin),
                            ),
                        )

                        val fullImageIntent = Intent(requireContext(), FullImageActivity::class.java)
                        fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)
                        startActivity(fullImageIntent)
                    }
                )
                true
            }
            R.id.hapus_foto -> {
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Hapus Foto Pembayaran",
                    mode = OperasiFotoPembayaran.HAPUS,
                    onTerminClick = { selectedTermin ->
                        // Show delete confirmation
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setTitle("Hapus Foto Pembayaran $selectedTermin?")
                            setMessage("Apakah Anda yakin menghapus Foto Pembayaran pada termin $selectedTermin?")
                            setPositiveButton("Ya") { dialog, _ ->
                                imageViewModel.deleteFotoPembayaran(
                                    kavlingKode = currentKavlingKode!!,
                                    termin = selectedTermin,
                                    onComplete = { msg ->
                                        dialog.dismiss()
                                        syncPembayaran()
                                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss()}
                        }.create()
                            .show()
                    }
                )
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

    /**
     * On click Radio Button of Jenis Pembayaran (ITJ, DP, Termin) selection.
     *
     * Once clicked, it automates the next sequence of selected Jenis Pembayaran. See "getNextPembayaranSequence()".
     *
     * It also automates for filling the Jumlah Uang Dibayar for Termin case.
     */
    private fun onRadioButtonJenisPembayaranClick(dialogBinding: DialogAddFormPembayaranBinding) {
        dialogBinding.rbItj.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                viewModel.getNextPembayaranSequence(DetailViewModel.JenisPembayaran.ITJ)
            )
//            dialogBinding.tilTermin.hint = "Masukkan urutan ITJ"
        }
        dialogBinding.rbDp.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                viewModel.getNextPembayaranSequence(DetailViewModel.JenisPembayaran.DP)
            )
//            dialogBinding.tilTermin.hint = "Masukkan urutan DP"
        }
        dialogBinding.rbTermin.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true

            dialogBinding.edtTermin.setText(
                viewModel.getNextPembayaranSequence(DetailViewModel.JenisPembayaran.TERMIN)
            )

            // Set automatic Jumlah Uang Dibayar for Termin type
            val jumlahUangDibayar = viewModel.getTerminJumlahUangDibayar()
            if (jumlahUangDibayar != null) {
                dialogBinding.edtJumlahUangDibayar.setText(jumlahUangDibayar)
            }
        }
    }

    private fun showFabs() {
        binding.fabAddPembayaranData?.show()
        binding.fabEditData?.show()
//        binding.tvInfoAddPembayaranData?.visibility = View.VISIBLE
//        binding.tvInfoEditData?.visibility = View.VISIBLE

        binding.fabActions?.extend()

        isAllFabsVisible = true
    }

    private fun hideFabs() {
        binding.fabAddPembayaranData?.hide()
        binding.fabEditData?.hide()
//        binding.tvInfoAddPembayaranData?.visibility = View.GONE
//        binding.tvInfoEditData?.visibility = View.GONE

        binding.fabActions?.shrink()

        isAllFabsVisible = false
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun imgVisibilityOnClick(dialogView: AlertDialog, dialogBinding: DialogAddFormPembayaranBinding) {
        dialogBinding.imgVisibility.setOnTouchListener { view, motionEvent ->
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
        val blockKode = currentKavlingKode!!.substring(0)
        val kavlingNum = currentKavlingKode!!.substring(1)

        val dataPembayaran = viewModel.listPembayaranLive.value
        val dataDiri = viewModel.dataDiriLive.value

        if ((dataDiri == null) and (dataPembayaran == null)) {
            Snackbar.make(binding.root, "Data Diri costumer atau Form Pembayaran masih kosong", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            val excelExporter = ExcelExporter(
                blockKode = blockKode,
                kavlingNumber = kavlingNum,
                namaPembayar = viewModel.dataDiriLive.value?.nama ?: "Null",
                hargaKavling = binding.tvHarga?.text.toString(),
                tambahLuasan = binding.tvTambahanLuas?.text.toString(),
                totalHarga = binding.tvTotalHarga?.text.toString(),
                sisaBelumTerbayar = binding.tvSisaBlmTerbayar?.text.toString(),
                dataPembayaran = viewModel.listPembayaranLive.value ?: emptyList()
            )
            val workbook = excelExporter.createPembayaranSpreadsheet()

            excelExporter.storeExcelInStorage(requireContext(),workbook, "pembayaran_$currentKavlingKode.xls")
        }
    }

    private fun shareUiPembayaran() {
        // TODO
    }

    private fun updateSelectedTermin(selectedTermin: String) {
        imageViewModel.currentTermin.value = selectedTermin
    }

    private enum class OperasiFotoPembayaran {
        LIHAT, TAMBAH, HAPUS, UBAH
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