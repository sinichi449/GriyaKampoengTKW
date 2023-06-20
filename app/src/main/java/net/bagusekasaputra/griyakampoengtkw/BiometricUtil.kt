package net.bagusekasaputra.griyakampoengtkw

import android.content.pm.PackageManager
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogPasswordAuthenticationBinding

@Suppress("DEPRECATION")
object BiometricUtil {

    fun beginAuthentication(activity: FragmentActivity, biometricManager: BiometricManager, biometricPrompt: BiometricPrompt) {
        val hasFingerPrintHw = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        if (hasFingerPrintHw) {
            val canAuthenticate = biometricManager.canAuthenticate()
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(getPromptInfo())
            } else {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("Gagal Menginisialisasi Biometrik")
                    .setMessage("Terjadi kesalahan dalam memulai proses autentikasi. Silakan hubungi developer aplikasi untuk mendapatkan perbaikan.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener { activity.finish() }
                    .create()
                    .show()
            }
        }
    }

    fun instanceOfBiometricPrompt(
        activity: FragmentActivity,
        onFailure: (errorCode: Int, errString: CharSequence) -> Unit,
        onSuccess: () -> Unit,
    ): BiometricPrompt {
        val context = activity.applicationContext
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d("DEBUG_ME", "Authentication error $errorCode : $errString")

                onFailure(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("DEBUG_ME", "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                onSuccess()
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    private fun getPromptInfo(): PromptInfo {
        return PromptInfo.Builder()
            .setTitle("Perlu Autentikasi")
            .setDescription("Anda memerlukan kunci biometrik untuk dapat mengakses data di dalam aplikasi ini.")
            .setDeviceCredentialAllowed(true)
            .build()
    }

    fun fallbackToPasswordAuthentication(
        activity: FragmentActivity,
        onCorrectPassword: () -> Unit,
        onFalsePassword: () -> Unit
    ) {
        val dialogBinding = DialogPasswordAuthenticationBinding.inflate(activity.layoutInflater)

        MaterialAlertDialogBuilder(activity).apply {
            setView(dialogBinding.root)
            setCancelable(false)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                val password = dialogBinding.edtPassword.text.toString()
                if (passwordIsCorrect(password)) onCorrectPassword() else onFalsePassword()
            }
            setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()

                onFalsePassword()
            }
        }
            .show()
    }

    private fun passwordIsCorrect(password: String): Boolean {
        return password == "r4h4s14d3cH!"
    }
}