package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.*
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getKavlingsByBlockUseCase: GetKavlingsByBlockUseCase,
    private val getAllBlocksUseCase: GetAllBlocksUseCase,
    private val addNewBlockUseCase: AddNewBlockUseCase,
    private val addKavlingUseCase: AddKavlingUseCase,
    private val editKavlingUseCase: EditKavlingUseCase,
): ViewModel() {

    private val _kavlings = MutableLiveData<List<Kavling>>()
    val kavlings: LiveData<List<Kavling>>
        get() = _kavlings

    private val _blocks = MutableLiveData<List<Block>>()
    val blocks: LiveData<List<Block>>
        get() = _blocks

    val currentBlock = MutableLiveData("A")

    val isFinishOperation = MutableLiveData<Boolean>()

    val operationResult = MutableLiveData<Operation>()


    fun getAllBlocks() {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllBlocksUseCase.Request
            getAllBlocksUseCase.execute(request).collect {
                val result = it.data.data

                result?.let { blocks ->
                    _blocks.postValue(blocks)
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun getKavlings(blockKode: String) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetKavlingsByBlockUseCase.Request(blockKode)
            getKavlingsByBlockUseCase.execute(request).collect {
                val result = it.data.result

                if (result.isSuccess) {
                    val unsortedKavlings = result.getOrNull()
                    unsortedKavlings?.let { kavling ->
                        _kavlings.postValue(sortKavling(kavling))
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    fun addNewBlock(block: Block) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddNewBlockUseCase.Request(block)
            addNewBlockUseCase.execute(request).collect {
                val result = it.data.result
                Log.d(LOG_TAG, "Got viewmodel value: ${it.data.result.getOrNull()}")
                if (result.isSuccess) {
                    operationResult.postValue(Operation(true, "Blok ${block.kode} berhasil ditambahkan"))
                } else {
                    operationResult.postValue(Operation(false, "Gagal: ${result.exceptionOrNull()?.message}"))
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addKavling(blockKode: String, kavling: Kavling) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddKavlingUseCase.Request(blockKode, kavling)
            addKavlingUseCase.execute(request).collect {
                val result = it.data.result

                if (result.isSuccess) {
                    operationResult.postValue(Operation(true, "Kavling ${kavling.kode} berhasil ditambahkan"))
                } else {
                    operationResult.postValue(Operation(false, "Gagal: ${result.exceptionOrNull()?.message}"))
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun editKavling(blockKode: String, oldKavling: Kavling, newKavling: Kavling) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = EditKavlingUseCase.Request(blockKode, oldKavling, newKavling)

            editKavlingUseCase.execute(request).collect { response ->
                val result = response.data.result
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        if (it) {
                            operationResult.postValue(Operation(true, "Berhasil mengubah data kavling ${oldKavling.kode}"))
                        } else {
                            operationResult.postValue(Operation(false, "Data kavling ${oldKavling.kode} tidak ditemukan!"))
                        }
                    }
                } else {
                    result.exceptionOrNull()?.let {
                        operationResult.postValue(Operation(false, "Gagal: ${it.message}"))
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    private fun sortKavling(kavlings: List<Kavling>): List<Kavling> {
        val mutableKavling = mutableListOf<Kavling>()

        kavlings.forEach {
            mutableKavling.add(it)
        }

        mutableKavling.sortBy {
            it.kode.substring(1).toInt()
        }

        return mutableKavling
    }

    data class Operation(
        val isSuccess: Boolean,
        val message: String?
    )
}