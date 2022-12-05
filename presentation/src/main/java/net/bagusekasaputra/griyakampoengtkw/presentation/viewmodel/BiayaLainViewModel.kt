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
                _listBiayaLainLive.postValue(it)
            },
            onFailure = {
                onFailure("Gagal mendapatkan biaya lain: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(getAllJobs)
    }


    override fun onCleared() {
        asyncJobs.forEach { it.cancel() }

        super.onCleared()
    }
}