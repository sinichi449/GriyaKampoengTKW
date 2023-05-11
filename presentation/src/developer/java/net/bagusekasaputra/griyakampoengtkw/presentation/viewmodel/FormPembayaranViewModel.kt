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
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.GetBaselinePembayaranByKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import javax.inject.Inject

/**
 * Soon, all "Pembayaran" related data will be moved here.
 */
@HiltViewModel
class FormPembayaranViewModel @Inject constructor(
    private val getBaselinePembayaranByKavlingAsyncUseCase: GetBaselinePembayaranByKavlingAsyncUseCase,
    private val setBaselinePembayaranAsyncUseCase: SetBaselinePembayaranAsyncUseCase,
    private val getListPembayaranBulananAsyncUseCase: GetListPembayaranBulananAsyncUseCase,
): ViewModel() {

    private val _baselinePembayaranLive = MutableLiveData<BaselinePembayaran?>()
    val baselinePembayaranLive: LiveData<BaselinePembayaran?>
        get() = _baselinePembayaranLive
    private fun setBaselinePembayaran(baselinePembayaran: BaselinePembayaran?) {
        _baselinePembayaranLive.postValue(baselinePembayaran)
    }


    private val _pembayaranBulanansLive = MutableLiveData<List<PembayaranBulanan>?>(null)
    val pembayaranBulanansLive: LiveData<List<PembayaranBulanan>?>
        get() = _pembayaranBulanansLive

    var dataMode = DataMode.ONLINE

    private var readBaselinePembayaranJob: Job? = null
    var writeBaselinePembayaranJob: Job? = null

    var readPembayaranBulananJob: Job? = null

    // Change the TableView on FormPembayaran fragment
    val tableTypeLive = MutableLiveData(TablePembayaranType.FORM_PEMBAYARAN)
    fun setTableType(type: TablePembayaranType) {
        tableTypeLive.value = type
    }


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

    fun getBaselinePembayaran(
        kavling: String,
        onLoading: () -> Unit,
        onComplete: () -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        readBaselinePembayaranJob?.cancel()

        onLoading()

        readBaselinePembayaranJob = viewModelScope.launch {
            val request = GetBaselinePembayaranByKavlingAsyncUseCase.Request(kavling, dataMode)

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

    fun hitungAngsuranPerBulan(hargaKavling: HargaKavling, timeFrame: Int, opsiTimeframe: String): Double {
        return when (opsiTimeframe) {
            "Tahun" -> {
                val tahunToBulan = timeFrame * 12

                BaselinePembayaran.hitungAngsuranPerBulan(hargaKavling, tahunToBulan)
            }
            "Bulan" -> {
                BaselinePembayaran.hitungAngsuranPerBulan(hargaKavling, timeFrame)
            }
            else -> {
                throw Exception("Opsi timeframe tidak dikenali: $opsiTimeframe")
            }
        }
    }


    enum class TablePembayaranType {
        FORM_PEMBAYARAN, PEMBAYARAN_BULANAN
    }
}