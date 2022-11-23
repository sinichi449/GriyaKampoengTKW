package net.bagusekasaputra.griyakampoengtkw.presentation.detail

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityDetailBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.adapter.ViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.fragment.BiayaMarketingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.fragment.DataDiriFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.fragment.FormPembayaranFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.main.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: DetailViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()

    // SharedPreferences to load the user settings, such as offline mode
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private lateinit var currentKavlingKode: String

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

        // Connectivity Check
        val offlineMode = sharedPrefs.getBoolean("offline_mode", false)
        if (offlineMode)
            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
        // Update offline mode state in viewModel
        viewModel.offlineMode = offlineMode

        val kavlingKode = intent.getStringExtra(MainActivity.INTENT_KAVLING_KODE)
        kavlingKode?.let {
            supportActionBar?.title = "Kavling $it"
            viewModel.currentKavlingKode.value = it
            currentKavlingKode = it
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
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
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}