package net.bagusekasaputra.griyakampoengtkw.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.GetDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.AddHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.AddPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.GetAllPembayaranUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val addDataDiriUseCase: AddDataDiriUseCase,
    private val getDataDiriUseCase: GetDataDiriUseCase,
    private val deleteDataDiriUseCase: DeleteDataDiriUseCase,
    private val getHargaKavlingUseCase: GetHargaKavlingUseCase,
    private val addHargaKavlingUseCase: AddHargaKavlingUseCase,
    private val getAllPembayaranUseCase: GetAllPembayaranUseCase,
    private val addPembayaranUseCase: AddPembayaranUseCase,
): ViewModel() {

    val dataDiriLive = MutableLiveData<DataDiri?>()

    val hargaKavlingLive = MutableLiveData<String>()

    val listPembayaranLive = MutableLiveData<List<Pembayaran>>()

    val currentKavlingKode = MutableLiveData<String>()

    val isFinishOperation = MutableLiveData<Boolean>()


    // Data Diri
    fun getDataDiri(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetDataDiriUseCase.Request(kavlingKode)

            getDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.dataDiri

                if (result.isSuccess) {
                    val dataDiri = result.getOrNull()

                    dataDiri?.let {
                        dataDiriLive.postValue(it)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan data diri: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value  = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddDataDiriUseCase.Request(kavlingKode, dataDiri)

            addDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                       onComplete( "Berhasil menambahakan data ${dataDiri.nama}")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan data diri: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteDataDiri(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = DeleteDataDiriUseCase.Request(kavlingKode)

            deleteDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    dataDiriLive.postValue(null)
                    withContext(Dispatchers.Main) {
                        onComplete("Hapus data diri berhasil")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus data diri: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    // Harga Kavling
    fun getHargaKavling(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetHargaKavlingUseCase.Request(kavlingKode)

            getHargaKavlingUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    val hargaKavling = result.getOrNull()

                    if (hargaKavling == null) {
                        hargaKavlingLive.postValue("0")
                    } else {
                        hargaKavlingLive.postValue(hargaKavling.harga)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan harga kavling: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addHargaKavling(hargaKavling: HargaKavling, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddHargaKavlingUseCase.Request(hargaKavling)

            addHargaKavlingUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan harga kavling ${hargaKavling.kavlingKode}")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan harga kavling: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    // Pembayaran
    fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddPembayaranUseCase.Request(kavlingKode, hargaKavling, pembayaran)

            addPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan pembayaran")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan pembayaran: ${result.exceptionOrNull()?.message?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun getAllPembayaran(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = GetAllPembayaranUseCase.Request(kavlingKode)

            getAllPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    val listPembayaran = result.getOrNull()

                    listPembayaran?.let {
                        listPembayaranLive.postValue(it)
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        onFailure("Gagal mendapatkan pembayaran: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

}