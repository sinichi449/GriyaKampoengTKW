package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.AddBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.DeleteBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.GetAllBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.UpdateBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import javax.inject.Inject

@HiltViewModel
class BiayaLainViewModel @Inject constructor(
    private val getAllBiayaLainAsyncUseCase: GetAllBiayaLainAsyncUseCase,
    private val addBiayaLainAsyncUseCase: AddBiayaLainAsyncUseCase,
    private val updateBiayaLainAsyncUseCase: UpdateBiayaLainAsyncUseCase,
    private val deleteBiayaLainAsyncUseCase: DeleteBiayaLainAsyncUseCase,
): ViewModel() {

    private val _isFinishOperation = MutableLiveData<Boolean>()
    val isFinishOperation: LiveData<Boolean>
        get() = _isFinishOperation


    private val _listBiayaLainLive = MutableLiveData<List<BiayaLain>>()
    val listBiayaLainLive: LiveData<List<BiayaLain>>
        get() = _listBiayaLainLive


    val asyncHelper = AsyncUseCaseHelper(_isFinishOperation)

    private val asyncJobs = mutableListOf<Job>()



    fun getAllBiayaLain(onFailure: (msg: String) -> Unit) {
        val request = GetAllBiayaLainAsyncUseCase.Request(false)

        val getAllJobs = asyncHelper.doWork(
            request = request,
            asyncUseCase = getAllBiayaLainAsyncUseCase,
            onSuccess = {
                _listBiayaLainLive.postValue(
                    it?.sortedWith { p0, p1 ->
                        val tanggal1 = p0.tanggal.toDate()
                        val tanggal2 = p1.tanggal.toDate()

                        tanggal1.compareTo(tanggal2)
                    }
                )
            },
            onFailure = {
                onFailure("Gagal mendapatkan biaya lain: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(getAllJobs)
    }

    fun addBiayaLain(
        jenisBiaya: String,
        harga: Long,
        tanggal: String,
        onComplete: (msg: String) -> Unit,
    ) {
        val biayaLain = BiayaLain(
            jenisBiaya = jenisBiaya,
            harga = harga,
            tanggal = tanggal
        )
        val request = AddBiayaLainAsyncUseCase.Request(biayaLain)

        val addingBiayaJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = addBiayaLainAsyncUseCase,
            onSuccess = {
                onComplete("Berhasil ditambahkan")
            },
            onFailure = {
                onComplete("Gagal menambahkan: ${it.message}")
            }
        )

        asyncJobs.add(addingBiayaJob)
    }

    fun updateBiayaLain(
        oldBiayaLain: BiayaLain,
        newJenisBiaya: String,
        newHarga: Long,
        newTanggal: String,
        onComplete: (msg: String) -> Unit,
    ) {
        val newBiayaLain = BiayaLain(
            jenisBiaya = newJenisBiaya,
            harga = newHarga,
            tanggal = newTanggal,
        )
        val request = UpdateBiayaLainAsyncUseCase.Request(oldBiayaLain, newBiayaLain)

        val updatingBiayaJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = updateBiayaLainAsyncUseCase,
            onSuccess = {
                onComplete("Berhasil mengubah biaya lain")
            },
            onFailure = {
                onComplete("Gagal mengubah: ${it.message}")
            }
        )

        asyncJobs.add(updatingBiayaJob)
    }

    fun deleteBiayaLain(biayaLain: BiayaLain, onComplete: (msg: String) -> Unit) {
        val request = DeleteBiayaLainAsyncUseCase.Request(biayaLain)

        val deletingBiayaJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = deleteBiayaLainAsyncUseCase,
            onSuccess = {
                onComplete("Berhasil menghapus")
            },
            onFailure = {
                onComplete("Gagal menghapus: ${it.message}")
            }
        )

        asyncJobs.add(deletingBiayaJob)
    }


    override fun onCleared() {
        asyncJobs.forEach { it.cancel() }

        super.onCleared()
    }
}