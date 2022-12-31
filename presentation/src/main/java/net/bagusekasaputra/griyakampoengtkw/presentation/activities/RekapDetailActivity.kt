package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

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
    }

    override fun onBackPressed() {
        finish()

        super.onBackPressed()
    }
}