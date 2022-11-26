package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ReportTotalUangMasuk
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumRowHeaders
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextLong

@HiltViewModel
class ReportViewModel @Inject constructor(

): ViewModel() {

    // This is the all data of ReportTotalUangMasuk. I don't want to repeat the traversing process
    // of getting all of these data, so I santized them here.
    private val allListReportTotalUangMasukLive = MutableLiveData<List<ReportTotalUangMasuk>>()

    // And this is the only report which is to be observed by ReportFragment.
    val listReportTotalUangMasukLive = MutableLiveData<List<ReportTotalUangMasuk>>()

    val isFinishOperation = MutableLiveData<Boolean>()

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // The list of Coroutines/Flows job that need to be cleared on
    // the onCleared() callback. See below.
    private val asyncJobs = ArrayList<Job>()


    fun provideReportUangMasuk() {
        isFinishOperation.value = false

        val randomUangMasuk = { Random.nextLong(10L..2500L) * 100000L }
        val randomMarketing = { Random.nextLong(100L..1000L) * 10000L }

        viewModelScope.launch {
            val blocks = listOf("A", "B", "C") // Daftar block
            val listReport = mutableListOf<ReportTotalUangMasuk>()

            blocks.forEach { block ->
                val sumBlock = when (block) {
                    "A" -> 14 // Jumlah kavling di block A
                    "B" -> 20 // Jumlah kavling di block B
                    "C" -> 9 // Jumlah kavling di block C
                    else -> 0 // Error
                }

                (1..sumBlock).forEach { noKavling ->
                    val uangMasuk = randomUangMasuk()
                    val feeMarketing = randomMarketing()
                    val biayaMarketing = randomMarketing()

                    // Perhitungan cuan
                    val totalCuan = uangMasuk - feeMarketing - biayaMarketing

                    // Masukkan data
                    listReport.add(
                        ReportTotalUangMasuk(
                            kavling = "$block$noKavling",
                            uangMasuk = uangMasuk,
                            feeMarketing = feeMarketing,
                            biayaMarketing = biayaMarketing,
                            totalCuan = totalCuan,
                        )
                    )
                }
            }


            listReportTotalUangMasukLive.postValue(listReport)


            isFinishOperation.postValue(true)
        }

    }

    fun getRekapSemuaPeriode() {
        listReportTotalUangMasukLive.value = allListReportTotalUangMasukLive.value
    }

    fun getRekapMingguIni() {
        // TODO
    }

    fun getRekapBulanIni() {
        // TODO
    }

    fun getRekapTahunIni() {
        // TODO
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