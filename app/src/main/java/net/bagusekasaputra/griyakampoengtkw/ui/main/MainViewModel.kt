package net.bagusekasaputra.griyakampoengtkw.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Operation
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.GetAllBlocksUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.GetKavlingsByBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getKavlingsByBlockUseCase: GetKavlingsByBlockUseCase,
    private val getAllBlocksUseCase: GetAllBlocksUseCase,
    private val addNewBlockUseCase: AddNewBlockUseCase,
    private val addKavlingUseCase: AddKavlingUseCase,
    private val editKavlingUseCase: EditKavlingUseCase,
    private val removeKavlingUseCase: RemoveKavlingUseCase,
    private val getAppUpdateInformationUseCase: GetUpdateInformationUseCase,
): ViewModel() {

    private val _kavlings = MutableLiveData<List<Kavling>>()
    val kavlings: LiveData<List<Kavling>>
        get() = _kavlings

    private val _blocksLive = MutableLiveData<List<Block>>()
    val blocksLive: LiveData<List<Block>>
        get() = _blocksLive

    val currentBlock = MutableLiveData("A")

    val isFinishOperation = MutableLiveData<Boolean>()

    val operationResult = MutableLiveData<Operation?>()

    private val asyncJobs = ArrayList<Job>()

    // Blocks
    fun getAllBlocks(onFailure: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val gettingBlocksJob = CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllBlocksUseCase.Request
            getAllBlocksUseCase.execute(request).collect {
                val result = it.data.result

                if (result.isSuccess) {
                    result.getOrNull()?.let { blocks ->
                        _blocksLive.postValue(blocks)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        result.exceptionOrNull()?.message?.let { failMsg ->
                            onFailure(failMsg)
                        }
                    }
                }

                isFinishOperation.postValue(true)
            }
        }

        asyncJobs.add(gettingBlocksJob)
    }

    fun addNewBlock(kode: String, warna: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val block = Block(kode, warna)
        val request = AddNewBlockUseCase.Request(block)

        val addingBlockJob = CoroutineScope(Dispatchers.IO).launch {
            addNewBlockUseCase.execute(request).collect {
                val result = it.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Blok ${block.kode} berhasil ditambahkan")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan blok: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }

        asyncJobs.add(addingBlockJob)
    }

    // Kavlings
    fun getKavlings(blockKode: String, onFailure: (msg: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetKavlingsByBlockUseCase.Request(blockKode)

            getKavlingsByBlockUseCase.execute(request).collect {
                val result = it.data.result

                result.onSuccess {  kavlingList ->
                    _kavlings.postValue(kavlingList)
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        throwable.message?.let(onFailure)
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addKavling(blockKode: String, kavling: Kavling) {
        isFinishOperation.value = false
        operationResult.value = null

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
        operationResult.value = null

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

    fun removeKavling(blockKode: String, kavlingKode: String) {
        isFinishOperation.value = false
        operationResult.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val request = RemoveKavlingUseCase.Request(blockKode, kavlingKode)

            removeKavlingUseCase.execute(request).collect {
                val result = it.data.result

                if (result.isSuccess) {
                    operationResult.postValue(Operation(true, "Berhasil menghapus kavling $kavlingKode"))
                } else {
                    operationResult.postValue(Operation(false, "Gagal menghapus kavling $kavlingKode: ${result.exceptionOrNull()}"))
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun checkUpdates(
        onAvailable: (appUpdate: AppUpdate) -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GetUpdateInformationUseCase.Request

            getAppUpdateInformationUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    result.getOrNull()?.let { appUpdate ->
                        withContext(Dispatchers.Main) {
                            onAvailable(appUpdate)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan update: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        asyncJobs.forEach { it.cancel() }
    }


}