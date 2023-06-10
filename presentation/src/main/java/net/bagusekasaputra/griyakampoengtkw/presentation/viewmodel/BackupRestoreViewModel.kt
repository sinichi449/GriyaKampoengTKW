package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore.GetListBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetListUnmigratedKavlingsAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.UnmigratedKavling
import javax.inject.Inject

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val getListBackupAsyncUseCase: GetListBackupAsyncUseCase,
    private val getListUnmigratedKavlingsAsyncUseCase: GetListUnmigratedKavlingsAsyncUseCase,
): ViewModel() {

    suspend fun getListBackup() : List<String>? {
        val request = GetListBackupAsyncUseCase.Request
        val completableDeferred = CompletableDeferred<List<String>?>()

        getListBackupAsyncUseCase.execute(request).collect { result ->
            result.onSuccess {
                completableDeferred.complete(it)
            }
            result.onFailure {
                completableDeferred.completeExceptionally(it)
            }
        }

        return completableDeferred.await()
    }

    suspend fun getListUnmigratedKavlings(backupName: String): List<UnmigratedKavling>? {
        val request = GetListUnmigratedKavlingsAsyncUseCase.Request(backupName)
        val completableDeferred = CompletableDeferred<List<UnmigratedKavling>?>()

        val listUnmigratedKavling = getListUnmigratedKavlingsAsyncUseCase.execute(request).first()
        listUnmigratedKavling.onSuccess {
            completableDeferred.complete(it)
        }
        listUnmigratedKavling.onFailure {
            completableDeferred.completeExceptionally(it)
        }

        return completableDeferred.await()
    }
}