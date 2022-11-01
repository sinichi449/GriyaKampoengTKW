package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri

interface DataDiriRepository {

    fun getDataDiri(kavlingKode: String): Flow<DataDiri?>


}