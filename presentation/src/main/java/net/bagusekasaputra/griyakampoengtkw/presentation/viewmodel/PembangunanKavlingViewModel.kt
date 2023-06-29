package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.GetAllMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.upahPekerja.GetAllUpahPekerjaAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.InformasiPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.DefaultTableViewAdapter
import javax.inject.Inject

@HiltViewModel
class PembangunanKavlingViewModel @Inject constructor(
    private val getAllMaterialPembangunanUseCase: GetAllMaterialPembangunanAsyncUseCase,
    private val getAllUpahPekerjaUseCase: GetAllUpahPekerjaAsyncUseCase,
): ViewModel() {

    var kavlingKode = ""
    var dataMode = DataMode.ONLINE

    var tableMaterialAdapter: DefaultTableViewAdapter? = null

    private var jobFetchMaterialPembangunan: Job? = null
    private var jobFetchUpahPekerja: Job? = null

    private val _informasiPembangunan = MutableStateFlow(InformasiPembangunan.EMPTY(kavlingKode))
    private val _materialList = MutableStateFlow(emptyList<MaterialPembangunan>())
    private val _upahPekerjaList = MutableStateFlow(emptyList<UpahPekerja>())
    private val _numJobsFinished = MutableStateFlow(0)

    val informasiPembangunan = _informasiPembangunan.asStateFlow()
    val materialList = _materialList.asStateFlow()
    val upahPekerjaList = _upahPekerjaList.asStateFlow()

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
                                _materialList.value = items
                            }
                        }
                        result.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
        }
    }

    fun fetchUpahPekerja(kavling: String, listener: ViewModelListener) {
        jobFetchUpahPekerja?.cancel()

        jobFetchUpahPekerja = viewModelScope.launch(Dispatchers.Main) {
            val request = GetAllUpahPekerjaAsyncUseCase.Request(kavling, dataMode)
            getAllUpahPekerjaUseCase.execute(request)
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
                                _upahPekerjaList.value = items
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