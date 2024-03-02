package net.bagusekasaputra.griyakampoengtkw

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan
import net.bagusekasaputra.griyakampoengtkw.model.ConnectionCheckResult
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BackupRestoreViewModel
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes as RemoteNodes

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bindingPure: ActivitySplashPureBinding
    private lateinit var bindingLoading: ActivitySplashWithLoadingBinding
    private val viewModel by viewModels<AppViewModel>()
    private val backupRestoreViewModel by viewModels<BackupRestoreViewModel>()

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

        // Clean up `SharedPreferences`' runtime keys, such as selected tahapan or selected jenis data.
        sharedPreferences.cleanUpOnStart(
            ConstsSharedPrefs.SELECTED_TAHAPAN,
            ConstsSharedPrefs.BACKUP_NAME
        )

        bindingPure = ActivitySplashPureBinding.inflate(layoutInflater)
        setContentView(bindingPure.root)

        // Disable Dark Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val fingerPrintHwAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
//        if (fingerPrintHwAvailable) {
//            // Init biometric authentication. If authentication is successful,
//            // then execute connectivityCheckAndInitServer() and initialize the cache.
//            biometricManager = BiometricManager.from(this)
//            biometricPrompt = BiometricUtil.instanceOfBiometricPrompt(this,
//                onFailure = { errorCode: Int, _ ->
//                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
//                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
//                        errorCode == BiometricPrompt.ERROR_CANCELED) {
//                        onAuthenticationFailed()
//                    }
//                },
//                onSuccess = { onAuthenticationSuccess() }
//            )
//        } else {
//            BiometricUtil.fallbackToPasswordAuthentication(
//                this,
//                onCorrectPassword = { onAuthenticationSuccess() },
//                onFalsePassword = { onAuthenticationFailed() }
//            )
//        }

        showSplashScreen(1.5f, fingerPrintHwAvailable)
    }

    @Suppress("SameParameterValue")
    private fun showSplashScreen(seconds: Float, fingerPrintAvailable: Boolean) {
        val handler = Handler()
        val splashRunnable = Runnable {
            bindingLoading = ActivitySplashWithLoadingBinding.inflate(layoutInflater)
            setContentView(bindingLoading.root)

            onAuthenticationSuccess()

//            if (fingerPrintAvailable) {
//                BiometricUtil.beginAuthentication(this, biometricManager, biometricPrompt)
//            }
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
    }

    /**
     * @param jenisData when it is [DATA_BARU], will write [SharedPreferences] of key `dataLamaPath`
     * to `null`. And if it is [DATA_LAMA], will put backup name which is got from [dialogPilihDataLama]
     * into [SharedPreferences].
     * @param tahapan will have to be put into [SharedPreferences], since it will be the main factor
     * to where the [FirebaseDatabase]'s node which needs to be accessed.
     * @param isOnline will be send to [MainActivity] via [Intent].
     */

    private fun onAuthenticationFailed() {
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

    private fun onAuthenticationSuccess() {
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
                // Get pending change tahapan if available
                val selectedTahapanChange = getChangeTahapanFromIntent(intent)
                if (selectedTahapanChange != null) {
                    viewModel.selectedTahapan.value = selectedTahapanChange
                } else {
                    dialogPilihTahapan { dialog, tahapan ->
                        viewModel.selectedTahapan.value = tahapan

                        dialog.dismiss()
                    }
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
                            handleTahapanAndJenisData(
                                selectedJenisData, selectedTahapan,
                                connectivityCheckResult.isDeviceOnline,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleTahapanAndJenisData(
        jenisData: Int,
        tahapan: Tahapan,
        isOnline: Boolean,
    ) {
        sharedPreferences.edit(true) {
            putString(ConstsSharedPrefs.SELECTED_TAHAPAN, tahapan.reference)
        }

        if (jenisData == DATA_BARU) {
            // Initialize cache
            viewModel.initializeCache(
                tahapan = tahapan,
                onProgress = {
                    bindingLoading.apply {
                        layoutPilihData.root.visibility = View.GONE
                        layoutCekKoneksi.root.visibility = View.VISIBLE
                        layoutCekKoneksi.tvInfoPeriksaInternet.text = "Menginisialisasi Cache"
                    }
                },
                onSuccess = {
                    goToMainActivity(if (isOnline) DataMode.ONLINE else DataMode.OFFLINE)
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
        } else {
            dialogPilihDataLama { _, namaBackup ->
                sharedPreferences.edit(true) {
                    putString(ConstsSharedPrefs.BACKUP_NAME, namaBackup)
                }

                goToMainActivity(DataMode.DATA_LAMA)
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

    /**
     * Navigate to [MainActivity] with some extras: [BuildConfig.VERSION_NAME],
     * [BuildConfig.VERSION_CODE], and a [DataMode].
     *
     * After invoking [startActivity], this activity will [finish].
     *
     * @param dataMode will have to be passed to [MainActivity].
     */
    private fun goToMainActivity(dataMode: DataMode) {
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(MainActivity.EXTRAS_VERSION_NAME, BuildConfig.VERSION_NAME)
        intent.putExtra(MainActivity.EXTRAS_VERSION_CODE, BuildConfig.VERSION_CODE)
        intent.putExtra(MainActivity.EXTRAS_DATA_MODE, dataMode.name)

        startActivity(intent)
        finish()
    }

    private fun dialogPilihTahapan(onSelectedTahapan: (dialog: DialogFragment, tahapan: Tahapan) -> Unit) {
        PilihTahapanBottomSheetDialog(
            onItemSelected = onSelectedTahapan,
            onFailure = { finish() }
        )
            .show(supportFragmentManager, null)
    }

    /**
     * Fetch backup names from [BackupRestoreViewModel.getListBackup].
     *
     * If the backup names are empty or `null`, will show a dialog which informs the user that there are
     * no available backups.
     *
     * **WARNING**: [dialogPilihDataLama] should be executed **BEFORE** any code that can represets
     * [onSelectedDataLama]. If whatever code executed _after_ [dialogPilihDataLama], the databaseReference
     * will pointing to the wrong child!.
     */
    private fun dialogPilihDataLama(onSelectedDataLama: (dialog: DialogInterface, namaBackup: String) -> Unit) {
        @Suppress("DEPRECATION")
        val loadingBackupsDialog = ProgressDialog(this).apply {
            setTitle("Memuat Backup")
            setMessage("Sedang memuat data cadangan yang tersedia, mohon tunggu ...")
            setOnCancelListener {
                backupRestoreViewModel.cancelFetchBackups()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // show loading
            withContext(Dispatchers.Main) { loadingBackupsDialog.show() }

            // Get available nama backup
            val arrBackup = backupRestoreViewModel.getListBackup()?.toTypedArray()

            // Show dialog pilih data lama
            withContext(Dispatchers.Main) {
                loadingBackupsDialog.dismiss()

                if (!arrBackup.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@SplashActivity).apply {
                        setTitle("Pilih Backup")
                        setCancelable(false)
                        setSingleChoiceItems(arrBackup, 0) { dialog, checkedPosition ->
                            dialog.dismiss()

                            onSelectedDataLama(dialog, arrBackup[checkedPosition])
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.show()
                } else {
                    // Show Backup is Unavailable
                    MaterialAlertDialogBuilder(this@SplashActivity).apply {
                        setTitle("Backup Tidak Ditemukan!")
                        setMessage("Tidak ada data lama yang bisa ditampilkan karena tidak dapat memuat data yang diperlukan dari server. Coba lagi nanti atau hubungi developer.")
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.show()
                }
            }
        }
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
            val maintenanceRef = database.reference.child(RemoteNodes.MAINTENTANCE)


            maintenanceRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    /**
     * Do cleanup on app start because, after process death, some [SharedPreferences]' keys will
     * meddle in the process.
     *
     * For example, if you did [SharedPreferences.edit] to put a `backupName` and have forgotten
     * to clean this up, then whatever [DataMode] user chooses on [showJenisDataButton] will be
     * [DATA_LAMA].
     */
    @Suppress("SameParameterValue")
    private fun SharedPreferences.cleanUpOnStart(vararg keys: String) {
        edit(true) {
            keys.forEach { key ->
                remove(key)
            }
        }
    }

    private fun getChangeTahapanFromIntent(intent: Intent?): Tahapan? {
        val selectedTahapanReference = intent?.extras?.getString(MainActivity.EXTRAS_CHANGE_TAHAPAN_REFERENCE)

        return selectedTahapanReference?.let {
            Tahapan.getSimpleInstance(it)
        }
    }


    companion object {
        const val DATA_LAMA = 0
        const val DATA_BARU = 1
    }
}