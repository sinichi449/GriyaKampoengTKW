package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

object InputUtil {

    fun isNullOrEmptyEditTexts(vararg textInputEditText: TextInputEditText): Boolean {
        var isEmpty = false
        for (edt in textInputEditText) {
            if (edt.text.isNullOrBlank()) {
                edt.error = "Masih kosong"

                isEmpty = true

                break
            }
        }

        return isEmpty
    }

    fun hideKeyboard(ctx: Context, rootWindowToken: IBinder) {
        (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(rootWindowToken, 0)
    }

}