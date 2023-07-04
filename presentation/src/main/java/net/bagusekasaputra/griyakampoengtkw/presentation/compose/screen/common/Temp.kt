package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja

@Composable
fun UpahPekerjaForms(
    modifier: Modifier = Modifier,
    upahPekerja: UpahPekerja? = null,
    onSubmit: (editMode: Boolean, result: UKFormsResult) -> Unit = {_, _->},
    onDeleteRequest: (keyId: String?) -> Unit = {},
) {
    val isEditMode = upahPekerja != null
    var showDatePicker by remember { mutableStateOf(false) }
    var isOnProgress by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var keyIdToDelete by remember { mutableStateOf("") }

    var ukFormsResult by remember {
        mutableStateOf(upahPekerja.let {
            UKFormsResult(
                tanggalDibayarkan = it?.tanggalDibayarkan?.toSlashedString() ?: DateUtil.getTodaysDate(),
                namaMandor = it?.mandor ?: "",
                mingguKe = it?.mingguKe?.toString() ?: "1",
                jumlahDibayarkan = it?.jumlahDibayarkan?.toString() ?: "0",
                progress = it?.progress?.toString() ?: "0.0",
                keterangan = it?.keterangan?.toString() ?: "-",
            )
        })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            FormsTitle(
                entityName = "Upah Pekerja",
                isEditMode = isEditMode,
            )
        }
        item {
            FormTextField(
                title = "Nama Mandor",
                value = ukFormsResult.namaMandor,
                onValueChange = {
                    ukFormsResult = ukFormsResult.copy(namaMandor = it)
                },
                onTextCleared = {
                    ukFormsResult = ukFormsResult.copy(namaMandor = "")
                }
            )
        }
        item {
            FormsSubmitAndDeleteButton(
                isOnProgress = isOnProgress,
                isEditMode = isEditMode,
                onSubmit = { /*TODO*/ },
                onDelete = { /*TODO*/ },
                onProgressChanges = { isOnProgress = it }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialogView(
            ctx = LocalContext.current,
            onDateSet = { year, month, day ->
                val dayPadded = day.toString().padStart(2, '0')
                val monthPadded = month.toString().padStart(2, '0')

                ukFormsResult = ukFormsResult.copy(tanggalDibayarkan = "${dayPadded}/${monthPadded}/${year}")
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    DeleteConfirmationDialog(
        show = showDeleteDialog,
        title = "Hapus Item?",
        content = "Apakah Anda yakin ingin menghapus item ini?",
        onConfirmed = {
            onDeleteRequest(keyIdToDelete)
            isOnProgress = true

            showDeleteDialog = false
        },
        onCancelled = {
            isOnProgress = false
            showDeleteDialog = false
        }
    )
}