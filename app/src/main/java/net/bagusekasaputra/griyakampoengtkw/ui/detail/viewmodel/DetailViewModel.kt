package net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.AddBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.GetAllBiayaMarketingByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.GetDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.AddHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.*
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
    private val updatePembayaranUseCase: UpdatePembayaranUseCase,
    private val deletePembayaranByTerminUseCase: DeletePembayaranByTerminUseCase,
    private val deleteAllPembayaranUseCase: DeleteAllPembayaranUseCase,
    private val getAllBiayaMarketingByKavlingKodeUseCase: GetAllBiayaMarketingByKavlingKodeUseCase,
    private val addBiayaMarketingUseCase: AddBiayaMarketingUseCase,
): ViewModel() {

    val dataDiriLive = MutableLiveData<DataDiri?>()

    val hargaKavlingLive = MutableLiveData<HargaKavling>()

    val listPembayaranLive = MutableLiveData<List<Pembayaran>?>()

    val listBiayaMarketingLive = MutableLiveData<List<BiayaMarketing>?>()

    val currentKavlingKode = MutableLiveData<String>()

    val isFinishOperation = MutableLiveData<Boolean>()

    val isFinishAddImage = MutableLiveData<Boolean>()


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
                        hargaKavlingLive.postValue(HargaKavling(kavlingKode, "0", "0"))
                    } else {
                        hargaKavlingLive.postValue(hargaKavling!!)
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

    fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = UpdatePembayaranUseCase.Request(kavlingKode, oldPembayaran, newPembayaran)

            updatePembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil mengubah pembayaran ${oldPembayaran.termin}")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghubah pembayaran: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = DeletePembayaranByTerminUseCase.Request(kavlingKode, termin)

            deletePembayaranByTerminUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus pembayaran $termin")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus pembayaran: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteAllPembayaran(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = DeleteAllPembayaranUseCase.Request(kavlingKode)

            deleteAllPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        listPembayaranLive.postValue(null)
                        onComplete("Berhasil menghapus semua pembayaran di $kavlingKode")
                    }
                    listPembayaranLive.postValue(null)
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus pembayaran: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    // Biaya Marketing
    fun getAllBiayaMarketing(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        val request = GetAllBiayaMarketingByKavlingKodeUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            getAllBiayaMarketingByKavlingKodeUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess { biayaMarketingList ->
                    listBiayaMarketingLive.postValue(biayaMarketingList)
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan biaya marketing: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addBiayaMarketing(
        kavlingKode: String,
        jenisBiaya: String,
        harga: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val biayaMarketing = BiayaMarketing(
            kavlingKode = kavlingKode,
            jenisBiaya = jenisBiaya,
            harga = harga,
        )
        val request = AddBiayaMarketingUseCase.Request(biayaMarketing)

        CoroutineScope(Dispatchers.IO).launch {
            addBiayaMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan biaya marketing.")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan biaya marketing: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun getBiayaMarketingColumnHeaders(): ArrayList<String> {
        return ArrayList<String>().apply {
            add("Jenis Biaya")
            add("Harga")
        }
    }

    fun getBiayaMarketingRowHeaders(): ArrayList<String> {
        val listBiayaMarketing = listBiayaMarketingLive.value

        return if (listBiayaMarketing != null) {
            val numberList = ArrayList<String>()

            listBiayaMarketing.forEach { biayaMarketing ->
                numberList.add(biayaMarketing.nomor.toString())
            }

            numberList
        } else {
            ArrayList<String>().apply { add("0") }
        }
    }

    fun getBiayaMarketingCellItems(): ArrayList<ArrayList<String>> {
        val listBiayaMarketing = listBiayaMarketingLive.value
        val firstOrderItemList = ArrayList<ArrayList<String>>()

        if (listBiayaMarketing != null) {
            for (biayaMarketing in listBiayaMarketing) {
                val secondOrderItemList = ArrayList<String>()

                secondOrderItemList.add(biayaMarketing.jenisBiaya)
                secondOrderItemList.add(biayaMarketing.harga)

                firstOrderItemList.add(secondOrderItemList)
            }
        } else {
            val secondOrderItemList = ArrayList<String>()

            secondOrderItemList.apply {
                add("-")
                add("-")
            }

            firstOrderItemList.add(secondOrderItemList)
        }

        return firstOrderItemList
    }
}