package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(

): ViewModel() {

    private val _uiModelIndenBooking = MutableLiveData<List<IndenBooking>>()
    val uiModelIndenBooking: LiveData<List<IndenBooking>>
        get() = _uiModelIndenBooking


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

        readIndenBookingJob = viewModelScope.launch {
            delay(3000L)

            val listIndenBooking = IndenBooking.getDummyModels()
            _uiModelIndenBooking.postValue(listIndenBooking)

            withContext(Dispatchers.Main) {
                onComplete()
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