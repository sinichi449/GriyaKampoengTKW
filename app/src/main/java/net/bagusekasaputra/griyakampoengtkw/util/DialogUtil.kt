package net.bagusekasaputra.griyakampoengtkw.util

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import net.bagusekasaputra.griyakampoengtkw.R

object DialogUtil {

    fun additionalDialogSetting(ctx: Context, dialog: AlertDialog) {
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.background_rounded_dialog))
        dialog.window?.attributes?.windowAnimations = R.style.FadingAlertDialog
    }

    fun additionalDialogSetting(ctx: Context, dialog: androidx.appcompat.app.AlertDialog) {
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.background_rounded_dialog))
        dialog.window?.attributes?.windowAnimations = R.style.FadingAlertDialog
    }

}