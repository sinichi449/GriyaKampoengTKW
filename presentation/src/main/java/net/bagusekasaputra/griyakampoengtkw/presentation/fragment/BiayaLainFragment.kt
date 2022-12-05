package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.*
import net.bagusekasaputra.griyakampoengtkw.presentation.toCalendar
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FabHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaLainViewModel
import java.util.*

@AndroidEntryPoint
class BiayaLainFragment: Fragment() {

    private lateinit var binding: FragmentBiayaLainBinding

    private val viewModel: BiayaLainViewModel by viewModels()
    private lateinit var fabActions: ExtendedFloatingActionButton
    private lateinit var fabAddBiayaLain: FloatingActionButton
    private lateinit var fabEditBiayaLain: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        viewModel.getAllBiayaLain {
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
            }
        }
    }

    private fun setupTableBiayaLain(listBiayaLain: List<BiayaLain>) {
        val adapter = TableBiayaLainViewAdapter()

        binding.tableviewBiayaLain.setAdapter(adapter)

        adapter.setAllItems(
            BlColumnHeader.getColumnHeaders(),
            BlRowHeader.getRowHeaders(listBiayaLain.size),
            BlCell.getListCellItems(listBiayaLain)
        )

        binding.tableviewBiayaLain.apply {
            setColumnWidth(BiayaLainColumnPosition.JENIS_BIAYA, 500)
            setColumnWidth(BiayaLainColumnPosition.HARGA, 300)
            setColumnWidth(BiayaLainColumnPosition.TANGGAL, 300)
        }

        adapter.notifyDataSetChanged()
    }

    private fun showActionBiayaLainDialog(biayaLain: BiayaLain?) {
        val dialogBinding = DialogActionBiayaLainBinding.inflate(layoutInflater)
        val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialogView)

        val editMode = biayaLain != null
        if (editMode) {
            dialogBinding.tvTitle.text = "Ubah Biaya Lain"

            dialogBinding.edtJenisBiaya.setText(biayaLain!!.jenisBiaya)
            dialogBinding.edtHarga.setText(biayaLain.parsedHarga)
            dialogBinding.edtTanggal.setText(biayaLain.tanggal)

            dialogBinding.btnTambahkan.text = "Ubah"
        }

        dialogView.show()

        dialogBinding.btnPilihTanggal.setOnClickListener {
            val inputtedTanggal = dialogBinding.edtTanggal.text.toString()

            val current = if ((editMode) or (inputtedTanggal.isNotEmpty()))
                inputtedTanggal.toCalendar()
            else
                Calendar.getInstance()
            val year = current.get(Calendar.YEAR)
            val month = current.get(Calendar.MONTH)
            val day = current.get(Calendar.DAY_OF_MONTH)


            val mListener = DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                val properDay = if (mDay < 10) "0$mDay" else mDay.toString()
                val properMonth = if (mMonth.plus(1) < 10) "0$mMonth" else mMonth.plus(1).toString()

                val result = "$properDay/$properMonth/$mYear"
                dialogBinding.edtTanggal.setText(result)
            }

            DatePickerDialog(requireContext(), R.style.DatePicker,mListener, year, month, day)
                .show()
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
}