package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
    private val getDataDiriIndenBookingAsyncUseCase: GetDataDiriIndenBookingAsyncUseCase,
    private val getFotoIdentitasIndenBookingAsyncUseCase: GetFotoIdentitasIndenBookingAsyncUseCase,
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


    fun updatePathFotoIndenBooking(path: String) {
        _pathFotoIndenBookingLive.value = path
    }

    override fun onCleared() {
        super.onCleared()

        readIndenBookingJob?.cancel()
    }
}