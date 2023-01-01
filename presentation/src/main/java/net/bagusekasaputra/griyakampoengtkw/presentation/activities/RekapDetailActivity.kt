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
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.entity.getPeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityRekapDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.getRekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RekapUangMasukTableViewAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapDetailBinding
    private lateinit var rekapType: RekapType

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

        val rekapPeriode = getPeriodeRekap(intent?.extras?.getString("INTENT_PERIODE_REKAP") ?: "-")
        val startDate = intent?.extras?.getString("INTENT_START_DATE")?.toDate()
        val endDate = intent?.extras?.getString("INTENT_END_DATE")?.toDate()

        rekapType = getRekapType(intent?.extras?.getString("INTENT_REKAP_TYPE") ?: "-") ?: RekapType.UangMasuk
        when (rekapType) {
            RekapType.UangMasuk -> {
                binding.toolbarMain.title = "Uang Masuk"
                viewModel.getListUangMasukRekap { failMsg ->
                    Toast.makeText(this.applicationContext, failMsg, Toast.LENGTH_LONG).show()
                }
            }
            RekapType.FeeMarketing -> {
                binding.toolbarMain.title = "Fee Marketing"
                if (rekapPeriode != null) {
                    viewModel.getFeeMarketingRekap(rekapPeriode, startDate, endDate) { failMsg ->
                        Toast.makeText(this.applicationContext, failMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this.applicationContext, "Terjadi kesalahan: NULL REKAP_TYPE from getPeriodeRekap()", Toast.LENGTH_LONG).show()
                }
            }
            RekapType.BiayaMarketing -> {
                binding.toolbarMain.title = "Biaya Marketing"
                if (rekapPeriode != null) {
                    viewModel.getBiayaMarketingRekap(rekapPeriode, startDate, endDate) { failMsg ->
                        Toast.makeText(this.applicationContext, failMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this.applicationContext, "Terjadi kesalahan: NULL REKAP_TYPE from getPeriodeRekap()", Toast.LENGTH_LONG).show()
                }
            }
            else -> Toast.makeText(this.applicationContext, "Masih tahap beta, belum bisa digunakan", Toast.LENGTH_LONG).show()
        }

        setupSortSelectionSpinner()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.listRekapUangMasukLive.observe(this) {
            if (it != null) {
                setupRekapUangMasukTable()

                val total = "Rp. ${NumberUtil.formatLongToString(RekapUangMasuk.hitungTotal(it))}"
                binding.tvTotal.text = total

            }
        }

        viewModel.listFeeMarketingRekapLive.observe(this) {
            if (it != null) {
                setupFeeMarketingTable()

                val total = it.let { feeMarketings ->
                    var mTotal = 0L
                    feeMarketings.forEach { item -> mTotal += item.parsedBiayaMarketer }

                    "- Rp. ${NumberUtil.formatLongToString(mTotal)}"
                }
                binding.tvTotal.text = total
            }
        }

        viewModel.listBiayaMarketingRekapLive.observe(this) {
            if (it != null) {
                setupBiayaMarketingTable()

                val total = it.let { batchBiayaMarketing ->
                    var mTotal = 0L

                    batchBiayaMarketing.forEach { item -> mTotal += BiayaMarketing.hitungTotalBiayaMarketing(item.value)}

                    "- Rp. ${NumberUtil.formatLongToString(mTotal)}"
                }
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

    private fun setupFeeMarketingTable() {
        val columnHeader = viewModel.getFeeMarketingColumnHeader()
        val rowHeader = viewModel.getFeeMarketingRowHeader()
        val listCells = viewModel.getFeeMarketingListCells()

        val adapter = RekapUangMasukTableViewAdapter()

        binding.tableRekap.setAdapter(adapter)

        adapter.setAllItems(columnHeader, rowHeader, listCells)

        binding.tableRekap.apply {
            setColumnWidth(0, 400) // Nama Marketer
            setColumnWidth(1, 300) // Tanggal Penerimaan
            setColumnWidth(2, 350) // Jumlah Uang
        }
    }

    private fun setupBiayaMarketingTable() {
        val columnHeader = viewModel.getBiayaMarketingColumnHeader()
        val rowHeader = viewModel.getBiayaMarketingRowHeader()
        val listCells = viewModel.getBiayaMarketingListCells()

        val adapter = RekapUangMasukTableViewAdapter()

        binding.tableRekap.setAdapter(adapter)

        adapter.setAllItems(columnHeader, rowHeader, listCells)

        binding.tableRekap.apply {
            setColumnWidth(0, 500) // Jenis Biaya
            setColumnWidth(1, 300) // Tanggal
            setColumnWidth(2, 350) // Harga
        }
    }

    private fun setupSortSelectionSpinner() {
        binding.spinnerSortMode.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listSortMode
        )

        binding.spinnerSortMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                when (rekapType) {
                    RekapType.UangMasuk -> viewModel.sortListRekapUangMasuk(listSortMode[position])
                    else -> {}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
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