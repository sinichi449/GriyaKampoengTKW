package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingSequentiallyByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetSingleProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Promotion
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.combineWith
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management.ManagementKavlingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
import net.bagusekasaputra.griyakampoengtkw.presentation.model.KavlingWithProgress
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // Blocks
    private val getAllBlocksUseCase: GetAllBlocksAsyncUseCase,
    private val addNewBlockUseCase: AddNewBlockUseCase,
    // Kavlings
    private val getKavlingByBlockUseCase: GetKavlingByBlockAsyncUseCase,
    private val getKavlingSequentiallyUseCase: GetKavlingSequentiallyByBlockAsyncUseCase,
    private val addKavlingUseCase: AddKavlingUseCase,
    private val editKavlingUseCase: EditKavlingUseCase,
    private val removeKavlingUseCase: RemoveKavlingUseCase,
    // Progress Kavling
    private val getProgressKavlingUseCase: GetProgressKavlingAsyncUseCase,
    private val getSingleProgressKavlingUseCase: GetSingleProgressKavlingAsyncUseCase,
    // App update
    private val getAppUpdateInformationUseCase: GetUpdateInformationUseCase,
    // Promotion
    private val getPromotionMessageUseCase: GetPromotionMessageAsyncUseCase,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _blocksLive = MutableLiveData<List<Block>>()
    val blocksLive: LiveData<List<Block>>
        get() = _blocksLive

    private val _kavlings = MutableLiveData<List<Kavling>>()

    private val _mapProgressKavling = MutableLiveData<Map<String, ProgressKavling>?>(null)

    // Kavling and Progress kavling combined
    val kavlingAndProgress = _kavlings.combineWith(_mapProgressKavling) { listKavling, mapProgress ->
        Pair(listKavling, mapProgress)
    }


    // Promotion Message
    private val _promotionMessage = MutableLiveData<Promotion?>(null)

    /**
     * [KavlingFragmentUiState] contains all the data needed for [net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management.KavlingFragment]'s screen.
     */
    private val _kavlingFragmentUiState = MutableStateFlow<UiState<KavlingFragmentUiState>?>(null)
    val kavlingFragmentUiState = _kavlingFragmentUiState.asStateFlow()

    val managementKavlingFragment = MutableLiveData<ManagementKavlingFragment?>(null)
    val shouldNavigateToKavlingFragment = MutableLiveData(false)

    // NEW!!
    private val _kavlingWithProgressList = MutableStateFlow<List<KavlingWithProgress>>(emptyList())
    val kavlingWithProgressList = _kavlingWithProgressList.asStateFlow()

    val currentBlock = MutableLiveData("A")

    val isFinishOperation = MutableLiveData<Boolean>()

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // For use case arguments
    var offlineMode = false

    var dataMode: DataMode = DataMode.ONLINE

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
    val blockRefreshed = MutableLiveData(false)
    val kavlingsRefreshed = mapOf(
        Pair("A", MutableLiveData(false)),
        Pair("B", MutableLiveData(false)),
        Pair("C", MutableLiveData(false)),
        Pair("D", MutableLiveData(false)),
    )

    private var jobFetchKavlings: Job? = null


    /**
     * Blocks
     */
    fun getAllBlocks(onFailure: (msg: String) -> Unit) {
        // Because Block is static, i.e, they always appears there
        // compared to the kavlings which need to be clicked when they need to be appeared ,
        // there's no need to pull the data locally.
        if (blockRefreshed.value != true) {
            logEvent("Blocks are already refreshed!")
            val request = GetAllBlocksAsyncUseCase.Request(dataMode)

            val gettingBlocksJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllBlocksUseCase,
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

                // If not refreshed, then pull from whatever data mode allow
                GetKavlingByBlockAsyncUseCase.Request(blockKode, dataMode)
            } else {
                logEvent("Kavling the block $blockKode already refreshed!")

                // Otherwise, pull from local storage by invoking "offline" parameter as TRUE
                // EXCEPT when the DataMode is DATA_LAMA
                if (dataMode == DataMode.DATA_LAMA)
                    GetKavlingByBlockAsyncUseCase.Request(blockKode, DataMode.DATA_LAMA)
                else
                    GetKavlingByBlockAsyncUseCase.Request(blockKode, DataMode.OFFLINE)
            }

        val gettingKavlingsJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getKavlingByBlockUseCase,
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

    // NEW METHOD!!
    fun fetchKavlingListOn(
        blockKode: String,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        jobFetchKavlings?.cancel()

       jobFetchKavlings = viewModelScope.launch(dispatchers) {
            val kavlingRequest = GetKavlingSequentiallyByBlockAsyncUseCase.Request(blockKode)
            getKavlingSequentiallyUseCase.execute(kavlingRequest)
                .onStart {
                    Log.d("SEQUENTIAL_KAVLING", "I'm on start!")
                    // Reset previous lists first
                    _kavlingWithProgressList.update { emptyList() }
                    withContext(Dispatchers.Main) { onLoading() }
                }
                .onCompletion { throwable ->
                    withContext(Dispatchers.Main) {
                        if (throwable != null) {
                            onFailure(throwable.localizedMessage ?: "Proses fetch Kavling dihentikan karena error!")
                        } else {
                            onComplete()
                        }
                    }
                }
                .collect { result ->
                    result.onFailure { throwable ->
                       withContext(Dispatchers.Main) {
                           onFailure(throwable.localizedMessage ?: "Error mendapatkan kavling!")
                       }
                    }
                    result.onSuccess { kavling ->
                        if (kavling != null) {
                            // As long as `kavling` is successfully emitted,
                            // this will fetch `ProgressKavling` continuously.
                            val progressRequest = GetSingleProgressKavlingAsyncUseCase
                                .Request(kavling.kode)
                            val fetchResult = getSingleProgressKavlingUseCase
                                .execute(progressRequest)
                                .first()

                            fetchResult.onFailure {
                                withContext(Dispatchers.Main) {
                                    "Gagal mendapatkan progress kavling: ${it.localizedMessage}"
                                }
                            }
                            fetchResult.onSuccess { progressKavling ->
                                val item = KavlingWithProgress(
                                    blok = blockKode,
                                    kavling = kavling,
                                    progress = progressKavling ?: ProgressKavling.EMPTY(kavling.kode),
                                )

                                item.updateStateFlow(_kavlingWithProgressList)
                            }
                        }
                    }
                }
        }
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

    @Deprecated("")
    fun getProgressAllKavling(blockKode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Get all kavling's in block
            val kavlingByBlockRequest = GetKavlingByBlockAsyncUseCase.Request(blockKode, dataMode)
            val kavlingList = getKavlingByBlockUseCase.execute(kavlingByBlockRequest).first()
                .getOrThrow()
                ?.let {
                    Kavling.getKavlingKodes(it)
                }
                ?: emptyList()

            // Progress Kavling
            val request = GetProgressKavlingAsyncUseCase.Request(kavlingList, dataMode)
            Log.d("DEBUG_ME", "Getting Progress Kavling is $dataMode")
            getProgressKavlingUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _mapProgressKavling.postValue(it)
                }
                result.onFailure {
                    Log.d("STATUS_PEMBAYARAN", "Terjadi kesalahan ViewModel : ${it.message}")
                }
            }
        }
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

    /**
     * Promotion Message
     */
    fun getPromotionMessage(onFailure: (msg: String) -> Unit = {}) {
        viewModelScope.launch {
            val request = GetPromotionMessageAsyncUseCase.Request
            getPromotionMessageUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _promotionMessage.postValue(it)
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan Promotion Message: ${it.message}")
                    }
                }
            }
        }
    }

    fun fetchKavlingFragmentUiState(blockKode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _kavlingFragmentUiState.update {
                UiState.Loading()
            }

            // If block has been refreshed or loaded before, set dataMode to DataMode.OFFLINE.
            val blockAlreadyRefreshed = kavlingsRefreshed[blockKode]?.value == true
            val fetchDataMode =
                if (dataMode != DataMode.DATA_LAMA) {
                    if (blockAlreadyRefreshed) DataMode.OFFLINE else dataMode
                } else {
                    DataMode.DATA_LAMA
                }

            val kavlingRequest = GetKavlingByBlockAsyncUseCase.Request(blockKode, fetchDataMode)
            val kavlingListDeffered = CompletableDeferred<List<Kavling>>()
            getKavlingByBlockUseCase.execute(kavlingRequest).collect { result ->
                result.onSuccess {
                    kavlingListDeffered.complete(it ?: emptyList())
                }
                result.onFailure {
                    kavlingListDeffered.completeExceptionally(it)
                }
            }
            val kavlingList = kavlingListDeffered.await()

            val progressKavlingRequest = GetProgressKavlingAsyncUseCase.Request(
                listKavling = Kavling.getKavlingKodes(kavlingList),
                dataMode = fetchDataMode
            )
            val progressKavlingMap = CompletableDeferred<Map<String, ProgressKavling>>()
            getProgressKavlingUseCase.execute(progressKavlingRequest).collect { result ->
                result.onSuccess {
                    progressKavlingMap.complete(it ?: emptyMap())
                }
                result.onFailure {
                    progressKavlingMap.completeExceptionally(it)
                }
            }

            kavlingsRefreshed[blockKode]?.postValue(true)

            _kavlingFragmentUiState.update {
                UiState.Success(KavlingFragmentUiState(
                    blockKode = blockKode,
                    kavlingList = kavlingList,
                    progressKavlingMap = progressKavlingMap.await(),
                ))
            }
        }
    }

    override fun onCleared() {
        logEvent("MainViewModel is about to be cleared!")

        asyncJobs.forEach { it.cancel() }
        super.onCleared()
    }
}

data class KavlingFragmentUiState(
    val blockKode: String,
    val kavlingList: List<Kavling>,
    val progressKavlingMap: Map<String, ProgressKavling>,
)