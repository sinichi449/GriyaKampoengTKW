package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal.GetAllRekapGlobalWithRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapBesar
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal.RgRowHeader
import javax.inject.Inject

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val getAllRekapGlobalWithRekapBesarAsyncUseCase: GetAllRekapGlobalWithRekapBesarAsyncUseCase,
): ViewModel() {

    val currentFragment = MutableLiveData<RekapType>()

    val progressState = getAllRekapGlobalWithRekapBesarAsyncUseCase.progressState

    private val _isFinishedProgress = MutableLiveData(true)
    val isFinishedOperation: LiveData<Boolean>
        get() = _isFinishedProgress


    private val _listRekapGlobalLive = MutableLiveData(
        listOf(RekapGlobal("-", "-", "-", 0L, 0L))
    )
    val listRekapGlobalLive: LiveData<List<RekapGlobal>>
        get() = _listRekapGlobalLive

    private val _rekapBesarLive = MutableLiveData<RekapBesar>()
    val rekapBesarLive: LiveData<RekapBesar>
        get() = _rekapBesarLive


    private val _isLoadingRekapDone = MutableLiveData<Boolean?>()
    val isLoadingRekapDone: LiveData<Boolean?>
        get() = _isLoadingRekapDone

    private var getRekapJob: Job? = null

    private val kavlingList = Kavling.getGriyaKavlingList()



    fun getAllRekap(onComplete: (msg: String) -> Unit) {
        getRekapJob?.cancel()

        val request = GetAllRekapGlobalWithRekapBesarAsyncUseCase.Request(kavlingList)

        _isLoadingRekapDone.value = false

        getRekapJob = CoroutineScope(Dispatchers.IO).launch {

            getAllRekapGlobalWithRekapBesarAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { rekapGlobalWithBesar ->
                    _isLoadingRekapDone.postValue(true)

                    if (rekapGlobalWithBesar != null) {
                        _listRekapGlobalLive.postValue(rekapGlobalWithBesar.listRekapGlobal)
                        _rekapBesarLive.postValue(rekapGlobalWithBesar.rekapBesar)

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
}