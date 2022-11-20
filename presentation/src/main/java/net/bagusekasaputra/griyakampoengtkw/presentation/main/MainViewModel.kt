package net.bagusekasaputra.griyakampoengtkw.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllBlocksAsyncUseCase: GetAllBlocksAsyncUseCase,
    private val addNewBlockUseCase: AddNewBlockUseCase,
    private val getKavlingByBlockAsyncUseCase: GetKavlingByBlockAsyncUseCase,
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

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // For use case arguments
    var offlineMode = false

    // The collection of jobs which need to be cancelled on onCleared()
    private val asyncJobs = ArrayList<Job>()


    /**
     * Blocks
     */
    fun getAllBlocks(onFailure: (msg: String) -> Unit) {
        val request = GetAllBlocksAsyncUseCase.Request(offlineMode)

        val gettingBlocksJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getAllBlocksAsyncUseCase,
            onSuccess = {
                _blocksLive.postValue(it)
            },
            onFailure = {
                onFailure("Gagal mendapatkan block: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

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

    /**
     * Kavlings
     */
    fun getKavlings(blockKode: String, onFailure: (msg: String) -> Unit) {
        val request = GetKavlingByBlockAsyncUseCase.Request(blockKode, offlineMode)

        val gettingKavlingsJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getKavlingByBlockAsyncUseCase,
            onSuccess = {
                _kavlings.postValue(it)
            },
            onFailure = {
                onFailure("Gagal mendapatkan kavling: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(gettingKavlingsJob)
    }

    fun addKavling(
        blockKode: String, noKavling: String, warna: String,
        type: String, panjang: String, lebar: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val request = AddKavlingUseCase.Request(
            blockKode = blockKode,
            noKavling = noKavling,
            warna = warna,
            type = type,
            panjang = panjang,
            lebar = lebar,
        )

        val addKavlingJob = CoroutineScope(Dispatchers.IO).launch {
            addKavlingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Kavling $blockKode$noKavling berhasil ditambahkan")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan kavling $blockKode$noKavling: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }

        asyncJobs.add(addKavlingJob)
    }

    fun editKavling(
        blockKode: String,
        oldKavling: Kavling,
        newPanjang: String,
        newLebar: String,
        newType: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val request = EditKavlingUseCase.Request(
            blockKode = blockKode,
            oldKavling = oldKavling,
            newType = newType,
            newPanjang = newPanjang,
            newLebar = newLebar,
        )

        val editKavlingJob = CoroutineScope(Dispatchers.IO).launch {

            editKavlingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil mengubah data kavling ${oldKavling.kode}")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal mengubah data kavling: ${throwable.message}")
                    }
                }


                isFinishOperation.postValue(true)
            }
        }

        asyncJobs.add(editKavlingJob)
    }

    fun removeKavling(
        blockKode: String,
        kavlingKode: String,
        onComplete: (msg: String) -> Unit
    ) {
        isFinishOperation.value = false

        val request = RemoveKavlingUseCase.Request(blockKode, kavlingKode)

        val removeKavlingJob = CoroutineScope(Dispatchers.IO).launch {

            removeKavlingUseCase.execute(request).collect {
                val result = it.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus kavling $kavlingKode")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus kavling $kavlingKode: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }

        }

        asyncJobs.add(removeKavlingJob)
    }

    fun checkUpdates(
        versionName: String,
        versionCode: Int,
        onAvailable: (appUpdate: AppUpdate) -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentBuildConfig = GetUpdateInformationUseCase.CurrentBuildConfig(
                versionName = versionName,
                versionCode = versionCode,
            )
            val request = GetUpdateInformationUseCase.Request(currentBuildConfig)

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