package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.calonPembeli

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CalonPembeliRepository

class GetAllCalonPembeliAsyncUseCase(
    private val calonPembeliRepository: CalonPembeliRepository
): AsyncUseCase<GetAllCalonPembeliAsyncUseCase.Request, List<CalonPembeli>>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<CalonPembeli>?>> {
        return calonPembeliRepository.getAll().map { result ->
            result.map { listCalonPembeli ->
                CalonPembeli.sortbyLastModified(listCalonPembeli)
            }
        }
    }
}