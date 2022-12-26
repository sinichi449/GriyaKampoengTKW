package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.igreenwood.loupe.Loupe
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.ActivityFullImageBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel

@AndroidEntryPoint
class FullImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullImageBinding
    private val imageViewModel: ImageViewModel by viewModels()

    private val translateListener = object : Loupe.OnViewTranslateListener {
        override fun onDismiss(view: ImageView) {
            finish()
        }

        override fun onRestore(view: ImageView) {

        }

        override fun onStart(view: ImageView) {

        }

        override fun onViewTranslate(view: ImageView, amount: Float) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        processIntentNew()

        setupViewModel()
    }

    private fun setupViewModel() {
        val progressDialog = ProgressDialog(this, R.style.AlertDialogTheme).apply {
            setTitle("Memuat gambar ...")
            setCancelable(false)
        }

        imageViewModel.isFinishAddImage.observe(this) { finished ->
            Log.d("DEBUG_ME", "FullImageActivity->isFinishAddImage: Status finished loading image is $finished")

            if (finished != null) {
                if (finished) progressDialog.dismiss()
                else progressDialog.show()
            }
        }
    }

    private fun createLoupe(bitmap: Bitmap) {
        binding.imgFullProfilCostumer.setImageBitmap(bitmap)

        val loupe = Loupe.create(binding.imgFullProfilCostumer, binding.container) {
            onViewTranslateListener = translateListener
            maxZoom = 5.0f
        }
    }

    private fun processIntentNew() {
        // NEW METHODOLOGY!!!
        val imageTransport = intent.getSerializableExtra(GriyaNodes.INTENT_SOURCE_IMAGE) as ImageTransport<*>
        val mapContent = imageTransport.content as Map<String, String>

        when (imageTransport.sendIntention) {
            GriyaNodes.INTENT_FOTO_PEMBAYARAN -> {
                val selectedTermin = mapContent["termin"]
                val kavlingKode1 = mapContent["kavlingKode"] // name shadowed, so we need extra "1" :(

                if ((selectedTermin == null) or (kavlingKode1 == null)) {
                    Toast.makeText(
                        this,
                        "Error: null ImageTransport untuk selectedTermin dan kavlingKode1",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Get first
                    imageViewModel.getFotoPembayaran(
                        kavlingKode = kavlingKode1!!,
                        termin = selectedTermin!!,
                        onFailure = { msg ->
                            Toast.makeText(
                                this,
                                "Gagal mendapatkan foto pembayaran: $msg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )

                    // Then observer, since this is an asynchronous operation.
                    imageViewModel.fotoPembayaranLive.observe(this) { fotoPembayaran ->
                        if (fotoPembayaran != null) {
                            val bitmap = imageViewModel.getBitmapFromUri(contentResolver, fotoPembayaran.uri)

                            createLoupe(bitmap)
                        } else {
                            Toast.makeText(
                                this,
                                "Foto Pembayaran $selectedTermin tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            GriyaNodes.INTENT_DATA_DIRI -> {
                val kavlingKode = mapContent["kavlingKode"]

                imageViewModel.getImageDataDiri(kavlingKode!!) { failMsg ->
                    Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show()
                }

                imageViewModel.imageDataDiriLive.observe(this) { imgDataDiri ->
                    imgDataDiri?.let {
                        createLoupe(it.bitmap)
                    }
                }
            }
            GriyaNodes.INTENT_FOTO_SPR -> {
                val kavlingKode = mapContent["kavlingKode"]

                imageViewModel.getSprImage(kavlingKode!!) { failMsg ->
                    Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show()
                }

                imageViewModel.imageSprLive.observe(this) { imageSpr ->
                    imageSpr?.let {
                        createLoupe(it.bitmap)
                    }
                }
            }
        }
    }
}