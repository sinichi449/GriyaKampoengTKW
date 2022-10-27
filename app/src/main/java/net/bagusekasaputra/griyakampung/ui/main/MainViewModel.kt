package net.bagusekasaputra.griyakampung.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampung.domain.entity.Kavling
import net.bagusekasaputra.griyakampung.domain.usecase.GetKavlingsByKodeUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getKavlingsByKodeUseCase: GetKavlingsByKodeUseCase
): ViewModel() {

    private val _kavlings = MutableLiveData<List<Kavling>>()
    val kavlings: LiveData<List<Kavling>>
        get() = _kavlings

    val currentKode = MutableLiveData<String>("A")


    fun getKavlings(kode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GetKavlingsByKodeUseCase.Request(kode)
            getKavlingsByKodeUseCase.execute(request).collect {
                val result = it.data.data
                _kavlings.postValue(result)
            }
        }
    }
}