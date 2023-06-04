package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.AddBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.DeleteBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.GetAllBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.UpdateBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain.Companion.sort
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
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


    private val _biayaLainList = MutableLiveData<UiState<List<BiayaLain>?>>()
    val biayaLainList: LiveData<UiState<List<BiayaLain>?>>
        get() = _biayaLainList


    val asyncHelper = AsyncUseCaseHelper(_isFinishOperation)

    private var fetchBiayaLainJob: Job? = null

    private val asyncJobs = mutableListOf<Job>()


    fun getAllBiayaLain(dataMode: DataMode) {
        fetchBiayaLainJob?.cancel()

        Log.d("DEBUG_ME", "BiayaLainViewModel: DataMode is set to ${dataMode.name}")

        _biayaLainList.value = UiState.Loading()

        fetchBiayaLainJob = viewModelScope.launch(Dispatchers.IO) {
            val request = GetAllBiayaLainAsyncUseCase.Request(dataMode)
            getAllBiayaLainAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    if (!it.isNullOrEmpty()) {
                        val sortedBiayaLain = it.sort(BiayaLain.SortMethod.TANGGAL)
                        _biayaLainList.postValue(UiState.Success(sortedBiayaLain))
                    } else {
                        val emptyBiayaLain = listOf(BiayaLain(jenisBiaya = "-", harga = 0L, tanggal = "-"))

                        _biayaLainList.postValue(UiState.Success(emptyBiayaLain))

                        Log.d("DEBUG_ME", "Biaya Lain is EMPTY or NULL !")
                    }
                }
                result.onFailure {
                    _biayaLainList.postValue(UiState.Failure("Gagal mendapatkan biaya lain: ${it.localizedMessage}"))
                }
            }
        }
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


    fun sortListBiayaLain(sortMethod: BiayaLain.SortMethod) {
        viewModelScope.launch(Dispatchers.Default) {
            _biayaLainList.postValue(UiState.Loading())
            
            with(_biayaLainList.value) {
                if (this is UiState.Success) {
                    val sortedBiayaLain = data?.sort(sortMethod)

                    _biayaLainList.postValue(UiState.Success(sortedBiayaLain))
                }
            }
        }
    }



    override fun onCleared() {
        asyncJobs.forEach { it.cancel() }

        super.onCleared()
    }
}