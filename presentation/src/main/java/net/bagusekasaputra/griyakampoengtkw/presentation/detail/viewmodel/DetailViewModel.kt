package net.bagusekasaputra.griyakampoengtkw.presentation.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetAllBiayaMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.dataDiri.GetDataDiriAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing.GetFeeMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.hargaKavling.GetHargaKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetAllPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.AddBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.DeleteAllBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.DeleteSingleBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.EditBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.AddCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.DeleteCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.AddFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.DeleteFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.UpdateFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.AddHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.AddPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.DeleteAllPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.DeletePembayaranByTerminUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.UpdatePembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.tableview.biayaMarketing.TableBiayaMarketingHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.tableview.formPembayaran.PembayaranCell
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.tableview.formPembayaran.PembayaranColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.detail.tableview.formPembayaran.PembayaranRowHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.logEvent
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumCell
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumColumnHeaders
import net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview.TumRowHeaders
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextLong

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val addDataDiriUseCase: AddDataDiriUseCase,
    private val getDataDiriAsyncUseCase: GetDataDiriAsyncUseCase,
    private val deleteDataDiriUseCase: DeleteDataDiriUseCase,
    private val getHargaKavlingAsyncUseCase: GetHargaKavlingAsyncUseCase,
    private val addHargaKavlingUseCase: AddHargaKavlingUseCase,
    private val getAllPembayaranAsyncUseCase: GetAllPembayaranAsyncUseCase,
    private val addPembayaranUseCase: AddPembayaranUseCase,
    private val updatePembayaranUseCase: UpdatePembayaranUseCase,
    private val deletePembayaranByTerminUseCase: DeletePembayaranByTerminUseCase,
    private val deleteAllPembayaranUseCase: DeleteAllPembayaranUseCase,
    private val getAllBiayaMarketingByKavlingKodeAsyncUseCase: GetAllBiayaMarketingByKavlingKodeAsyncUseCase,
    private val addBiayaMarketingUseCase: AddBiayaMarketingUseCase,
    private val editBiayaMarketingUseCase: EditBiayaMarketingUseCase,
    private val deleteSingleBiayaMarketingUseCase: DeleteSingleBiayaMarketingUseCase,
    private val deleteAllBiayaMarketingUseCase: DeleteAllBiayaMarketingUseCase,
    private val getFeeMarketingByKavlingKodeAsyncUseCase: GetFeeMarketingByKavlingKodeAsyncUseCase,
    private val addFeeMarketingUseCase: AddFeeMarketingUseCase,
    private val updateFeeMarketingUseCase: UpdateFeeMarketingUseCase,
    private val deleteFeeMarketingUseCase: DeleteFeeMarketingUseCase,
    private val getCatatanPembayaranAsyncUseCase: GetCatatanPembayaranAsyncUseCase,
    private val addCatatanPembayaranUseCase: AddCatatanPembayaranUseCase,
    private val deleteCatatanPembayaranUseCase: DeleteCatatanPembayaranUseCase,
): ViewModel() {

    val dataDiriLive = MutableLiveData<DataDiri?>()

    val hargaKavlingLive = MutableLiveData<HargaKavling>()

    val listPembayaranLive = MutableLiveData<List<Pembayaran>?>()

    val feeMarketingLive = MutableLiveData<FeeMarketing?>()

    val listBiayaMarketingLive = MutableLiveData<List<BiayaMarketing>?>()

    val catatanPembayaranLive = MutableLiveData<CatatanPembayaran?>()

    val listReportTotalUangMasukLive = MutableLiveData<List<ReportTotalUangMasuk>>()

    val currentKavlingKode = MutableLiveData<String>()

    val isFinishOperation = MutableLiveData<Boolean>()

    val isFinishAddImage = MutableLiveData<Boolean>()

    /**
     * To ensure just one time loading of Data Diri, Pembayaran, and Biaya Marketing.
     *
     * Currently I'm prioritizing the List<?> data, which require larger amount of bandwidth
     * and possibly impacting the device performance.
     *
     * For write operations such as edit, delete, and add, the UI need to be refreshed.
     * In such operations, we need to set "xRefreshed" to be false.
     *
     * Also, when user invokes refresh command, like swipe-to-refresh,
     * we also need to update these value into FALSE.
     *
     * Whenever the "GET" operation is success, we need to update these value into TRUE.
     */
    val dataDiriRefreshed = MutableLiveData(false)
    val formPembayaranRefreshed = MutableLiveData(false)
    val biayaMarketingRefreshed = MutableLiveData(false)

    // Need to be put on UseCase argument
    var offlineMode = false

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    // The list of Coroutines/Flows job that need to be cleared on
    // the onCleared() callback. See below.
    private val asyncJobs = ArrayList<Job>()


    /**
     * Data Diri
     */
    fun getDataDiri(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        if (dataDiriRefreshed.value != true) {
            logEvent("Syncing data diri ...")
            val request = GetDataDiriAsyncUseCase.Request(kavlingKode, offlineMode)

            val gettingDataDiriJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getDataDiriAsyncUseCase,
                onSuccess = {
                    dataDiriLive.postValue(it)
                    dataDiriRefreshed.postValue(true)
                },
                onFailure = {
                    onFailure("Gagal mendapatkan data diri: ${it.message}")
                },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingDataDiriJob)
        }
    }

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri, onComplete: (msg: String) -> Unit) {
        dataDiriRefreshed.value = false
        isFinishOperation.value  = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddDataDiriUseCase.Request(kavlingKode, dataDiri)

            addDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                       onComplete( "Berhasil menambahkan data ${dataDiri.nama}")
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
        dataDiriRefreshed.value = false
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


    /**
     * Harga Kavling
     */
    fun getHargaKavling(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        val request = GetHargaKavlingAsyncUseCase.Request(kavlingKode, offlineMode)

        val gettingHargaKavlingJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getHargaKavlingAsyncUseCase,
            onSuccess = {
                hargaKavlingLive.postValue(it ?: HargaKavling(kavlingKode, "0", "0"))
            },
            onFailure = {
                onFailure("Gagal mendapatkan harga kavling: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(gettingHargaKavlingJob)

        gettingHargaKavlingJob.invokeOnCompletion {
            logEvent("Getting harga kavling completed!")
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


    /**
     * Pembayaran
     */
    fun getAllPembayaran(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        if (formPembayaranRefreshed.value != true) {
            logEvent("Syncing pembayaran ...")
            val request = GetAllPembayaranAsyncUseCase.Request(kavlingKode, offlineMode)

            val gettingAllPembayaranJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllPembayaranAsyncUseCase,
                onSuccess = {
                    // so many bugs caused by this unchecked isNotEmpty()
                    if (it?.isNotEmpty() == true)
                        listPembayaranLive.postValue(it)

                    formPembayaranRefreshed.postValue(true)
                },
                onFailure = {
                    onFailure("Gagal mendapatkan pembayaran: ${it.message}")
                },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingAllPembayaranJob)
        }
    }

    fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        formPembayaranRefreshed.value = false
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

    fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
        onComplete: (msg: String) -> Unit,
    ) {
        formPembayaranRefreshed.value = false
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
        formPembayaranRefreshed.value = false
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
        formPembayaranRefreshed.value = false
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

    /**
     * Fee Marketing
     */
    fun getFeeMarketing(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        val request = GetFeeMarketingByKavlingKodeAsyncUseCase.Request(kavlingKode, offlineMode)

        val gettingFeeMarketingJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getFeeMarketingByKavlingKodeAsyncUseCase,
            onSuccess = {
                // We need to transform fee marketing into a comma separated value
                it?.biayaMarketer =
                    NumberUtil.formatLongToString(it?.biayaMarketer?.toLong() ?: 0)

                feeMarketingLive.postValue(it)
            },
            onFailure = {
                onFailure("Gagal mendapatkan biaya afiliasi: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(gettingFeeMarketingJob)
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


    /**
     * Biaya Marketing
     */
    fun getAllBiayaMarketing(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        if (biayaMarketingRefreshed.value != true) {
            logEvent("Syncing biaya marketing ...")
            val request =
                GetAllBiayaMarketingByKavlingKodeAsyncUseCase.Request(kavlingKode, offlineMode)

            val gettingAllBiayaMarketingJob = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllBiayaMarketingByKavlingKodeAsyncUseCase,
                onSuccess = {
                    listBiayaMarketingLive.postValue(it)

                    biayaMarketingRefreshed.postValue(true)
                },
                onFailure = {
                    onFailure("Gagal mendapatkan biaya marketing: ${it.message}")
                },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingAllBiayaMarketingJob)
        }
    }

    fun addBiayaMarketing(
        kavlingKode: String,
        jenisBiaya: String,
        harga: String,
        onComplete: (msg: String) -> Unit,
    ) {
        biayaMarketingRefreshed.value = false
        isFinishOperation.value = false

        val biayaMarketing = BiayaMarketing(
            timeMillis = System.currentTimeMillis(),
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
        biayaMarketingRefreshed.value = false
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
        biayaMarketingRefreshed.value = false
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
        biayaMarketingRefreshed.value = false
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


    /**
     * Catatan Pembayaran
     */
    fun getCatatanPembayaran(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        val request = GetCatatanPembayaranAsyncUseCase.Request(kavlingKode, offlineMode)

        val gettingCatatanPembayaranJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = getCatatanPembayaranAsyncUseCase,
            onSuccess = {
                catatanPembayaranLive.postValue(it)
            },
            onFailure = {
                onFailure("Gagal mendapatkan catatan pembayaran: ${it.message}")
            },
            successMsgOnUiThread = false,
        )

        asyncJobs.add(gettingCatatanPembayaranJob)
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

    fun getBiayaMarketingCellItems(): List<List<String>> {
        return TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getBiayaMarketingCellItems()
    }

    fun getTotalBiayaMarketing() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getTotalBiayaMarketing()

    fun getCuanBiayaMarketing() =
        TableBiayaMarketingHelper(listBiayaMarketingLive.value)
            .getCuanBiayaMarketing(
                lastTotalUangMasuk = listPembayaranLive.value?.last()?.totalUangMasuk ?: "0",
                biayaMarketer = feeMarketingLive.value?.biayaMarketer ?: "0",
            )

    fun getAllArrayTerminPembayaran(): Array<String> {
        val terminList = ArrayList<String>()

        listPembayaranLive.value?.forEach { pembayaran ->
            terminList.add(pembayaran.termin)
        }

        // We need to convert into an Array ... How botherful.
        return terminList.toTypedArray()
    }

    fun getBelumIsiFotoTerminPembayaran(): Array<String> {
        val terminList = ArrayList<String>()

        listPembayaranLive.value?.forEach { pembayaran ->
            if (pembayaran.sudahIsiFotoPembayaran.not()) {
                terminList.add(pembayaran.termin)
            }
        }

        return terminList.toTypedArray()
    }

    fun getSudahIsiFotoTerminPembayaran(): Array<String> {
        val terminList = ArrayList<String>()

        listPembayaranLive.value?.forEach { pembayaran ->
            if (pembayaran.sudahIsiFotoPembayaran)
                terminList.add(pembayaran.termin)
        }

        return terminList.toTypedArray()
    }


    fun getPembayaranTableColumnHeaders(): List<PembayaranColumnHeader> {
        return listOf(
            PembayaranColumnHeader(text = "Tanggal"),
            PembayaranColumnHeader(text = "Jumlah Uang dibayar"),
            PembayaranColumnHeader(text = "Total Uang Masuk"),
            PembayaranColumnHeader(text = "Persentase"),
            PembayaranColumnHeader(text = "Keterangan Progress"),
        )
    }

    fun getPembayaranTableRowHeaders(): List<PembayaranRowHeader> {
        // In this case the row headers of Pembayaran table are the Termins.
        // First we populate the termins in a list, then return that list as Row Headers.
        val termins = mutableListOf<PembayaranRowHeader>()

        val listPembayaran = listPembayaranLive.value
        if (listPembayaran != null) {
            listPembayaran.forEach { pembayaran ->
                // Sudah Isi Foto property means to be used as a marker.
                // In this case I will mark a yellow background color on the row headers
                // whenever sudahIsiFoto is true.
                termins.add(
                    PembayaranRowHeader(
                    text = pembayaran.termin,
                    sudahIsiFoto = pembayaran.sudahIsiFotoPembayaran
                )
                )
            }
        } else {
            // If null, return "-" character, I think ...
            termins.add(PembayaranRowHeader(text = "-"))
        }

        return termins
    }

    fun getPembayaranTableCellItems(): List<List<PembayaranCell>> {
        val firstOrderList = mutableListOf<List<PembayaranCell>>()

        val listPembayaran = listPembayaranLive.value
        if (listPembayaran != null) {
            listPembayaran.forEach { pembayaran ->
                val secondOrderList = mutableListOf<PembayaranCell>().apply {
                    add(PembayaranCell(mData = pembayaran.tanggal))
                    add(PembayaranCell(mData = pembayaran.jumlahUangDibayar))
                    add(PembayaranCell(mData = pembayaran.totalUangMasuk))
                    add(PembayaranCell(mData = pembayaran.presentase.toString()))
                    add(PembayaranCell(mData = pembayaran.keterangan))
                }

                firstOrderList.add(secondOrderList)
            }
        } else {
            firstOrderList.add(
                listOf(
                    PembayaranCell(mData = "-"), // Tanggal
                    PembayaranCell(mData = "-"), // Jumlah Uang dibayar
                    PembayaranCell(mData = "-"), // Total uang masuk
                    PembayaranCell(mData = "-"), // Persentase
                    PembayaranCell(mData = "-"), // Keterangan Progress
                )
            )
        }

        return firstOrderList
    }


    enum class JenisPembayaran(val text: String) {
        ITJ("ITJ"),
        DP("DP"),
        TERMIN("Termin"),
    }

    fun getNextPembayaranSequence(jenisPembayaran: JenisPembayaran): String {
        // Check if not null listPembayaran.
        // If null returns "1"
        val listPembayaran = listPembayaranLive.value

        if (listPembayaran != null) {
            // Check if any requested jenis pembayaran Exists
            val requestedJenisPembayaranList = listPembayaran.filter { it.termin.startsWith(jenisPembayaran.text) }
            return if (requestedJenisPembayaranList.isNotEmpty()) {
                // If exists, then get the last index of the requested pembayaran.
                // I speculate that the UseCase already do the sorting, so
                // the last of Any Pembayaran Sequence should be on the last index.
                val lastPembayaran = requestedJenisPembayaranList.last()

                // +1 on the last number of urutan
                val urutan = lastPembayaran.getUrutan()
                urutan.plus(1).toString()
            } else {
                "1"
            }
        } else {
            return "1"
        }
    }

    fun getTerminJumlahUangDibayar(): String? {
        val listPembayaran = listPembayaranLive.value

        val listTermins = listPembayaran?.filter { it.termin.startsWith("Termin") }

        if (listTermins?.isNotEmpty() == true) {
            return listTermins.last().jumlahUangDibayar
        } else {
            return null
        }
    }


    /**
     * Total Uang Masuk Table in ReportFragment's helpers
     */
    fun getOverallTotalMasuk(): String {
        val listReportTum = listReportTotalUangMasukLive.value
        var overallTotalUangMasuk = 0L

        listReportTum?.forEach {
            overallTotalUangMasuk += it.uangMasuk
        }

        return "Rp. ${NumberUtil.formatLongToString(overallTotalUangMasuk)}"
    }

    fun getOverallTotalPengeluaran(): String {
        val listReportTum = listReportTotalUangMasukLive.value

        var overallFeeMarketing = 0L
        var overallBiayaMarketing = 0L

        listReportTum?.forEach {
            overallFeeMarketing += it.feeMarketing
            overallBiayaMarketing += it.biayaMarketing
        }

        val pengeluaran = overallFeeMarketing + overallBiayaMarketing

        return "- Rp. ${NumberUtil.formatLongToString(pengeluaran)}"
    }

    fun getOverallTotalCuan(): String {
        val listReportTum = listReportTotalUangMasukLive.value

        var overallCuan = 0L

        listReportTum?.forEach {
            overallCuan += it.totalCuan
        }

        return "Rp. ${NumberUtil.formatLongToString(overallCuan)}"
    }




    fun getTotalUangMasukColumnHeaders(): List<TumColumnHeaders> {
        return listOf(
            TumColumnHeaders("Uang Masuk"),
            TumColumnHeaders("Fee Marketing"),
            TumColumnHeaders("Biaya Marketing"),
            TumColumnHeaders("Total Cuan"),
        )
    }

    fun getTotalUangMasukRowHeaders(): List<TumRowHeaders> {
        val listReportTum = listReportTotalUangMasukLive.value
        return if (listReportTum != null) {
            val rowHeaders = mutableListOf<TumRowHeaders>()

            listReportTum.forEach {
                rowHeaders.add(TumRowHeaders(it.kavling))
            }

            rowHeaders
        } else {
            listOf(TumRowHeaders("-"))
        }
    }

    fun getTotalUangMasukCellItems(): List<List<TumCell>> {
        val listReportTum = listReportTotalUangMasukLive.value

        return if (listReportTum != null) {
            val firstOrderList = mutableListOf<List<TumCell>>()

            listReportTum.forEach {
                val secondOrderList = mutableListOf<TumCell>().apply {
                    // parse numbers
                    val parsedUangMasuk = NumberUtil.formatLongToString(it.uangMasuk)
                    val parsedFeeMarketing = NumberUtil.formatLongToString(it.feeMarketing)
                    val parsedBiayaMarketing = NumberUtil.formatLongToString(it.biayaMarketing)
                    val parsedTotalCuan = NumberUtil.formatLongToString(it.totalCuan)

                    add(TumCell(parsedUangMasuk))
                    add(TumCell(parsedFeeMarketing))
                    add(TumCell(parsedBiayaMarketing))
                    add(TumCell(parsedTotalCuan))
                }

                firstOrderList.add(secondOrderList)
            }

            firstOrderList

        } else {
            listOf(
                listOf(
                    TumCell("-"),
                    TumCell("-"),
                    TumCell("-"),
                    TumCell("-"),
                )
            )
        }
    }

    /**
     * Mocking uang masuk
     */
    fun provideReportUangMasuk() {
        isFinishOperation.value = false

        val randomUangMasuk = { Random.nextLong(10L..2500L) * 100000L }
        val randomMarketing = { Random.nextLong(100L..1000L) * 10000L }

        viewModelScope.launch {
            val blocks = listOf("A", "B", "C")
            val listReport = mutableListOf<ReportTotalUangMasuk>()

            blocks.forEach { block ->
                val sumBlock = when (block) {
                    "A" -> 14
                    "B" -> 20
                    "C" -> 9
                    else -> 0
                }

                (1..sumBlock).forEach {
                    val uangMasuk = randomUangMasuk()
                    val feeMarketing = randomMarketing()
                    val biayaMarketing = randomMarketing()
                    val totalCuan = uangMasuk - feeMarketing - biayaMarketing

                    listReport.add(
                        ReportTotalUangMasuk(
                            kavling = "$block$it",
                            uangMasuk = uangMasuk,
                            feeMarketing = feeMarketing,
                            biayaMarketing = biayaMarketing,
                            totalCuan = totalCuan,
                        )
                    )
                }
            }


            listReportTotalUangMasukLive.postValue(listReport)


            isFinishOperation.postValue(true)
        }
    }

    override fun onCleared() {
        super.onCleared()

        asyncJobs.forEach { it.cancel() }
    }
}