package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.net.Uri
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
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.AddCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.EditDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.GetDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.InsertDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah.GetHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah.UpdateHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.DeleteFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.InsertFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.UpdateFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran.GetAllPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran.InsertPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
    // Data Diri
    private val getDataDiriIndenBookingAsyncUseCase: GetDataDiriIndenBookingAsyncUseCase,
    private val insertDataDiriIndenBookingAsyncUseCase: InsertDataDiriIndenBookingAsyncUseCase,
    private val editDataDiriIndenBookingAsyncUseCase: EditDataDiriIndenBookingAsyncUseCase,
    // Foto Identitas / Image Data Diri
    private val insertFotoIdentitasIndenBookingAsyncUseCase: InsertFotoIdentitasIndenBookingAsyncUseCase,
    private val updateFotoIdentitasIndenBookingAsyncUseCase: UpdateFotoIdentitasIndenBookingAsyncUseCase,
    private val deleteFotoIdentitasIndenBookingAsyncUseCase: DeleteFotoIdentitasIndenBookingAsyncUseCase,
    // Pembayaran
    private val getAllPembayaranIndenBookingAsyncUseCase: GetAllPembayaranIndenBookingAsyncUseCase,
    private val insertPembayaranIndenBookingAsyncUseCase: InsertPembayaranIndenBookingAsyncUseCase,
    // Harga Rumah
    private val getHargaRumahIndenBookingAsyncUseCase: GetHargaRumahIndenBookingAsyncUseCase,
    private val updateHargaRumahIndenBookingAsyncUseCase: UpdateHargaRumahIndenBookingAsyncUseCase,
    // Catatan Pembayaran
    private val getCatatanPembayaranAsyncUseCase: GetCatatanPembayaranAsyncUseCase,
    private val addCatatanPembayaranAsyncUseCase: AddCatatanPembayaranAsyncUseCase,
): ViewModel() {

    // Inden Booking
    private val _indenBookings = MutableLiveData<List<IndenBooking>>()
    val indenBookings: LiveData<List<IndenBooking>>
        get() = _indenBookings

    // Data Diri
    private val _dataDiriIndenBooking = MutableLiveData<DataDiri>()
    val dataDiriIndenBooking: LiveData<DataDiri>
        get() = _dataDiriIndenBooking

    // Pembayaran
    private val _pembayaranListIndenBooking = MutableLiveData<List<Pembayaran>>()
    val pembayaranListIndenBooking: LiveData<List<Pembayaran>>
        get() = _pembayaranListIndenBooking

    // Harga Rumah
    private val _hargaRumahIndenBooking = MutableLiveData<HargaRumahIndenBooking>()
    val hargaRumahIndenBooking: LiveData<HargaRumahIndenBooking>
        get() = _hargaRumahIndenBooking

    // Path Foto Inden Booking
    private val _pathFotoIndenBookingLive = MutableLiveData<String?>()
    val pathFotoIndenBookingLive: LiveData<String?>
        get() = _pathFotoIndenBookingLive

    // Catatan Pembayaran
    private val _catatanPembayaran = MutableLiveData<IndenBookingCatatanPembayaran?>()
    val catatanPembayaran: LiveData<IndenBookingCatatanPembayaran?>
        get() = _catatanPembayaran

    // For distribution to fragments
    var namaCostumer = "NULL"
    var currentKeyId = ""

    val showFab = MutableLiveData(false)
    var dataMode = DataMode.ONLINE

    private var readIndenBookingJob: Job? = null
    var writeIndenBookingJob: Job? = null



    fun getListIndenBooking(
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        onProgress()

        readIndenBookingJob = CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllIndenBookingAsyncUseCase.Request(dataMode)
            getAllIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _indenBookings.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Terjadi kesalahan mendapatkan Inden Booking: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }


    /**
     * Data Diri
     */
    fun getDataDiri(
        keyId: String,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetDataDiriIndenBookingAsyncUseCase.Request(keyId)
            getDataDiriIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _dataDiriIndenBooking.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan data diri: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }

    fun insertDataDiri(
        dataDiri: DataDiri,
        onProgress: () -> Unit = {},
        onComplete: (generatedKeyId: String?) -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = InsertDataDiriIndenBookingAsyncUseCase.Request(dataDiri)
            insertDataDiriIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete(it)
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal: ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun updateDataDiri(
        keyId: String,
        newDataDiri: DataDiri,
        onProgress: () -> Unit = {},
        onComplete: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = EditDataDiriIndenBookingAsyncUseCase.Request(keyId, newDataDiri)
            editDataDiriIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mengubah data diri : ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    /**
     * Foto Identitas / Image Data Diri
     */
    fun insertFotoIdentitas(
        keyId: String,
        uri: Uri,
        onProgress: () -> Unit = {},
        onComplete: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = InsertFotoIdentitasIndenBookingAsyncUseCase.Request(keyId, uri)
            insertFotoIdentitasIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Terjadi kesalahan: ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun updateFotoIdentitas(
        keyId: String,
        uri: Uri,
        onProgress: () -> Unit = {},
        onComplete: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = UpdateFotoIdentitasIndenBookingAsyncUseCase.Request(keyId, uri)
            updateFotoIdentitasIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Terjadi kesalahan: ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun deleteFotoIdentitas(
        keyId: String,
        uri: Uri,
        onProgress: () -> Unit = {},
        onComplete: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = DeleteFotoIdentitasIndenBookingAsyncUseCase.Request(keyId, uri)
            deleteFotoIdentitasIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Terjadi kesalahan: ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    /**
     * Harga Rumah
     */
    fun getHargaRumah(
        keyId: String,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetHargaRumahIndenBookingAsyncUseCase.Request(keyId)
            getHargaRumahIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _hargaRumahIndenBooking.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan Harga Rumah: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }

    fun updateHargaRumah(
        keyId: String,
        newHargaRumah: HargaRumahIndenBooking,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = UpdateHargaRumahIndenBookingAsyncUseCase.Request(keyId, newHargaRumah)
            updateHargaRumahIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menubah harga rumah: ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    /**
     * Pembayaran
     */
    fun getAllPembayaran(
        keyId: String,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllPembayaranIndenBookingAsyncUseCase.Request(keyId)
            getAllPembayaranIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _pembayaranListIndenBooking.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan pembayaran: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }

    fun insertPembayaran(
        keyId: String,
        pembayaran: Pembayaran,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = InsertPembayaranIndenBookingAsyncUseCase.Request(keyId, pembayaran)
            insertPembayaranIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menambahkan: ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    /**
     * Catatan Pembayaran
     */
    fun getCatatanPembayaran(
        keyId: String,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetCatatanPembayaranAsyncUseCase.IndenBookingRequest(keyId, dataMode)
            getCatatanPembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _catatanPembayaran.postValue(it as IndenBookingCatatanPembayaran?)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan catatan pembayaran : ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun insertCatatanPembayaran(
        catatanPembayaran: IndenBookingCatatanPembayaran,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = AddCatatanPembayaranAsyncUseCase.Request(
                CatatanPembayaran.INDEN_BOOKING, catatanPembayaran
            )
            addCatatanPembayaranAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menambahkan catatan pembayaran : ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    fun updatePathFotoIndenBooking(path: String) {
        _pathFotoIndenBookingLive.value = path
    }

    override fun onCleared() {
        super.onCleared()

        readIndenBookingJob?.cancel()
    }
}