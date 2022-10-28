package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDetailBinding
import net.bagusekasaputra.griyakampoengtkw.ui.main.MainActivity

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.keyboard_arrow_left_36px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val kavlingKode = intent.getStringExtra(MainActivity.INTENT_KAVLING_KODE)
        kavlingKode?.let {
            supportActionBar?.title = "Kavling $it"
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.apply {
            addFragment(DataDiriFragment(), "Data Diri")
            addFragment(FormPembayaranFragment(), "Form Pembayaran")
            addFragment(FeeMarketingFragment(), "Fee Marketing")
        }

        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
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