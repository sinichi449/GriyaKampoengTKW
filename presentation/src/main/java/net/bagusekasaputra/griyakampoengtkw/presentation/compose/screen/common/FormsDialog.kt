package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
                modifier = Modifier.fillMaxWidth().then(modifier),
                color = MaterialTheme.colorScheme.surface,
            ) {
                forms()
            }
        }
    }
}