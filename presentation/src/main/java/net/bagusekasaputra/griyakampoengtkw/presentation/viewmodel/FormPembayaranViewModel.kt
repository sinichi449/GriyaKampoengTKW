package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi.InsertAmbilKuitansiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.AddCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.DeleteCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.DeletePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran.GetStatusPembayaranKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.AddPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.combineWith
import javax.inject.Inject

/**
 * Soon, all "Pembayaran" related data will be moved here.
 */
@HiltViewModel
class FormPembayaranViewModel @Inject constructor(
    // Baseline Pembayaran
    private val setBaselinePembayaranAsyncUseCase: SetBaselinePembayaranAsyncUseCase,
    // Pembayaran Bulanan
    private val getListPembayaranBulananAsyncUseCase: GetListPembayaranBulananAsyncUseCase,
    private val getStatusPembayaranKavlingAsyncUseCase: GetStatusPembayaranKavlingAsyncUseCase,
    // Pembayaran
    private val addPembayaranUseCase: AddPembayaranUseCase,
    private val deletePembayaranAsyncUseCase: DeletePembayaranAsyncUseCase,
    // Ambil Kuitansi
    private val insertAmbilKuitansiAsyncUseCase: InsertAmbilKuitansiAsyncUseCase,
    // Kavling Catatan Pembayaran
    private val getCatatanPembayaranAsyncUseCase: GetCatatanPembayaranAsyncUseCase,
    private val addCatatanPembayaranAsyncUseCase: AddCatatanPembayaranAsyncUseCase,
    private val deleteCatatanPembayaranAsyncUseCase: DeleteCatatanPembayaranAsyncUseCase,
): ViewModel() {

    // Pembayaran Bulanan
    private val _pembayaranBulanansLive = MutableLiveData<List<PembayaranBulanan>?>(null)
    val pembayaranBulanansLive: LiveData<List<PembayaranBulanan>?>
        get() = _pembayaranBulanansLive

    // Baseline Pembayaran
    private val _baselinePembayaranLive = MutableLiveData<BaselinePembayaran?>()
    val baselinePembayaranLive: LiveData<BaselinePembayaran?>
        get() = _baselinePembayaranLive
    private fun setBaselinePembayaran(baselinePembayaran: BaselinePembayaran?) {
        _baselinePembayaranLive.postValue(baselinePembayaran)
    }

    // Full Pembayaran
    private val _fullPembayaransLive = MutableLiveData<List<Pembayaran>?>(null)
    val fullPembayaransLive: LiveData<List<Pembayaran>?>
        get() = _fullPembayaransLive
    private fun setFullPembayaran(pembayaranBulanans: List<PembayaranBulanan>?) {
        val pembayarans = mutableListOf<Pembayaran>()
        pembayaranBulanans?.forEach {
            pembayarans.addAll(it.listPembayaran)
        }

        if (pembayarans.isEmpty()) {
            _fullPembayaransLive.postValue(null)
        } else {
            _fullPembayaransLive.postValue(pembayarans)
        }
    }

    // Status Pembayaran
    private val _statusPembayaranLive = MutableLiveData<StatusPembayaran?>(null)
    val statusPembayaranLive: LiveData<StatusPembayaran?>
        get() = _statusPembayaranLive

    // Catatan Pembayaran
    private val _catatanPembayaranLive = MutableLiveData<KavlingCatatanPembayaran?>()
    val catatanPembayaranLive: LiveData<KavlingCatatanPembayaran?>
        get() = _catatanPembayaranLive

    // Basline Pembayaran + Full Pembayaran
    val baselineAndFullPembayaran = _baselinePembayaranLive
        .combineWith(_fullPembayaransLive) { baselinePembayaran, pembayarans ->
            Pair(baselinePembayaran, pembayarans)
        }

    // Operation Observer
    private val isFinishOperation = MutableLiveData<Boolean>()


    var currentKavlingKode: String? = null
    var dataMode = DataMode.ONLINE
    var isFullScreenTable = false

    private var readBaselinePembayaranJob: Job? = null
    var writeBaselinePembayaranJob: Job? = null

    var readPembayaranBulananJob: Job? = null

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)
    // The list of Coroutines/Flows job that need to be cleared on
    // the onCleared() callback. See below.
    private val asyncJobs = ArrayList<Job>()


    // Change the TableView on FormPembayaran fragment
    val tableTypeLive = MutableLiveData(TablePembayaranType.FORM_PEMBAYARAN)
    fun setTableType(type: TablePembayaranType) {
        tableTypeLive.value = type
    }



    /**
     * Pembayaran bulanan
     */
    fun getListPembayaranBulanan(
        kavling: String,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        readPembayaranBulananJob?.cancel()

        onLoading()

        readPembayaranBulananJob = viewModelScope.launch {
            val request = GetListPembayaranBulananAsyncUseCase.Request(kavling, dataMode)
            getListPembayaranBulananAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _pembayaranBulanansLive.postValue(it)

                    setBaselinePembayaran(it?.get(0)?.baselinePembayaran)
                    setFullPembayaran(it)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }

                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan List Pembayaran Bulanan : ${it.message}")
                    }
                }
            }
        }
    }


    /**
     * Pembayaran
     */
    fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = AddPembayaranUseCase.Request(kavlingKode, hargaKavling, pembayaran)

            addPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan pembayaran")
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan pembayaran: ${result.exceptionOrNull()?.message?: "null"}")
                    }
                }
            }
        }
    }

    fun deletePembayaran(kavling: String, pembayaran: Pembayaran,
                         onProgress: () -> Unit = {},
                         onSuccess: () -> Unit = {},
                         onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = DeletePembayaranAsyncUseCase.Request(kavling, pembayaran)
            deletePembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menghapus pembayaran: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }


    /**
     * Baseline Pembayaran
     */
    fun insertBaselinePembayaran(
        baselinePembayaran: BaselinePembayaran,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        writeBaselinePembayaranJob?.cancel()

        onLoading()

        writeBaselinePembayaranJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d("DEBUG_ME", "BaselinePembayaran: Sending to use case")
            val request = SetBaselinePembayaranAsyncUseCase.Request(baselinePembayaran)
            setBaselinePembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }

                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("ERROR menambahkan Baseline Pembayaran: ${it.message}")
                    }
                }
            }
        }
    }

    fun getStatusPembayaran(kavling: String,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {}
    ) {
        onLoading()

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetStatusPembayaranKavlingAsyncUseCase.Request(kavling, dataMode)
            getStatusPembayaranKavlingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _statusPembayaranLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan Status Pembayaran: ${it.message}")
                    }
                }
            }
        }
    }


    /**
     * Ambil Kuitansi
     */
    fun insertAmbilKuitansi(standardAmbilKuitansi: StandardAmbilKuitansi,
                            onProgress: () -> Unit = {},
                            onSuccess: () -> Unit = {},
                            onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = InsertAmbilKuitansiAsyncUseCase.StandardRequest(standardAmbilKuitansi)
            insertAmbilKuitansiAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mengubah Sudah Ambil Kuitansi: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }


    /**
     * Catatan Pembayaran
     */
    fun getCatatanPembayaran(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        val request = GetCatatanPembayaranAsyncUseCase.KavlingRequest(kavlingKode, dataMode)

        val gettingCatatanPembayaranJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getCatatanPembayaranAsyncUseCase,
            onSuccess = {
                _catatanPembayaranLive.postValue(it as KavlingCatatanPembayaran?)
            },
            onFailure = {
                onFailure("Gagal mendapatkan catatan pembayaran: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(gettingCatatanPembayaranJob)
    }

    fun addCatatanPembayaran(
        kavlingKode: String,
        catatan: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val kavlingCatatanPembayaran = KavlingCatatanPembayaran(kavlingKode, catatan)
        val request = AddCatatanPembayaranAsyncUseCase.Request(
            CatatanPembayaran.KAVLING,
            kavlingCatatanPembayaran
        )

        CoroutineScope(Dispatchers.IO).launch {
            addCatatanPembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan catatan pembayaran")
                    }
                }
                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan catatan: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteCatatanPembayaran(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val request = DeleteCatatanPembayaranAsyncUseCase.KavlingRequest(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            deleteCatatanPembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _catatanPembayaranLive.postValue(null)

                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus catatan pembayaran")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus catatan pembayaran: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    enum class TablePembayaranType {
        FORM_PEMBAYARAN, PEMBAYARAN_BULANAN
    }

    fun getTerminJumlahUangDibayar(): String? {
        val listPembayaran = _fullPembayaransLive.value

        val listTermins = listPembayaran?.filter { it.termin.startsWith("Termin") }

        return if (listTermins?.isNotEmpty() == true) {
            listTermins.last().jumlahUangDibayar
        } else {
            null
        }
    }
}