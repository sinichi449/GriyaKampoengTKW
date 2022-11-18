package net.bagusekasaputra.griyakampoengtkw.ui.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.*
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.AddCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.DeleteCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.GetCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.GetDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.AddFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.DeleteFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.GetFeeMarketingByKavlingKode
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.UpdateFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.AddHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.*
import net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.TableBiayaMarketingHelper
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
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
    private val editBiayaMarketingUseCase: EditBiayaMarketingUseCase,
    private val deleteSingleBiayaMarketingUseCase: DeleteSingleBiayaMarketingUseCase,
    private val deleteAllBiayaMarketingUseCase: DeleteAllBiayaMarketingUseCase,
    private val getFeeMarketingByKavlingKode: GetFeeMarketingByKavlingKode,
    private val addFeeMarketingUseCase: AddFeeMarketingUseCase,
    private val updateFeeMarketingUseCase: UpdateFeeMarketingUseCase,
    private val deleteFeeMarketingUseCase: DeleteFeeMarketingUseCase,
    private val getCatatanPembayaranUseCase: GetCatatanPembayaranUseCase,
    private val addCatatanPembayaranUseCase: AddCatatanPembayaranUseCase,
    private val deleteCatatanPembayaranUseCase: DeleteCatatanPembayaranUseCase,
): ViewModel() {

    val dataDiriLive = MutableLiveData<DataDiri?>()

    val hargaKavlingLive = MutableLiveData<HargaKavling>()

    val listPembayaranLive = MutableLiveData<List<Pembayaran>?>()

    val feeMarketingLive = MutableLiveData<FeeMarketing?>()

    val listBiayaMarketingLive = MutableLiveData<List<BiayaMarketing>?>()

    val catatanPembayaranLive = MutableLiveData<CatatanPembayaran?>()

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

    // Fee Marketing
    fun getFeeMarketing(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        val request = GetFeeMarketingByKavlingKode.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            getFeeMarketingByKavlingKode.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess { feeMarketing ->
                    // We need to transform the biayaAfiliasi into comma separated value here
                    feeMarketing?.biayaMarketer = NumberUtil
                        .formatLongToString(feeMarketing?.biayaMarketer?.toLong() ?: 0)

                    feeMarketingLive.postValue(feeMarketing)
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan biaya afiliasi: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addFeeMarketing(
        kavlingKode: String,
        namaMarketer: String,
        biayaMarketer: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val feeMarketing = FeeMarketing(
            kavlingKode = kavlingKode,
            namaMarketer = namaMarketer,
            biayaMarketer = biayaMarketer,
        )
        val request = AddFeeMarketingUseCase.Request(feeMarketing)

        CoroutineScope(Dispatchers.IO).launch {
            addFeeMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan Fee Marketing")
                    }
                }
                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan Fee Marketing: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun updateFeeMarketing(
        kavlingKode: String,
        newNamaMarketer: String,
        newBiayaMarketer: String,
        onComplete: (msg: String) -> Unit
    ) {
        // Check null
        if (feeMarketingLive.value == null) {
            onComplete("Gagal mengupdate Fee Marketing: Null feeMarketingLive value")
        } else {
            // Check must be a same kavling kode between the current live data and the potential new fee marketing
            if (kavlingKode != feeMarketingLive.value!!.kavlingKode) {
                onComplete("Gagal mengupdate Fee Marketing: kavlingKode mismatch between old and new Fee Marketing.")

            } else {
                isFinishOperation.value = false

                val newFeeMarketing = FeeMarketing(
                    kavlingKode = kavlingKode,
                    namaMarketer = newNamaMarketer,
                    biayaMarketer = newBiayaMarketer,
                )
                val request = UpdateFeeMarketingUseCase.Request(
                    oldFeeMarketing = feeMarketingLive.value!!,
                    newFeeMarketing = newFeeMarketing,
                )

                CoroutineScope(Dispatchers.IO).launch {
                    updateFeeMarketingUseCase.execute(request).collect { response ->
                        val result = response.data.result

                        result.onSuccess {
                            withContext(Dispatchers.Main) {
                                onComplete("Berhasil mengubah Fee Marketing")
                            }
                        }

                        result.onFailure { throwable ->
                            withContext(Dispatchers.Main) {
                                onComplete("Gagal mengubah Fee Marketing: ${throwable.message}")
                            }
                        }

                        isFinishOperation.postValue(true)
                    }
                }
            }
        }
    }

    fun deleteFeeMarketing(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val request = DeleteFeeMarketingUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            deleteFeeMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus Fee Marketing")
                    }

                    feeMarketingLive.postValue(null)
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus Fee Marketing: ${throwable.message}")
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

    fun editBiayaMarketing(
        oldBiayaMarketing: BiayaMarketing,
        kavlingKode: String,
        newJenisHarga: String,
        newHarga: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val newBiayaMarketing = BiayaMarketing(
            kavlingKode = kavlingKode,
            jenisBiaya = newJenisHarga,
            harga = newHarga,
        )
        val request = EditBiayaMarketingUseCase.Request(oldBiayaMarketing, newBiayaMarketing)

        CoroutineScope(Dispatchers.IO).launch {
            editBiayaMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil mengubah biaya pembayaran")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal mengubah biaya pembayaran: ${throwable.message}")
                    }
                }


                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteBiayaMarketing(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val request = DeleteSingleBiayaMarketingUseCase.Request(kavlingKode, biayaMarketing)

        CoroutineScope(Dispatchers.IO).launch {
            deleteSingleBiayaMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus ${biayaMarketing.jenisBiaya}")
                    }
                }
                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus ${biayaMarketing.jenisBiaya}: ${throwable.message}")
                    }
                }
            }

            isFinishOperation.postValue(true)
        }
    }

    fun deleteAllBiayaMarketing(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val request = DeleteAllBiayaMarketingUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            deleteAllBiayaMarketingUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        listBiayaMarketingLive.postValue(null)
                        onComplete("Berhasil menghapus semua biaya marketing")
                    }
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus biaya marketing: ${it.message}")
                    }
                }
            }

            isFinishOperation.postValue(true)
        }
    }


    // Catatan Pembayaran
    fun getCatatanPembayaran(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        isFinishOperation.value = false

        val request = GetCatatanPembayaranUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            getCatatanPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    catatanPembayaranLive.postValue(result.getOrNull())
                }
                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan catatan pembayaran: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun addCatatanPembayaran(
        kavlingKode: String,
        catatan: String,
        onComplete: (msg: String) -> Unit,
    ) {
        isFinishOperation.value = false

        val catatanPembayaran = CatatanPembayaran(kavlingKode, catatan)
        val request = AddCatatanPembayaranUseCase.Request(kavlingKode, catatanPembayaran)

        CoroutineScope(Dispatchers.IO).launch {
            addCatatanPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menambahkan catatan pembayaran")
                    }
                }
                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan catatan: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }

    fun deleteCatatanPembayaran(kavlingKode: String, onComplete: (msg: String) -> Unit) {
        isFinishOperation.value = false

        val request = DeleteCatatanPembayaranUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            deleteCatatanPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    catatanPembayaranLive.postValue(null)

                    withContext(Dispatchers.Main) {
                        onComplete("Berhasil menghapus catatan pembayaran")
                    }
                }

                result.onFailure { throwable ->
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menghapus catatan pembayaran: ${throwable.message}")
                    }
                }

                isFinishOperation.postValue(true)
            }
        }
    }


    // Wrapper functions for table view
    fun getBiayaMarketingColumnHeaders() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getBiayaMarketingColumnHeaders()

    fun getBiayaMarketingRowHeaders() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getBiayaMarketingRowHeaders()

    fun getBiayaMarketingCellItems() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getBiayaMarketingCellItems()

    fun getTotalBiayaMarketing() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getTotalBiayaMarketing()

    fun getCuanBiayaMarketing() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getCuanBiayaMarketing(
                lastTotalUangMasuk = listPembayaranLive.value?.last()?.totalUangMasuk ?: "0",
                biayaMarketer = feeMarketingLive.value?.biayaMarketer ?: "0",
            )

    fun getListTerminPembayaran(): Array<String> {
        val terminList = ArrayList<String>()

        listPembayaranLive.value?.forEach { pembayaran ->
            terminList.add(pembayaran.termin)
        }

        // We need to convert into an Array ... How botherful.
        return terminList.toTypedArray()
    }
}