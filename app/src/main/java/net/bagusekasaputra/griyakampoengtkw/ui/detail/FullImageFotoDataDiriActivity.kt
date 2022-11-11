package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.igreenwood.loupe.Loupe
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityFullImageFotoDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

@AndroidEntryPoint
class FullImageFotoDataDiriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullImageFotoDataDiriBinding
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
        binding = ActivityFullImageFotoDataDiriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stringsExtra = intent.getStringArrayListExtra(GriyaNodes.INTENT_SOURCE_IMAGE)
        val sender = stringsExtra?.get(0)!!
        val kavlingKode = stringsExtra[1]!!

        if (sender == GriyaNodes.INTENT_DATA_DIRI) {
            imageViewModel.getImageDataDiri(kavlingKode) { failMsg ->
                Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show()
            }

            imageViewModel.imageDataDiriLive.observe(this) { imgDataDiri ->
                imgDataDiri?.let {
                    createLoupe(it.bitmap)
                }
            }
        } else if (sender == GriyaNodes.INTENT_FOTO_KUITANSI) {
            imageViewModel.getFotoKuitansi(kavlingKode) { failMsg ->
                Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show()
            }

            imageViewModel.fotoKuitansiLive.observe(this) { fotoKuitansi ->
                fotoKuitansi?.let {
                    createLoupe(it.bitmap)
                }
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

    private fun getBitmapFromAnotherActivity(): Bitmap? {
        return intent.getParcelableExtra<Bitmap>(GriyaNodes.INTENT_BITMAP)
    }

    private fun getSender(): String? {
        return intent.getStringExtra(GriyaNodes.INTENT_SOURCE_IMAGE)
    }
}