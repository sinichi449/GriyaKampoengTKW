package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.DatePickerDialog
import android.content.Context
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerHelper(
    private val ctx: Context,
    private val triggerButton: MaterialButton,
    private val targetEdt: TextInputEditText,
) {

    /**
     * default date will provide the default of current date.
     */
    fun setupDateDefaultOrPick(defaultDate: Boolean) {
        val currentDate = getTodayDate()
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        if (defaultDate) {
            targetEdt.setText(dateFormatter.format(currentDate.time))
        }

        triggerButton.setOnClickListener {

            val onDateListenerSet = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }
                targetEdt.setText(dateFormatter.format(newDate.time))
            }

            val datePickerDialog = DatePickerDialog(ctx, R.style.DatePicker,onDateListenerSet,
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(
                    Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }
    }

    private fun getTodayDate(): Calendar {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)

        return Calendar.getInstance().apply {
            set(currentYear, currentMonth, currentDate)
        }
    }

}