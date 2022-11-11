package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDetailBinding
import net.bagusekasaputra.griyakampoengtkw.ui.custom.DepthPageTransformer
import net.bagusekasaputra.griyakampoengtkw.ui.detail.adapter.ViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.DetailViewModel
import net.bagusekasaputra.griyakampoengtkw.ui.main.MainActivity
import net.bagusekasaputra.griyakampoengtkw.ui.network.ConnectivityAnimation
import net.bagusekasaputra.griyakampoengtkw.ui.network.InternetAvailability
import net.bagusekasaputra.griyakampoengtkw.ui.network.NetworkStatus
import net.bagusekasaputra.griyakampoengtkw.ui.network.NetworkStatusHelper
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: DetailViewModel by viewModels()

    private lateinit var connectivityAnimation: ConnectivityAnimation
    private lateinit var currentKavlingKode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.keyboard_arrow_left_36px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set connectivity
        connectivityAnimation = ConnectivityAnimation(this, binding.root, binding.connectivityStatus)

        val kavlingKode = intent.getStringExtra(MainActivity.INTENT_KAVLING_KODE)
        kavlingKode?.let {
            supportActionBar?.title = "Kavling $it"
            viewModel.currentKavlingKode.value = it
            currentKavlingKode = it
        }

        setupViewPager()

        initialInternetCheck()

        setupInternetMonitoring()
    }

    private fun setupInternetMonitoring() {
        val networkHelper = NetworkStatusHelper(this)

        networkHelper.observe(this) { status ->
            status?.let {
                if (it == NetworkStatus.Available) {
                    connectivityAnimation.onOnlineAnimation()
                    // TODO on internet available
                } else if (it == NetworkStatus.Unavailable) {
                    connectivityAnimation.onOfflineAnimation()
                    // TODO on internet unavailable
                }
            }
        }
    }

    private fun initialInternetCheck() {
        lifecycleScope.launch {
            val hasInternet = InternetAvailability.check()

            if (!hasInternet) {
                withContext(Dispatchers.Main) {
                    connectivityAnimation.onOfflineAnimation()
                }
            }
        }
    }

    private fun setupViewPager() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.apply {
            addFragment(putKavlingKode(DataDiriFragment(), currentKavlingKode), "Data Diri")
            addFragment(putKavlingKode(FormPembayaranFragment(), currentKavlingKode), "Form Pembayaran")
            addFragment(putKavlingKode(BiayaMarketingFragment(), currentKavlingKode), "Biaya Marketing")
        }

        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setPageTransformer(true, DepthPageTransformer())
        binding.tabLayout.setupWithViewPager(binding.viewPager)
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

    override fun onResume() {
        super.onResume()

//        viewModel.currentKavlingKode.value?.let {
//            viewModel.getDataDiri(it)
//        }
    }

}