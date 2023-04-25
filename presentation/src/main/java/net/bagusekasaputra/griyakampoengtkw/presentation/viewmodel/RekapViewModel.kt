package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetListUnmigratedKavlingsAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.UnmigratedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val getListRekapGlobalAsyncUseCase: GetListRekapGlobalAsyncUseCase,
    private val getListUnmigratedKavlingsAsyncUseCase: GetListUnmigratedKavlingsAsyncUseCase,
): ViewModel() {

    val currentFragment = MutableLiveData<RekapType>()
    val currentPeriodeRekap = MutableLiveData<PeriodeRekap>()
    val currentStartDate = MutableLiveData<String>()
    val currentEndDate = MutableLiveData<String>()



    private val _listRekapGlobalLive = MutableLiveData(
        listOf(RekapGlobal("-", "-", "-", 0L, 0L))
    )
    val listRekapGlobalLive: LiveData<List<RekapGlobal>>
        get() = _listRekapGlobalLive


    // TODO: Pass the value here
    private val _rekapBesarOverviewLive = MutableLiveData<RekapBesarOverview>()
    val rekapBesarOverviewLive: LiveData<RekapBesarOverview>
        get() = _rekapBesarOverviewLive


    val isRekapGlobalLoaded = MutableStateFlow(false)
    val isRekapBesarLoaded = MutableLiveData<Boolean>()

    val rekapGlobalProgress = getListRekapGlobalAsyncUseCase.progressState.asLiveData(Dispatchers.Default)

    val rangeTanggal = MutableLiveData<String>()

    private val kavlingList = Kavling.getGriyaKavlingList()

    private val _listKavlingDataLamaRekapBesarIncludedLive = MutableLiveData(emptyList<String>())
    val listKavlingDataLamaRekapBesarIncluded: LiveData<List<String>>
        get() = _listKavlingDataLamaRekapBesarIncludedLive


    var kavlingLamaRekapBesarJob: Job? = null


    fun getListRekapGlobal(onFailure: (msg: String) -> Unit) {
        val request = GetListRekapGlobalAsyncUseCase.Request(kavlingList)
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

    fun getRekapBesar(
        periode: PeriodeRekap,
        startDate: Date? = null,
        endDate: Date? = null,
        onFailure: (msg: String) -> Unit,
    ) {
        currentPeriodeRekap.value = periode
        currentStartDate.value = startDate?.toSlashedDate()
        currentEndDate.value = endDate?.toSlashedDate()

        // Need to be set like this to show progress dialog
//        isRekapBesarLoaded.value = false

        // TODO

        rangeTanggal.value = when (periode) {
            PeriodeRekap.SEMUA -> "Semua"
            PeriodeRekap.TAHUN_INI -> DateUtil.getTahunSekarang().toString()
            PeriodeRekap.BULAN_INI -> DateUtil.getMonthlyRangeDate().toRangeString()
            PeriodeRekap.MINGGU_INI -> DateUtil.getWeeklyRangeDate().toRangeString()
            PeriodeRekap.CUSTOM -> "${startDate?.toSlashedDate()} - ${endDate?.toSlashedDate()}"
        }
    }


    fun getListKavlingDataLama(
        onProgress: () -> Unit,
        onSuccess: (listUnmigratedKavling: List<UnmigratedKavling>?) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        kavlingLamaRekapBesarJob = viewModelScope.launch {
            onProgress()

            val request = GetListUnmigratedKavlingsAsyncUseCase.Request
            getListUnmigratedKavlingsAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    val listKavlingStr = mutableListOf<String>().apply {
                        it?.forEach { unmigratedKavling ->
                            add(unmigratedKavling.kavlingKode)
                        }
                    }
                    setListDataLamaRekapBesarIncluded(listKavlingStr)

                    onSuccess(it)
                }
                result.onFailure {
                    onFailure("ERROR: Gagal mendapatkan Kavling data Lama -> ${it.message}")
                }
            }
        }
    }

    fun setListDataLamaRekapBesarIncluded(listKavlingStr: List<String>) {
        _listKavlingDataLamaRekapBesarIncludedLive.postValue(listKavlingStr.sorted())
    }


    /**
     * Rekap Global Table Util
     */
    fun getRowHeaderRekapTable(): List<RgRowHeader> {
        val listRowHeaders = mutableListOf<RgRowHeader>()

        kavlingList.forEachIndexed { index, kavlingKode ->
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

    /**
     * Rekap Uang Masuk Table Util
     */


    /**
     * Rekap Biaya Marketing Table Util
     */

    private fun List<Date>.toRangeString(): String {
        return "${this[0].toSlashedDate()} - ${this[1].toSlashedDate()}"
    }
}