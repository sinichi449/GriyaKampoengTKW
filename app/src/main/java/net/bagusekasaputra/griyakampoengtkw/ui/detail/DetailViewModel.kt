package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.firebaseUrl
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(

): ViewModel() {

    private val _dataDiri = MutableLiveData<DataDiri>()
    val dataDiri: LiveData<DataDiri>
        get() = _dataDiri

    val currentKavlingKode = MutableLiveData<String>()

    fun getDataDiri(kavlingKode: String) {
        Log.d("DEBUG_ME", "KavlingKode: $kavlingKode")

        val mDatabase = FirebaseDatabase.getInstance(firebaseUrl).reference

        val blockKode = kavlingKode[0].toString()
        mDatabase.child(GriyaNodes.blocks).child(blockKode).child(kavlingKode).child(GriyaNodes.dataDiri).get()
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "Got value ${it.value}")
            }
            .addOnFailureListener {
                Log.d("DEBUG_ME", "Error getting data")
            }
    }
}