package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import java.util.Calendar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FormsDialog(
    modifier: Modifier = Modifier,
    cancelable: Boolean = false,
    show: Boolean = false,
    fullWidth: Boolean = true,
    onDismissRequest: () -> Unit = {},
    forms: @Composable () -> Unit = {},
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = cancelable,
                usePlatformDefaultWidth = !fullWidth,
            ),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
                color = MaterialTheme.colorScheme.surface,
            ) {
                forms()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FormsOnProgressDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onDismiss: () -> Unit,
) {
    if (show) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true,
            ),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
                color = MaterialTheme.colorScheme.surface
            ) {
                ProgressLayout(onCancelled = onDismiss)
            }
        }
    }
}

@Composable
private fun ProgressLayout(
    modifier: Modifier = Modifier,
    onCancelled: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (layoutRef, cancelRef) = createRefs()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .constrainAs(layoutRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .then(modifier),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Sedang Memproses ...", style = MaterialTheme.typography.bodyLarge)
            }

            TextButton(
                onClick = onCancelled,
                modifier = Modifier.constrainAs(cancelRef) {
                    top.linkTo(layoutRef.bottom, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
            ) {
                Text(
                    text = "Batalkan".uppercase(),
                    style = MaterialTheme.typography.bodyMedium.merge(
                        TextStyle(fontWeight = FontWeight.Bold)
                    ),
                )
            }
        }
    }
}

@Composable
private fun TextProgress(
    modifier: Modifier = Modifier,
    text: String = "Memproses data ..."
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String = "This is title",
    content: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit?",
    positiveButtonText: String = "Ya",
    negativeButtonText: String = "Tidak",
    onConfirmed: () -> Unit = {},
    onCancelled: () -> Unit = {},
) {
    if (show) {
        Dialog(onDismissRequest = onCancelled) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
                color = MaterialTheme.colorScheme.background
            ) {
                DeleteConfirmationLayout(
                    modifier = Modifier.padding(16.dp),
                    title = title,
                    content = content,
                    positiveButtonText = positiveButtonText,
                    negativeButtonText = negativeButtonText,
                    onPositiveButtonClick = onConfirmed,
                    onNegativeButtonClick = onCancelled,
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationLayout(
    modifier: Modifier = Modifier,
    title: String = "This is title",
    content: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit?",
    positiveButtonText: String = "Ya",
    negativeButtonText: String = "Tidak",
    onPositiveButtonClick: () -> Unit = {},
    onNegativeButtonClick: () -> Unit = {},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNegativeButtonClick) {
                Text(text = negativeButtonText.uppercase())
            }
            TextButton(onClick = onPositiveButtonClick) {
                Text(text = positiveButtonText.uppercase())
            }
        }
    }
}

@Composable
fun FormsTitle(
    modifier: Modifier = Modifier,
    entityName: String,
    isEditMode: Boolean,
) {
    Text(
        text = buildString {
            append(if (isEditMode) "Edit " else "Tambahkan ")
            append(entityName)
        },
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier,
    )
}

@Composable
fun FormsSubmitAndDeleteButton(
    modifier: Modifier = Modifier,
    isOnProgress: Boolean,
    isEditMode: Boolean,
    onSubmit: () -> Unit,
    onDelete: () -> Unit,
    onProgressChanges: (changed: Boolean) -> Unit,
) {
    Column(modifier = modifier) {
        // Submit Button
        Button(
            onClick = {
                onSubmit()
                onProgressChanges(true)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isOnProgress,
        ) {
            if (!isOnProgress) {
                Text(text = if (isEditMode) "Ubah" else "Tambahkan")
            } else {
                TextProgress(text = "Memproses data ...")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Delete Button -> Only shown on edit mode
        if (isEditMode) {
            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                enabled = !isOnProgress,
            ) {
                Text(text = "Hapus")
            }
        }
    }
}

fun DatePickerDialogView(
    ctx: Context,
    onDateSet: (tahun: Int, bulan: Int, tanggal: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        onDateSet(year, month + 1, dayOfMonth)
    }

    val datePicker = DatePickerDialog(
        ctx, R.style.DatePicker, listener,
        currentYear, currentMonth, currentDay
    )
    datePicker.setOnDismissListener{ onDismiss() }

    datePicker.show()
}

@Preview(showBackground = true, group = "isolated")
@Composable
fun DeleteConfirmationLayoutScreen() {
    DeleteConfirmationLayout()
}

@Preview(showBackground = true, group = "isolated")
@Composable
private fun TextProgressPreview() {
    TextProgress()
}

@Preview(showBackground = true, group = "components")
@Composable
fun ProgressLayoutPreview() {
    ProgressLayout(onCancelled = {})
}