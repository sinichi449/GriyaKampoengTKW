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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembangunan.GetAllBiayaMaterialStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import javax.inject.Inject

@HiltViewModel
class BiayaPembangunanViewModel @Inject constructor(
    private val getAllBiayaMaterialUseCase: GetAllBiayaMaterialStreamAsyncUseCase,
): ViewModel() {

    /* Biaya Material */
    private val _biayaMaterialList = MutableStateFlow(listOf(BiayaMaterial.EMPTY("-")))
    val biayaMaterialList = _biayaMaterialList.asStateFlow()

    /* List Kavling for filter */
    private val _kavlingKodeList = MutableStateFlow(emptyList<String>())
    val kavlingKodeList = _kavlingKodeList.asStateFlow()

    /* Current ViewPager page */
    private val _currentViewPagerPage = MutableStateFlow(0)
    val currentViewPagerPage = _currentViewPagerPage

    /* Data Mode */
    var dataMode = DataMode.ONLINE

    private var jobFetchAllBiayaMaterial: Job? = null

    fun getAllBiayaMaterial(listener: OnResultListener) {
        jobFetchAllBiayaMaterial?.cancel()

        jobFetchAllBiayaMaterial = viewModelScope.launch(Dispatchers.IO) {
            val request = GetAllBiayaMaterialStreamAsyncUseCase.Request(dataMode)
            getAllBiayaMaterialUseCase.execute(request)
                .onStart {
                    withContext(Dispatchers.Main) { listener.onLoading() }
                }
                .onCompletion {
                    populateKavlingKodeList(_biayaMaterialList.value)
                    withContext(Dispatchers.Main) { listener.onCompleted() }
                }
                .collect { result ->
                    result.onFailure {
                        it.printStackTrace()

                        withContext(Dispatchers.Main) { listener.onFailure(it.message) }
                    }
                    result.onSuccess { items ->
                        if (!items.isNullOrEmpty()) {
                            _biayaMaterialList.update { items }
                        }
                    }
                }
        }
    }

    private fun populateKavlingKodeList(biayaMaterials: List<BiayaMaterial>) {
        _kavlingKodeList.update {
            // TODO: Sort and distinct kavlings
            buildList {
                biayaMaterials.forEach { item ->
                    add(item.kavling)
                }
            }
        }
    }

    fun setViewPagerPage(page: Int) {
        _currentViewPagerPage.update { page }
    }

}