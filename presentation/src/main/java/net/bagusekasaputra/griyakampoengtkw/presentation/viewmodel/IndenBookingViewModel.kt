package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.AddNewIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.DeleteSingleIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
    private val addNewIndenBookingAsyncUseCase: AddNewIndenBookingAsyncUseCase,
    private val deleteSingleIndenBookingAsyncUseCase: DeleteSingleIndenBookingAsyncUseCase,
): ViewModel() {

    private val _listIndenBookingLive = MutableLiveData<List<IndenBooking>?>()
    val listIndenBookingLive: LiveData<List<IndenBooking>?>
        get() = _listIndenBookingLive

    private val _pathFotoIndenBookingLive = MutableLiveData<String?>()
    val pathFotoIndenBookingLive: LiveData<String?>
        get() = _pathFotoIndenBookingLive

    val showFab = MutableLiveData(false)
    var dataMode = DataMode.ONLINE

    private var readIndenBookingJob: Job? = null
    var writeIndenBookingJob: Job? = null

    fun getListIndenBooking(onProgress: () -> Unit, onComplete: () -> Unit, onFailure: (msg: String) -> Unit) {
        onProgress()

        readIndenBookingJob = viewModelScope.launch {
            val request = GetAllIndenBookingAsyncUseCase.Request(dataMode)

            getAllIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _listIndenBookingLive.postValue(it)
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan List Inden Booking: ${it.message}")
                        onComplete()
                    }
                }
            }
        }
    }

    fun insertIndenBooking(
        indenBooking: IndenBooking,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        onProgress()

        writeIndenBookingJob = CoroutineScope(Dispatchers.IO).launch {
            val request = AddNewIndenBookingAsyncUseCase.Request(indenBooking)
            addNewIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menambahkan Inden Booking: ${it.message}")
                    }
                }
            }
        }
    }

    fun deleteIndenBooking(
        indenBooking: IndenBooking,
        onProgress: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        writeIndenBookingJob?.cancel()

        onProgress()

        writeIndenBookingJob = CoroutineScope(Dispatchers.IO).launch {
            val request = DeleteSingleIndenBookingAsyncUseCase.Request(indenBooking)
            deleteSingleIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }

                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menghapus Inden Booking: ${it.message}")
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