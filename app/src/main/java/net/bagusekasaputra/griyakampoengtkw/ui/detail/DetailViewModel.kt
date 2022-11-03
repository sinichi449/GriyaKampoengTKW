package net.bagusekasaputra.griyakampoengtkw.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Operation
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.GetDataDiriUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val addDataDiriUseCase: AddDataDiriUseCase,
    private val getDataDiriUseCase: GetDataDiriUseCase,
    private val deleteDataDiriUseCase: DeleteDataDiriUseCase,
): ViewModel() {

    private val _dataDiriLive = MutableLiveData<DataDiri>()
    val dataDiriLive: LiveData<DataDiri>
        get() = _dataDiriLive

    val currentKavlingKode = MutableLiveData<String>()

    val isFinishOperation = MutableLiveData<Boolean>()

    val operationResult = MutableLiveData<Operation?>()


    // Data Diri
    fun getDataDiri(kavlingKode: String) {
        isFinishOperation.value = false
        operationResult.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetDataDiriUseCase.Request(kavlingKode)

            getDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.dataDiri

                if (result.isSuccess) {
                    val dataDiri = result.getOrNull()

                    if (dataDiri == null) {
                        operationResult.postValue(Operation(true, "Data diri pada kavling $kavlingKode masih kosong"))
                    } else {
                        _dataDiriLive.postValue(dataDiri!!)
                        operationResult.postValue(Operation(true, null))
                    }
                } else {
                    operationResult.postValue(Operation(false, "Gagal mendapatkan data diri dari server: ${result.exceptionOrNull()?.message}"))
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri) {
        isFinishOperation.value  = false
        operationResult.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddDataDiriUseCase.Request(kavlingKode, dataDiri)

            addDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    operationResult.postValue(Operation(true, "Berhasil menambahakan data ${dataDiri.nama}"))
                } else {
                    result.exceptionOrNull()?.let {
                        operationResult.postValue(Operation(false, "Gagal menambahkan data diri: ${it.message}"))
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteDataDiri(kavlingKode: String) {
        isFinishOperation.value = false
        operationResult.value = null

        CoroutineScope(Dispatchers.IO).launch {
            val request = DeleteDataDiriUseCase.Request(kavlingKode)

            deleteDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    operationResult.postValue(Operation(true, "Hapus data diri berhasil"))
                } else {
                    operationResult.postValue(Operation(false, "Gagal menghapus data diri: ${result.exceptionOrNull()?.message}"))
                }

                isFinishOperation.postValue(true)
            }
        }
    }

}