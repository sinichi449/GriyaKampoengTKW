package net.bagusekasaputra.griyakampoengtkw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.model.Tahapan
import net.bagusekasaputra.griyakampoengtkw.presentation.combineWith
import net.bagusekasaputra.griyakampoengtkw.repository.TahapanRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tahapanRepository: TahapanRepository,
): ViewModel() {

    private val _tahapanList = MutableLiveData<List<Tahapan>?>(null)
    val tahapanList: LiveData<List<Tahapan>?>
        get() = _tahapanList

    private var tahapanFetchJob: Job? = null

    val selectedTahapan = MutableLiveData<Tahapan?>(null)
    val selectedJenisData = MutableLiveData<Int?>(null)
    val tahapanAndJenisData = selectedTahapan.combineWith(selectedJenisData) { tahapan, jenisData ->
        Pair(tahapan, jenisData)
    }


    fun getAllTahapan(
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (failMsg: String) -> Unit = {}
    )  {
        onProgress()

        tahapanFetchJob = viewModelScope.launch(Dispatchers.IO) {
            tahapanRepository.getAllTahapan()
                .onSuccess {
                    _tahapanList.postValue(it)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan Tahapan Pembangunan : ${it.localizedMessage}")
                    }
                }
        }
    }

    override fun onCleared() {
        tahapanFetchJob?.cancel()

        super.onCleared()
    }
}