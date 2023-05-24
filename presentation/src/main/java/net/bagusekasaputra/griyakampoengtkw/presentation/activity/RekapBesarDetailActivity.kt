@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.RekapDetailTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityRekapBesarDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
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
        setSupportActionBar(binding.toolbarRekapDetail)
        binding.toolbarRekapDetail.subtitle = rekapDetailTransport?.getRangeTanggal()


        // Navigate to corresponding fragments
        navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView_rekap_detail) as NavHostFragment).navController
        val bundleForFragments = Bundle().apply {
            putSerializable(EXTRAS_REKAP_DETAIL_TRANSPORT, rekapDetailTransport)
        }
        navController.navigate(when (rekapDetailTransport?.rekapType) {
            RekapType.UangMasuk -> R.id.nav_rekap_detail_uang_masuk
            RekapType.SisaPembayaran -> R.id.nav_rekap_detail_sisa_pembayaran
            RekapType.FeeMarketing -> R.id.nav_rekap_detail_fee_marketing
            RekapType.BiayaMarketing -> R.id.nav_rekap_detail_biaya_marketing
            RekapType.BiayaLain -> R.id.nav_rekap_detail_biaya_lain
            else -> R.id.nav_rekap_detail_sisa_pembayaran
        }, bundleForFragments)

        // Disable FAB Scroll Type on NOT-INCLUDED DATA LAMA
        binding.fabScrollDataType.visibility = if (rekapDetailTransport?.includeDataLama == true)
            View.VISIBLE else View.GONE

        setupViewModel()
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

    fun setFabScrollingBehavior(scrollView: NestedScrollView, upwardView: View, downwardView: View) {
        binding.fabScrollDataType.setOnClickListener {
            val fabMode = viewModel.fabScrollMode
            if (fabMode == FabMode.Upward) {
                setFabScrollDataTypeIcon(FabMode.Downward)
                UiUtils.scrollToView(scrollView, downwardView)
            } else {
                setFabScrollDataTypeIcon(FabMode.Upward)
                UiUtils.scrollToView(scrollView, upwardView)
            }
        }
    }

    fun setToolbarTitle(rekapType: RekapType, total: Long) {
        val parsedTotal = "Rp. ${NumberUtil.formatLongToString(total)}"

        binding.toolbarRekapDetail.apply {
            title = when (rekapType) {
                RekapType.UangMasuk -> "Uang Masuk"
                RekapType.SisaPembayaran -> "Sisa Pembayaran"
                RekapType.FeeMarketing -> "Fee Marketing"
                RekapType.BiayaMarketing -> "Biaya Marketing"
                RekapType.BiayaLain -> "Biaya Lain-lain"
                else -> "Unknown/NULL Rekap Type"
            } + " - $parsedTotal"
        }
    }

    private fun setFabScrollDataTypeIcon(fabMode: FabMode) {
        val resId = if (fabMode == FabMode.Upward) R.drawable.baseline_arrow_downward_24
            else R.drawable.baseline_arrow_upward_24
        val drawable = ContextCompat.getDrawable(this, resId)
        binding.fabScrollDataType.setImageDrawable(drawable)

        viewModel.fabScrollMode = fabMode
    }

    override fun onResume() {
        super.onResume()

        viewModel.getRekapBesarDetail {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }
}