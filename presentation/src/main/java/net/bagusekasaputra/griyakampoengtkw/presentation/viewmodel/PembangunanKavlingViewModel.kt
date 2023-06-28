package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.GetAllMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.upahPekerja.GetAllUpahPekerjaAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import javax.inject.Inject

@HiltViewModel
class PembangunanKavlingViewModel @Inject constructor(
    private val getAllMaterialPembangunanUseCase: GetAllMaterialPembangunanAsyncUseCase,
    private val getAllUpahPekerjaUseCase: GetAllUpahPekerjaAsyncUseCase,
): ViewModel() {

    var kavlingKode = ""
    var dataMode = DataMode.ONLINE

    private var jobFetchMaterialPembangunan: Job? = null
    private var jobFetchUpahPekerja: Job? = null

    private val _materialList = MutableStateFlow(emptyList<MaterialPembangunan>())
    private val _upahPekerjaList = MutableStateFlow(emptyList<UpahPekerja>())

    fun fetchMaterialPembangunan(kavling: String, listener: ViewModelListener) {
        jobFetchMaterialPembangunan?.cancel()

        jobFetchMaterialPembangunan = viewModelScope.launch(Dispatchers.Main) {
            val request = GetAllMaterialPembangunanAsyncUseCase.Request(kavling, dataMode)
            getAllMaterialPembangunanUseCase.execute(request)
                .onStart { listener.onProgress() }
                .onCompletion { throwable ->
                    if (throwable == null) {
                        listener.onCompleted()
                    } else {
                        listener.onFailed(throwable.message)
                    }
                }
                .collect { result ->
                    withContext(Dispatchers.IO) {
                        result.onSuccess { items ->
                            if (!items.isNullOrEmpty()) {
                                _materialList.update { items }
                            }
                        }
                        result.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
        }
    }
}