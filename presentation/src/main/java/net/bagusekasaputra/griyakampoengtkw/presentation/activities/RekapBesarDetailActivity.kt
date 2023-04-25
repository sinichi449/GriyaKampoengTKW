package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.RekapDetailTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityRekapBesarDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapBesarDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapBesarDetailBinding
    private lateinit var navController: NavController
    private val viewModel: RekapViewModel by viewModels()

    companion object {
        const val EXTRAS_REKAP_DETAIL_TRANSPORT = "EXTRAS_REKAP_DETAIL_TRANSPORT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekapBesarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        val rekapDetailTransport = intent?.extras?.getSerializable(EXTRAS_REKAP_DETAIL_TRANSPORT) as RekapDetailTransport?
        rekapDetailTransport?.also { viewModel.setRekapDetailTransport(it) }
        setupToolbar(rekapDetailTransport)


        // Navigate to corresponding fragments
        navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView_rekap_detail) as NavHostFragment).navController
        val bundleForFragments = Bundle().apply {
            putSerializable(EXTRAS_REKAP_DETAIL_TRANSPORT, rekapDetailTransport)
        }
        navController.navigate(when (rekapDetailTransport?.rekapType) {
            RekapType.UangMasuk -> R.id.nav_rekap_detail_uang_masuk
            RekapType.SisaPembayaran -> R.id.nav_rekap_detail_sisa_pembayaran
            else -> R.id.nav_rekap_detail_sisa_pembayaran
        }, bundleForFragments)
    }

    private fun setupToolbar(rekapDetailTransport: RekapDetailTransport?) {
        setSupportActionBar(binding.toolbarRekapDetail)

        binding.toolbarRekapDetail.apply {
            title = when (rekapDetailTransport?.rekapType) {
                RekapType.UangMasuk -> "Uang Masuk"
                RekapType.SisaPembayaran -> "Sisa Pembayaran"
                RekapType.FeeMarketing -> "Fee Marketing"
                RekapType.BiayaMarketing -> "Biaya Marketing"
                else -> "Unknown/NULL Rekap Type"
            }
            subtitle = rekapDetailTransport?.getRangeTanggal()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}