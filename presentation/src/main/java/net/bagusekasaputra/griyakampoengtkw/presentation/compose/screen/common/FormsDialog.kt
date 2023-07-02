package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Preview(showBackground = true, group = "isolated")
@Composable
fun DeleteConfirmationLayoutScreen() {
    DeleteConfirmationLayout()
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

@Preview(showBackground = true, group = "components")
@Composable
fun ProgressLayoutPreview() {
    ProgressLayout(onCancelled = {})
}