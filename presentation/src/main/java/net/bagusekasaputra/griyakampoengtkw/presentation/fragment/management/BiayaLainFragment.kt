package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.BiayaLainTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.toCalendar
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FabHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaLainViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import java.util.Calendar
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class BiayaLainFragment: Fragment() {

    private lateinit var binding: FragmentBiayaLainBinding

    private val viewModel: BiayaLainViewModel by viewModels()
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var fabActions: ExtendedFloatingActionButton
    private lateinit var fabAddBiayaLain: FloatingActionButton
    private lateinit var fabEditBiayaLain: FloatingActionButton

    @Inject lateinit var sharedPrefs: SharedPreferences
    private var dataMode = DataMode.ONLINE

    private val listUrutkanSpinner = listOf(
        "Tanggal",
        "Harga",
        "A-Z",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBiayaLainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabActions = requireActivity().findViewById(R.id.fab_actions)
        fabAddBiayaLain = requireActivity().findViewById(R.id.fab_tambah_biaya_lain)
        fabEditBiayaLain = requireActivity().findViewById(R.id.fab_edit_biaya_lain)

        val fabHelper = FabHelper(
            fabAction = fabActions,
            fabs = arrayOf(fabAddBiayaLain, fabEditBiayaLain)
        )
        fabHelper.setupFabs()

        setupSpinnerUrutkan()

        // Check offline mode
        val offlineMode = sharedPrefs.getBoolean("offline_mode", false)
        if (offlineMode) {
            dataMode = DataMode.OFFLINE
        }

        // Data Lama / Baru?
        val pathDataLama = sharedPrefs.getString("dataLamaPath", null)
        if (pathDataLama != null) {
            dataMode = DataMode.DATA_LAMA
        }

        setupViewModel()


        binding.swipeRefreshBiayaLain.setOnRefreshListener {
            sync()
        }

        fabAddBiayaLain.setOnClickListener {
            fabHelper.hideFabs()
            showActionBiayaLainDialog(null)
        }

        fabEditBiayaLain.setOnClickListener {
            fabHelper.hideFabs()

            val listBiayaLain = viewModel.listBiayaLainLive.value

            if (listBiayaLain != null) {
                showBiayaLainSelectionDialog(listBiayaLain)
            } else {
                Toast.makeText(requireContext(), "Data biaya lain masih kosong!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onResume() {
        super.onResume()

        sync()
    }

    private fun sync() {
        viewModel.getAllBiayaLain(dataMode) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) {
            if (it != null) {
                binding.swipeRefreshBiayaLain.isRefreshing = it.not()
            }
        }

        viewModel.listBiayaLainLive.observe(requireActivity()) {
            if (it != null) {
                setupTableBiayaLain(it)

                binding.tvTotalBiayaLain.text = NumberUtil.formatLongToString(viewModel.getTotalBiayaLain())
            }
        }
    }

    private fun setupTableBiayaLain(listBiayaLain: List<BiayaLain>) {
        BiayaLainTableWrapper(binding.tableviewBiayaLain, listBiayaLain)
            .createTable()
    }

    private fun showActionBiayaLainDialog(biayaLain: BiayaLain?) {
        val dialogBinding = DialogActionBiayaLainBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

        dialogBinding.edtHarga.apply {
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        val editMode = biayaLain != null
        if (editMode) {
            dialogBinding.tvTitle.text = "Ubah Biaya Lain"

            dialogBinding.edtJenisBiaya.setText(biayaLain!!.jenisBiaya)
            dialogBinding.edtHarga.setText(biayaLain.parsedHarga)
            dialogBinding.edtTanggal.setText(biayaLain.tanggal)

            dialogBinding.btnTambahkan.text = "Ubah"
            dialogBinding.btnHapus.visibility = View.VISIBLE
        } else {
            // Add tanggal hari ini on edtTanggal in Non-Edit Mode
            val tanggalHariIni = Calendar.getInstance().time
                .toSlashedDate()
            dialogBinding.edtTanggal.setText(tanggalHariIni)
        }

        dialogView.show()

        dialogBinding.btnPilihTanggal.setOnClickListener {
            val inputtedTanggal = dialogBinding.edtTanggal.text.toString()

            val current = if (editMode || inputtedTanggal.isNotEmpty())
                inputtedTanggal.toCalendar()
            else
                Calendar.getInstance()
            val year = current.get(Calendar.YEAR)
            val month = current.get(Calendar.MONTH)
            val day = current.get(Calendar.DAY_OF_MONTH)


            val mListener = DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                val properDay = String.format("%02d", mDay)
                val properMonth = String.format("%02d", mMonth + 1)

                val result = "$properDay/$properMonth/$mYear"
                dialogBinding.edtTanggal.setText(result)
            }

            DatePickerDialog(requireContext(), R.style.DatePicker, mListener, year, month, day)
                .show()
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtHarga,
                dialogBinding.edtJenisBiaya,
                dialogBinding.edtTanggal,
            )

            if (isInvalidEdt.not()) {
                dialogBinding.btnTambahkan.text = "Menyimpan data ..."
                dialogBinding.btnTambahkan.isEnabled = false

                val jenisBiaya = dialogBinding.edtJenisBiaya.text.toString()
                val harga = dialogBinding.edtHarga.text.toString()
                val tanggal = dialogBinding.edtTanggal.text.toString()

                if (editMode) {
                    viewModel.updateBiayaLain(
                        oldBiayaLain = biayaLain!!,
                        newJenisBiaya = jenisBiaya,
                        newHarga = NumberUtil.formatStringToLong(harga),
                        newTanggal = tanggal,
                        onComplete = {
                            dialogView.dismiss()

                            onCompleteDialogOperation(it)
                        }
                    )
                } else {
                    viewModel.addBiayaLain(
                        jenisBiaya = jenisBiaya,
                        harga = NumberUtil.formatStringToLong(harga),
                        tanggal = tanggal,
                        onComplete = {
                            dialogView.dismiss()

                            onCompleteDialogOperation(it)
                        }
                    )
                }
            }
        }

        dialogBinding.btnHapus.setOnClickListener {
            if (editMode) {
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
                    setTitle("Hapus \"${biayaLain!!.jenisBiaya}\"?")
                    setMessage("Apakah Anda yakin ingin menghapus data biaya ini?")
                    setPositiveButton("Ya") { dialog, _ ->
                        viewModel.deleteBiayaLain(
                            biayaLain = biayaLain,
                            onComplete = {
                                dialog.dismiss()
                                dialogView.dismiss()

                                onCompleteDialogOperation(it)
                            }
                        )
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                        .create()
                        .show()
            } else {
                Toast.makeText(requireContext(), "ERROR: Data biaya lain tidak ditemukan, tapi operasi penghapusan dilakukan.", Toast.LENGTH_LONG)
                    .show()
                dialogView.dismiss()
            }
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }
    }

    private fun showBiayaLainSelectionDialog(listBiayaLain: List<BiayaLain>) {
        val arrayJenisBiaya = listBiayaLain.let { list ->
            val listJenisBiaya = mutableListOf<String>()
            list.forEach {
                listJenisBiaya.add(it.jenisBiaya)
            }

            return@let listJenisBiaya.toTypedArray()
        }

        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
            setTitle("Pilih Jenis Biaya")
            setItems(arrayJenisBiaya) { dialog, position ->
                showActionBiayaLainDialog(listBiayaLain[position])

                dialog.dismiss()
            }
        }.create()
            .show()
    }

    private fun onCompleteDialogOperation(msg: String) {
        sync()

        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).apply {
            setAction("OK") { this.dismiss() }
        }.show()
    }

    private fun setupSpinnerUrutkan() {
        binding.spinnerUrutkan?.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listUrutkanSpinner
        )

        binding.spinnerUrutkan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                val sortMethod = when (position) {
                    0 -> BiayaLain.SortMethod.TANGGAL
                    1 -> BiayaLain.SortMethod.HARGA
                    2 -> BiayaLain.SortMethod.JENIS_BIAYA
                    else -> BiayaLain.SortMethod.TANGGAL
                }
                viewModel.sortListBiayaLain(sortMethod)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }
}