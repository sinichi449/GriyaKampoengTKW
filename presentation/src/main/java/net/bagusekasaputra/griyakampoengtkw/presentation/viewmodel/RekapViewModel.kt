package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal.GetAllRekapGlobalUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.report.TumRowHeaders
import javax.inject.Inject

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val getAllRekapGlobalUseCase: GetAllRekapGlobalUseCase,
): ViewModel() {

    val progressState = getAllRekapGlobalUseCase.progressState

    private val _isFinishedProgress = MutableLiveData(true)
    val isFinishedOperation: LiveData<Boolean>
        get() = _isFinishedProgress

    private val _listRekapGlobalLive = MutableLiveData(
        listOf(RekapGlobal("-", "-", "-", 0L, 0L))
    )
    val listRekapGlobalLive: LiveData<List<RekapGlobal>>
        get() = _listRekapGlobalLive

    private val _isLoadingRekapDone = MutableLiveData<Boolean?>()
    val isLoadingRekapDone: LiveData<Boolean?>
        get() = _isLoadingRekapDone

    private var getRekapJob: Job? = null

    private val kavlingList = Kavling.getGriyaKavlingList()



    fun getAllRekapGlobal(onComplete: (msg: String) -> Unit) {
        getRekapJob?.cancel()

        val request = GetAllRekapGlobalUseCase.Request(kavlingList)
        _isLoadingRekapDone.value = false
        getRekapJob = CoroutineScope(Dispatchers.IO).launch {
            getAllRekapGlobalUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _isLoadingRekapDone.postValue(true)

                    if (it != null) {
                        _listRekapGlobalLive.postValue(it)

                        onComplete("Berhasil mendapatkan semua rekap")
                    } else {
                        onComplete("Data yang diperlukan pada database masih belum tersedia!")
                    }
                }

                result.onFailure {
                    _isLoadingRekapDone.postValue(true)

                    onComplete("Gagal mendapatkan rekap -> ${it.message}")
                }
            }
        }
    }

    fun getRowHeaderRekapTable(): List<TumRowHeaders> {
        val listRowHeaders = mutableListOf<TumRowHeaders>()

        kavlingList.forEachIndexed { index, kavlingKode ->
            listRowHeaders.add(
                TumRowHeaders(
                    numStr = index.plus(1).toString(),
                    kavling = kavlingKode,
                )
            )
        }

        return listRowHeaders
    }

    fun getColumnHeaderRekapTable(): List<TumColumnHeaders> {
        return listOf(
            TumColumnHeaders("Nama"),
            TumColumnHeaders("Tanggal Pembelian"),
            TumColumnHeaders("Harga"),
            TumColumnHeaders("Jumlah Uang Masuk"),
            TumColumnHeaders("Sisa Pembayaran"),
            TumColumnHeaders("Persentase"),
        )
    }

    fun getListCellsRekapTable(): List<List<TumCell>> {
        val listRekapGlobal = _listRekapGlobalLive.value

        return if (listRekapGlobal != null) {
            val listCells = mutableListOf<List<TumCell>>()

            listRekapGlobal.forEach {
                val listValues = mutableListOf<TumCell>().apply {
                    add(TumCell(it.namaCostumer))
                    add(TumCell(it.tanggalPembelian))
                    add(TumCell(it.parsedHarga))
                    add(TumCell(it.parsedJumlahUangMasuk))
                    add(TumCell(it.parsedSisaPembayaran))
                    add(TumCell(it.parsedPersentase))
                }

                listCells.add(listValues)
            }

            listCells
        } else {
            listOf(
                listOf(
                    TumCell("-"), // Nama
                    TumCell("-"), // Tanggal pembelian
                    TumCell("-"), // Harga
                    TumCell("-"), // Jumlah uang masuk
                    TumCell("-"), // Sisa pembayaran
                    TumCell("-"), // Persentase
                )
            )
        }
    }
}