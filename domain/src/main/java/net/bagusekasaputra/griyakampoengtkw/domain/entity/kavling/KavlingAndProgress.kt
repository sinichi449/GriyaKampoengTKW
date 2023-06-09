package net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling

data class KavlingAndProgress(
    val blok: String,
    val kavling: Kavling,
    val progress: ProgressKavling,
) {

    fun updateStateFlow(stateFlow: MutableStateFlow<List<KavlingAndProgress>>) {
        stateFlow.update {
            val newList = it.toMutableList()
            newList.add(this)

            newList
        }
    }

}