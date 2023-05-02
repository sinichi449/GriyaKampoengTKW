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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.calonPembeli.GetAllCalonPembeliAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import javax.inject.Inject

@HiltViewModel
class CalonPembeliViewModel @Inject constructor(
    private val getAllCalonPembeliAsyncUseCase: GetAllCalonPembeliAsyncUseCase,
): ViewModel() {

    private val _listCalonPembeliLive = MutableLiveData<List<CalonPembeli>?>()
    val listCalonPembeliLive: LiveData<List<CalonPembeli>?>
        get() = _listCalonPembeliLive

    val showExtendedFab = MutableLiveData(false)

    var gettingListJob: Job? = null


    fun getListCalonPembeli(
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        gettingListJob = viewModelScope.launch {
            val request = GetAllCalonPembeliAsyncUseCase.Request

            getAllCalonPembeliAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _listCalonPembeliLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onComplete()
                        onFailure("Gagal mendapatkan List Calon Pembeli: ${it.message}")
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