package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndNamaAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingAndNama
import javax.inject.Inject

@HiltViewModel
class PembatalanViewModel @Inject constructor(
    private val getAllBlocksUseCase: GetAllBlocksAsyncUseCase,
    private val getKavlingAndNamaUseCase: GetKavlingAndNamaAsyncUseCase,
): ViewModel() {

    private val _blocksLive = MutableLiveData<List<Block>>()
    val blocksLive: LiveData<List<Block>>
        get() = _blocksLive

    private val _kavlingAndNamaLive = MutableLiveData<List<KavlingAndNama>>()
    val kavlingAndNamaLive: LiveData<List<KavlingAndNama>>
        get() = _kavlingAndNamaLive

    val currentBlock = MutableLiveData("")

    fun getAllBlocks(
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (failMsg: String) -> Unit,
    ) {
        onLoading()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetAllBlocksAsyncUseCase.Request(DataMode.ONLINE, true)
            getAllBlocksUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _blocksLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure(it.localizedMessage ?: "Unknown Error")
                    }
                }
            }
        }
    }

    fun getAllKavlings(
        blok: String,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (failMsg: String) -> Unit,
    ) {
        onLoading()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetKavlingAndNamaAsyncUseCase.Request(blok)
            getKavlingAndNamaUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _kavlingAndNamaLive.postValue(it ?: emptyList())

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure(it.localizedMessage ?: "UNKNOWN ERROR")
                    }
                }
            }
        }
    }
}