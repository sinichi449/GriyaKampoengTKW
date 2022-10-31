package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogAddFormPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentFormPembayaranBinding
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFormPembayaranBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFormPembayaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateTableLayout()

        binding.fabAddPembayaranData.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogBinding = DialogAddFormPembayaranBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }.create()

        dialogView.show()

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }

        dialogBinding.btnTambahkan.setOnClickListener {
            // TODO
            dialogView.dismiss()
            Snackbar.make(requireContext(), binding.root, "Berhasil ditambahkan! (fake)", Snackbar.LENGTH_SHORT).show()
        }

        dialogBinding.btnPilihTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
            val onDateListenerSet = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance().apply {
                        set(year, monthOfYear, dayOfMonth)
                    }
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    dialogBinding.edtTanggal.setText(dateFormatter.format(newDate.time))
                }
            val datePickerDialog = DatePickerDialog(requireContext(), onDateListenerSet, currentYear, currentMonth, currentDate)

            datePickerDialog.show()
        }
    }

    private fun populateTableLayout() {
        val data = generatePseudoPembayaran()

        for (test in data) {
            val termin = MaterialTextView(requireContext())
            val tanggal = MaterialTextView(requireContext())
            val jumlahUangDibayar = MaterialTextView(requireContext())
            val totalUangMasuk = MaterialTextView(requireContext())
            val presentase = MaterialTextView(requireContext())
            val keterangan = MaterialTextView(requireContext())

            termin.text = test.termin
            tanggal.text = test.tanggal
            jumlahUangDibayar.text = test.jumlahUang.toString()
            totalUangMasuk.text = test.totalUangMasuk.toString()
            presentase.text = test.presentase.toString()
            keterangan.text = test.keterangan

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

    private fun generatePseudoPembayaran(): ArrayList<Pembayaran> {
        val data = ArrayList<Pembayaran>()

        data.add(Pembayaran("ITJ", "12/07/2022", 5010000, 5010000, 2.18, ""))
        data.add(Pembayaran("DP1", "13/07/2022", 5010000, 1002000, 4.36, ""))

        return data
    }

    data class Pembayaran(
        val termin: String,
        val tanggal: String,
        val jumlahUang: Int,
        val totalUangMasuk: Int,
        val presentase: Double,
        val keterangan: String
    )

}