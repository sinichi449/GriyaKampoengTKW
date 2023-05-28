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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi.InsertAmbilKuitansiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.GetBaselinePembayaranByKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran.GetStatusPembayaranKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.combineWith
import javax.inject.Inject

/**
 * Soon, all "Pembayaran" related data will be moved here.
 */
@HiltViewModel
class FormPembayaranViewModel @Inject constructor(
    private val getBaselinePembayaranByKavlingAsyncUseCase: GetBaselinePembayaranByKavlingAsyncUseCase,
    private val setBaselinePembayaranAsyncUseCase: SetBaselinePembayaranAsyncUseCase,
    private val getListPembayaranBulananAsyncUseCase: GetListPembayaranBulananAsyncUseCase,
    private val getStatusPembayaranKavlingAsyncUseCase: GetStatusPembayaranKavlingAsyncUseCase,
    private val insertAmbilKuitansiAsyncUseCase: InsertAmbilKuitansiAsyncUseCase,
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

    // Status Pembayaran
    private val _statusPembayaranLive = MutableLiveData<StatusPembayaran?>(null)
    val statusPembayaranLive: LiveData<StatusPembayaran?>
        get() = _statusPembayaranLive


    // Combine
    val baselineAndFullPembayaran = _baselinePembayaranLive
        .combineWith(_fullPembayaransLive) { baselinePembayaran, pembayarans ->
            Pair(baselinePembayaran, pembayarans)
        }


    var currentKavlingKode: String? = null
    var dataMode = DataMode.ONLINE
    var isFullScreenTable = false

    private var readBaselinePembayaranJob: Job? = null
    var writeBaselinePembayaranJob: Job? = null

    var readPembayaranBulananJob: Job? = null

    // Change the TableView on FormPembayaran fragment
    val tableTypeLive = MutableLiveData(TablePembayaranType.FORM_PEMBAYARAN)
    fun setTableType(type: TablePembayaranType) {
        tableTypeLive.value = type
    }

    // Sync listener
    val needSyncPembayaran = MutableLiveData<Boolean>(false)


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

    fun insertAmbilKuitansi(ambilKuitansi: AmbilKuitansi,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        CoroutineScope(Dispatchers.IO).launch {
            val request = InsertAmbilKuitansiAsyncUseCase.Request(ambilKuitansi)
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

    fun getSudahIsiFotoPembayaranTermins(): Array<String> {
        val terminList = ArrayList<String>()

        _fullPembayaransLive.value?.forEach { pembayaran ->
            if (pembayaran.sudahIsiFotoPembayaran) {
                terminList.add(pembayaran.termin)
            }
        }

        Log.d("TERMINS", "Termin is: $terminList")

        return terminList.toTypedArray()
    }

    fun getBelumIsiFotoPembayaranTermins(): Array<String> {
        val terminList = ArrayList<String>()

        _fullPembayaransLive.value?.forEach { pembayaran ->
            if (!pembayaran.sudahIsiFotoPembayaran) {
                terminList.add(pembayaran.termin)
            }
        }

        Log.d("TERMINS", "Termin is: $terminList")

        return terminList.toTypedArray()
    }

    fun getTerminFromListPembayaran(): Array<String> {
        val terminList = ArrayList<String>()

        _fullPembayaransLive.value?.forEach { pembayaran ->
            terminList.add(pembayaran.termin)
        }

        Log.d("TERMINS", "Termin is: $terminList")

        // We need to convert into an Array ... How botherful.
        return terminList.toTypedArray()
    }


    enum class TablePembayaranType {
        FORM_PEMBAYARAN, PEMBAYARAN_BULANAN
    }

    enum class JenisPembayaran(val text: String) {
        ITJ("ITJ"),
        DP("DP"),
        TERMIN("Termin"),
    }


    fun getNextPembayaranSequence(jenisPembayaran: JenisPembayaran): String {
        // Check if not null listPembayaran.
        // If null returns "1"
        val listPembayaran = _fullPembayaransLive.value

        if (listPembayaran != null) {
            // Check if any requested jenis pembayaran Exists
            val requestedJenisPembayaranList = listPembayaran.filter { it.termin.startsWith(jenisPembayaran.text) }
            return if (requestedJenisPembayaranList.isNotEmpty()) {
                // If exists, then get the last index of the requested pembayaran.
                // I speculate that the UseCase already do the sorting, so
                // the last of Any Pembayaran Sequence should be on the last index.
                val lastPembayaran = requestedJenisPembayaranList.last()

                // +1 on the last number of urutan
                val urutan = lastPembayaran.getUrutan()
                urutan.plus(1).toString()
            } else {
                "1"
            }
        } else {
            return "1"
        }
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
}