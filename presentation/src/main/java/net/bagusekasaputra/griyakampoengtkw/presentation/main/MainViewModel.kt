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
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
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

    val tabSelectedLive = MutableLiveData(0)

    val isFinishOperation = MutableLiveData<Boolean>()

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // For use case arguments
    var offlineMode = false

    // The collection of jobs which need to be cancelled on onCleared()
    private val asyncJobs = ArrayList<Job>()

    /**
     * To ensure just one time loading of Kavling, Blocks and (soon) Report
     *
     * Currently I'm prioritizing the List<?> data, which require larger amount of bandwidth
     * and possibly impacting the device performance.
     *
     * For write operations such as edit, delete, and add, the UI need to be refreshed.
     * In such operations, we need to set "xRefreshed" to be false.
     *
     * Also, when user invokes refresh command, like swipe-to-refresh,
     * we also need to update these value into FALSE.
     *
     * Whenever the "GET" operation is success, we need to update these value into TRUE.
     */
    val blockRefreshed = MutableLiveData<Boolean>(false)
    val kavlingsRefreshed = mapOf(
        Pair("A", MutableLiveData(false)),
        Pair("B", MutableLiveData(false)),
        Pair("C", MutableLiveData(false)),
        Pair("D", MutableLiveData(false)),
    )
    // soon
    // val laporanRefreshed = MutableLiveData<Boolean>(false)


    /**
     * Blocks
     */
    fun getAllBlocks(onFailure: (msg: String) -> Unit) {
        // Because Block is static, i.e, they always appears there
        // compared to the kavlings which need to be clicked when they need to be appeared ,
        // there's no need to pull the data locally.
        if (blockRefreshed.value != true) {
            logEvent("Blocks are already refreshed!")
            val request = GetAllBlocksAsyncUseCase.Request(offlineMode)

            val gettingBlocksJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllBlocksAsyncUseCase,
                onSuccess = {
                    _blocksLive.postValue(it)

                    blockRefreshed.postValue(true)
                },
                onFailure = {
                    onFailure("Gagal mendapatkan block: ${it.message}")
                },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingBlocksJob)
        } else {
            logEvent("Blocks not refreshed, refreshing now ...")
        }
    }

    fun addNewBlock(kode: String, warna: String, onComplete: (msg: String) -> Unit) {
        blockRefreshed.value = false
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
        // Kavlings entity are dynamic, to load them you need to click the corresponding blocks.
        // So, when the kavlings are refreshed, I need to only pull the data from local storage
        // by manipulating the "offlineMode" parameter of GET request.
        val request = if (kavlingsRefreshed[blockKode]?.value != true) {
                logEvent("Kavling on the block $blockKode isn't refreshed, refreshing now ...")

                // If not refreshed, then pull from remote storage
                GetKavlingByBlockAsyncUseCase.Request(blockKode, offlineMode)
            } else {
                logEvent("Kavling the block $blockKode already refreshed!")

                // Otherwise, pull from local storage by invoking "offline" parameter as TRUE
                GetKavlingByBlockAsyncUseCase.Request(blockKode, true)
            }

        val gettingKavlingsJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getKavlingByBlockAsyncUseCase,
            onSuccess = {
                _kavlings.postValue(it)

                kavlingsRefreshed[blockKode]?.postValue(true)
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
        kavlingsRefreshed[blockKode]?.value = false
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
        kavlingsRefreshed[blockKode]?.value = false
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
        kavlingsRefreshed[blockKode]?.value = false
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
        logEvent("MainViewModel is about to be cleared!")

        asyncJobs.forEach { it.cancel() }
        super.onCleared()
    }


}