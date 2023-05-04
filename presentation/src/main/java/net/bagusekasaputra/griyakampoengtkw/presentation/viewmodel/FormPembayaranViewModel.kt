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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.GetBaselinePembayaranByKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import javax.inject.Inject

/**
 * Soon, all "Pembayaran" related data will be moved here.
 */
@HiltViewModel
class FormPembayaranViewModel @Inject constructor(
    private val getBaselinePembayaranByKavlingAsyncUseCase: GetBaselinePembayaranByKavlingAsyncUseCase,
    private val setBaselinePembayaranAsyncUseCase: SetBaselinePembayaranAsyncUseCase,
): ViewModel() {

    private val _baselinePembayaranLive = MutableLiveData<BaselinePembayaran?>()
    val baselinePembayaranLive: LiveData<BaselinePembayaran?>
        get() = _baselinePembayaranLive

    private val _jumlahAngsuranPerBulanLive = MutableLiveData<Double?>()
    val jumlahAngsuranPerBulanLive: LiveData<Double?>
        get() = _jumlahAngsuranPerBulanLive

    private var readBaselinePembayaranJob: Job? = null
    var writeBaselinePembayaranJob: Job? = null



    fun getBaselinePembayaran(
        kavling: String,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        readBaselinePembayaranJob?.cancel()

        onLoading()

        readBaselinePembayaranJob = viewModelScope.launch {
            val request = GetBaselinePembayaranByKavlingAsyncUseCase.Request(kavling)

            getBaselinePembayaranByKavlingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _baselinePembayaranLive.postValue(it)

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }

                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan baseline pembayaran: ${it.message}")
                        onComplete()
                    }
                }
            }
        }
    }

    fun insertBaselinePembayaran(
        kavling: String,
        jumlahUang: Long,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        writeBaselinePembayaranJob?.cancel()

        onLoading()

        writeBaselinePembayaranJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d("DEBUG_ME", "BaselinePembayaran: Sending to use case")
            val request = SetBaselinePembayaranAsyncUseCase.Request(
                BaselinePembayaran(
                    kavling = kavling,
                    jumlahUang = jumlahUang,
                )
            )
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
}