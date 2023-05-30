package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    // Needs to be defined here, since needs to be removed when activity stop
    private val viewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            // Change fab icon
            val fabIconResId = when (position) {
                0 -> R.drawable.ic_baseline_camera_alt_24 // Data Diri
                1 -> R.drawable.ic_baseline_add_24 // Pembayaran
                else -> R.drawable.ic_baseline_edit_24
            }
            val fabIconDrawable = ContextCompat.getDrawable(
                this@DetailIndenBookingActivity, fabIconResId
            )

            binding.fabEdit.setImageDrawable(fabIconDrawable)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    companion object {
        const val EXTRAS_NAMA_COSTUMER = "EXTRAS_NAMA_COSTUMER"
        const val EXTRAS_KEY_ID = "EXTRAS_KEY_ID"
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
        val keyId = intent?.extras?.getString(EXTRAS_KEY_ID) ?: "NULL_ID"

        viewModel.currentKeyId = keyId

        binding.toolbarDetail.title = viewModel.namaCostumer
        binding.toolbarDetail.subtitle = keyId

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPagerAdapter = DetailIndenBookingViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.apply {
            addFragment(DataDiriIndenBookingFragment(), "Data Diri")
            addFragment(FormPembayaranIndenBookingFragment(), "Form Pembayaran")
        }

        binding.viewPagerDetailIndenBooking.apply {
            adapter = viewPagerAdapter
            addOnPageChangeListener(viewPagerListener)

        }
        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPagerDetailIndenBooking)
            tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC
        }
    }

    fun getFab(): FloatingActionButton {
        return binding.fabEdit
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onStop() {
        binding.viewPagerDetailIndenBooking.removeOnPageChangeListener(viewPagerListener)
        super.onStop()
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