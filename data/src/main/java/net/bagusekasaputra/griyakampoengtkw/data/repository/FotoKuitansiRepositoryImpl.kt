package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoKuitansiRepository

class FotoKuitansiRepositoryImpl(
    private val localFotoKuitansiDataSource: LocalFotoKuitansiDataSource,
    private val contentResolver: ContentResolver,
): FotoKuitansiRepository {

    override fun getFoto(kavlingKode: String): Flow<Result<FotoKuitansi?>> {
        return flow<Result<FotoKuitansi?>> {
            val resultLocal = localFotoKuitansiDataSource.getFotoKuitansi(kavlingKode)

            val mappedResult = DataUtil.mapSingleResult(resultLocal, ::mapFotoKuitansi)

            emit(mappedResult)
        }
    }

    override fun addFoto(kavlingKode: String, dstUri: Uri): Flow<Result<Nothing?>> {
        return flow<Result<Nothing?>> {
            val model = FotoKuitansiModel(
                kavlingKode = kavlingKode,
                fotoUri = dstUri.toString(),
            )

            val resultLocal = localFotoKuitansiDataSource.addFotoKuitansi(model)

            emit(resultLocal)
        }
    }

    private fun mapFotoKuitansi(fotoKuitansiModel: FotoKuitansiModel): FotoKuitansi {
        return fotoKuitansiModel.let {
            FotoKuitansi(
                kavlingKode = it.kavlingKode,
                bitmap = ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.fotoUri)),
            )
        }
    }
}