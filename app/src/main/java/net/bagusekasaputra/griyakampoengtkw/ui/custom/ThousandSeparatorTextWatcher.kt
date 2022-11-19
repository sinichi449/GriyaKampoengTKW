package net.bagusekasaputra.griyakampoengtkw.ui.custom

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

class ThousandSeparatorTextWatcher(private val editText: TextInputEditText): TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        editText.removeTextChangedListener(this)

        try {
            var originalString = p0.toString()

            if (originalString.contains(",")) {
                originalString = originalString.replace(",", "")
            }

            val longVal = originalString.toLong()
            val parsed = NumberUtil.formatLongToString(longVal)

            editText.setText(parsed)
            editText.setSelection(editText.text!!.length)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        editText.addTextChangedListener(this)
    }
}