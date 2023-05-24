package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.DetailIndenBookingViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityDetailIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking.DataDiriIndenBookingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking.FormPembayaranIndenBookingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class DetailIndenBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailIndenBookingBinding
    private val viewModel by viewModels<IndenBookingViewModel>()

    companion object {
        const val EXTRAS_NAMA_COSTUMER = "EXTRAS_NAMA_COSTUMER"
        const val EXTRAS_NOMOR_URUT = "EXTRAS_NOMOR_URUT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailIndenBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.keyboard_arrow_left_36px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val namaCostumer = intent?.extras?.getString(EXTRAS_NAMA_COSTUMER)
        if (namaCostumer.isNullOrEmpty()) {
            Toast.makeText(this, "ERROR: Intent Nama Customer hasn't been passed properly!", Toast.LENGTH_LONG).show()
        } else {
            viewModel.namaCostumer = namaCostumer
        }
        val sortingNum = intent?.extras?.getString(EXTRAS_NOMOR_URUT)

        binding.toolbarDetail.title = viewModel.namaCostumer
        binding.toolbarDetail.subtitle = "Inden Booking #${sortingNum}"

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPagerAdapter = DetailIndenBookingViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.apply {
            addFragment(DataDiriIndenBookingFragment(), "Data Diri")
            addFragment(FormPembayaranIndenBookingFragment(), "Form Pembayaran")
        }

        binding.viewPagerDetailIndenBooking.adapter = viewPagerAdapter
        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPagerDetailIndenBooking)
            tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Back Arrow Icon onClick
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}