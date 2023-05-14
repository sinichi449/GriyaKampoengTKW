package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.databaseUser.GetAllDatabaseUserAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import javax.inject.Inject

@HiltViewModel
class DatabaseUserViewModel @Inject constructor(
    private val getAllDatabaseUserAsyncUseCase: GetAllDatabaseUserAsyncUseCase,
): ViewModel() {

    private val _listDatabaseUserLive = MutableLiveData<List<DatabaseUser>?>()
    val listDatabaseUserLive: LiveData<List<DatabaseUser>?>
        get() = _listDatabaseUserLive

    var gettingListJob: Job? = null


    fun getListDatabaseUser(
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        gettingListJob = viewModelScope.launch {
            val request = GetAllDatabaseUserAsyncUseCase.Request

            getAllDatabaseUserAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _listDatabaseUserLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onComplete()
                        onFailure("Gagal mendapatkan List Database User: ${it.message}")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        gettingListJob?.cancel()
    }
}