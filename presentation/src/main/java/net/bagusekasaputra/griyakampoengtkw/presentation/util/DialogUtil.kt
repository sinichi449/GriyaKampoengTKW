package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    fun createConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit,
    ) = MaterialAlertDialogBuilder(context).apply {
        setTitle(title)
        setMessage(message)
        setCancelable(true)
        setPositiveButton("Ya") { dialog, _ ->
            dialog.dismiss()

            onConfirm()
        }
        setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
    }
        .create()
        .show()
}