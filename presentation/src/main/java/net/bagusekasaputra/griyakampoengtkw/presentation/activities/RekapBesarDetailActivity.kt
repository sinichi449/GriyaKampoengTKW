package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityRekapBesarDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.getRekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapBesarDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapBesarDetailBinding
    private lateinit var navController: NavController
    private val viewModel: RekapViewModel by viewModels()

    companion object {
        const val EXTRAS_REKAP_TYPE = "EXTRAS_REKAP_TYPE"
        const val EXTRAS_START_DATE = "EXTRAS_START_DATE"
        const val EXTRAS_END_DATE = "EXTRAS_END_DATE"
        const val EXTRAS_INCLUDE_DATA_LAMA = "EXTRAS_INCLUDE_DATA_LAMA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekapBesarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        val rekapType = intent?.extras?.getString(EXTRAS_REKAP_TYPE)?.let { getRekapType(it) }
        val startDate = intent?.extras?.getString(EXTRAS_START_DATE)
        val endDate = intent?.extras?.getString(EXTRAS_END_DATE)
        setupToolbar(rekapType, startDate, endDate)


        // Navigate to corresponding fragments
        navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView_rekap_detail) as NavHostFragment).navController
        val dataLamaIncluded = intent?.extras?.getBoolean(EXTRAS_INCLUDE_DATA_LAMA) ?: false
        val bundleForFragments = Bundle().apply {
            putString(EXTRAS_START_DATE, startDate)
            putString(EXTRAS_END_DATE, endDate)
            putBoolean(EXTRAS_INCLUDE_DATA_LAMA, dataLamaIncluded)
        }
        navController.navigate(when (rekapType) {
            RekapType.UangMasuk -> R.id.nav_rekap_detail_uang_masuk
            RekapType.SisaPembayaran -> R.id.nav_rekap_detail_sisa_pembayaran
            else -> R.id.nav_rekap_detail_sisa_pembayaran
        }, bundleForFragments)
    }

    private fun setupToolbar(rekapType: RekapType?, startDate: String?, endDate: String?) {
        setSupportActionBar(binding.toolbarRekapDetail)

        binding.toolbarRekapDetail.apply {
            title = when (rekapType) {
                RekapType.UangMasuk -> "Uang Masuk"
                RekapType.SisaPembayaran -> "Sisa Pembayaran"
                RekapType.FeeMarketing -> "Fee Marketing"
                RekapType.BiayaMarketing -> "Biaya Marketing"
                else -> "Unknown Rekap Type"
            }
            val dateRange = "$startDate - $endDate"
            subtitle = dateRange
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}