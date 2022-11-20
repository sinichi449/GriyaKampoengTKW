package net.bagusekasaputra.griyakampoengtkw.presentation.network

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutConnectivityStatusBinding
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ConnectivityAnimation(
    private val activity: Activity,
    private val rootView: View,
    private val binding: LayoutConnectivityStatusBinding,
) {

    private val ctx = activity.applicationContext
    private val animationDone = AtomicBoolean(true)
    private var currentAnimation: Animation? = null
    private var currentTimer: Timer? = null

    private val isOffline = AtomicBoolean(false)

    enum class Status {
        Online, Offline
    }

    fun onOnlineAnimation() {
        if (!animationDone.get()) {
            currentTimer?.cancel()
            currentAnimation?.cancel()
            animationDone.set(true)
        }

        if (isOffline.get()) {
            enterAnimation(Status.Online)
            isOffline.set(false)
        }
    }

    fun onOfflineAnimation() {
        if (!animationDone.get()) {
            currentTimer?.cancel()
            currentAnimation?.cancel()
            animationDone.set(true)
        }

        if (!isOffline.get()) {
            enterAnimation(Status.Offline)
            isOffline.set(true)
        }
    }

    private fun enterAnimation(status: Status) {
        if (status == Status.Offline) {
            setupOfflineLayout()

        } else {
            setupOnlineLayout()
        }

        val entryAnimation = getEntryAnimation()
        currentAnimation = entryAnimation

        entryAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                animationDone.set(false)
            }

            override fun onAnimationEnd(p0: Animation?) {

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })

        applyAnimation(entryAnimation)

        if (status == Status.Online) {
            setTimer(2000) {
                binding.constraintConnectivity.startAnimation(getOutroAnimation())
            }
        }
    }

    private fun setupOfflineLayout() {
        binding.constraintConnectivity.visibility = View.VISIBLE
        binding.constraintConnectivity.background = ContextCompat.getDrawable(ctx, R.drawable.background_connectivity_offline)
        binding.imgCloud.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_baseline_cloud_off_24))
        binding.tvStatus.text = "Offline"
    }

    private fun setupOnlineLayout() {
        binding.constraintConnectivity.visibility = View.VISIBLE
        binding.constraintConnectivity.background = ContextCompat.getDrawable(ctx, R.drawable.background_connectivity_online)
        binding.imgCloud.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_baseline_cloud_queue_24))
        binding.tvStatus.text = "Online"
    }

    private fun applyAnimation(animation: Animation) {
        binding.constraintConnectivity.startAnimation(animation)
    }

    private fun getEntryAnimation(): Animation {
        return AnimationUtils.loadAnimation(ctx, R.anim.slide_in_connectivity_layout)
    }

    private fun getOutroAnimation(): Animation {
        val outroAnim = AnimationUtils.loadAnimation(ctx, R.anim.slide_up_connectivity_layout)
        currentAnimation = outroAnim

        outroAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                animationDone.set(true)
                binding.constraintConnectivity.visibility = View.GONE
                rootView.requestLayout()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })

        return outroAnim
    }

    private fun setTimer(timeMillis: Long, onComplete: () -> Unit) {
        activity.runOnUiThread {
            val timer = Timer()
            val timerTask = object : TimerTask() {
                override fun run() {
                    timer.cancel()
                    onComplete()
                }
            }

            timer.schedule(timerTask, timeMillis)
            currentTimer = timer
        }
    }

}