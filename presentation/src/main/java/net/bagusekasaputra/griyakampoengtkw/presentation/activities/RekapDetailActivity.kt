package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapUangMasuk
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityRekapDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RekapUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapDetailBinding
    private val viewModel: RekapViewModel by viewModels()

    private val listSortMode = listOf(
        "Kavling",
        "Tanggal",
        "Jumlah Pembayaran",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        binding.toolbarMain.subtitle = intent?.extras?.getString("INTENT_REKAP_DATE_RANGE") ?: "null"

        setupSortSelectionSpinner()
        setupViewModel()

        when (intent?.extras?.getString("INTENT_REKAP_TYPE")) {
            RekapType.UangMasuk.name -> {
                binding.toolbarMain.title = "Uang Masuk"
                viewModel.getListUangMasukRekap { failMsg ->
                    Toast.makeText(this.applicationContext, failMsg, Toast.LENGTH_LONG).show()
                }
            }
            else -> Toast.makeText(this.applicationContext, "Masih tahap beta, belum bisa digunakan", Toast.LENGTH_LONG).show()
        }

        binding.spinnerSortMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                viewModel.sortListRekapUangMasuk(listSortMode[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun setupViewModel() {
        viewModel.listRekapUangMasukLive.observe(this) {
            if (it != null) {
                setupRekapUangMasukTable()

                val total = "Rp. ${NumberUtil.formatLongToString(RekapUangMasuk.hitungTotal(it))}"
                binding.tvTotal.text = total
            }
        }
    }

    private fun setupRekapUangMasukTable() {
        val columnHeader = viewModel.getRumColumnHeader()
        val rowHeader = viewModel.getRumRowHeader()
        val listCells = viewModel.getRumListCells()

        val adapter = RekapUangMasukTableViewAdapter()

        binding.tableRekap.setAdapter(adapter)

        adapter.setAllItems(columnHeader, rowHeader, listCells)

        binding.tableRekap.apply {
            setColumnWidth(0, 400) // Nama Costumer
            setColumnWidth(1, 300) // Tanggal
            setColumnWidth(2, 300) // Jenis Pembelian
            setColumnWidth(3, 350) // Jumlah pembayaran
        }
    }

    private fun setupSortSelectionSpinner() {
        binding.spinnerSortMode.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listSortMode
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()

        super.onBackPressed()
    }
}