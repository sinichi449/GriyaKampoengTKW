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
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.GetRekapBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetRekapBiayaMarketingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing.GetRekapFeeMarketingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetUangMasukRekapAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.BlCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.BlColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.BlRowHeader
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
    private val getRekapFeeMarketingAsyncUseCase: GetRekapFeeMarketingAsyncUseCase,
    private val getRekapBiayaMarketingAsyncUseCase: GetRekapBiayaMarketingAsyncUseCase,
    private val getRekapBiayaLainAsyncUseCase: GetRekapBiayaLainAsyncUseCase,
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

    private val _rekapBesarLive = MutableLiveData<RekapBesar>()
    val rekapBesarLive: LiveData<RekapBesar>
        get() = _rekapBesarLive

    private val _listRekapUangMasukLive = MutableLiveData<List<RekapUangMasuk>>()
    val listRekapUangMasukLive: LiveData<List<RekapUangMasuk>>
        get() = _listRekapUangMasukLive

    private val _listFeeMarketingRekapLive = MutableLiveData<List<FeeMarketing>>()
    val listFeeMarketingRekapLive: LiveData<List<FeeMarketing>>
        get() = _listFeeMarketingRekapLive

    private val _listBiayaMarketingRekapLive = MutableLiveData<Map<String, List<BiayaMarketing>>>()
    val listBiayaMarketingRekapLive: LiveData<Map<String, List<BiayaMarketing>>>
        get() = _listBiayaMarketingRekapLive

    private val _listBiayaLainRekapLive = MutableLiveData<List<BiayaLain>>()
    val listBiayaLainRekapLive: LiveData<List<BiayaLain>>
        get() = _listBiayaLainRekapLive


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
        currentPeriodeRekap.value = periode
        currentStartDate.value = startDate?.toSlashedDate()
        currentEndDate.value = endDate?.toSlashedDate()

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
            PeriodeRekap.SEMUA -> "Semua"
            PeriodeRekap.TAHUN_INI -> DateUtil.getTahunSekarang().toString()
            PeriodeRekap.BULAN_INI -> DateUtil.getMonthlyRangeDate().toRangeString()
            PeriodeRekap.MINGGU_INI -> DateUtil.getWeeklyRangeDate().toRangeString()
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

    fun getFeeMarketingRekap(
        periode: PeriodeRekap,
        startDate: Date?,
        endDate: Date?,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = GetRekapFeeMarketingAsyncUseCase.Request(kavlingList, periode, startDate, endDate)

        CoroutineScope(Dispatchers.IO).launch {
            getRekapFeeMarketingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { listFeeMarketing ->
                    listFeeMarketing?.let {
                        _listFeeMarketingRekapLive.postValue(it)
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("ERROR: ${it.message}")
                    }
                }
            }
        }
    }

    fun getBiayaMarketingRekap(
        periode: PeriodeRekap,
        startDate: Date?,
        endDate: Date?,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = GetRekapBiayaMarketingAsyncUseCase.Request(kavlingList, periode, startDate, endDate)

        CoroutineScope(Dispatchers.IO).launch {
            getRekapBiayaMarketingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { mapBiayaMarketing ->
                    mapBiayaMarketing?.let {
                        _listBiayaMarketingRekapLive.postValue(it)
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("ERROR: ${it.message}")
                    }
                }
            }
        }
    }

    fun getBiayaLainRekap(
        periode: PeriodeRekap,
        startDate: Date?,
        endDate: Date?,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = GetRekapBiayaLainAsyncUseCase.Request(kavlingList, periode, startDate, endDate)

        CoroutineScope(Dispatchers.IO).launch {
            getRekapBiayaLainAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { listBiayaLain ->
                    if (listBiayaLain.isNullOrEmpty().not()) {
                        _listBiayaLainRekapLive.postValue(listBiayaLain)
                    }
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
     * Rekap Uang Masuk Table Util
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

    fun sortListRekapUangMasuk(byWhat: String) {
        if (_listRekapUangMasukLive.value.isNullOrEmpty().not()) {
                when (byWhat) {
                "Kavling" -> _listRekapUangMasukLive.value = RekapUangMasuk.sortByKavlingAsc(_listRekapUangMasukLive.value)
                "Tanggal" -> _listRekapUangMasukLive.value = RekapUangMasuk.sortByTanggalAsc(_listRekapUangMasukLive.value)
                "Jumlah Pembayaran" -> _listRekapUangMasukLive.value = RekapUangMasuk.sortByJumlahPembayaranDesc(_listRekapUangMasukLive.value)
                else -> {}
            }
        }
    }

    /**
     * Rekap Fee Marketing Table Util
     */
    fun getFeeMarketingRowHeader(): List<RumRowHeader> {
        val feeMarketings = listFeeMarketingRekapLive.value
        val rowHeaders = mutableListOf<RumRowHeader>()

        if (feeMarketings.isNullOrEmpty().not()) {
            feeMarketings!!.forEachIndexed { index, item ->
                rowHeaders.add(RumRowHeader(
                    nomor = index.plus(1).toString(),
                    kavling = item.kavlingKode,
                ))
            }
        }

        return rowHeaders
    }

    fun getFeeMarketingColumnHeader(): List<RumColumnHeader> {
        return listOf(
            RumColumnHeader("Nama Marketer"),
            RumColumnHeader("Tanggal Penerimaan"),
            RumColumnHeader("Jumlah Uang"),
        )
    }

    fun getFeeMarketingListCells(): List<List<RumCell>> {
        val cells = mutableListOf<List<RumCell>>()
        val feeMarketings = listFeeMarketingRekapLive.value

        if (feeMarketings.isNullOrEmpty().not()) {
            feeMarketings!!.forEach {
                val items = mutableListOf<RumCell>()
                items.apply {
                    add(RumCell(it.namaMarketer))
                    add(RumCell(it.tanggalPenerimaan))
                    add(RumCell(NumberUtil.formatLongToString(it.parsedBiayaMarketer)))
                }

                cells.add(items)
            }
        }

        return cells
    }


    /**
     * Rekap Biaya Marketing Table Util
     */
    fun getBiayaMarketingRowHeader(): List<RumRowHeader> {
        val batchBiayaMarketing = listBiayaMarketingRekapLive.value
        val rowHeaders = mutableListOf<RumRowHeader>()

        if (batchBiayaMarketing.isNullOrEmpty().not()) {
            var index = 1
            batchBiayaMarketing!!.forEach { item ->
                item.value.forEach {
                    rowHeaders.add(RumRowHeader(
                        nomor = index.toString(),
                        kavling = it.kavlingKode,
                    ))
                    index += 1
                }
            }
        }

        return rowHeaders
    }

    fun getBiayaMarketingColumnHeader(): List<RumColumnHeader> {
        return listOf(
            RumColumnHeader("Jenis Biaya"),
            RumColumnHeader("Tanggal"),
            RumColumnHeader("Harga"),
        )
    }

    fun getBiayaMarketingListCells(): List<List<RumCell>> {
        val cells = mutableListOf<List<RumCell>>()
        val batchBiayaMarketing = listBiayaMarketingRekapLive.value

        if (batchBiayaMarketing.isNullOrEmpty().not()) {
            batchBiayaMarketing!!.forEach { item ->
                item.value.forEach {
                    val listItem = mutableListOf<RumCell>()
                    listItem.apply {
                        add(RumCell(it.jenisBiaya))
                        add(RumCell(it.tanggal))
                        add(RumCell(NumberUtil.formatLongToString(it.parsedHarga)))
                    }

                    cells.add(listItem)
                }
            }
        }

        return cells
    }

    /**
     * Rekap Biaya Lain-lain Table Util
     */
    fun getBiayaLainRowHeader(): List<BlRowHeader> {
        val listBiayaLain = listBiayaLainRekapLive.value
        val rowHeaders = mutableListOf<BlRowHeader>()

        if (listBiayaLain.isNullOrEmpty().not()) {
            val size = listBiayaLain!!.size
            var index = 1
            repeat(size) {
                rowHeaders.add(BlRowHeader(nomor = index.toString()))
                index += 1
            }
        }

        return rowHeaders
    }

    fun getBiayaLainColumnHeaders(): List<BlColumnHeader> {
        return listOf(
            BlColumnHeader("Jenis Biaya"),
            BlColumnHeader("Tanggal"),
            BlColumnHeader("Harga"),
        )
    }

    fun getBiayaLainListCells(): List<List<BlCell>> {
        val cells = mutableListOf<List<BlCell>>()
        val listBiayaLain = listBiayaLainRekapLive.value

        if (listBiayaLain.isNullOrEmpty().not()) {
            listBiayaLain!!.forEach {
                val items = mutableListOf<BlCell>()
                items.apply {
                    add(BlCell(it.jenisBiaya))
                    add(BlCell(it.tanggal))
                    add(BlCell(NumberUtil.formatLongToString(it.harga)))
                }

                cells.add(items)
            }
        }

        return cells
    }


    private fun List<Date>.toRangeString(): String {
        return "${this[0].toSlashedDate()} - ${this[1].toSlashedDate()}"
    }
}