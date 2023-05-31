@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityFormBinding

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarForm)

        navController = (supportFragmentManager.findFragmentById(R.id.container_form)
                as NavHostFragment).navController

        // Send bundle key id of data diri if not null or empty
        val keyId = intent?.extras?.getString(EXTRAS_KEY_ID_INDEN_BOOKING)
        val bundleKeyId = if (!keyId.isNullOrEmpty())
                bundleOf(EXTRAS_KEY_ID_INDEN_BOOKING to keyId)
            else null
        when (val requestedFormType = intent?.extras?.getString(EXTRAS_FORM_TYPE)) {
            FORM_DATA_DIRI_INDEN_BOOKING -> {
                navController.navigate(R.id.nav_form_data_diri_inden_booking, args = bundleKeyId)
            }
            FORM_PEMBAYARAN_INDEN_BOOKING -> {
                navController.navigate(R.id.nav_form_pembayaran_inden_booking, args = bundleKeyId)
            }
            else -> {
                Log.d("FORM_ACTIVITY", "Unknown form type $requestedFormType !!")
                Snackbar.make(binding.root, "Tipe form tidak dikenali!", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun setFormTitle(title: String) {
        binding.toolbarForm.title = title
    }

    fun getFabDone(): FloatingActionButton {
        return binding.fabDone
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            confirmationOnExitDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmationOnExitDialog() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Batalkan?")
            setMessage("Apakah Anda yakin ingin membatalkan pengisian form? Semua input yang " +
                    "telah Anda masukkan TIDAK akan tersimpan!")
            setPositiveButton("Ya") { _, _ -> finish() }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
            .show()
    }

    companion object {
        const val EXTRAS_FORM_TYPE = "EXTRAS_FORM_TYPE"
        const val EXTRAS_FAIL_MSG = "EXTRAS_FAIL_MSG"
        const val EXTRAS_SUCCESS_DATA = "EXTRAS_SUCCESS_DATA"

        const val FORM_DATA_DIRI_INDEN_BOOKING = "FORM_DATA_DIRI_INDEN_BOOKING"
        const val FORM_PEMBAYARAN_INDEN_BOOKING = "FORM_PEMBAYARAN_INDEN_BOOKING"

        const val EXTRAS_KEY_ID_INDEN_BOOKING = "EXTRAS_KEY_ID_INDEN_BOOKING"
    }

}