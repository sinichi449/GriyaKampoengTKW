package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityMainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.HeaderMainNavBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import javax.inject.Inject

/**
 * App Update and Promotion Banner
 */
@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KAVLING_KODE = "kavling_kode"
        
        const val EXTRAS_VERSION_NAME = "versionName"
        const val EXTRAS_VERSION_CODE = "versionCode"
        const val EXTRAS_DATA_MODE = "dataMode"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * [appBarTopLevelDestinations] is equal to a question:
     * "At which navigation menu should I show [R.drawable.baseline_menu_24] instead of
     * [R.drawable.ic_baseline_arrow_back_24] icon?"
     */
    private val appBarTopLevelDestinations by lazy {
        setOf(
            R.id.nav_management_kavling,
            R.id.nav_database_user,
            R.id.nav_inden_booking,
            R.id.nav_biaya_pribadi,
            R.id.nav_biaya_pembangunan,
            R.id.nav_pengaturan,
        )
    }

    // SharedPreferences to load the user settings, selected tahapan, and selected
    // data lama name.
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment_main)
                as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = appBarTopLevelDestinations,
            drawerLayout = binding.drawerMain,
        )

        binding.toolbarMain.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbarMain)

        // Toolbar subtitle -> Tahapan
        sharedPrefs.getString("SELECTED_TAHAPAN", null)?.also {
            binding.toolbarMain.subtitle = Tahapan.getSimpleInstance(it).nama.uppercase()
        }

        // Setup navigation view and header layout
        with(binding.navViewMain) {
            setupWithNavController(navController)

            // Getting BuildConfig from Splash Activity
            val appVersionName = intent.getStringExtra(EXTRAS_VERSION_NAME) ?: "NULL"
            val appVersionCode = intent.getIntExtra(EXTRAS_VERSION_CODE, 0)

            val navHeaderLayoutBinding = HeaderMainNavBinding.inflate(layoutInflater)
            navHeaderLayoutBinding.tvVersionName.text = "v$appVersionName"
            navHeaderLayoutBinding.tvVersionCode.text = appVersionCode.toString()

            addHeaderView(navHeaderLayoutBinding.root)
        }

        // Connectivity check
        val deviceOnline = intent.getBooleanExtra(GriyaNodes.INTENT_IS_ONLINE, true)

        if (!deviceOnline) {
            Toast.makeText(this, "Device terdeteksi offline, data tidak akan tersinkronisasi!", Toast.LENGTH_LONG).show()
        }

        // Data Lama / Data Baru Mode?
        handleDataMode(intent, sharedPrefs) { dataMode ->
            val layoutConnectivityVisibility : Int
            val tvStatusText: String
            when (dataMode) {
                DataMode.ONLINE -> {
                    layoutConnectivityVisibility = View.GONE
                    tvStatusText = "Online"
                }
                DataMode.OFFLINE -> {
                    layoutConnectivityVisibility = View.VISIBLE
                    tvStatusText = "Offline"
                }
                DataMode.DATA_LAMA -> {
                    layoutConnectivityVisibility = View.GONE
                    tvStatusText = "Mode DataLama"
                }
            }

            // Connectivity status will visibile if `dataMode` != `DataMode.ONLINE`
            with(binding.connectivityStatus) {
                constraintConnectivity.visibility = layoutConnectivityVisibility
                tvStatus.text = tvStatusText
            }
        }

        setupViewModel()

        checkUpdate()

        // Get Promotion Message
        viewModel.getPromotionMessage(onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun setupViewModel() {
//        viewModel.promotionMessage.observe(this) {
//            it?.also {
//                showPromotionMessageDialog(it)
//            }
//        }
    }

    /**
     * Edit [sharedPreferences] following the received [EXTRAS_DATA_MODE] `Intent`.
     */
    private fun handleDataMode(
        intent: Intent?,
        sharedPreferences: SharedPreferences,
        onDataModeReceived: (dataMode: DataMode) -> Unit,
    ) {
        val dataMode = intent?.extras?.getString(EXTRAS_DATA_MODE)

        if (!dataMode.isNullOrEmpty()) {
            sharedPreferences.edit(true) {
                putString("DATA_MODE", dataMode)
            }

            val result: DataMode = when (dataMode) {
                DataMode.ONLINE.name -> DataMode.ONLINE
                DataMode.OFFLINE.name -> DataMode.OFFLINE
                DataMode.DATA_LAMA.name -> DataMode.DATA_LAMA
                else -> {
                    Toast.makeText(this, "Data Mode $dataMode tidak dikenali!", Toast.LENGTH_SHORT)
                        .show()

                    DataMode.ONLINE
                }
            }
            onDataModeReceived(result)

            viewModel.dataMode = result
            viewModel.offlineMode = result == DataMode.OFFLINE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        with(viewModel.managementKavlingFragment.value) {
            val shouldNavigatetoKavlingFragment = viewModel.shouldNavigateToKavlingFragment.value

            if ((this != null) && (shouldNavigatetoKavlingFragment == true)) {
                navigateToKavlingFragment()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun checkUpdate() {
        // Getting BuildConfig from Splash Activity, and check available update.
        val appVersionName = intent.getStringExtra(EXTRAS_VERSION_NAME)
        val appVersionCode = intent.getIntExtra(EXTRAS_VERSION_CODE, 0)
        if (appVersionName != null) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }
}