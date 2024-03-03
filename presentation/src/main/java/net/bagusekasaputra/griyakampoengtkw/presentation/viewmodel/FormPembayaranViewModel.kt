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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetSinglePembayaranByKavlingAndTerminAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.InsertPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.UpdatePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan.AddNewTambahLuasanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan.DeleteByIdTambahLuasanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan.GetAllPembayaranTambahLuasanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetUserPaymentStatusWithSpecifiedInvoiceAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran.GetStatusPembayaranKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.AddPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.combineWith
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import java.util.UUID
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
    private val getSinglePembayaranUseCase: GetSinglePembayaranByKavlingAndTerminAsyncUseCase,
    private val updatePembayaranUseCase: UpdatePembayaranAsyncUseCase,
    private val insertPembayaranUseCase: InsertPembayaranAsyncUseCase,
    private val addPembayaranUseCase: AddPembayaranUseCase,
    private val deletePembayaranAsyncUseCase: DeletePembayaranAsyncUseCase,
    // User Payment Status
    private val getUserPaymentStatusWithInvoice: GetUserPaymentStatusWithSpecifiedInvoiceAsyncUseCase,
    // Ambil Kuitansi
    private val insertAmbilKuitansiAsyncUseCase: InsertAmbilKuitansiAsyncUseCase,
    // Kavling Catatan Pembayaran
    private val getCatatanPembayaranAsyncUseCase: GetCatatanPembayaranAsyncUseCase,
    private val addCatatanPembayaranAsyncUseCase: AddCatatanPembayaranAsyncUseCase,
    private val deleteCatatanPembayaranAsyncUseCase: DeleteCatatanPembayaranAsyncUseCase,
    // Tambah Luasan
    private val getAllTambahanLuasPembayaran: GetAllPembayaranTambahLuasanAsyncUseCase,
    private val addNewTambahLuasanUseCase: AddNewTambahLuasanPembayaranAsyncUseCase,
    private val deleteTambahLuasanUseCase: DeleteByIdTambahLuasanPembayaranAsyncUseCase,
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
    @Deprecated("Migrate to pembayaranList instead!")
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

    // Pembayaran List
    private val _pembayaranList = MutableLiveData<UiState<List<Pembayaran>?>>()
    val pembayaranList: LiveData<UiState<List<Pembayaran>?>>
        get() = _pembayaranList

    // User Payment Status List
    private val _userPaymentStatus = MutableLiveData<List<List<String>>?>()
    val userPaymentStatus: LiveData<List<List<String>>?>
        get() = _userPaymentStatus

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

    // Pembayaran (for edit mode)
    private val _pembayaran = MutableLiveData<UiState<Pembayaran?>>()
    val pembayaran: LiveData<UiState<Pembayaran?>>
        get() = _pembayaran

    // Tambahan Luasan Pembayarna
    private val _tambahanLuasPembayaran = MutableLiveData<UiState<List<PembayaranTambahLuasan>?>>()
    val tambahanLuasPembayaran: LiveData<UiState<List<PembayaranTambahLuasan>?>>
        get() = _tambahanLuasPembayaran

    private val _tambahanLuasItem = MutableLiveData<UiState<PembayaranTambahLuasan?>>()
    val tambahanLuasItem: LiveData<UiState<PembayaranTambahLuasan?>>
        get() = _tambahanLuasItem

    // Sync Request
    private val _syncRequests = MutableLiveData<Array<Int>?>(null)
    val syncRequests: LiveData<Array<Int>?>
        get() = _syncRequests

    fun requestSync(vararg requestCode: Int) {
        _syncRequests.value = requestCode.toTypedArray()
    }


    var currentKavlingKode: String? = null
    var currentTermin: String? = null
    var currentTambahLuasanId: String? = null
    var formIsEditMode = false
    var dataMode = DataMode.ONLINE
    var isFullScreenTable = false

    var writeBaselinePembayaranJob: Job? = null
    var readPembayaranBulananJob: Job? = null

    var readTambahanLuasPembayaranJob: Job? = null
    var writeTambahanLuasPembayaranJob: Job? = null

    var bulanRekapUser = 1
    var tahunRekapUser = 2022

    private val isFinishOperation = MutableLiveData<Boolean>()
    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)
    // The list of Coroutines/Flows job that need to be cleared on
    // the onCleared() callback. See below.
    private val asyncJobs = ArrayList<Job>()


    // Change the TableView on FormPembayaran fragment
    val tableTypeLive = MutableLiveData(TablePembayaranType.FORM_PEMBAYARAN)
    fun setTableType(type: TablePembayaranType) {
        tableTypeLive.value = type
    }

    // Fab Tambah Luasan
    private val _fabTambahLuasanVisibility = MutableLiveData<Boolean>(false)
    val fabTambahLuasanVisibility: LiveData<Boolean> get() = _fabTambahLuasanVisibility
    fun setFabTambahLuasanVisibility(visible: Boolean) {
        _fabTambahLuasanVisibility.value = visible
    }



    /**
     * Pembayaran bulanan
     */
    @Deprecated("Will soon removed! Migrate to fetchPembayaranData() as soon as possible.")
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
     * Fetch [_pembayaranBulanansLive], [_baselinePembayaranLive], and [_pembayaranList].
      */
    fun fetchPembayaranData(kavling: String) {
        _pembayaranList.value = UiState.Loading()

        readPembayaranBulananJob?.cancel()

        readPembayaranBulananJob = viewModelScope.launch(Dispatchers.IO) {
            val request = GetListPembayaranBulananAsyncUseCase.Request(kavling, dataMode)
            getListPembayaranBulananAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _pembayaranBulanansLive.postValue(it)

                    setBaselinePembayaran(it?.get(0)?.baselinePembayaran)

                    val list = it?.let { _ ->
                        PembayaranBulanan.getPembayaranList(it)
                    }
                    _pembayaranList.postValue(UiState.Success(list))
                }
                result.onFailure {
                    _pembayaranList.postValue(UiState.Failure("Gagal mendapatkan List Pembayaran Bulanan : ${it.localizedMessage}"))
                }
            }
        }
    }


    /**
     * Pembayaran
     */
    fun getSinglePembayaran(kavling: String, termin: String) {
        _pembayaran.value = UiState.Loading()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetSinglePembayaranByKavlingAndTerminAsyncUseCase.KavlingRequest(kavling, termin)
            getSinglePembayaranUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _pembayaran.postValue(UiState.Success(it))
                }
                result.onFailure {
                    _pembayaran.postValue(UiState.Failure("Gagal mendapatkan pembayaran $termin : ${it.localizedMessage}"))
                }
            }
        }
    }

    @Deprecated("Migrated to insertPembayaran().")
    fun addPembayaran(
        kavlingKode: String,
        pembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = AddPembayaranUseCase.Request(kavlingKode, pembayaran)

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

    fun addPembayaran(kavlingKode: String, pembayaran: Pembayaran) {
        viewModelScope.launch(Dispatchers.IO) {
            _insertPembayaranOperation.update { UiState.Loading() }

            val request = InsertPembayaranAsyncUseCase.KavlingRequest(kavlingKode, pembayaran)
            insertPembayaranUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _insertPembayaranOperation.update { UiState.Success() }
                }
                result.onFailure {  throwable ->
                    _insertPembayaranOperation.update { UiState.Failure("Gagal menambahkan pembayaran : ${throwable.localizedMessage}") }
                }
            }
        }
    }

    fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran
    ) {
        _ubahPembayaranOperation.value = UiState.Loading()

        viewModelScope.launch(Dispatchers.IO) {
            val kavlingRequest = UpdatePembayaranAsyncUseCase.KavlingRequest(
                kavlingKode, oldPembayaran, newPembayaran
            )
            updatePembayaranUseCase.execute(kavlingRequest).collect { result ->
                result.onSuccess {
                    _ubahPembayaranOperation.postValue(UiState.Success())
                }
                result.onFailure {
                    _ubahPembayaranOperation.postValue(UiState.Failure(it.message))
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

    fun getUserPaymentStatusWithInvoice(
        bulanAngsuran: BulanAngsuran,
        onSuccess: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        val request = GetUserPaymentStatusWithSpecifiedInvoiceAsyncUseCase.Request(bulanAngsuran)

        viewModelScope.launch(Dispatchers.IO) {
            getUserPaymentStatusWithInvoice.execute(request).collect { result ->
                result.onSuccess {
                    _userPaymentStatus.postValue(result.getOrNull() ?: emptyList())
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure(it.message ?: "Terjadi kesalahan!")
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

    /**
     * Pembayaran Tambahan Luasan
     */
    fun getAllTambahanLuasPembayaran(kavlingKode: String) {
        readTambahanLuasPembayaranJob?.cancel()

        _tambahanLuasPembayaran.value = UiState.Loading()

        val request = GetAllPembayaranTambahLuasanAsyncUseCase.Request(kavlingKode)
        readTambahanLuasPembayaranJob = viewModelScope.launch(Dispatchers.IO) {
            getAllTambahanLuasPembayaran.execute(request).collect { result ->
                result.onSuccess {
                    _tambahanLuasPembayaran.postValue(UiState.Success(it))
                }

                result.onFailure {
                    _tambahanLuasPembayaran.postValue(UiState.Failure(
                        failMsg = "Gagal mendapatkan list tambahan pembayaran: ${it.message}",
                    ))
                }
            }
        }
    }

    fun getTambahanLuasPembayaran(kavling: String, id: String) {
        readTambahanLuasPembayaranJob?.cancel()

        _tambahanLuasItem.value = UiState.Loading()

        // TODO: Requests
        readTambahanLuasPembayaranJob = viewModelScope.launch(Dispatchers.IO) {
            delay(4000)
            _tambahanLuasItem.postValue(UiState.Success(
                PembayaranTambahLuasan(
                    id = id,
                    kavling = kavling,
                    sudahAmbilKuitansi = false,
                    fotoUri = "",
                    tanggal = "06/06/2006",
                    jumlahUang = 5_250_000L,
                    keterangan = "tes 5",
                    timeMillis = System.currentTimeMillis(),
                )
            ))
        }
    }

    fun addTambahLuasanPembayaran(entity: PembayaranTambahLuasan) {
        writeTambahanLuasPembayaranJob?.cancel()

        _addTambahLuasanOperation.value = UiState.Loading()

        val request = AddNewTambahLuasanPembayaranAsyncUseCase.Request(entity)
        writeTambahanLuasPembayaranJob = viewModelScope.launch(Dispatchers.IO) {
            addNewTambahLuasanUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _addTambahLuasanOperation.postValue(UiState.Success(null))
                }

                result.onFailure {
                    _addTambahLuasanOperation.postValue(UiState.Failure(it.message))
                }
            }
        }
    }

    fun deleteTambahLuasanPembayaran(
        kavling: String,
        id: String,
        onSuccess: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        writeTambahanLuasPembayaranJob?.cancel()

        val request = DeleteByIdTambahLuasanPembayaranAsyncUseCase.Request(kavling, id)
        writeTambahanLuasPembayaranJob = viewModelScope.launch(Dispatchers.IO) {
            deleteTambahLuasanUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure(it.message ?: "Unknown error deleting Tambah Luasan Pembayaran!")
                    }
                }
            }
        }
    }

    /**
     * Operation observers
     */
    /**
     * Pembayaran Kavling
     */
    private val _ubahPembayaranOperation = MutableLiveData<UiState<Nothing?>>()
    val ubahPembayaranOperation: LiveData<UiState<Nothing?>>
        get() = _ubahPembayaranOperation

    private val _insertPembayaranOperation = MutableStateFlow<UiState<Nothing?>?>(null)
    val insertPembayaranOperation = _insertPembayaranOperation.asStateFlow()

    private val _addTambahLuasanOperation = MutableLiveData<UiState<Nothing?>>()
    val addTambahLuasanOperation: LiveData<UiState<Nothing?>>
        get() = _addTambahLuasanOperation
}

object PembayaranSyncRequest {
    const val HARGA_KAVLING = 0
    const val BASELINE_PEMBAYARAN = 1
    const val TABEL_PEMBAYARAN = 2
    const val CATATAN_PEMBAYARAN = 3
    const val STATUS_PEMBAYARAN = 4
    const val TAMBAHAN_PEMBAYARAN = 5

    val ALL = intArrayOf(HARGA_KAVLING, BASELINE_PEMBAYARAN,
        TABEL_PEMBAYARAN, CATATAN_PEMBAYARAN, STATUS_PEMBAYARAN, TAMBAHAN_PEMBAYARAN)
}