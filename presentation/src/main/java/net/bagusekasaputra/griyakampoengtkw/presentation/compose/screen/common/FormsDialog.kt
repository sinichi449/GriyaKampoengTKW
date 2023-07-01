package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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