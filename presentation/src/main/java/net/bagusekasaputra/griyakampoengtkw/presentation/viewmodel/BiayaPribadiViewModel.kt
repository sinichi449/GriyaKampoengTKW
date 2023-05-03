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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaPribadi.GetAllBiayaPribadiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import javax.inject.Inject

@HiltViewModel
class BiayaPribadiViewModel @Inject constructor(
    private val getAllBiayaPribadiAsyncUseCase: GetAllBiayaPribadiAsyncUseCase,
): ViewModel() {

    private val _listBiayaPribadiLive = MutableLiveData<List<BiayaPribadi>?>()
    val listBiayaPribadiLive: LiveData<List<BiayaPribadi>?>
        get() = _listBiayaPribadiLive

    val showFab = MutableLiveData<Boolean>(false)

    var biayaPribadiJob: Job? = null


    fun getListBiayaPribadi(onComplete: () -> Unit, onFailure: (msg: String) -> Unit) {
        biayaPribadiJob = viewModelScope.launch {
            val request = GetAllBiayaPribadiAsyncUseCase.Request

            getAllBiayaPribadiAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _listBiayaPribadiLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan list Biaya Pribadi: ${it.message}")
                        onComplete()
                    }
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()

        biayaPribadiJob?.cancel()
    }
}