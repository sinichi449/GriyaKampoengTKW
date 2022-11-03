package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDetailBinding
import net.bagusekasaputra.griyakampoengtkw.ui.detail.adapter.ViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.ui.main.MainActivity
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: DetailViewModel by viewModels()

    private lateinit var currentKavlingKode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.keyboard_arrow_left_36px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        binding.viewPager.adapter = pagerAdapter
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