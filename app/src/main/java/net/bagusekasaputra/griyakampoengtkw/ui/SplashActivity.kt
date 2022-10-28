package net.bagusekasaputra.griyakampoengtkw.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivitySplashBinding
import net.bagusekasaputra.griyakampoengtkw.ui.main.MainActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showSplashScreen(1.5f)
    }

    @Suppress("SameParameterValue")
    private fun showSplashScreen(seconds: Float) {
        val handler = Handler()
        val splashRunnable = Runnable {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        }
        // Convert integer to long milliseconds
        val millis = (seconds * 1000).toLong()

        handler.postDelayed(splashRunnable, millis)
    }
}