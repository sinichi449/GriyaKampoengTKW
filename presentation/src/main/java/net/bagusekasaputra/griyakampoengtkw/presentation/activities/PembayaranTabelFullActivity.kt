package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.TabelPembayaranViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.TabelPembayaranNavHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityPembayaranTabelFullBinding
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
                binding.btnSwitchTabel?.text = when (tableType) {
                    TablePembayaranType.FORM_PEMBAYARAN -> "Per Bulan"
                    TablePembayaranType.PEMBAYARAN_BULANAN -> "Semua"
                }
            }

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}