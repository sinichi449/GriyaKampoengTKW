package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.CreateBackupAsyncUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val createBackupUseCase: CreateBackupAsyncUseCase,
): ViewModel() {

    private val _progressLive = MutableLiveData("Sedang memproses...")
    val progressLive: LiveData<String>
        get() = _progressLive

    private val _isBackupComplete = MutableLiveData<Boolean?>(null)
    val isBackupComplete: LiveData<Boolean?>
        get() = _isBackupComplete

    private var activeJob: Job? = null



    fun createBackup(
        onFailure: (reason: String) -> Unit,
    ) {
        val request = CreateBackupAsyncUseCase.Request

        _progressLive.value = "Sedang memproses..."
        _isBackupComplete.value = false

        activeJob = viewModelScope.launch {
            createBackupUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _progressLive.value = it ?: "null"

                    if (it == "Completed") {
                        _isBackupComplete.postValue(true)
                    }
                }

                result.onFailure {
                    onFailure(it.message ?: "Unknown error")
                    activeJob?.cancel()

                    _isBackupComplete.postValue(true)
                }
            }
        }
    }
}