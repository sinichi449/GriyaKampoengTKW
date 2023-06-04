package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.TabelPembayaranNavHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityPembayaranTabelFullBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.BulananPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran.FullPembayaranTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel.TablePembayaranType

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class PembayaranTabelFullActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_KAVLING_KODE = "EXTRAS_KAVLING_KODE"
    }

    private lateinit var binding: ActivityPembayaranTabelFullBinding
    private val pembayaranViewModel by viewModels<FormPembayaranViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranTabelFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kavling = intent?.extras?.getString(EXTRAS_KAVLING_KODE)
        if (!kavling.isNullOrEmpty()) {
            val snackBarLoading = Snackbar.make(binding.root, "Mendapatkan List Pembayaran ...", Snackbar.LENGTH_INDEFINITE)
            pembayaranViewModel.getListPembayaranBulanan(
                kavling,
                onLoading = { snackBarLoading.show() },
                onSuccess = { snackBarLoading.dismiss() },
                onFailure = {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                },
            )
        }

        // Show sisa blm dibayar bulan ini
        pembayaranViewModel.isFullScreenTable = true

        val tabelNavHelper = TabelPembayaranNavHelper(
            lifecycleOwner = this,
            fragmentManager = supportFragmentManager,
            pembayaranViewModel = pembayaranViewModel,
            containerId = R.id.navHostFragment_full_pembayaran,
            triggerViews = arrayOf(
                binding.btnSwitchTabel
            ),
        )
        tabelNavHelper.listener = object : TabelPembayaranNavHelper.TabelPembayaranListener {
            override fun onTabelChanged(tableType: TablePembayaranType) {
                when (tableType) {
                    TablePembayaranType.FORM_PEMBAYARAN -> {
                        binding.btnSwitchTabel?.text = "Per Bulan"

                        binding.columnHeaderTabelFullPembayaran?.apply {
                            val emptyPembayaran = Pembayaran("ITJ 1", "01/01/1979", "0", "0", 0.0, "0", "", 0L)

                            FullPembayaranTableWrapper(this, listOf(emptyPembayaran))
                                .createTable(lifecycleScope)

                            visibility = View.VISIBLE
                        }
                        binding.columnHeaderTabelBulananPembayaran?.visibility = View.GONE
                    }
                    TablePembayaranType.PEMBAYARAN_BULANAN -> {
                        binding.btnSwitchTabel?.text = "Semua"

                        binding.columnHeaderTabelFullPembayaran?.visibility = View.GONE
                        binding.columnHeaderTabelBulananPembayaran?.apply {
                            val emptyBaselinePembayaran = BaselinePembayaran(kavling ?: "D1", 48, 0, 1)
                            val emptyPembayaranBulanans = listOf(
                                PembayaranBulanan(kavling ?: "D1", 1, 1, emptyList(), emptyBaselinePembayaran)
                            )

                            BulananPembayaranTableWrapper(this, emptyPembayaranBulanans)
                                .createTable(lifecycleScope)

                            visibility = View.VISIBLE
                        }
                    }
                }
            }

        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}