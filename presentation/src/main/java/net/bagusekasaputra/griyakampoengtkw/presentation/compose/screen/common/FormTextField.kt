package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onValueChange: (value: String) -> Unit,
    onTextCleared: () -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        label = {
            Text(text = title)
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (singleLine) {
                ImeAction.Next
            } else {
                ImeAction.Done
            }
        ),
        trailingIcon = {
            IconButton(onClick = onTextCleared) {
                Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
            }
        }
    )
}

@Composable
fun FormDivider(
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier)
    Divider()
    Spacer(modifier = modifier)
}