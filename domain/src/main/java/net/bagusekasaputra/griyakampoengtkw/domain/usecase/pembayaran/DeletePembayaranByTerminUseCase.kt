package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class DeletePembayaranByTerminUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): UseCase<DeletePembayaranByTerminUseCase.Request, DeletePembayaranByTerminUseCase.Response>() {

    data class Request(val kavlingKode: String, val termin: String): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.deletePembayaranByTermin(request.kavlingKode, request.termin)
            // Deleting Foto Pembayaran along with deleting Pembayaran.
//            .zip(fotoPembayaranRepository.deleteFotoPembayaran(request.kavlingKode, request.termin)) { deletePembayaran, deleteFoto ->
//                // Both operation success
//                if ((deletePembayaran.isSuccess) and (deleteFoto.isSuccess)) {
//                    return@zip Result.success(true)
//                } else {
//                    return@zip when {
//                        // Delete Pembayaran success, but Delete Foto fails.
//                        deletePembayaran.isSuccess and deleteFoto.isFailure ->
//                            Result.failure<Boolean>(deleteFoto.exceptionOrNull() ?: UnknownError("ERROR: Menghapus pembayaran berhasil, foto TIDAK berhasil dihapus,"))
//
//                        // Delete Pembayaran fails, but Delete Foto success.
//                        deletePembayaran.isFailure and deleteFoto.isSuccess ->
//                            Result.failure<Boolean>(deletePembayaran.exceptionOrNull() ?: UnknownError("ERROR: GAGAL menghapus pembayaran, foto berhasil dihapus."))
//
//                        // IDK!! Maybe both are fails.
//                        else ->  Result.failure<Boolean>(UnknownError("ERROR: Kegagalan sistem yang tidak diketahui terjadi."))
//                    }
//                }
//            }
            .map {
                Response(it)
            }
    }
}