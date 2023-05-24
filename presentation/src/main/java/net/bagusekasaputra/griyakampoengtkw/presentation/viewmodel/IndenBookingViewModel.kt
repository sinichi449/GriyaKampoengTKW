package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
): ViewModel() {

    private val _indenBookings = MutableLiveData<List<IndenBooking>>()
    val indenBookings: LiveData<List<IndenBooking>>
        get() = _indenBookings


    private val _pathFotoIndenBookingLive = MutableLiveData<String?>()
    val pathFotoIndenBookingLive: LiveData<String?>
        get() = _pathFotoIndenBookingLive


    // For distribution to fragments
    var namaCostumer = "NULL"

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


    fun updatePathFotoIndenBooking(path: String) {
        _pathFotoIndenBookingLive.value = path
    }

    override fun onCleared() {
        super.onCleared()

        readIndenBookingJob?.cancel()
    }
}