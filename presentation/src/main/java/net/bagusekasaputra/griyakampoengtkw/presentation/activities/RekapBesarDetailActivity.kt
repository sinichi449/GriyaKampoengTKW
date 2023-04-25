package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    enum class FabMode {
        Upward, Downward
    }

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

        // Disable FAB Scroll Type on NOT-INCLUDED DATA LAMA
        binding.fabScrollDataType.visibility = if (rekapDetailTransport?.includeDataLama == true)
            View.VISIBLE else View.GONE

        setupViewModel()
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



    private fun setupViewModel() {
        val progressDialog = ProgressDialog(this).apply {
            setTitle("Tunggu sebentar...")
            setMessage("Menyiapkan data ...")
            setCancelable(false)
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                viewModel.gettingRekapBesarJob?.cancel()

                dialog.dismiss()
            }
        }

        viewModel.isRekapBesarDetailLoaded.observe(this) {
            it?.also { loaded ->
                if (loaded) {
                    progressDialog.dismiss()
                } else {
                    progressDialog.show()
                }
            }
        }
    }

    fun getFabScrollDataType(): FloatingActionButton {
        return binding.fabScrollDataType
    }

    fun setFabScrollDataTypeIcon(fabMode: FabMode) {
        // TODO
    }

    override fun onResume() {
        super.onResume()

        viewModel.getRekapBesarDetail {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}