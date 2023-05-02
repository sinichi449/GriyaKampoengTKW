package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import net.bagusekasaputra.griyakampoengtkw.domain.AsyncUseCaseHelper
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.AddPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.DeletePengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.GetAllPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.TurnOnOffPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.UpdatePengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import javax.inject.Inject

@HiltViewModel
class PengingatViewModel @Inject constructor(
    private val getAllPengingatAsyncUseCase: GetAllPengingatAsyncUseCase,
    private val addPengingatAsyncUseCase: AddPengingatAsyncUseCase,
    private val deletePengingatAsyncUseCase: DeletePengingatAsyncUseCase,
    private val updatePengingatAsyncUseCase: UpdatePengingatAsyncUseCase,
    private val turnOnOffPengingatAsyncUseCase: TurnOnOffPengingatAsyncUseCase,
): ViewModel() {

    val isFinishOperation = MutableLiveData(true)

    private val _listPengingatLive = MutableLiveData(
        listOf(
            Pengingat(
                title = "Pengingat pertama saya!",
                content = "Lorem ipsum dolor sit amet",
                date = "01/01/2022",
                time = "00:00",
                isActive = false,
            )
        )
    )
    val listPengingat: LiveData<List<Pengingat>?>
        get() = _listPengingatLive

    val pengingatRefreshed = MutableLiveData(false)

    private val asyncHelper = AsyncUseCaseHelper(isFinishOperation)

    private val asyncJobs = mutableListOf<Job>()


    fun getAllPengingat(onFailure: (msg: String) -> Unit) {
        val request = GetAllPengingatAsyncUseCase.Request
        val isRefreshed = pengingatRefreshed.value

        if (isRefreshed == false) {
            val gettingAllPengingatJobs = asyncHelper.doWork(
                request = request,
                asyncUseCase = getAllPengingatAsyncUseCase,
                onSuccess = {
                    _listPengingatLive.postValue(it)

                    pengingatRefreshed.postValue(true)
                },
                onFailure = { onFailure("Gagal mendapatkan pengingat: ${it.message}") },
                successMsgOnUiThread = false,
            )

            asyncJobs.add(gettingAllPengingatJobs)
        }
    }

    fun addPengingat(
        title: String,
        content: String,
        date: String,
        time: String,
        onComplete: (msg: String, data: Pengingat?) -> Unit,
    ) {
        val pengingat = Pengingat(
            title = title,
            content = content,
            date = date,
            time = time,
            isActive = true,
        )
        val request = AddPengingatAsyncUseCase.Request(pengingat)

        pengingatRefreshed.value = false

        val addingPengingatJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = addPengingatAsyncUseCase,
            onSuccess = { onComplete("Berhasil menambahkan pengingat", pengingat) },
            onFailure = { onComplete("Gagal menambahkan pengingat: ${it.message}", null) },
        )

        asyncJobs.add(addingPengingatJob)
    }

    fun updatePengingat(
        oldPengingat: Pengingat,
        newTitle: String,
        newContent: String,
        newDate: String,
        newTime: String,
        isActive: Boolean,
        onComplete: (msg: String, data: Pengingat?) -> Unit
    ) {
        val newPengingat = Pengingat(
            id = oldPengingat.id,
            title = newTitle,
            content = newContent,
            date = newDate,
            time = newTime,
            isActive = isActive,
        )

        val request = UpdatePengingatAsyncUseCase.Request(oldPengingat, newPengingat)

        pengingatRefreshed.value = false

        val updatingPengingatJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = updatePengingatAsyncUseCase,
            onSuccess = { onComplete("Berhasil mengubah pengingat", newPengingat) },
            onFailure = { onComplete("Gagal mengubah pengingat: ${it.message}", null) }
        )

        asyncJobs.add(updatingPengingatJob)
    }

    fun deletePengingat(
        oldPengingat: Pengingat,
        onComplete: (msg: String, id: Long?) -> Unit,
    ) {
        val request = DeletePengingatAsyncUseCase.Request(oldPengingat)

        pengingatRefreshed.value = false

        val deletingPengingatJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = deletePengingatAsyncUseCase,
            onSuccess = { onComplete("Berhasil menghapus pengingat", oldPengingat.id) },
            onFailure = { onComplete("Gagal menghapus pengingat: ${it.message}", null) },
        )

        asyncJobs.add(deletingPengingatJob)
    }

    fun turnOnOrOffPengingat(pengingat: Pengingat, onComplete: (msg: String) -> Unit) {
        val request = TurnOnOffPengingatAsyncUseCase.Request(pengingat)

        pengingatRefreshed.value = false

        val turningOffPengingatJob = asyncHelper.doWork(
            request = request,
            asyncUseCase = turnOnOffPengingatAsyncUseCase,
            onSuccess = {
                if (pengingat.isActive) onComplete("Pengingat telah dimatikan")
                else onComplete("Pengingat diaktifkan")
            },
            onFailure = { onComplete("Gagal mengatur pengingat: ${it.message}") }
        )

        asyncJobs.add(turningOffPengingatJob)
    }


    override fun onCleared() {
        asyncJobs.forEach { it.cancel() }

        super.onCleared()
    }
}