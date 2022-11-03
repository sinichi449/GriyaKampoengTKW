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
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val addDataDiriUseCase: AddDataDiriUseCase
): ViewModel() {

    private val _dataDiriList = MutableLiveData<DataDiri>()
    val dataDiriList: LiveData<DataDiri>
        get() = _dataDiriList

    val currentKavlingKode = MutableLiveData<String>()

    val isFinishOperation = MutableLiveData<Boolean>()

    val operationResult = MutableLiveData<Operation?>()


    // Data Diri
    fun getDataDiri(kavlingKode: String) {
        // TODO
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

}