package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndProgressStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Promotion
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.CombinedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TahapanRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management.ManagementKavlingFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.juta
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // Blocks
    private val getAllBlocksUseCase: GetAllBlocksAsyncUseCase,
    private val addNewBlockUseCase: AddNewBlockUseCase,
    // Kavlings and KavlingAndProgress
    private val getKavlingAndProgressStreamUseCase: GetKavlingAndProgressStreamAsyncUseCase,
    private val addKavlingUseCase: AddKavlingUseCase,
    private val editKavlingUseCase: EditKavlingUseCase,
    private val removeKavlingUseCase: RemoveKavlingUseCase,
    // App update
    private val getAppUpdateInformationUseCase: GetUpdateInformationUseCase,
    // Promotion
    private val getPromotionMessageUseCase: GetPromotionMessageAsyncUseCase,
    private val tahapanRepository: TahapanRepository,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _blocksLive = MutableLiveData<List<Block>>()
    val blocksLive: LiveData<List<Block>>
        get() = _blocksLive

    // Promotion Message
    private val _promotionMessage = MutableLiveData<Promotion?>(null)

    /**
     * Back button listener for MainActivity
     */
    val managementKavlingFragment = MutableLiveData<ManagementKavlingFragment?>(null)
    val shouldNavigateToKavlingFragment = MutableLiveData(false)

    /** * [KavlingAndProgress] * **/
    private val _kavlingAndProgressList = MutableStateFlow<List<KavlingAndProgress>>(emptyList())
    val kavlingAndProgressList = _kavlingAndProgressList.asStateFlow()

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
    fun getAllBlocks(
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        // Because Block is static, i.e, they always appears there
        // compared to the kavlings which need to be clicked when they need to be appeared ,
        // there's no need to pull the data locally.
        if (blockRefreshed.value != true) {
            logEvent("Blocks are already refreshed!")

            onLoading()

            viewModelScope.launch(Dispatchers.IO) {
                val request = GetAllBlocksAsyncUseCase.Request(dataMode)
                getAllBlocksUseCase.execute(request).collect { result ->
                    result.onFailure {
                        withContext(Dispatchers.Main) {
                            onFailure("Gagal mendapatkan Blok : ${it.localizedMessage}")
                        }
                    }
                    result.onSuccess {
                        _blocksLive.postValue(it)
                        blockRefreshed.postValue(true)

                        withContext(Dispatchers.Main) {
                            onComplete()
                        }
                    }
                }
            }
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
    fun fetchKavlingListOn(
        blockKode: String,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        jobFetchKavlings?.cancel()

        jobFetchKavlings = viewModelScope.launch(dispatchers) {
            val request = GetKavlingAndProgressStreamAsyncUseCase.Request(blockKode, dataMode)
            getKavlingAndProgressStreamUseCase.execute(request)
                .onStart {
                    // reset current `_kavlingAndProgressList`
                    _kavlingAndProgressList.update { emptyList() }

                    withContext(Dispatchers.Main) { onLoading() }
                }
                .onCompletion { throwable ->
                    // if `Flow.collect` has completed, call `onComplete` if `throwable`
                    // is `null`, or `onFailure` when it is not null.
                    val success = throwable == null
                    withContext(Dispatchers.Main) {
                        if (success) {
                            onComplete()

                            // Add single combined kavling
                            if (blockKode == "A") {
                                _kavlingAndProgressList.update {
                                    val newList = it.toMutableList()
                                    val warna = it[0].kavling.warna
                                    val combinedKavling = CombinedKavling(
                                        kavlingKodeList = listOf("A19", "A20"),
                                        belumIsi = false,
                                        warna = warna,
                                        ukuran = "12x24",
                                        type = "Type 2 Unit",
                                        numKode = 19,
                                    )
                                    newList.add(KavlingAndProgress(
                                        blok = blockKode,
                                        kavling = combinedKavling,
                                        progress = ProgressKavling(
                                            combinedKavling.kode,
                                            angsuranBulanan = 10.0.juta(),
                                            uangMasukBulanIni = 9.8.juta(),
                                        )
                                    ))

                                    newList.toList()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                onFailure("Terjadi kesalahan mendapatkan progress kavling : ${throwable?.localizedMessage}")
                            }
                        }
                    }
                }
                .collect { result ->
                    result.onFailure {
                        it.printStackTrace()
                        withContext(Dispatchers.Main) {
                            onFailure("Gagal mendapatkan kavling : $it")
                        }
                    }
                    result.onSuccess { items ->
                        // continuously updating `_kavlingAndProgressList` until `Flow.collect`
                        // has completed.
                        if (!items.isNullOrEmpty()) {
                            _kavlingAndProgressList.update { items }
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

    /* Tahapan */
    suspend fun getAvailableTahapan(listener: ViewModelListener): List<Tahapan> {
        withContext(Dispatchers.Main) { listener.onProgress() }

        val tahapanList = withContext(Dispatchers.IO) {
            val tahapanResult = tahapanRepository.getAllTahapan()
            if (tahapanResult.isFailure) {
                withContext(Dispatchers.Main) {
                    listener.onFailed(tahapanResult.exceptionOrNull()?.message)
                }
                emptyList()
            } else {
                tahapanResult.getOrNull()
            }
        }

        return withContext(Dispatchers.Main) {
            listener.onCompleted()

            tahapanList ?: emptyList()
        }
    }

    override fun onCleared() {
        logEvent("MainViewModel is about to be cleared!")

        asyncJobs.forEach { it.cancel() }
        super.onCleared()
    }
}