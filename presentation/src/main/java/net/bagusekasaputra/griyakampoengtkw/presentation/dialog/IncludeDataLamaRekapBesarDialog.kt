package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.UnmigratedKavling
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BackupRestoreViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@AndroidEntryPoint
class IncludeDataLamaRekapBesarDialog: DialogFragment() {

    private lateinit var fetchingFromRemoteProgressDialog: ProgressDialog
    private val rekapViewModel by activityViewModels<RekapViewModel>()
    private val backupRestoreViewModel by viewModels<BackupRestoreViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initFetchingProgressDialog()

        fetchingFromRemoteProgressDialog.setMessage("Mendapatkan list Backup yang tersedia ...")

        CoroutineScope(Dispatchers.IO).launch {
            val listBackup = backupRestoreViewModel.getListBackup() ?: emptyList()
            // Open Dialog to get "backupName"
            val backupName = withContext(Dispatchers.Main) {
                val selectedBackup = getUserSelectedBackup(listBackup.toTypedArray())

                rekapViewModel.selectedBackupName = listBackup[selectedBackup]

                listBackup[selectedBackup]
            }

            // Change ProgressDialog's message
            withContext(Dispatchers.Main) {
                fetchingFromRemoteProgressDialog.setMessage("Mendapatkan List Kavling dari Data Lama ...")
            }

            val listUnmigratedKavling = backupRestoreViewModel.getListUnmigratedKavlings(backupName) ?: emptyList()
            Log.d("DEBUG_ME", "UI: List Unmigrated Kavling : $listUnmigratedKavling")
            // Open Dialog to get user's selected UnmigratedKavling
            val selectedUnmigratedKavling = withContext(Dispatchers.Main) {
                getUserSelectedUnmigratedKavlings(listUnmigratedKavling)
            }

            withContext(Dispatchers.Main) {
                rekapViewModel.setListDataLamaRekapBesarIncluded(selectedUnmigratedKavling)

                fetchingFromRemoteProgressDialog.dismiss()
            }
        }

        return fetchingFromRemoteProgressDialog
    }

    /**
     * Setting Title, Cancelable, and Cancel Button behaviour
     */
    private fun initFetchingProgressDialog() {
        fetchingFromRemoteProgressDialog = ProgressDialog(requireContext())
        fetchingFromRemoteProgressDialog.apply {
            setTitle("Tunggu Sebentar")
            setCancelable(false)
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }
    }

    private suspend fun getUserSelectedBackup(arrBackup: Array<String>): Int {
        return suspendCoroutine { continuation ->
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Pilih Backup")
                setCancelable(false)
                setSingleChoiceItems(arrBackup, 0) { dialog, checkedPosition ->
                    dialog.dismiss()

                    continuation.resume(checkedPosition)
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create()
                .show()
        }
    }

    private suspend fun getUserSelectedUnmigratedKavlings(listUnmigratedKavling: List<UnmigratedKavling>): List<String> {
        return suspendCoroutine { continuation ->
            val listStrKavling = mutableListOf<String>().apply {
                listUnmigratedKavling.forEach {
                    add("${it.kavlingKode} (${it.namaCostumer})")
                }
            }.toTypedArray()
            val checkedKavling = mutableListOf<Boolean>().apply {
                repeat(listStrKavling.size) {
                    add(true)
                }
            }.toBooleanArray()

            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("List Kavling Not-Migrated")
                setCancelable(false)
                setMultiChoiceItems(listStrKavling, checkedKavling) { _, which, checked ->
                    checkedKavling[which] = checked
                }
                setPositiveButton("OK") { dialog, _ ->
                    val listCheckedKavlingDataLama = mutableListOf<String>().apply {
                        checkedKavling.forEachIndexed { index, isChecked ->
                            if (isChecked) {
                                add(listUnmigratedKavling[index].kavlingKode)
                            }
                        }
                    }

                    continuation.resume(listCheckedKavlingDataLama)

                    dialog.dismiss()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    continuation.resume(emptyList<String>())

                    dialog.dismiss()
                }
            }.create()
                .show()
        }
    }
}