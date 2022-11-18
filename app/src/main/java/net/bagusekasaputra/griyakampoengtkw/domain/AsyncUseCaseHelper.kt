package net.bagusekasaputra.griyakampoengtkw.domain

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase

class AsyncUseCaseHelper(private val operationStatusLiveData: MutableLiveData<Boolean>) {

    // The reason for returning a Job is because I want to cancel() all job in the ViewModel
    // in the onCleared() callback.
    fun <I: AsyncUseCase.Request, O> doWork(
        request: I,
        asyncUseCase: AsyncUseCase<I, O>,
        onSuccess: (resultObject: O?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
        successMsgOnUiThread: Boolean = true,
        failureMsgOnUiThread: Boolean = true,
    ): Job {
        operationStatusLiveData.value = false

        return CoroutineScope(Dispatchers.IO).launch {
            asyncUseCase.execute(request).collect { result ->

                result.onSuccess { resultObject ->
                    if (successMsgOnUiThread)
                        withContext(Dispatchers.Main) { onSuccess(resultObject) }
                    else
                        onSuccess(resultObject)
                }

                result.onFailure { throwable ->
                    if (failureMsgOnUiThread)
                        withContext(Dispatchers.Main) { onFailure(throwable) }
                    else
                        onFailure(throwable)
                }

                operationStatusLiveData.postValue(true)
            }
        }
    }
}