package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.AddFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.DeleteFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.GetFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.IsFotoPembayaranExistAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.AddFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.GetFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.AddImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.DeleteImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.GetImageDataDiriByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.AddImageSprUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.GetImageSprByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val getImageDataDiriByKavlingKodeUseCase: GetImageDataDiriByKavlingKodeUseCase,
    private val addImageDataDiriUseCase: AddImageDataDiriUseCase,
    private val deleteImageDataDiriUseCase: DeleteImageDataDiriUseCase,
    private val getImageSprByKavlingKodeUseCase: GetImageSprByKavlingKodeUseCase,
    private val addImageSprUseCase: AddImageSprUseCase,
    private val getFotoKuitansiUseCase: GetFotoKuitansiUseCase,
    private val addFotoKuitansiUseCase: AddFotoKuitansiUseCase,
    private val getFotoPembayaranAsyncUseCase: GetFotoPembayaranAsyncUseCase,
    private val addFotoPembayaranAsyncUseCase: AddFotoPembayaranAsyncUseCase,
    private val deleteFotoPembayaranAsyncUseCase: DeleteFotoPembayaranAsyncUseCase,
    private val isFotoPembayaranExistAsyncUseCase: IsFotoPembayaranExistAsyncUseCase,
): ViewModel() {

    val fotoKuitansiLive = MutableLiveData<FotoKuitansi>()

    val imageSprLive = MutableLiveData<ImageSpr>()

    val imageDataDiriLive = MutableLiveData<ImageDataDiri?>()

    val fotoPembayaranLive = MutableLiveData<FotoPembayaran?>()

    // Shamefully, I need this to be able to pass an argument of AddFotoPembayaranAsyncUseCase ...
    // This value is updated on "showFotoPembayaranSelectionDialog()" -> FormPembayaranFragment.
    val currentTermin  = MutableLiveData<String>()

    val isFinishLoadingImage = MutableLiveData<Boolean>()

    // Used in detail activity
    val allowExit: LiveData<Boolean>
        get() = isFinishLoadingImage

    val isFinishAddImage = MutableLiveData<Boolean>()

    private val jobs = ArrayList<Job>()

    private val asyncUseCaseHelper = AsyncUseCaseHelper(isFinishAddImage)

    var dataMode = DataMode.ONLINE


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
        val request = GetImageSprByKavlingKodeUseCase.Request(kavlingKode)
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
        uri: Uri,
        onComplete: (msg: String) -> Unit
    ) {
        val termin = currentTermin.value

        if (termin != null) {
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
        } else {
            onComplete("ERROR: Null termin argument passed on ImageViewModel.addFotoPembayaran()")
        }
    }

    fun deleteFotoPembayaran(
        kavlingKode: String,
        termin: String,
        onComplete: (msg: String) -> Unit
    ) {
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



    fun <T> createImageTransport(sendIntent: String, content: T): ImageTransport<T> {
        return ImageTransport(sendIntent, content, dataMode)
    }

    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        return net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil.getBitmapFromUri(contentResolver, uri)
    }



    override fun onCleared() {
        super.onCleared()

        jobs.forEach { it.cancel() }
    }
}