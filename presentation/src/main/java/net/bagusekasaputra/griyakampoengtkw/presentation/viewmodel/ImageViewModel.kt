package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.AddFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.DeleteFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.GetFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.IsFotoPembayaranExistAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan.DeleteFotoTambahLuasanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan.GetFotoTambahLuasanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan.InsertFotoTambahLuasanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.DeleteFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.GetFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.InsertFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.GetImageDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.AddFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.GetFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.AddImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.DeleteImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.GetImageDataDiriByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.AddImageSprUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.GetImageSprByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.model.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    // Image Data Diri
    private val getImageDataDiriByKavlingKodeUseCase: GetImageDataDiriByKavlingKodeUseCase,
    private val addImageDataDiriUseCase: AddImageDataDiriUseCase,
    private val deleteImageDataDiriUseCase: DeleteImageDataDiriUseCase,
    // Image SPR
    private val getImageSprByKavlingKodeUseCase: GetImageSprByKavlingKodeUseCase,
    private val addImageSprUseCase: AddImageSprUseCase,
    // Foto Kuitansi ?
    private val getFotoKuitansiUseCase: GetFotoKuitansiUseCase,
    private val addFotoKuitansiUseCase: AddFotoKuitansiUseCase,
    // Foto Pembayaran
    private val getFotoPembayaranAsyncUseCase: GetFotoPembayaranAsyncUseCase,
    private val addFotoPembayaranAsyncUseCase: AddFotoPembayaranAsyncUseCase,
    private val deleteFotoPembayaranAsyncUseCase: DeleteFotoPembayaranAsyncUseCase,
    private val isFotoPembayaranExistAsyncUseCase: IsFotoPembayaranExistAsyncUseCase,
    // Image Data Diri Inden Booking,
    private val getImageDataDiriIndenBookingAsyncUseCase: GetImageDataDiriIndenBookingAsyncUseCase,
    // Foto Pembayaran Inden Booking
    private val getFotoPembayaranIndenBookingAsyncUseCase: GetFotoPembayaranIndenBookingAsyncUseCase,
    private val insertFotoPembayaranIndenBookingAsyncUseCase: InsertFotoPembayaranIndenBookingAsyncUseCase,
    private val deleteFotoPembayaranIndenBookingAsyncUseCase: DeleteFotoPembayaranIndenBookingAsyncUseCase,
    // Foto Tambah Luasan
    private val getFotoTambahLuasanUseCase: GetFotoTambahLuasanAsyncUseCase,
    private val insertFotoTambahLuasanUseCase: InsertFotoTambahLuasanAsyncUseCase,
    private val deleteFotoTambahLuasanUseCase: DeleteFotoTambahLuasanAsyncUseCase,
): ViewModel() {

    val fotoKuitansiLive = MutableLiveData<FotoKuitansi>()

    val imageSprLive = MutableLiveData<ImageSpr>()

    val imageDataDiriLive = MutableLiveData<ImageDataDiri?>()

    val fotoPembayaranLive = MutableLiveData<FotoPembayaran?>()

    val isFinishLoadingImage = MutableLiveData<Boolean>()

    private val _fotoTambahLuasan = MutableLiveData<UiState<FotoTambahLuasan?>>()
    val fotoTambahLuasan: LiveData<UiState<FotoTambahLuasan?>>
        get() = _fotoTambahLuasan

    // Image Data Diri Inden Booking
    private val _imageDataDiriIndenBooking = MutableLiveData<ImageDataDiriIndenBooking?>()
    val imageDataDiriIndenBooking: LiveData<ImageDataDiriIndenBooking?>
        get() = _imageDataDiriIndenBooking

    // Used in detail activity
    val allowExit: LiveData<Boolean>
        get() = isFinishLoadingImage

    val isFinishAddImage = MutableLiveData<Boolean>()

    private val jobs = ArrayList<Job>()

    private val asyncUseCaseHelper = AsyncUseCaseHelper(isFinishAddImage)

    var dataMode = DataMode.ONLINE

    private var readFotoTambahLuasanJob: Job? = null
    private var writeFotoTambahLuasanJob: Job? = null


    // Image Data Diri
    fun getImageDataDiri(kavlingKode: String, onFailure: (cause: String) -> Unit) {
        Log.d("DEBUG_ME", "ImageViewModel::getImageDataDiri() started on DataMode ${dataMode.name}")

        val request = GetImageDataDiriByKavlingKodeUseCase.Request(kavlingKode, dataMode)
        isFinishLoadingImage.value = false

        CoroutineScope(Dispatchers.IO).launch {
            getImageDataDiriByKavlingKodeUseCase.execute(request)
                .collect { response ->
                    val result = response.data.result
                    result.onSuccess { imageDatadiri ->
                        imageDatadiri?.let {
                            imageDataDiriLive.postValue(it)
                        }
                        isFinishLoadingImage.postValue(true)
                    }

                    result.onFailure {
                        withContext(Dispatchers.Main) {
                            onFailure("Gagal mendapatkan image data diri: ${it.message ?: "null"}")
                        }
                        isFinishLoadingImage.postValue(true)
                    }
                }
        }
    }

    fun addImageDataDiri(kavlingKode: String, uri: Uri, onComplete: (msg: String) -> Unit) {
        isFinishAddImage.value = false

        CoroutineScope(Dispatchers.IO).launch {
            val request = AddImageDataDiriUseCase.Request(kavlingKode, uri)

            addImageDataDiriUseCase.execute(request).collect { response ->
                val result = response.data.result

                result.onSuccess {
                    isFinishAddImage.postValue(true)
                }

                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onComplete("Gagal menambahkan image data diri: ${result.exceptionOrNull()?.message ?: "null"}")
                    }
                    isFinishAddImage.postValue(true)
                }
            }
        }
    }

    fun deleteImageDataDiri(onComplete: (msg: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageDataDiri = imageDataDiriLive.value

            if (imageDataDiri == null) {
                withContext(Dispatchers.Main) {
                    onComplete("Foto masih kosong")
                }
            } else {
                val request = DeleteImageDataDiriUseCase.Request(imageDataDiri)

                deleteImageDataDiriUseCase.execute(request).collect { response ->
                    val result = response.data.result

                    result.onSuccess {
                        withContext(Dispatchers.Main) {
                            onComplete("Berhasil menghapus foto")
                        }
                        imageDataDiriLive.postValue(null)
                    }

                    result.onFailure {
                        withContext(Dispatchers.Main) {
                            onComplete("Gagal menghapus foto: ${it.message}")
                        }
                    }
                }
            }
        }
    }


    // SPR
    fun getSprImage(kavlingKode: String, onFailure: (msg: String) -> Unit) {
        Log.d("DEBUG_ME", "ImageViewModel->getSPRImage(): Initiated on DataMode $dataMode")

        val request = GetImageSprByKavlingKodeUseCase.Request(kavlingKode, dataMode)
        isFinishAddImage.value = false

        CoroutineScope(Dispatchers.IO).launch {
            getImageSprByKavlingKodeUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    result.getOrNull()?.let { imageSprLive.postValue(it) }
                } else {
                    withContext(Dispatchers.Main) {
                        result.exceptionOrNull()?.let {
                            onFailure(it.message ?: "null")
                        }
                    }
                }

                isFinishAddImage.postValue(true)
            }
        }
    }

    fun addSprImage(
        kavlingKode: String,
        uri: Uri,
        onComplete: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        val request = AddImageSprUseCase.Request(kavlingKode, uri)
        isFinishAddImage.value = false

        CoroutineScope(Dispatchers.IO).launch {
            addImageSprUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    onComplete("Berhasil menambahkan foto SPR")
                } else {
                    result.exceptionOrNull()?.let {
                        onFailure(it.message ?: "null")
                    }
                }

                isFinishAddImage.postValue(true)
            }
        }
    }



    // Foto Kuitansi
    fun getFotoKuitansi(
        kavlingKode: String,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = GetFotoKuitansiUseCase.Request(kavlingKode)

        CoroutineScope(Dispatchers.IO).launch {
            getFotoKuitansiUseCase.execute(request).collect { response ->
                val result = response.data.result

                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        fotoKuitansiLive.postValue(it)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        result.exceptionOrNull()?.let {
                            onFailure(it.message ?: "null")
                        }
                    }
                }
            }
        }
    }

    fun addFotoKuitansi(
        kavlingKode: String,
        srcUri: Uri,
        onComplete: (msg: String) -> Unit,
    ) {
        val request = AddFotoKuitansiUseCase.Request(kavlingKode, srcUri)

        CoroutineScope(Dispatchers.IO).launch {
            addFotoKuitansiUseCase.execute(request).collect { response ->
                val result = response.data.result

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        onComplete("Berhasil menambahkan foto kuitansi")
                    } else {
                        val failCause = result.exceptionOrNull()
                        onComplete("Gagal menambahkan foto kuitansi: ${failCause?.message}")
                    }
                }
            }
        }
    }


    // Foto Pembayaran
    fun getFotoPembayaran(
        kavlingKode: String,
        termin: String,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = GetFotoPembayaranAsyncUseCase.Request(kavlingKode, termin, dataMode)

        // Will update the isFinishAddImage LiveData when the operation is finished.
        // It then will be observed by FullImageActivity to either dismiss() or show() a ProgressDialog.
        val gettingFotoPembayaranJob = asyncUseCaseHelper.doWork(
            request = request,
            asyncUseCase = getFotoPembayaranAsyncUseCase,
            onSuccess = { fotoPembayaran ->
                fotoPembayaranLive.postValue(fotoPembayaran)
            },
            onFailure = { throwable ->
                onFailure("Gagal mendapatkan Foto Pembayaran ${termin}: ${throwable.message}")
            },
            successMsgOnUiThread = false,
            failureMsgOnUiThread = true,
        )
        
        jobs.add(gettingFotoPembayaranJob)
    }

    fun addFotoPembayaran(
        kavlingKode: String,
        termin: String,
        uri: Uri,
        onProgress: () -> Unit = {},
        onComplete: (msg: String) -> Unit = {},
    ) {
        onProgress()
        val request = AddFotoPembayaranAsyncUseCase.Request(kavlingKode, termin, uri)

        val insertingFotoPembayaranJob = asyncUseCaseHelper.doWork(
            request = request,
            asyncUseCase = addFotoPembayaranAsyncUseCase,
            onSuccess = {
                onComplete("Berhasil menambahkan Foto Pembayaran $termin")
            },
            onFailure = { throwable ->
                onComplete("Gagal menambahkan Foto Pembayaran: ${throwable.message}")
            },
            successMsgOnUiThread = true,
            failureMsgOnUiThread = true,
        )

        jobs.add(insertingFotoPembayaranJob)
    }

    fun deleteFotoPembayaran(
        kavlingKode: String,
        termin: String,
        onProgress: () -> Unit = {},
        onComplete: (msg: String) -> Unit = {},
    ) {
        onProgress()

        val request = DeleteFotoPembayaranAsyncUseCase.Request(kavlingKode, termin)

        val deletingFotoPembayaranJob = asyncUseCaseHelper.doWork(
            request = request,
            asyncUseCase = deleteFotoPembayaranAsyncUseCase,
            onSuccess = {
                onComplete("Berhasil menghapus Foto Pembayaran $termin")
            },
            onFailure = { throwable ->
                onComplete("Gagal menghapus Foto Pembayaran $termin: ${throwable.message}")
            },
            // All of success and failure message are in UI Thread.
        )

        jobs.add(deletingFotoPembayaranJob)
    }

    fun checkIsExistFotoPembayaran(
        kavlingKode: String,
        termin: String,
        onComplete: (exist: Boolean) -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        val request = IsFotoPembayaranExistAsyncUseCase.Request(kavlingKode, termin, dataMode)

        val checkingFotoPembayaranJob = asyncUseCaseHelper.doWork(
            request = request,
            asyncUseCase = isFotoPembayaranExistAsyncUseCase,
            onSuccess = { onComplete(it ?: false) },
            onFailure = { onFailure(it.message ?: "null") },
        )

        jobs.add(checkingFotoPembayaranJob)
    }


    /**
     * Foto Pembayaran Inden Booking
     */
    fun getFotoPembayaranIndenBooking(
        keyId: String,
        termin: String,
        onProgress: () -> Unit = {},
        onSuccess: (uriStr: String?) -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetFotoPembayaranIndenBookingAsyncUseCase.Request(keyId, termin)
            getFotoPembayaranIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess(it?.uriStr)
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan foto : ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun insertFotoPembayaranIndenBooking(
        fotoPembayaran: FotoPembayaranIndenBooking,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = InsertFotoPembayaranIndenBookingAsyncUseCase.Request(fotoPembayaran)
            insertFotoPembayaranIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menambahkan foto pembayaran : ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    fun deleteFotoPembayaranIndenBooking(
        keyId: String,
        termin: String,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = DeleteFotoPembayaranIndenBookingAsyncUseCase.Request(keyId, termin)
            deleteFotoPembayaranIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal menghapus foto pembayaran : ${it.localizedMessage}")
                    }
                }
            }
        }
    }


    /**
     * Image Data Diri Inden Booking
     */
    fun getImageDataDiriIndenBooking(
        keyId: String,
        onProgress: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onFailure: (msg: String) -> Unit = {},
    ) {
        onProgress()

        viewModelScope.launch(Dispatchers.IO) {
            val request = GetImageDataDiriIndenBookingAsyncUseCase.Request(keyId)
            getImageDataDiriIndenBookingAsyncUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _imageDataDiriIndenBooking.postValue(it)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                result.onFailure {
                    withContext(Dispatchers.Main) {
                        onFailure("Gagal mendapatkan foto data diri : ${it.localizedMessage}")
                    }
                }
            }
        }
    }

    /**
     * Foto Tambah Luasan
     */
    fun getFotoTambahLuasan(tambahLuasanId: String, kavlingKode: String) {
        readFotoTambahLuasanJob?.cancel()

        _fotoTambahLuasan.value = UiState.Loading()

        val request = GetFotoTambahLuasanAsyncUseCase.Request(kavlingKode, tambahLuasanId)
        readFotoTambahLuasanJob = viewModelScope.launch(Dispatchers.IO) {
            getFotoTambahLuasanUseCase.execute(request).collect { result ->
                result.onSuccess { data ->
                    _fotoTambahLuasan.postValue(
                        UiState.Success(data)
                    )
                }
                result.onFailure {
                    _fotoTambahLuasan.postValue(UiState.Failure(it.message))
                }
            }
        }
    }

    fun insertFotoTambahLuasan(entity: FotoTambahLuasan) {
        writeFotoTambahLuasanJob?.cancel()

        _writeFotoTambahLuasanOperation.value = UiState.Loading()

        val request = InsertFotoTambahLuasanAsyncUseCase.Request(entity)
        writeFotoTambahLuasanJob = viewModelScope.launch(Dispatchers.IO) {
            insertFotoTambahLuasanUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _writeFotoTambahLuasanOperation.postValue(UiState.Success())
                }
                result.onFailure {
                    _writeFotoTambahLuasanOperation.postValue(UiState.Failure(it.message))
                }
            }
        }
    }

    fun deleteFotoTambahLuasan(tambahLuasanId: String, kavling: String) {
        writeFotoTambahLuasanJob?.cancel()

        _writeFotoTambahLuasanOperation.value = UiState.Loading()

        val request = DeleteFotoTambahLuasanAsyncUseCase.Request(kavling, tambahLuasanId)
        writeFotoTambahLuasanJob = viewModelScope.launch(Dispatchers.IO) {
            deleteFotoTambahLuasanUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _writeFotoTambahLuasanOperation.postValue(UiState.Success())
                }
                result.onFailure {
                    _writeFotoTambahLuasanOperation.postValue(UiState.Failure(it.message))
                }
            }
        }
    }


    /**
     * Utility Functions
     */
    fun <T> createImageTransport(sendIntent: String, content: T): ImageTransport<T> {
        return ImageTransport(sendIntent, content, dataMode)
    }

    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        return net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil.getBitmapFromUri(contentResolver, uri)
    }


    private val _writeFotoTambahLuasanOperation = MutableLiveData<UiState<Nothing?>>()
    val writeFotoTambahLuasanOperation: LiveData<UiState<Nothing?>>
        get() = _writeFotoTambahLuasanOperation

    override fun onCleared() {
        super.onCleared()

        jobs.forEach { it.cancel() }
    }
}