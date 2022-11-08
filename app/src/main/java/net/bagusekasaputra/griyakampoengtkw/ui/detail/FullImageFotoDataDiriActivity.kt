package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.igreenwood.loupe.Loupe
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityFullImageFotoDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

@AndroidEntryPoint
class FullImageFotoDataDiriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullImageFotoDataDiriBinding

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

        createLoupe()
    }

    private fun createLoupe() {
        getBitmapFromAnotherActivity()?.let { bitmap ->
            binding.imgFullProfilCostumer.setImageBitmap(bitmap)

            val loupe = Loupe.create(binding.imgFullProfilCostumer, binding.container) {
                onViewTranslateListener = translateListener
                maxZoom = 5.0f
            }
        }
    }

    private fun getBitmapFromAnotherActivity(): Bitmap? {
        return intent.getParcelableExtra<Bitmap>(GriyaNodes.INTENT_BITMAP)
    }
}