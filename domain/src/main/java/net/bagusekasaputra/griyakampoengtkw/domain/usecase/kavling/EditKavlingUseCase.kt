package net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class EditKavlingUseCase(
    private val kavlingRepository: KavlingRepository
): UseCase<EditKavlingUseCase.Request, EditKavlingUseCase.Response>() {

    data class Request(
        val blockKode: String,
        val oldKavling: Kavling,
        val newType: String,
        val newPanjang: String,
        val newLebar: String,
    ): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        val ukuran = Kavling.getCompleteUkuran(request.newPanjang, request.newLebar)

        // Beware with "belumIsi" variable. If the oldKavling object has
        // "belumIsi" of false, you might get a problem by not PRESERVING
        // this into the newKavling.
        val belumIsi  = request.oldKavling.belumIsi

        val newKavling = Kavling(
            kode = request.oldKavling.kode, // I don't allow to set a new kavlingKode value in edit mode.
            warna = request.oldKavling.warna, // I don't set an interface for editting "warna".
            type = request.newType,
            ukuran = ukuran,
            belumIsi = belumIsi,
        )

        return kavlingRepository.updateKavling(
            blockCode = request.blockKode,
            oldKavling = request.oldKavling,
            newKavling = newKavling
        ).map {
            Response(it)
        }
    }
}