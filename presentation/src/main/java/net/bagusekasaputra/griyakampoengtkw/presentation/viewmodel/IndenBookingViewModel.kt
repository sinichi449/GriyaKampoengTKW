package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import javax.inject.Inject

@HiltViewModel
class IndenBookingViewModel @Inject constructor(
    private val getAllIndenBookingAsyncUseCase: GetAllIndenBookingAsyncUseCase,
): ViewModel() {

    private val _listIndenBookingLive = MutableLiveData<List<IndenBooking>?>()
    val listIndenBookingLive: LiveData<List<IndenBooking>?>
        get() = _listIndenBookingLive

    val showFab = MutableLiveData(false)

    private var indenBookingJob: Job? = null


    fun getListIndenBooking(onComplete: () -> Unit, onFailure: (msg: String) -> Unit) {
        indenBookingJob = viewModelScope.launch {
            val request = GetAllIndenBookingAsyncUseCase.Request

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


    override fun onCleared() {
        super.onCleared()

        indenBookingJob?.cancel()
    }
}