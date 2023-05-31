package net.bagusekasaputra.griyakampoengtkw

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.interfaces.CacheInitializer
import net.bagusekasaputra.griyakampoengtkw.model.ConnectionCheckResult
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

/**
 * Developer version is capable of bypassing Biometric authentication.
 * And don't forget that purple status bar color ...
 */
@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bindingPure: ActivitySplashPureBinding
    private lateinit var bindingLoading: ActivitySplashWithLoadingBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var cacheInitializer: CacheInitializer

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set transparent status bar for seamless view with GKT Background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ContextCompat.getColor(this@SplashActivity, R.color.app_theme_color)
        }

        bindingPure = ActivitySplashPureBinding.inflate(layoutInflater)
        setContentView(bindingPure.root)

        // Disable Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



        showSplashScreen(1.5f, onSplashFinished = {
            // Connectivity check and init server
            val dispatcher = Dispatchers.IO

            lifecycleScope.launch(dispatcher) {
                val connectivityCheckResult = connectivityCheckAndInitServer(
                    dispatcher,
                    onDeviceConnectivityCheck = { isOnline ->
                        if (isOnline) {
                            bindingLoading.layoutCekKoneksi.tvInfoPeriksaInternet.text = "Memeriksa status server"
                        } else {
                            Toast.makeText(this@SplashActivity, "Device terdeteksi offline, mohon cek koneksi Anda.", Toast.LENGTH_LONG).show()
                        }
                    },
                    onServerMaintenance = {
                        MaterialAlertDialogBuilder(this@SplashActivity)
                            .setTitle("Server Maintenance")
                            .setCancelable(false)
                            .setMessage("Mohon maaf, untuk saat ini server sedang menjalani proses pemeliharaan. Anda hanya bisa membuka Data Lama. Silakan coba lagi nanti.")
                            .setPositiveButton("Oke") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    },
                    onFailureCheckMaintenance = { failMsg ->
                        Toast.makeText(this@SplashActivity, "Gagal mengecek status server: $failMsg", Toast.LENGTH_LONG).show()
                    },
                )

                // Initialize cache
                cacheInitializer.initialize()
                    .onSuccess {
                        Log.d("INIT_CACHE", "Success initializing cache!")
                    }
                    .onFailure {
                        Log.e("INIT_CACHE", "Error on cache initialization: ${it.localizedMessage}")
                    }

                withContext(Dispatchers.Main) {
                    showJenisDataButton(connectivityCheckResult)
                }
            }
        })
    }

    @Suppress("SameParameterValue")
    private fun showSplashScreen(seconds: Float, onSplashFinished: () -> Unit) {
        val handler = Handler()
        val splashRunnable = Runnable {
            bindingLoading = ActivitySplashWithLoadingBinding.inflate(layoutInflater)
            setContentView(bindingLoading.root)

            onSplashFinished()
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
    }

    private fun showJenisDataButton(connectionCheckResult: ConnectionCheckResult) {
        with(bindingLoading) {
            // Remove layout cek koneksi
            layoutCekKoneksi.root.visibility = View.INVISIBLE

            // When checking Maintenance Status or Device Connectivity fails,
            // disable "Data Baru" button.
            if (!connectionCheckResult.shouldShowDataBaru) {
                layoutPilihData.btnDataBaru.visibility = View.GONE
            }

            // Show layout pilih data
            layoutPilihData.root.visibility = View.VISIBLE

            // Setup button Data Lama and Data Baru
            layoutPilihData.btnDataLama.setOnClickListener {
                goToDocumentLamaActivity()
            }
            layoutPilihData.btnDataBaru.setOnClickListener {
                // Nullify the sharedPreference Data Lama to prevent MainActivity/DetailActivity
                // to DataLama mode
                sharedPreferences.edit(true) {
                    putString("dataLamaPath", null)
                }

                goToMainActivity(connectionCheckResult.isDeviceOnline, true)
            }
        }
    }

    @Suppress("SameParameterValue")
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

    private suspend fun connectivityCheckAndInitServer(
        dispatcher: CoroutineDispatcher,
        onDeviceConnectivityCheck: (isOnline: Boolean) -> Unit,
        onServerMaintenance: () -> Unit,
        onFailureCheckMaintenance: (failMsg: String) -> Unit,
    ): ConnectionCheckResult {
        return callbackFlow {
            if (deviceIsOnline()) {
                withContext(Dispatchers.Main) {
                    onDeviceConnectivityCheck(true)
                }
                // Check Maintenance status
                checkMaintenance()
                    .onSuccess { maintenance ->
                        if (maintenance) {
                            withContext(Dispatchers.Main) {
                                onServerMaintenance()
                            }

                            trySendBlocking(ConnectionCheckResult(
                                isDeviceOnline = true,
                                shouldShowDataBaru = false,
                            ))
                        } else {
                            trySendBlocking(ConnectionCheckResult(
                                isDeviceOnline = true,
                                shouldShowDataBaru = true,
                            ))
                        }
                    }
                    .onFailure {
                        withContext(Dispatchers.Main) {
                            onFailureCheckMaintenance(it.localizedMessage ?: "Unknown Error")
                        }
                        trySendBlocking(ConnectionCheckResult(
                            isDeviceOnline = true,
                            shouldShowDataBaru = false,
                        ))
                    }
            } else {
                withContext(Dispatchers.Main) {
                    onDeviceConnectivityCheck(false)
                }

                trySendBlocking(ConnectionCheckResult(
                    isDeviceOnline = false, shouldShowDataBaru = false
                ))
            }

            awaitClose {  }
        }
            .flowOn(dispatcher)
            .first()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun deviceIsOnline(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            try {
                val timeOutMs = 3000
                val sock = Socket()
                val sockAddr = InetSocketAddress("8.8.8.8", 53)

                sock.connect(sockAddr, timeOutMs)
                sock.close()

                if (continuation.isActive) {
                    continuation.resume(true, null)
                }
            } catch (e: IOException) {
                if (continuation.isActive) {
                    continuation.resume(false, null)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun checkMaintenance(): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val statusServer = snapshot.getValue<Boolean>()

                    if (continuation.isActive) {
                        continuation.resume(Result.success(statusServer ?: true), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        val exception = error.toException()
                        continuation.resume(Result.failure(exception), null)
                    }
                }

            }

            val database = FirebaseDatabase.getInstance(GriyaNodes.firebaseUrl)
            val maintenanceRef = database.reference.child(FirebaseNodes.MAINTENTANCE)


            maintenanceRef.addListenerForSingleValueEvent(eventListener)
        }
    }
}