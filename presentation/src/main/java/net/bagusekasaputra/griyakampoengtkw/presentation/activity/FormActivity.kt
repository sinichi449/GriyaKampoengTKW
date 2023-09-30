package net.bagusekasaputra.griyakampoengtkw.presentation.activity

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
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
        val keyIdBundle = if (!keyId.isNullOrEmpty())
                bundleOf(EXTRAS_KEY_ID_INDEN_BOOKING to keyId)
            else null


        intent?.extras?.getString(EXTRAS_FORM_TYPE)?.also {
            when (it) {
                FORM_DATA_DIRI_INDEN_BOOKING -> {
                    navController.navigate(R.id.nav_form_data_diri_inden_booking, args = keyIdBundle)
                }
                FORM_PEMBAYARAN_INDEN_BOOKING -> {
                    navController.navigate(R.id.nav_form_pembayaran_inden_booking, args = keyIdBundle)
                }
                else -> { Log.d("FORM_ACTIVITY", "Unknown form type $it !!") }
            }
        }

        /**
         * New method!
         */
        intent?.also {
            val parcelable: Parcelable? = IntentCompat.getParcelableExtra(
                it, EXTRAS_PARCEL, Parcelable::class.java
            )
            val bundle = bundleOf(EXTRAS_PARCEL to parcelable)
            val destination = when (parcelable) {
                is InsertFormPembayaranParcel, is UpdateFormPembayaranParcel -> R.id.nav_form_pembayaran_kavling
                is InsertTambahanPembayaranParcel, is UpdateTambahanPembayaranParcel -> R.id.nav_form_tambahan_pembayaran
                else -> null
            }

            if (destination != null) {
                navController.navigate(destination, args = bundle)
            } else {
                finish()

                Toast.makeText(this, "Unknown parcelable data type!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setFormTitle(title: String) {
        binding.toolbarForm.title = title
    }

    fun getToolbar(): MaterialToolbar {
        return binding.toolbarForm
    }

    fun getFabDone(): FloatingActionButton {
        return binding.fabDone
    }

    @Suppress("DEPRECATION")
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
        @Deprecated("Will be removed! Migrate to use Parcelable!")
        const val EXTRAS_FORM_TYPE = "EXTRAS_FORM_TYPE"

        const val EXTRAS_PARCEL = "EXTRAS_PARCEL"

        const val EXTRAS_FAIL_MSG = "EXTRAS_FAIL_MSG"
        const val EXTRAS_SUCCESS_DATA = "EXTRAS_SUCCESS_DATA"

        // Standard
        @Deprecated("Will be removed! Migrate to [TambahFormPembayaranParcel] or [UbahFormPembayaranParcel]!")
        const val FORM_PEMBAYARAN_KAVLING = "FORM_PEMBAYARAN_KAVLING"

        // Inden Booking
        const val FORM_DATA_DIRI_INDEN_BOOKING = "FORM_DATA_DIRI_INDEN_BOOKING"
        @Deprecated("Use either pembayaran type from Pembayaran companion object.")
        const val FORM_PEMBAYARAN_INDEN_BOOKING = "FORM_PEMBAYARAN_INDEN_BOOKING"

        @Deprecated("Will be removed! Migrate to [TambahFormPembayaranParcel] or [UbahFormPembayaranParcel]!")
        const val EXTRAS_TERMIN = "EXTRAS_TERMIN"
        @Deprecated("Will be removed! Migrate to [TambahFormPembayaranParcel] or [UbahFormPembayaranParcel]!")
        const val EXTRAS_KAVLING = "EXTRAS_KAVLING"
        @Deprecated("Will be removed! Migrate to [TambahFormPembayaranParcel] or [UbahFormPembayaranParcel]!")
        const val EXTRAS_KEY_ID_INDEN_BOOKING = "EXTRAS_KEY_ID_INDEN_BOOKING"
    }
}

/** Pembayaran Parcels **/
@Parcelize
data class InsertFormPembayaranParcel(
    val tipePembayaran: Int,
    val kavling: String,
): Parcelable

@Parcelize
data class UpdateFormPembayaranParcel(
    val tipePembayaran: Int,
    val kavling: String,
    val termin: String,
): Parcelable

/** Tambahan Pembayaran Parcels **/
@Parcelize
data class InsertTambahanPembayaranParcel(
    val kavling: String
): Parcelable

@Parcelize
data class UpdateTambahanPembayaranParcel(
    val kavling: String,
    val id: String,
): Parcelable
