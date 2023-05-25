package net.bagusekasaputra.griyakampoengtkw

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bindingPure: ActivitySplashPureBinding
    private lateinit var bindingLoading: ActivitySplashWithLoadingBinding
    // Need to be initialized at onCreate()
    private lateinit var biometricManager: BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ContextCompat.getColor(this@SplashActivity, R.color.abang)
        }

        bindingPure = ActivitySplashPureBinding.inflate(layoutInflater)
        setContentView(bindingPure.root)

        // Disable Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        biometricManager = BiometricManager.from(this)
        biometricPrompt = BiometricUtil.instanceOfBiometricPrompt(this,
            onFailure = { errorCode: Int, _ ->
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_CANCELED) {
                    MaterialAlertDialogBuilder(this).apply {
                        setTitle("Authentikasi Gagal")
                        setMessage("Aplikasi ini memerlukan autentikasi pengguna. Jika tidak ada proses autentikasi yang berjalan sukses, aplikasi ini akan keluar.")
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setCancelable(false)

                        setOnDismissListener { finish() }
                    }.create()
                        .show()
                }
            },
            onSuccess = {
                connectivityCheckAndInitServer()
            }
        )

        showSplashScreen(1.5f)
    }

    @Suppress("SameParameterValue")
    private fun showSplashScreen(seconds: Float) {
        val handler = Handler()
        val splashRunnable = Runnable {
            bindingLoading = ActivitySplashWithLoadingBinding.inflate(layoutInflater)
            setContentView(bindingLoading.root)

            BiometricUtil.beginAuthentication(this, biometricManager, biometricPrompt)
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
    }

    @SuppressLint("SetTextI18n")
    private fun connectivityCheckAndInitServer() {
        CoroutineScope(Dispatchers.IO).launch {
            deviceOnline().collect { online ->
                if (online) {
                    withContext(Dispatchers.Main) {
                        bindingLoading.layoutCekKoneksi.tvInfoPeriksaInternet.text = "Memeriksa status server"
                    }
                    // Check Maintenance status
                    val isMaintenance = checkMaintenance()

                    isMaintenance.onSuccess { maintenance ->
                        if (maintenance) {
                            withContext(Dispatchers.Main) {
                                MaterialAlertDialogBuilder(this@SplashActivity)
                                    .setTitle("Server Maintenance")
                                    .setCancelable(false)
                                    .setMessage("Mohon maaf, untuk saat ini server sedang menjalani proses pemeliharaan. Anda hanya bisa membuka Data Lama. Silakan coba lagi nanti.")
                                    .setPositiveButton("Oke") { dialog, _ ->
                                        dialog.dismiss()

                                        showJenisDataChoice(isOnline = true, shouldShowDataBaruOption = false)
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
    }

    private fun deviceOnline(): Flow<Boolean> {
        return flow {
            try {
                val timeOutMs = 3000
                val sock = Socket()
                val sockAddr = InetSocketAddress("8.8.8.8", 53)

                sock.connect(sockAddr, timeOutMs)
                sock.close()

                emit(true)
            } catch (e: IOException) {
                emit(false)
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

    private suspend fun checkMaintenance(): Result<Boolean> {
        return callbackFlow<Result<Boolean>> {
            val database = FirebaseDatabase.getInstance(GriyaNodes.firebaseUrl)
            val maintenanceRef = database.reference.child(FirebaseNodes.MAINTENTANCE)

            maintenanceRef
                .get()
                .addOnSuccessListener { snapshot ->
                    val statusServer = snapshot.getValue<Boolean>()
                    trySendBlocking(Result.success(statusServer ?: false))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }
}