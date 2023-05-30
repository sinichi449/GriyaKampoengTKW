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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran.GetAllPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.EditDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.GetDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.InsertDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.DeleteFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.GetFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.InsertFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.UpdateFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
    // Data Diri
    private val getDataDiriIndenBookingAsyncUseCase: GetDataDiriIndenBookingAsyncUseCase,
    private val insertDataDiriIndenBookingAsyncUseCase: InsertDataDiriIndenBookingAsyncUseCase,
    private val editDataDiriIndenBookingAsyncUseCase: EditDataDiriIndenBookingAsyncUseCase,
    // Foto Identitas / Image Data Diri
    private val getFotoIdentitasIndenBookingAsyncUseCase: GetFotoIdentitasIndenBookingAsyncUseCase,
    private val insertFotoIdentitasIndenBookingAsyncUseCase: InsertFotoIdentitasIndenBookingAsyncUseCase,
    private val updateFotoIdentitasIndenBookingAsyncUseCase: UpdateFotoIdentitasIndenBookingAsyncUseCase,
    private val deleteFotoIdentitasIndenBookingAsyncUseCase: DeleteFotoIdentitasIndenBookingAsyncUseCase,
    // Pembayaran
    private val getAllPembayaranIndenBookingAsyncUseCase: GetAllPembayaranIndenBookingAsyncUseCase,
    // Harga Rumah
    private val getHargaRumahIndenBookingAsyncUseCase: GetHargaRumahIndenBookingAsyncUseCase,
): ViewModel() {

    private val _indenBookings = MutableLiveData<List<IndenBooking>>()
    val indenBookings: LiveData<List<IndenBooking>>
        get() = _indenBookings

    private val _dataDiriIndenBooking = MutableLiveData<DataDiri>()
    val dataDiriIndenBooking: LiveData<DataDiri>
        get() = _dataDiriIndenBooking

    private val _fotoIdentitasUri = MutableLiveData<Uri>()
    val fotoIdentitasUri: LiveData<Uri>
        get() = _fotoIdentitasUri

    private val _pembayaranListIndenBooking = MutableLiveData<List<Pembayaran>>()
    val pembayaranListIndenBooking: LiveData<List<Pembayaran>>
        get() = _pembayaranListIndenBooking

    private val _hargaRumahIndenBooking = MutableLiveData<HargaRumahIndenBooking>()
    val hargaRumahIndenBooking: LiveData<HargaRumahIndenBooking>
        get() = _hargaRumahIndenBooking

    private val _pathFotoIndenBookingLive = MutableLiveData<String?>()
    val pathFotoIndenBookingLive: LiveData<String?>
        get() = _pathFotoIndenBookingLive


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
    fun getFotoIdentitas(
        keyId: String,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetFotoIdentitasIndenBookingAsyncUseCase.Request(keyId)
            getFotoIdentitasIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _fotoIdentitasUri.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan foto identitas: " +
                                "${it.javaClass.simpleName}:${it.message}")
                    }
                }
            }
        }
    }

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


    fun updatePathFotoIndenBooking(path: String) {
        _pathFotoIndenBookingLive.value = path
    }

    override fun onCleared() {
        super.onCleared()

        readIndenBookingJob?.cancel()
    }
}