package net.bagusekasaputra.griyakampoengtkw

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
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
import net.bagusekasaputra.griyakampoengtkw.dataLama.ui.DataLamaActivity
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.model.ConnectionCheckResult
import net.bagusekasaputra.griyakampoengtkw.model.Tahapan
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
    private val viewModel by viewModels<AppViewModel>()

    // Need to be initialized at onCreate()
    private lateinit var biometricManager: BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set transparent status bar for seamless view with GKT Background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ContextCompat.getColor(this@SplashActivity, R.color.abang)
        }

        bindingPure = ActivitySplashPureBinding.inflate(layoutInflater)
        setContentView(bindingPure.root)

        // Disable Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Init biometric authentication. If authentication is successful,
        // then execute connectivityCheckAndInitServer() and initialize the cache.
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
                // Connectivity check and init server
                val dispatcher = Dispatchers.IO

                lifecycleScope.launch(dispatcher) {
                    val connectivityCheckResult = connectivityCheckAndInitServer(
                        dispatcher = dispatcher,
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

                    // Pilih Tahapan and save Tahapan to ViewModel
                    withContext(Dispatchers.Main) {
                        dialogPilihTahapan { dialog, tahapan ->
                            viewModel.selectedTahapan.value = tahapan

                            dialog.dismiss()
                        }
                    }

                    // Show jenis data
                    withContext(Dispatchers.Main) {
                        showJenisDataButton(connectivityCheckResult) {
                            viewModel.selectedJenisData.value = it
                        }
                    }

                    // Handle Tahapan and Jenis Data And initialize cache
                    withContext(Dispatchers.Main) {
                        viewModel.tahapanAndJenisData.observe(this@SplashActivity) {
                            it?.also { tahapanAndJenisData ->
                                val selectedTahapan = tahapanAndJenisData.first
                                val selectedJenisData = tahapanAndJenisData.second

                                if ((selectedTahapan != null) && (selectedJenisData != null)) {
                                    handleTahapanAndJenisData(selectedJenisData, selectedTahapan)

                                    // Skip cache initialization on DATA_LAMA
                                    if (selectedJenisData == DATA_LAMA) {
                                        goToDocumentLamaActivity()
                                    } else {
                                        // Initialize cache
                                        viewModel.initializeCache(
                                            tahapan = selectedTahapan,
                                            onProgress = {
                                                bindingLoading.apply {
                                                    layoutPilihData.root.visibility = View.GONE
                                                    layoutCekKoneksi.root.visibility = View.VISIBLE
                                                    layoutCekKoneksi.tvInfoPeriksaInternet.text = "Menginisialisasi Cache"
                                                }
                                            },
                                            onSuccess = {
                                                goToMainActivity(connectivityCheckResult.isDeviceOnline)
                                            },
                                            onFailure = {
                                                MaterialAlertDialogBuilder(this@SplashActivity).apply {
                                                    setTitle("Gagal Menginisialisasi Cache")
                                                    setMessage(it)
                                                    setCancelable(false)
                                                    setPositiveButton("Keluar") { _, _ ->
                                                        this@SplashActivity.finish()
                                                    }
                                                }.show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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

    private fun handleTahapanAndJenisData(
        jenisData: Int,
        tahapan: Tahapan
    ) {
        sharedPreferences.edit(true) {
            putString(ConstsSharedPrefs.SELECTED_TAHAPAN, tahapan.reference)
        }

        if (jenisData == DATA_BARU) {
            // Nullify the sharedPreference Data Lama to prevent MainActivity/DetailActivity
            // to DataLama mode
            sharedPreferences.edit(true) {
                putString("dataLamaPath", null)
            }
        }
    }

    private fun showJenisDataButton(
        connectionCheckResult: ConnectionCheckResult,
        onSelectedJenisData: (jenisData: Int) -> Unit,
    ) {
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
                onSelectedJenisData(DATA_LAMA)
            }
            layoutPilihData.btnDataBaru.setOnClickListener {
                onSelectedJenisData(DATA_BARU)
            }
        }
    }

    private fun goToMainActivity(isOnline: Boolean) {
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(GriyaNodes.INTENT_IS_ONLINE, isOnline)

        // Passing BuildConfig for update check to MainActivity
        intent.putExtra("versionName", BuildConfig.VERSION_NAME)
        intent.putExtra("versionCode", BuildConfig.VERSION_CODE)

        intent.putExtra("isNewDataSelected", true)

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

    private fun dialogPilihTahapan(onSelectedTahapan: (dialog: DialogFragment, tahapan: Tahapan) -> Unit) {
        PilihTahapanBottomSheetDialog(
            onItemSelected = onSelectedTahapan,
            onFailure = { finish() }
        )
            .show(supportFragmentManager, null)
    }

    companion object {
        const val DATA_LAMA = 0
        const val DATA_BARU = 1
    }
}