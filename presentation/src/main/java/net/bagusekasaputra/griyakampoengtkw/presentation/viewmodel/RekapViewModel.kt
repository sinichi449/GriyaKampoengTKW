package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetUangMasukRekapAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk.RumRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val getListRekapGlobalAsyncUseCase: GetListRekapGlobalAsyncUseCase,
    private val getRekapBesarAsyncUseCase: GetRekapBesarAsyncUseCase,
    private val getUangMasukRekapAsyncUseCase: GetUangMasukRekapAsyncUseCase,
): ViewModel() {

    val currentFragment = MutableLiveData<RekapType>()

    private val _listRekapGlobalLive = MutableLiveData(
        listOf(RekapGlobal("-", "-", "-", 0L, 0L))
    )
    val listRekapGlobalLive: LiveData<List<RekapGlobal>>
        get() = _listRekapGlobalLive

    private val _rekapBesarLive = MutableLiveData<RekapBesar>()
    val rekapBesarLive: LiveData<RekapBesar>
        get() = _rekapBesarLive

    private val _listRekapUangMasukLive = MutableLiveData<List<RekapUangMasuk>>()
    val listRekapUangMasukLive: LiveData<List<RekapUangMasuk>>
        get() = _listRekapUangMasukLive

    val isRekapGlobalLoaded = MutableStateFlow(false)
    val isRekapBesarLoaded = MutableLiveData<Boolean>()

    val rekapGlobalProgress = getListRekapGlobalAsyncUseCase.progressState.asLiveData(Dispatchers.Default)
    val rekapBesarProgress = getRekapBesarAsyncUseCase.progressState.asLiveData(Dispatchers.Default)

    val rangeTanggal = MutableLiveData<String>()

    private val kavlingList = Kavling.getGriyaKavlingList()


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
        val request = GetRekapBesarAsyncUseCase.Request(kavlingList, periode, startDate, endDate)

        isRekapBesarLoaded.value = false

        CoroutineScope(Dispatchers.IO).launch {
            getRekapBesarAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { rekapBesar ->
                    if (rekapBesar == null) {
                        withContext(Dispatchers.Main) {
                            onFailure("Rekap Besar is NULL")
                        }
                    } else {
                        _rekapBesarLive.postValue(rekapBesar)
                    }

                    isRekapBesarLoaded.postValue(true)
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure(it.message ?: "null")
                    }

                    isRekapBesarLoaded.postValue(true)
                }
            }
        }

        rangeTanggal.value = when (periode) {
            PeriodeRekap.SEMUA -> "-"
            PeriodeRekap.TAHUN_INI -> getRekapBesarAsyncUseCase.getTahunSekarang().toString()
            PeriodeRekap.BULAN_INI -> getRekapBesarAsyncUseCase.getMonthlyRangeDate().toRangeString()
            PeriodeRekap.MINGGU_INI -> getRekapBesarAsyncUseCase.getWeeklyRangeDate().toRangeString()
            PeriodeRekap.CUSTOM -> "${startDate?.toSlashedDate()} - ${endDate?.toSlashedDate()}"
        }
    }

    fun getListUangMasukRekap(onFailure: (msg: String) -> Unit) {
        val request = GetUangMasukRekapAsyncUseCase.Request

        CoroutineScope(Dispatchers.IO).launch {
            getUangMasukRekapAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _listRekapUangMasukLive.postValue(it)
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("ERROR: ${it.message}")
                    }
                }
            }
        }
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
     * Rekap Uang Masuk Util
     */
    fun getRumRowHeader(): List<RumRowHeader> {
        val listRekapUangMasuk = listRekapUangMasukLive.value
        val listRowHeader = mutableListOf<RumRowHeader>()

        if (listRekapUangMasuk.isNullOrEmpty().not()) {
            listRekapUangMasuk!!.forEachIndexed { index, rekap ->
                listRowHeader.add(RumRowHeader(
                    nomor = index.plus(1).toString(),
                    kavling = rekap.noKavling,
                ))
            }
        }

        return listRowHeader
    }

    fun getRumColumnHeader(): List<RumColumnHeader> {
        return listOf(
            RumColumnHeader("Nama Costumer"),
            RumColumnHeader("Tanggal"),
            RumColumnHeader("Jenis Pembayaran"),
            RumColumnHeader("Jumlah Pembayaran"),
        )
    }

    fun getRumListCells(): List<List<RumCell>> {
        val listRekapUangMasuk = listRekapUangMasukLive.value
        val listCells = mutableListOf<List<RumCell>>()

        if (listRekapUangMasuk.isNullOrEmpty().not()) {
            listRekapUangMasuk!!.forEach {
                val cell = mutableListOf<RumCell>()
                cell.apply {
                    add(RumCell(it.namaCostumer))
                    add(RumCell(it.tanggal))
                    add(RumCell(it.jenisPembayaran))
                    add(RumCell(NumberUtil.formatLongToString(it.jumlahPembayaran)))
                }

                listCells.add(cell)
            }
        }

        return listCells
    }

    private fun List<Date>.toRangeString(): String {
        return "${this[0].toSlashedDate()} - ${this[1].toSlashedDate()}"
    }
}