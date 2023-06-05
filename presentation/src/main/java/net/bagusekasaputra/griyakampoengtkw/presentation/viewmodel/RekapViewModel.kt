package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetListUnmigratedKavlingsAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarDetailAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarOverviewAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.RekapBesarDetailActivity.FabMode
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.model.RekapDetailTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val getListRekapGlobalAsyncUseCase: GetListRekapGlobalAsyncUseCase,
    private val getRekapBesarOverviewAsyncUseCase: GetRekapBesarOverviewAsyncUseCase,
    private val getRekapBesarDetailAsyncUseCase: GetRekapBesarDetailAsyncUseCase,
    private val getListUnmigratedKavlingsAsyncUseCase: GetListUnmigratedKavlingsAsyncUseCase,
): ViewModel() {

    val currentFragment = MutableLiveData<RekapType>()

    private val _rekapDetailTransportLive = MutableLiveData<RekapDetailTransport>()
    val rekapDetailTransportLive: LiveData<RekapDetailTransport>
        get() = _rekapDetailTransportLive

    private val _listRekapGlobalLive = MutableLiveData(
        listOf(RekapGlobal("-", "-", "-", 0L, 0L))
    )
    val listRekapGlobalLive: LiveData<List<RekapGlobal>>
        get() = _listRekapGlobalLive


    private val _rekapBesarOverviewLive = MutableLiveData<RekapBesarOverview>()
    val rekapBesarOverviewLive: LiveData<RekapBesarOverview>
        get() = _rekapBesarOverviewLive

    private val _rekapBesarDetailLive = MutableLiveData<RekapBesarDetail>()
    val rekapBesarDetailLive: LiveData<RekapBesarDetail>
        get() = _rekapBesarDetailLive


    val isRekapGlobalLoaded = MutableStateFlow(false)
    val isRekapBesarOverviewLoaded = MutableLiveData<Boolean>()
    private val _isRekapBesarDetailLoaded = MutableLiveData<Boolean>()
    val isRekapBesarDetailLoaded: LiveData<Boolean>
        get() = _isRekapBesarDetailLoaded

    val rekapGlobalProgress = getListRekapGlobalAsyncUseCase.progressState.asLiveData(Dispatchers.Default)
    val rekapBesarProgress = getRekapBesarOverviewAsyncUseCase.messageProgress

    private val _kavlingList = MutableLiveData<String>()

    private val _listKavlingDataLamaRekapBesarIncludedLive = MutableLiveData(emptyList<String>())
    val listKavlingDataLamaRekapBesarIncluded: LiveData<List<String>>
        get() = _listKavlingDataLamaRekapBesarIncludedLive

    // Selected Periode Rekap
    private val _selectedPeriodeRekap = MutableLiveData<PeriodeRekap?>()
    val selectedPeriodeRekap: LiveData<PeriodeRekap?>
        get() = _selectedPeriodeRekap

    var fabScrollMode = FabMode.Downward
    var selectedBackupName: String? = null

    var gettingRekapBesarJob: Job? = null
    var kavlingLamaRekapBesarJob: Job? = null


    fun getListRekapGlobal(onFailure: (msg: String) -> Unit) {
        // Create request for all available Kavlings
        val request = GetListRekapGlobalAsyncUseCase.Request(null)
        isRekapGlobalLoaded.update { false }

        CoroutineScope(Dispatchers.IO).launch {
            getListRekapGlobalAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { listRekapGlobal ->
                    listRekapGlobal?.let {
                        _listRekapGlobalLive.postValue(it)
                    }

                    isRekapGlobalLoaded.update { true }
                }

                result.onFailure {
                    isRekapGlobalLoaded.update { true }

                    withContext(Dispatchers.Main) {
                        onFailure(it.message ?: "null")
                    }
                }
            }
        }
    }

    fun getRekapBesarOverview(
        periode: PeriodeRekap,
        startDate: Date? = null,
        endDate: Date? = null,
        onFailure: (msg: String) -> Unit,
    ) {
        _selectedPeriodeRekap.value = periode

        val listRangeTanggal = when (periode) {
            PeriodeRekap.SEMUA -> emptyList()
            PeriodeRekap.TAHUN_INI -> DateUtil.getYearlyRangeDate()
            PeriodeRekap.BULAN_INI -> DateUtil.getMonthlyRangeDate()
            PeriodeRekap.MINGGU_INI -> DateUtil.getWeeklyRangeDate()
            PeriodeRekap.CUSTOM -> DateUtil.getCustomRangeDate(startDate!!, endDate!!)
        }
        _rekapDetailTransportLive.value = RekapDetailTransport(
            rekapType = null,
            periodeRekap = periode,
            startDate = if (listRangeTanggal.isNotEmpty()) listRangeTanggal[0] else null,
            endDate = if (listRangeTanggal.isNotEmpty()) listRangeTanggal[1] else null,
            includeDataLama = doesIncludeDataLama(),
        )

        // Need to be set like this to show progress dialog
        isRekapBesarOverviewLoaded.value = false

        gettingRekapBesarJob = viewModelScope.launch(Dispatchers.IO) {
            val request = GetRekapBesarOverviewAsyncUseCase.Request(
                periodeRekap = periode,
                startDate = startDate,
                endDate = endDate,
                backupName = selectedBackupName,
                listIncludedKavlingDataLama = _listKavlingDataLamaRekapBesarIncludedLive.value!!,
                // All kavling
                listKavling = null,
            )
            getRekapBesarOverviewAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _rekapBesarOverviewLive.postValue(it)

                    isRekapBesarOverviewLoaded.postValue(true)
                }
                result.onFailure {
                    onFailure("Gagal merekap: ${it.cause}")

                    isRekapBesarOverviewLoaded.postValue(true)
                }
            }
        }
    }

    fun getRekapBesarDetail(onFailure: (msg: String) -> Unit) {
        _isRekapBesarDetailLoaded.value = false

        gettingRekapBesarJob = viewModelScope.launch {
            val request = GetRekapBesarDetailAsyncUseCase.Request
            getRekapBesarDetailAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _rekapBesarDetailLive.postValue(it)

                    _isRekapBesarDetailLoaded.postValue(true)
                }
                result.onFailure {
                    it.printStackTrace()

                    _isRekapBesarDetailLoaded.postValue(true)

                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan Rekap Besar Detail: ${it.message}")
                    }
                }
            }
        }
    }

    fun setListDataLamaRekapBesarIncluded(listKavlingStr: List<String>) {
        _listKavlingDataLamaRekapBesarIncludedLive.postValue(listKavlingStr.sorted())
    }

    fun setRekapTypeDetailTransport(rekapType: RekapType) {
        _rekapDetailTransportLive.value?.let {
            _rekapDetailTransportLive.value = RekapDetailTransport(
                rekapType = rekapType,
                periodeRekap = it.periodeRekap,
                startDate = it.startDate,
                endDate = it.endDate,
                includeDataLama = it.includeDataLama,
            )
        }
    }

    fun setRekapDetailTransport(rekapDetailTransport: RekapDetailTransport) {
        _rekapDetailTransportLive.value = rekapDetailTransport
    }

    fun doesIncludeDataLama(): Boolean {
        return _listKavlingDataLamaRekapBesarIncludedLive.value.isNullOrEmpty().not()
    }


    /**
     * Rekap Global Table Util
     */
    fun getRowHeaderRekapTable(): List<RgRowHeader> {
        val listRowHeaders = mutableListOf<RgRowHeader>()

        val kavlingList = _listRekapGlobalLive.value?.map {
            it.noKavling
        }
        kavlingList?.forEachIndexed { index, kavlingKode ->
            listRowHeaders.add(
                RgRowHeader(
                    nomor = index.plus(1).toString(),
                    kavling = kavlingKode,
                )
            )
        }

        return listRowHeaders
    }

    fun getColumnHeaderRekapTable(): List<RgColumnHeader> {
        return listOf(
            RgColumnHeader("Nama"),
            RgColumnHeader("Tanggal Pembelian"),
            RgColumnHeader("Harga"),
            RgColumnHeader("Jumlah Uang Masuk"),
            RgColumnHeader("Sisa Pembayaran"),
            RgColumnHeader("Persentase"),
        )
    }

    fun getListCellsRekapTable(): List<List<RgCell>> {
        val listRekapGlobal = _listRekapGlobalLive.value

        return if (listRekapGlobal != null) {
            val listCells = mutableListOf<List<RgCell>>()

            listRekapGlobal.forEach {
                val listValues = mutableListOf<RgCell>().apply {
                    add(RgCell(it.namaCostumer))
                    add(RgCell(it.tanggalPembelian))
                    add(RgCell(it.parsedHarga))
                    add(RgCell(it.parsedJumlahUangMasuk))
                    add(RgCell(it.parsedSisaPembayaran))
                    add(RgCell(it.parsedPersentase))
                }

                listCells.add(listValues)
            }

            listCells
        } else {
            listOf(
                listOf(
                    RgCell("-"), // Nama
                    RgCell("-"), // Tanggal pembelian
                    RgCell("-"), // Harga
                    RgCell("-"), // Jumlah uang masuk
                    RgCell("-"), // Sisa pembayaran
                    RgCell("-"), // Persentase
                )
            )
        }
    }

    private fun List<Date>.toRangeString(): String {
        return "${this[0].toSlashedString()} - ${this[1].toSlashedString()}"
    }
}