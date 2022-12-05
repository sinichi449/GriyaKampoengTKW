package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.MainViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.BiayaLainFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.KavlingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val rekapViewModel: RekapViewModel by viewModels()

    // SharedPreferences to load the user settings, such as offline mode
    @Inject
    lateinit var sharedPrefs: SharedPreferences


    // Hide fabs on Report tabs
    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            logEvent("Tab selected -> ${tab?.position}")

            viewModel.tabSelectedLive.value = tab?.position
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            logEvent("Tab unselected -> ${tab?.position}")
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            logEvent("Tab reselected -> ${tab?.position}")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbarMain)

        // Connectivity check
        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, true)

        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
        }
        val offlineMode = sharedPrefs.getBoolean("offline_mode", false)
        if (offlineMode) {
            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
        }
        // Update offlineMode state in viewModel
        viewModel.offlineMode = offlineMode

        // For setup the fabs
        setupViewModel()

        // Getting BuildConfig from Splash Activity, and check available update.
        val appVersionName = intent.getStringExtra("versionName") ?: ""
        val appVersionCode = intent.getIntExtra("versionCode", 0)
        if ((appVersionName != "") and (appVersionCode != 0)) {
            viewModel.checkUpdates(
                versionName = appVersionName,
                versionCode = appVersionCode,
                onAvailable = {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Update Tersedia!")
                        .setMessage(
                            it.releaseNotes.let { notes ->
                                val result = StringBuilder()

                                notes.forEach { text ->
                                    result.append("- ")
                                        .append(text)
                                        .append("\n")
                                }

                                return@let result.toString()
                            }
                        )
                        .setPositiveButton("Update") { _, _ ->
                            openBrowser(Uri.parse(it.url))
                        }
                        .create()
                        .show()
                },
                onFailure = {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            )
        }


        // On setting icon listener
        binding.toolbarMain.setNavigationOnClickListener {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            finish()
        }

        setupViewPager()

        binding.fabActions.shrink()
    }

    private fun setupViewModel() {
        viewModel.tabSelectedLive.observe(this) {
            it?.let { tabSelected ->
                val tabKavling = 0
                val tabRekap = 1
                val tabReportMisc = 2
                val tabPengingat = 3

                if (tabSelected == tabRekap) {
                    binding.fabActions.visibility = View.INVISIBLE
                } else {
                    binding.fabActions.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupViewPager() {
//        val fragments = listOf(KavlingFragment(), ReportFragment(), PengingatFragment())
//        val fragments = listOf(
//            KavlingFragment(),
//            RekapFragment(),
//            BiayaLainFragment(),
//            PengingatFragment(),
//        )
        val fragments = listOf(
            KavlingFragment(),
            RekapFragment(),
            BiayaLainFragment(),
        )
        val pagerAdapter = MainViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            fragments = fragments,
        )

        binding.viewpagerMain.apply {
            adapter = pagerAdapter
        }

        binding.tabLayoutMain.apply {
            setupWithViewPager(binding.viewpagerMain)
            tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC

            val getIcon = { iconId: Int -> ContextCompat.getDrawable(this@MainActivity, iconId) }
            getTabAt(0)?.icon = getIcon(R.drawable.ic_baseline_kavling_24)
            getTabAt(1)?.icon = getIcon(R.drawable.ic_baseline_report_24)
            getTabAt(2)?.icon = getIcon(R.drawable.ic_baseline_attach_money_24)
            getTabAt(3)?.icon = getIcon(R.drawable.ic_baseline_alarm_24)
        }

        binding.tabLayoutMain.addOnTabSelectedListener(tabSelectedListener)
    }


    private fun openBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.tabLayoutMain.removeOnTabSelectedListener(tabSelectedListener)
    }
}