package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.DetailViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.BiayaMarketingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.DataDiriFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.FormPembayaranFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.receiver.ProgressReceiver
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var pagerAdapter: DetailViewPagerAdapter
    private val viewModel: DetailViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()

    // SharedPreferences to load the user settings, such as offline mode
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private lateinit var currentKavlingKode: String

    private val progressReceiver = ProgressReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // Set
//        window.apply {
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            statusBarColor = ContextCompat.getColor(this@DetailActivity, R.color.purple_500)
//        }


        binding = ActivityDetailBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbarDetail)

        // For removing app bar on landscape mode.
        // The Pembayaran table, in the FormPembayaranFragment need this.
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
        }

        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.keyboard_arrow_left_36px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Data Lama / Data Baru Mode?
        val pathDataLama = sharedPrefs.getString("dataLamaPath", null)
        if (pathDataLama != null) {
            viewModel.dataMode = DataMode.DATA_LAMA

            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
        }


        // DataMode check
        val offlineMode = sharedPrefs.getBoolean("offline_mode", false)
        if (offlineMode) {
            viewModel.offlineMode = true
            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
        }


        val kavlingKode = intent.getStringExtra(MainActivity.INTENT_KAVLING_KODE)
        kavlingKode?.let {
            supportActionBar?.title = "Kavling $it"
            viewModel.currentKavlingKode.value = it
            currentKavlingKode = it
        }

        setupViewPager()

        // setup broadcast receiver
        val intentFilter = IntentFilter("net.bagusekasaputra.griyakampoengtkw.ACTION_NOTIFY_PROGRESS")
        registerReceiver(progressReceiver, intentFilter)
    }

    private fun setupViewPager() {
        pagerAdapter = DetailViewPagerAdapter(supportFragmentManager)
        pagerAdapter.apply {
            addFragment(putKavlingKode(DataDiriFragment(), currentKavlingKode), "Data Diri")
            addFragment(putKavlingKode(FormPembayaranFragment(), currentKavlingKode), "Form Pembayaran")
            addFragment(putKavlingKode(BiayaMarketingFragment(), currentKavlingKode), "Biaya Marketing")
        }

        binding.viewPager.apply {
            adapter = pagerAdapter
//            setPageTransformer(true, DepthPageTransformer(0.75f))
        }
        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPager)
            tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC
//            val getIcon = { iconId: Int -> ContextCompat.getDrawable(this@DetailActivity, iconId) }
//            getTabAt(0)?.icon = getIcon(R.drawable.ic_baseline_person_24)
//            getTabAt(1)?.icon = getIcon(R.drawable.ic_baseline_attach_money_24)
//            getTabAt(2)?.icon = getIcon(R.drawable.ic_baseline_account_balance_wallet_24)
        }
    }

    private fun putKavlingKode(fragment: Fragment, kavlingKode: String): Fragment {
        return fragment.apply {
            arguments = Bundle().apply {
                putString(GriyaNodes.INTENT_KAVLING_KODE, kavlingKode)
            }
        }
    }

    override fun onBackPressed() {
        val allowExit = imageViewModel.allowExit.value

        if (allowExit?.not() == true) {
            MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).apply {
                setTitle("Batalkan Sinkronisasi Gambar?")
                setMessage("Sistem mendeteksi sedang terjadi sinkronisasi gambar. Apakah Anda yakin ingin keluar dengan membatalkan proses tersebut?")
                setPositiveButton("Ya") { dialog, _ ->
                    dialog.dismiss()

                    super.onBackPressed()
                    finish()
                }
                setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            }
                .create()
                .show()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(progressReceiver)

        super.onDestroy()
    }
}