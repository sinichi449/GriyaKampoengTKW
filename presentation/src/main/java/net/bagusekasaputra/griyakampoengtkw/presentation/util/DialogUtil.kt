package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.AlertDialog
import android.content.Context
import android.view.ViewAnimationUtils
import androidx.core.content.ContextCompat
import net.bagusekasaputra.griyakampoengtkw.presentation.R

object DialogUtil {

    fun additionalDialogSetting(ctx: Context, dialog: AlertDialog) {
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.background_rounded_dialog))
        dialog.window?.attributes?.windowAnimations = R.style.FadingAlertDialog
    }

    fun additionalDialogSetting(ctx: Context, dialog: androidx.appcompat.app.AlertDialog) {
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.background_rounded_dialog))
        dialog.window?.attributes?.windowAnimations = R.style.FadingAlertDialog
    }

    private fun androidx.appcompat.app.AlertDialog.setRevealAnimation() {
        this.setOnShowListener { dialog ->
            val view = this.window?.decorView
            val centerX = view?.width?.div(2)
            val centerY = view?.height?.div(2)

            val startRadius = 20f
            val endRadius = view?.height?.toFloat()

            val animator = ViewAnimationUtils.createCircularReveal(view, centerX ?: 0, centerY ?: 0, startRadius, endRadius!!)

            animator.duration = 250L
            animator.start()
        }
    }

    fun AlertDialog.setRevealAnimation() {
        this.setOnShowListener { dialog ->
            val view = this.window?.decorView
            val centerX = view?.width?.div(2)
            val centerY = view?.height?.div(2)

            val startRadius = 20f
            val endRadius = view?.height?.toFloat()

            val animator = ViewAnimationUtils.createCircularReveal(view, centerX ?: 0, centerY ?: 0, startRadius, endRadius!!)

            animator.duration = 250L
            animator.start()
        }
    }


}