package net.bagusekasaputra.griyakampoengtkw.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.GetAllBlocksUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.GetKavlingsByBlockUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getKavlingsByBlockUseCase: GetKavlingsByBlockUseCase,
    private val getAllBlocksUseCase: GetAllBlocksUseCase,
    private val addKavlingUseCase: AddKavlingUseCase
): ViewModel() {

    private val _kavlings = MutableLiveData<List<Kavling>>()
    val kavlings: LiveData<List<Kavling>>
        get() = _kavlings

    private val _blocks = MutableLiveData<List<Block>>()
    val blocks: LiveData<List<Block>>
        get() = _blocks

    val currentBlock = MutableLiveData("A")


    fun refresh() {
        val block = Block(
            kode = currentBlock.value!!
        )
        getKavlings(block)
    }

    fun getKavlings(block: Block) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GetKavlingsByBlockUseCase.Request(block)
            getKavlingsByBlockUseCase.execute(request).collect {
                val result = it.data.data
                _kavlings.postValue(result)
            }
        }
    }

    fun getAllBlocks() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllBlocksUseCase.Request
            getAllBlocksUseCase.execute(request).collect {
                val result = it.data.data
                _blocks.postValue(result)
            }
        }
    }

    fun addKavling(block: Block) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = AddKavlingUseCase.Request(block)
            addKavlingUseCase.execute(request).collect {
                val result = it.data.isSuccess
            }
        }
    }
}