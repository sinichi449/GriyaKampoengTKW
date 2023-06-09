package net.bagusekasaputra.griyakampoengtkw.presentation.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling

data class KavlingWithProgress(
    val blok: String,
    val kavling: Kavling,
    val progress: ProgressKavling,
) {

    fun updateStateFlow(stateFlow: MutableStateFlow<List<KavlingWithProgress>>) {
        stateFlow.update {
            val newList = it.toMutableList()
            newList.add(this)

            newList
        }
    }

}