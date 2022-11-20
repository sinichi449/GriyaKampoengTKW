package net.bagusekasaputra.griyakampoengtkw.presentation.util

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

}