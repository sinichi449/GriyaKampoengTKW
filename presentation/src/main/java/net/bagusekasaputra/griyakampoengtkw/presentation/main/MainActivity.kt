package net.bagusekasaputra.griyakampoengtkw.presentation.main

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
import net.bagusekasaputra.griyakampoengtkw.presentation.SettingsActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.main.adapter.MainViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.main.fragment.KavlingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.main.fragment.ReportFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var isAllFabsVisible = false

    // SharedPreferences to load the user settings, such as offline mode
    @Inject
    lateinit var sharedPrefs: SharedPreferences

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
    }

    private fun setupViewPager() {
        val fragments = listOf(KavlingFragment(), ReportFragment())
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
        }
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
}