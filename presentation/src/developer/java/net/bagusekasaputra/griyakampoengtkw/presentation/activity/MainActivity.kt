package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import javax.inject.Inject

/**
 * Removed App Update checking and Promotion Benner
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // SharedPreferences to load the user settings
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment_main)
                as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_management_kavling,
                R.id.nav_inden_booking,
                R.id.nav_pengaturan,
            ),
            drawerLayout = binding.drawerMain,
        )

        binding.toolbarMain.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbarMain)

        binding.navViewMain.setupWithNavController(navController)

        // Connectivity check
        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, true)

        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
        }

        // Data Lama / Data Baru Mode?
        val pathDataLama = intent?.getStringExtra("dataLamaPath")
        if (pathDataLama != null) {
            viewModel.dataMode = DataMode.DATA_LAMA

            sharedPrefs.edit(true) {
                putString("dataLamaPath", pathDataLama)
            }

            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
            binding.connectivityStatus.tvStatus.text = "Mode Data Lama"
        }

        val offlineMode = sharedPrefs.getBoolean("offline_mode", false)
        if (offlineMode) {
            viewModel.offlineMode = true
            viewModel.dataMode = DataMode.OFFLINE

            binding.connectivityStatus.constraintConnectivity.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }
}