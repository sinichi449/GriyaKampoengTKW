package net.bagusekasaputra.griyakampoengtkw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashPureBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashWithLoadingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.main.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bindingPure: ActivitySplashPureBinding
    private lateinit var bindingLoading: ActivitySplashWithLoadingBinding

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

        showSplashScreen(1.5f)
    }

    @Suppress("SameParameterValue")
    private fun showSplashScreen(seconds: Float) {
        val handler = Handler()
        val splashRunnable = Runnable {
            bindingLoading = ActivitySplashWithLoadingBinding.inflate(layoutInflater)
            setContentView(bindingLoading.root)

            CoroutineScope(Dispatchers.IO).launch {
                deviceOnline().collect { online ->
                    if (online) {
                        withContext(Dispatchers.Main) {
                            bindingLoading.tvInfoPeriksaInternet.text = "Memeriksa status server"
                        }
                        // Check Maintenance status
                        val isMaintenance = checkMaintenance()

                        isMaintenance.onSuccess { maintenance ->
                            if (maintenance) {
                                withContext(Dispatchers.Main) {
                                    MaterialAlertDialogBuilder(this@SplashActivity)
                                        .setTitle("Server Maintenance")
                                        .setCancelable(false)
                                        .setMessage("Mohon maaf, untuk saat ini server sedang menjalani proses pemeliharaan. Silakan coba lagi nanti.")
                                        .setPositiveButton("Oke") { dialog, _ ->
                                            dialog.dismiss()
                                            this@SplashActivity.finish()
                                        }
                                        .create()
                                        .show()
                                }
                            } else {
                                goToMainActivity(online)
                            }
                        }

                        isMaintenance.onFailure {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@SplashActivity, "Gagal mengecek status server: $it", Toast.LENGTH_LONG).show()
                            }

                            goToMainActivity(online)
                        }
                    } else {
                        goToMainActivity(online)
                    }
                }

            }
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
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

    private fun goToMainActivity(isOnline: Boolean) {
        // I also want to pass a BuildConfig for checking update.
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra(GriyaNodes.INTENT_IS_ONLINE, isOnline)
        intent.putExtra("versionName", BuildConfig.VERSION_NAME)
        intent.putExtra("versionCode", BuildConfig.VERSION_CODE)
        startActivity(intent)
        finish()
    }

    private suspend fun checkMaintenance(): Result<Boolean> {
        return callbackFlow<Result<Boolean>> {
            val database = FirebaseDatabase.getInstance(GriyaNodes.firebaseUrl)
            val maintenanceRef = database.reference.child("maintenance")

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