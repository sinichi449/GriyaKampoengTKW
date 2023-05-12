package net.bagusekasaputra.griyakampoengtkw

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.interfaces.remote.InitRemote
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bindingPure: ActivitySplashPureBinding
    private lateinit var bindingLoading: ActivitySplashWithLoadingBinding


    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var initRemote: InitRemote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ContextCompat.getColor(this@SplashActivity, R.color.app_theme_color)
        }

        bindingPure = ActivitySplashPureBinding.inflate(layoutInflater)
        setContentView(bindingPure.root)

        // Disable Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        showSplashScreen(1.5f)
    }

    private fun showSplashScreen(seconds: Float) {
        val handler = Handler()
        val splashRunnable = Runnable {
            bindingLoading = ActivitySplashWithLoadingBinding.inflate(layoutInflater)
            setContentView(bindingLoading.root)

            connectivityCheckAndInitServer()
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
    }

    private fun connectivityCheckAndInitServer() {
        CoroutineScope(Dispatchers.IO).launch {
            if (isDeviceOnline()) {
                withContext(Dispatchers.Main) {
                    bindingLoading.layoutCekKoneksi.tvInfoPeriksaInternet.text = "Memeriksa status server"
                }
                // Check Maintenance status
                val isMaintenance = initRemote.checkMaintenance()

                isMaintenance.onSuccess { maintenance ->
                    if (maintenance) {
                        withContext(Dispatchers.Main) {
                            MaterialAlertDialogBuilder(this@SplashActivity)
                                .setTitle("Server Maintenance")
                                .setCancelable(false)
                                .setMessage("Server terdekteksi dalam pemeliharaan.")
                                .setPositiveButton("Oke") { dialog, _ ->
                                    dialog.dismiss()

                                    showJenisDataChoice(isOnline = true, shouldShowDataBaruOption = true)
                                }
                                .create()
                                .show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showJenisDataChoice(isOnline = true, shouldShowDataBaruOption = true)
                        }
                    }
                }

                isMaintenance.onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SplashActivity, "Gagal mengecek status server: $it", Toast.LENGTH_LONG).show()
                        showJenisDataChoice(isOnline = true, shouldShowDataBaruOption = false)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SplashActivity,
                        "Device terdeteksi offline, mohon cek koneksi Anda.",
                        Toast.LENGTH_LONG
                    ).show()

                    showJenisDataChoice(isOnline = false, shouldShowDataBaruOption = false)
                }
            }
        }
    }
    private suspend fun isDeviceOnline(): Boolean {
        return suspendCoroutine { continuation ->
            try {
                val timeOutMs = 3000
                val sock = Socket()
                val sockAddr = InetSocketAddress("8.8.8.8", 53)

                sock.connect(sockAddr, timeOutMs)
                sock.close()

                continuation.resume(true)
            } catch (e: IOException) {
                e.printStackTrace()

                continuation.resumeWithException(e)
            }
        }
    }

    private fun showJenisDataChoice(isOnline: Boolean, shouldShowDataBaruOption: Boolean) {
        bindingLoading.layoutCekKoneksi.root.visibility = View.INVISIBLE

        // When checking Maintenance Status or Device Connectivity fails,
        // disable "Data Baru" button.
        if (!shouldShowDataBaruOption) {
            bindingLoading.layoutPilihData.btnDataBaru.visibility = View.GONE
        }

        bindingLoading.layoutPilihData.root.visibility = View.VISIBLE
        bindingLoading.layoutPilihData.btnDataLama.setOnClickListener {
            goToDocumentLamaActivity()
        }
        bindingLoading.layoutPilihData.btnDataBaru.setOnClickListener {
            // Nullify the sharedPreference Data Lama to prevent MainActivity/DetailActivity
            // to DataLama mode
            sharedPreferences.edit(true) {
                putString("dataLamaPath", null)
            }
            goToMainActivity(isOnline, true)
        }
    }

    private fun goToMainActivity(isOnline: Boolean, isNewDataSelected: Boolean) {
        // I also want to pass a BuildConfig for checking update.
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(GriyaNodes.INTENT_IS_ONLINE, isOnline)
        intent.putExtra("versionName", BuildConfig.VERSION_NAME)
        intent.putExtra("versionCode", BuildConfig.VERSION_CODE)
        intent.putExtra("isNewDataSelected", isNewDataSelected)
        startActivity(intent)
        finish()
    }

    private fun goToDocumentLamaActivity() {
        val intent = Intent(this, DataLamaActivity::class.java)
        startActivity(intent)
    }
}