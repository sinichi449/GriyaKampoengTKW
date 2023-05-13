package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityPembayaranTabelFullBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class PembayaranTabelFullActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_KAVLING_KODE = "EXTRAS_KAVLING_KODE"
    }

    private lateinit var binding: ActivityPembayaranTabelFullBinding
//    private val pembayaranViewModel by viewModels<FormPembayaranViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranTabelFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kavling = intent?.extras?.getString(EXTRAS_KAVLING_KODE)
        if (kavling.isNullOrEmpty()) {
            Snackbar.make(binding.root, "Error mendapatkan informasi kode Kavling!", Snackbar.LENGTH_LONG)
                .show()
        } else {
            binding.tvStatus?.text = "Requesting Kav. $kavling ..."
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}