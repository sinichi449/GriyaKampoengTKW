package net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class AddKavlingUseCase(
    private val kavlingRepository: KavlingRepository
): UseCase<AddKavlingUseCase.Request, AddKavlingUseCase.Response>() {

    data class Request(
        val blockKode: String,
        val noKavling: String,
        val warna: String,
        val type: String,
        val panjang: String,
        val lebar: String,
    ): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
//        // Formatting ukuran as WidthXHeight, so "6x12", so to speak.
//        val ukuran = Kavling.getCompleteUkuran(request.panjang, request.lebar)
//
//        // I've made an error where I think that the "request.kode" is "A5".
//        // But, it's actually just the "5" part, the number part.
//        val kavlingKode = Kavling.getKavlingKode(request.blockKode, request.noKavling) // this is perfect.
//
//        // Default parameter for "belumIsi" is always true
//        val belumIsi = true
//
//        val kavling = Kavling(
//            warna = request.warna,
//            type = request.type,
//            kode = kavlingKode,
//            ukuran = ukuran,
//            belumIsi = belumIsi,
//        )
//        return kavlingRepository.addKavling(request.blockKode, kavling).map {
//            Response(it)
//        }
        return flow {
            emit(Response(Result.failure(NotImplementedError("Penambahan kavling tidak diizinkan"))))
        }
    }
}