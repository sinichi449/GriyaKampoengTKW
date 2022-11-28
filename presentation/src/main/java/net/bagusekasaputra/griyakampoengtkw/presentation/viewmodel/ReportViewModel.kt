package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling.GetAllReportKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ReportKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ReportTotalUangMasuk
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumRowHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DateUtil
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getAllReportKavlingAsyncUseCase: GetAllReportKavlingAsyncUseCase,
): ViewModel() {

    // This is the all data of ReportKavling. I don't want to repeat the traversing process
    // of getting all of these data, so I santized them here.
    private val listReportKavlingLive = MutableLiveData<List<ReportKavling>>()

    // And this is the only report which is to be observed by ReportFragment.
    // This will consume the data from "listReportKavlingLive" and manipulate it
    // according to the specified "Periode" from ReportFragment.
    val listReportTotalUangMasukLive = MutableLiveData<List<ReportTotalUangMasuk>>()

    // Only when the user need the ReportKavling to be refreshed.. because I want
    // to save the bandwidth on the server
    val reportKavlingRefreshed = MutableLiveData(false)

    val rangeTanggalLive = MutableLiveData("-")

    val isFinishOperation = MutableLiveData<Boolean>()

    val isFinishedFetchingReport = MutableLiveData<Boolean>()

    val progressStateLive = MutableLiveData<ProgressState>()

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // The list of Coroutines/Flows job that need to be cleared on
    // the onCleared() callback. See below.
    private val asyncJobs = ArrayList<Job>()


    fun getLoadingMessage() {
        val loadingMsg = getAllReportKavlingAsyncUseCase.progressStateLive.asFlow()

        viewModelScope.launch {
            loadingMsg.collect {
                progressStateLive.postValue(it)
            }
        }
    }

    fun getAllReportKavling(onFailure: (msg: String) -> Unit) {
        val isRefreshed = reportKavlingRefreshed.value ?: false

        if (isRefreshed.not()) {
            logEvent("Starting to fetch report data ...")
            isFinishedFetchingReport.value = false

            val request = GetAllReportKavlingAsyncUseCase.Request

            val gettingAllReportJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllReportKavlingAsyncUseCase,
                onSuccess = {
                    listReportKavlingLive.postValue(it)

                    reportKavlingRefreshed.postValue(true)

                    isFinishedFetchingReport.postValue(true)

                    logEvent("Fetching report data done successfully")
                },
                onFailure = {
                    logEvent("Fetching report data failed -> $it")
                    isFinishedFetchingReport.postValue(true)
                    onFailure("Gagal mendapatkan ringkasan: ${it.message}")
                },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingAllReportJob)
        }
    }

    fun getRekapSemuaPeriode() {
        val listReportKavling = listReportKavlingLive.value

        if (listReportKavling != null) {
            isFinishOperation.value = false

            viewModelScope.launch {
                val listRekapTotalUangMasuk = mutableListOf<ReportTotalUangMasuk>()
                val listTanggal = mutableListOf<Date>()

                listReportKavling.forEach {
                    it.listPembayaran?.forEach { pembayaran ->
                        listTanggal.add(pembayaran.tanggal.toDate())
                    }
                    it.feeMarketing?.tanggalPenerimaan?.let { tanggalFeeMarketing ->
                        listTanggal.add(tanggalFeeMarketing.toDate())
                    }
                    it.listBiayaMarketing?.forEach { biayaMarketing ->
                        listTanggal.add(biayaMarketing.tanggal.toDate())
                    }

                    val rekapTotalUangMasuk = ReportTotalUangMasuk(
                        kavling = it.kavling,
                        uangMasuk = it.getTotalPembayaran(),
                        feeMarketing = NumberUtil.formatStringToLong(it.feeMarketing?.biayaMarketer ?: "0"),
                        biayaMarketing = it.getTotalBiayaMarketing(),
                        totalCuan = it.getTotalCuan(),
                    )

                    listRekapTotalUangMasuk.add(rekapTotalUangMasuk)
                }
                listReportTotalUangMasukLive.postValue(listRekapTotalUangMasuk)
                rangeTanggalLive.postValue(getRangePeriode(listTanggal))

                isFinishOperation.postValue(true)
            }
        }
    }

    fun getRekapPeriode(periode: Int) {
        val listReportKavling = listReportKavlingLive.value

        if (listReportKavling != null) {
            isFinishOperation.value = false

            viewModelScope.launch {
                val listPeriode = DateUtil.generateRangeDate(periode)
                val listReportTotalUangMasuk = mutableListOf<ReportTotalUangMasuk>()
                val listTanggal = mutableListOf<Date>()

                listReportKavling.forEach { reportKavling ->
                    var uangMasuk = 0L
                    var feeMarketing = 0L
                    var biayaMarketing = 0L

                    reportKavling.listPembayaran?.forEach {
                        val tanggalPembayaran = it.tanggal.toDate()

                        if (listPeriode.contains(tanggalPembayaran)) {
                            uangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
                            listTanggal.add(tanggalPembayaran)
                        }
                    }

                    reportKavling.feeMarketing?.tanggalPenerimaan?.toDate()?.let {
                        if (listPeriode.contains(it)) {
                            feeMarketing += NumberUtil.formatStringToLong(reportKavling.feeMarketing?.biayaMarketer ?: "0")
                            listTanggal.add(it)
                        }
                    }

                    reportKavling.listBiayaMarketing?.forEach {
                        val tanggalBiayaMarketing = it.tanggal.toDate()

                        if (listPeriode.contains(tanggalBiayaMarketing)) {
                            biayaMarketing += NumberUtil.formatStringToLong(it.harga)
                            listTanggal.add(tanggalBiayaMarketing)
                        }
                    }

                    listReportTotalUangMasuk.add(
                        ReportTotalUangMasuk(
                            kavling = reportKavling.kavling,
                            uangMasuk = uangMasuk,
                            feeMarketing = feeMarketing,
                            biayaMarketing = biayaMarketing,
                            totalCuan = uangMasuk - feeMarketing - biayaMarketing,
                        )
                    )
                }

                listReportTotalUangMasukLive.postValue(listReportTotalUangMasuk)
                rangeTanggalLive.postValue(getRangePeriode(listTanggal))

                isFinishOperation.postValue(true)
            }
        }
    }


    fun getRangePeriode(listTanggal: List<Date>): String {
        // Get the earliest date and the latest date
        // from List<Pembayaran>, FeeMarketing, and List<BiayaMarketing>
        return if (listTanggal.isNotEmpty()) {
            val sortedTanggal = listTanggal.sortedWith { firstDate, secondDate ->
                firstDate.compareTo(secondDate)
            }

            val earliestDate = sortedTanggal.first()
            val latestDate = sortedTanggal.last()

            "${earliestDate.toSlashedDate()} - ${latestDate.toSlashedDate()}"
        } else {
            "null"
        }
    }

    private fun getAllTanggalFromReportKavling(listReportKavling: List<ReportKavling>): List<Date> {
        val listTanggalPembayaran = mutableListOf<Date>()
        val listTanggalFeeMarketing = mutableListOf<Date>()
        val listTanggalBiayaMarketing = mutableListOf<Date>()

        listReportKavling.forEach { reportKavling ->
            reportKavling.listPembayaran?.forEach {
                listTanggalPembayaran.add(it.tanggal.toDate())
            }
            reportKavling.feeMarketing?.let {
                listTanggalFeeMarketing.add(it.tanggalPenerimaan.toDate())
            }
            reportKavling.listBiayaMarketing?.forEach {
                listTanggalBiayaMarketing.add(it.tanggal.toDate())
            }
        }

        return mutableListOf<Date>().apply {
            addAll(listTanggalPembayaran)
            addAll(listTanggalFeeMarketing)
            addAll(listTanggalBiayaMarketing)
        }
    }



    /**
     * Total Uang Masuk Table in ReportFragment's helpers
     */
    fun getOverallTotalMasuk(): String {
        val listReportTum = listReportTotalUangMasukLive.value
        var overallTotalUangMasuk = 0L

        listReportTum?.forEach {
            overallTotalUangMasuk += it.uangMasuk
        }

        return "Rp. ${NumberUtil.formatLongToString(overallTotalUangMasuk)}"
    }

    fun getOverallTotalPengeluaran(): String {
        val listReportTum = listReportTotalUangMasukLive.value

        var overallFeeMarketing = 0L
        var overallBiayaMarketing = 0L

        listReportTum?.forEach {
            overallFeeMarketing += it.feeMarketing
            overallBiayaMarketing += it.biayaMarketing
        }

        val pengeluaran = overallFeeMarketing + overallBiayaMarketing

        return "- Rp. ${NumberUtil.formatLongToString(pengeluaran)}"
    }

    fun getOverallTotalCuan(): String {
        val listReportTum = listReportTotalUangMasukLive.value

        var overallCuan = 0L

        listReportTum?.forEach {
            overallCuan += it.totalCuan
        }

        return "Rp. ${NumberUtil.formatLongToString(overallCuan)}"
    }

    fun getTotalUangMasukColumnHeaders(): List<TumColumnHeaders> {
        return listOf(
            TumColumnHeaders("Uang Masuk"),
            TumColumnHeaders("Fee Marketing"),
            TumColumnHeaders("Biaya Marketing"),
            TumColumnHeaders("Total Cuan"),
        )
    }

    fun getTotalUangMasukRowHeaders(): List<TumRowHeaders> {
        val listReportTum = listReportTotalUangMasukLive.value
        return if (listReportTum != null) {
            val rowHeaders = mutableListOf<TumRowHeaders>()

            listReportTum.forEachIndexed { index, item ->
                rowHeaders.add(TumRowHeaders(index.plus(1).toString(), item.kavling))
            }

            rowHeaders
        } else {
            listOf(TumRowHeaders("0", "-"))
        }
    }

    fun getTotalUangMasukCellItems(): List<List<TumCell>> {
        val listReportTum = listReportTotalUangMasukLive.value

        return if (listReportTum != null) {
            val firstOrderList = mutableListOf<List<TumCell>>()

            listReportTum.forEach {
                val secondOrderList = mutableListOf<TumCell>().apply {
                    // parse numbers
                    val parsedUangMasuk = NumberUtil.formatLongToString(it.uangMasuk)
                    val parsedFeeMarketing = NumberUtil.formatLongToString(it.feeMarketing)
                    val parsedBiayaMarketing = NumberUtil.formatLongToString(it.biayaMarketing)
                    val parsedTotalCuan = NumberUtil.formatLongToString(it.totalCuan)

                    add(TumCell(parsedUangMasuk))
                    add(TumCell(parsedFeeMarketing))
                    add(TumCell(parsedBiayaMarketing))
                    add(TumCell(parsedTotalCuan))
                }

                firstOrderList.add(secondOrderList)
            }

            firstOrderList

        } else {
            listOf(
                listOf(
                    TumCell("-"),
                    TumCell("-"),
                    TumCell("-"),
                    TumCell("-"),
                )
            )
        }
    }



    override fun onCleared() {
        super.onCleared()

        asyncJobs.forEach { it.cancel() }
    }
}