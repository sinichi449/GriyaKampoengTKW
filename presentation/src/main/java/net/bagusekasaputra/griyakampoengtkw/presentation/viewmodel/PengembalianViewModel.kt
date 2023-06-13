package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengembalian.GetPengembalianStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import javax.inject.Inject

@HiltViewModel
class PengembalianViewModel @Inject constructor(
    private val getPengembalianStreamUseCase: GetPengembalianStreamAsyncUseCase,
    private val dispatcher: CoroutineDispatcher,
): ViewModel() {

    private val _pengembalianList = MutableStateFlow<List<Pengembalian>?>(null)
    val pengembalianList = _pengembalianList.asStateFlow()

    private var jobFetchPengembalian: Job? = null

    var dataMode = DataMode.ONLINE

    fun getAllPengembalianList(
        onLoading: () -> Unit,
        onCompleted: () -> Unit,
        onFailed: (failMsg: String?) -> Unit,
    ) {
        jobFetchPengembalian?.cancel()

        viewModelScope.launch(dispatcher) {
            val request = GetPengembalianStreamAsyncUseCase.Request()
            getPengembalianStreamUseCase.execute(request)
                .onStart {
                    withContext(Dispatchers.Main) { onLoading() }
                }
                .onCompletion {
                    withContext(Dispatchers.Main) { onCompleted() }
                }
                .collect { result ->
                    result.onFailure {
                        it.printStackTrace()
                        withContext(Dispatchers.Main) { onFailed(it.message) }
                    }
                    result.onSuccess { items ->
                        _pengembalianList.update { items }
                    }
            }
        }
    }

}