package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogAddFormPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogEditHargaBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogPilihTerminBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentFormPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.ui.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.ui.detail.adapter.TerminRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFormPembayaranBinding
    private val viewModel: DetailViewModel by viewModels()
    private var currentKavlingKode: String? = null

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
            listPembayaran?.let {
                populateTableLayout(it)
                binding.tvSisaBlmTerbayar?.text = it.last().sisaBelumTerbayar
            }
        }
    }

    private fun setupExtendedFloatingButton() {
        binding.fabAddPembayaranData?.visibility = View.GONE
        binding.fabEditData?.visibility = View.GONE
        binding.tvInfoAddPembayaranData?.visibility = View.GONE
        binding.tvInfoEditData?.visibility = View.GONE

        var isAllFabsVisible = false

        binding.fabActions?.shrink()

        binding.fabActions?.setOnClickListener {
            if (!isAllFabsVisible) {
                binding.fabAddPembayaranData?.show()
                binding.fabEditData?.show()
                binding.tvInfoAddPembayaranData?.visibility = View.VISIBLE
                binding.tvInfoEditData?.visibility = View.VISIBLE

                binding.fabActions?.extend()

                isAllFabsVisible = true
            } else {
                binding.fabAddPembayaranData?.hide()
                binding.fabEditData?.hide()
                binding.tvInfoAddPembayaranData?.visibility = View.GONE
                binding.tvInfoEditData?.visibility = View.GONE

                binding.fabActions?.shrink()

                isAllFabsVisible = false
            }
        }

        binding.fabAddPembayaranData?.setOnClickListener {
            showAddFormPembayaranDialog()
        }

        binding.fabEditData?.setOnClickListener {
            showTerminSelectionButtonsDialog()
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
            val dialogView = AlertDialog.Builder(requireContext()).apply {
                setView(dialogBinding.root)
                setCancelable(false)
            }.create()

            // Set Action for TextInputLayout everytime Radio Button Clicked
            onRadioButtonJenisPembayaranClick(dialogBinding)

            dialogView.show()

            // setting layout
            dialogBinding.edtJumlahUangDibayar.apply {
                addTextChangedListener(ThousandSeparatorTextWatcher(this))
            }

            setDateDefaulOrPickEdtTanggal(true, dialogBinding)

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
        }
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
        val dialogView = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

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

    private fun populateTableLayout(listPembayaran: List<Pembayaran>) {
        // avoiding multiple table, so we need to clear the table for each
        // "populateTableLayout()" function call
        binding.tableLayout.apply {
            removeViews(1, max(0, this.childCount - 1))
        }

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

    private fun showDeleteAllPembayaranDialog() {
        val dialogView = AlertDialog.Builder(requireContext())
            .setTitle("Hapus Semua Pembayaran")
            .setMessage("Apakah Anda yakin ingin menghapus semua pembayaran di kavling $currentKavlingKode?")
            .setPositiveButton("Ya") { dialog, _ ->
                viewModel.deleteAllPembayaran(currentKavlingKode!!) { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    syncPembayaran()
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
            val dialogView = AlertDialog.Builder(requireContext()).apply {
                setView(dialogBinding.root)
            }.create()

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

    private fun showEditPembayaranDialog(pembayaran: Pembayaran) {
        // inflate
        val dialogBinding = DialogAddFormPembayaranBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }.create()

        dialogView.show()

        // setting layout
        dialogBinding.edtJumlahUangDibayar.apply {
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }
        dialogBinding.tilTermin.isEnabled = true
        dialogBinding.edtTermin.isEnabled = true
        dialogBinding.btnHapus.visibility = View.VISIBLE
        dialogBinding.btnTambahkan.text = "Simpan Perubahan"

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
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(dialogBinding.edtTermin, dialogBinding.edtTanggal, dialogBinding.edtJumlahUangDibayar, dialogBinding.edtKeteranganProgress)

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
            val confirmDialog = AlertDialog.Builder(requireContext())
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

        setDateDefaulOrPickEdtTanggal(false, dialogBinding)
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

    private fun createTextViewForTableRows(): MaterialTextView {
        return MaterialTextView(requireContext(), null, com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1).apply {
            setPadding(8, 4, 8, 4)
        }
    }

}