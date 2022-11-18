package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TableRow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.ui.UiUtils
import net.bagusekasaputra.griyakampoengtkw.ui.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.ui.detail.adapter.TerminRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.util.*
import net.bagusekasaputra.griyakampoengtkw.util.DialogUtil.additionalDialogSetting
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    companion object {
        private const val WRITE_CSV_PERMISSION_REQUEST_CODE = 250
    }

    private lateinit var binding: FragmentFormPembayaranBinding
    private val viewModel: DetailViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()

    private var currentKavlingKode: String? = null
    private var isAllFabsVisible = false

    private val startForFotoPembayaranResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val intent = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = intent?.data

                    uri?.let {
                        imageViewModel.addFotoPembayaran(
                            kavlingKode = currentKavlingKode!!,
                            uri = it,
                            onComplete = { msg ->
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

        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onFullScreenLandscapeMode()
        }

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

        setupExtendedFloatingButton()

        setupViewModel()

        binding.imgEdit?.setOnClickListener {
            showEditHargaDialog()
        }

        binding.swipeRefreshFormPembayaran.setOnRefreshListener {
            syncPembayaran()
        }

        binding.imgEditCatatan?.setOnClickListener {
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
    }

    override fun onResume() {
        super.onResume()

        syncPembayaran()
    }

    /**
     * Synchronizations of:
     *
     * 1. Harga Kavling
     *
     * 2. List Pembayaran
     *
     * 3. Foto Kuitansi
     *
     * 4. Catatan Pembayaran
     */
    private fun syncPembayaran() {
        viewModel.getHargaKavling(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }
        viewModel.getAllPembayaran(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG).show()
        }
        imageViewModel.getFotoKuitansi(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_SHORT).show()
        }
        viewModel.getCatatanPembayaran(currentKavlingKode!!) { failMsg ->
            Toast.makeText(requireContext(), failMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) { finish ->
            finish?.let {
                binding.swipeRefreshFormPembayaran.isRefreshing = !it
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

        viewModel.listPembayaranLive.observe(requireActivity()) { listPembayaran ->
            if (listPembayaran != null) {
                populateTableLayout(listPembayaran)
                binding.tvSisaBlmTerbayar?.text = listPembayaran.last().sisaBelumTerbayar
            } else {
                clearTableLayout()
                clearPembayaranField()
            }
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

        binding.fabAddPembayaranData?.setOnClickListener {
            showAddFormPembayaranDialog()
            hideFabs()
        }

        binding.fabEditData?.setOnClickListener {
            showTerminSelectionButtonsDialog()
            hideFabs()
        }
    }

    private fun onFullScreenLandscapeMode() {
        requireActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            onRadioButtonJenisPembayaranClick(dialogBinding)

            // setting layout
            dialogBinding.edtJumlahUangDibayar.apply {
                addTextChangedListener(ThousandSeparatorTextWatcher(this))
            }
            setDateDefaulOrPickEdtTanggal(true, dialogBinding)
            additionalDialogSetting(requireContext(), dialogView)
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

        setDateDefaulOrPickEdtTanggal(false, dialogBinding)
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
            val confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pembayaran")
                .setMessage("Apakah Anda yakin menghapus pembayaran ${pembayaran.termin}?")
                .setPositiveButton("Ya") { dialog, _ ->
                    dialogBinding.btnTambahkan.text = "Menghapus data ..."
                    dialogBinding.btnTambahkan.isEnabled = false
                    dialogBinding.btnHapus.isEnabled = false

                    dialog.dismiss()
                    getPembayaranFromEdt()?.let { pembayaran ->
                        viewModel.deletePembayaranByTermin(currentKavlingKode!!, pembayaran.termin) { msg ->
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            dialogView.dismiss()
                            syncPembayaran()
                        }
                    }
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }.create()

            confirmDialog.show()
        }

        imgVisibilityOnClick(dialogView, dialogBinding)
    }

    private fun getTodayDate(): Calendar {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)

        return Calendar.getInstance().apply {
            set(currentYear, currentMonth, currentDate)
        }
    }

    private fun setDateDefaulOrPickEdtTanggal(defaultDate: Boolean, dialogBinding: DialogAddFormPembayaranBinding) {
        val currentDate = getTodayDate()
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        if (defaultDate) {
            dialogBinding.edtTanggal.setText(dateFormatter.format(currentDate.time))
        }

        dialogBinding.btnPilihTanggal.setOnClickListener {

            val onDateListenerSet = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }
                dialogBinding.edtTanggal.setText(dateFormatter.format(newDate.time))
            }
            val datePickerDialog = DatePickerDialog(requireContext(), onDateListenerSet,
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
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
                    syncPembayaran()
                    dialogView.dismiss()
                }

            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun clearTableLayout() {
        binding.tableLayout.apply {
            removeViews(1, max(0, this.childCount - 1))
        }
    }

    /**
     * This is clearing the pembayaran field: TableLayout, TvSisaBelumBayar,
     * TvTambahanLuas, and TvTotalHarga.
     */
    private fun clearPembayaranField() {
        binding.tableLayout.apply {
            removeViews(1, max(0, this.childCount - 1))
        }
        binding.tvSisaBlmTerbayar?.text = "0"
        binding.tvTambahanLuas?.text = "0"
        binding.tvTotalHarga?.text = "0"
    }

    private fun populateTableLayout(listPembayaran: List<Pembayaran>) {
        // avoiding multiple table, so we need to clear the table for each
        // "populateTableLayout()" function call
        clearTableLayout()

        listPembayaran.forEach {
            // Creating Textview for each rows
            val termin = createTextViewForTableRows()
            val tanggal = createTextViewForTableRows()
            val jumlahUangDibayar = createTextViewForTableRows()
            val totalUangMasuk = createTextViewForTableRows()
            val presentase = createTextViewForTableRows()
            val keterangan = createTextViewForTableRows().apply { textAlignment = View.TEXT_ALIGNMENT_VIEW_START }

            termin.text = it.termin
            tanggal.text = it.tanggal
            jumlahUangDibayar.text = it.jumlahUangDibayar.toString()
            totalUangMasuk.text = it.totalUangMasuk.toString()
            presentase.text = it.presentase.toString()
            keterangan.text = it.keterangan

            val textViews = ArrayList<MaterialTextView>().apply {
                add(termin)
                add(tanggal)
                add(jumlahUangDibayar)
                add(totalUangMasuk)
                add(presentase)
                add(keterangan)
            }

            val tableRow = TableRow(requireContext())
            for (tv in textViews) {
                tv.gravity = Gravity.CENTER
                tableRow.addView(tv)
            }

            binding.tableLayout.addView(tableRow)
        }
    }

    private fun createTextViewForTableRows(): MaterialTextView {
        return MaterialTextView(
            requireContext(),
            null,
            com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1
        ).apply {
            setPadding(8, 4, 8, 4)
        }
    }

    private fun showDeleteAllPembayaranDialog() {
        val dialogView = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Semua Pembayaran")
            .setMessage("Apakah Anda yakin ingin menghapus semua pembayaran di kavling $currentKavlingKode?")
            .setPositiveButton("Ya") { dialog, _ ->
                viewModel.deleteAllPembayaran(
                    kavlingKode = currentKavlingKode!!,
                    onComplete = { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        syncPembayaran()
                    }
                )
                viewModel.deleteAllPembayaran(currentKavlingKode!!) { msg ->

                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogView.show()
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

    /**
     * This gotta be used by lihat foto and tambahkan foto.
     */
    private fun showFotoPembayaranSelectionDialog(dialogTitle: String, onTerminClick: (selectedTermin: String) -> Unit) {
        val listTerminPembayaran = viewModel.getListTerminPembayaran()

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(dialogTitle)
            setItems(listTerminPembayaran) { dialog, selectionPosition ->
                // Go to Full Image Activity
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
                showDeleteAllPembayaranDialog()
                true
            }
            R.id.tambahkan_foto -> {
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Tambah Foto Kuitansi",
                    onTerminClick = { showImagePickerDialog() }
                )

                true
            }
            R.id.lihat_foto -> {
                showFotoPembayaranSelectionDialog(
                    dialogTitle = "Lihat Foto Pembayaran",
                    onTerminClick = { selectedTermin ->
                        val imageTransport = imageViewModel.createImageTransport(
                            sendIntent = GriyaNodes.INTENT_FOTO_PEMBAYARAN,
                            content = mapOf<String, String>(
                                Pair("kavlingKode", currentKavlingKode!!),
                                Pair("termin", selectedTermin),
                            )
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

    private fun onRadioButtonJenisPembayaranClick(dialogBinding: DialogAddFormPembayaranBinding) {
        dialogBinding.rbItj.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true
            dialogBinding.tilTermin.hint = "Masukkan urutan ITJ"
        }
        dialogBinding.rbDp.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true
            dialogBinding.tilTermin.hint = "Masukkan urutan DP"
        }
        dialogBinding.rbTermin.setOnClickListener {
            dialogBinding.edtTermin.isEnabled = true
            dialogBinding.tilTermin.isEnabled = true
            dialogBinding.tilTermin.hint = "Masukkan urutan Termin"
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

    private fun showImagePickerDialog() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .createIntent {
                startForFotoPembayaranResult.launch(it)
            }
    }

    private fun lihatFotoKuitansi() {
        Intent(requireContext(), FullImageActivity::class.java).let { intent ->
            imageViewModel.fotoKuitansiLive.value.let { fotoKuitansi ->
                if (fotoKuitansi == null) {
                    Toast.makeText(requireContext(),
                        "Bukti kuitansi tidak ditemukan",
                        Toast.LENGTH_SHORT).show()
                } else {
                    val stringExtra = ArrayList<String>().apply {
                        add(GriyaNodes.INTENT_FOTO_KUITANSI)
                        add(currentKavlingKode!!)
                    }
                    intent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, stringExtra)
                    startActivity(intent)
                }
            }
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
}