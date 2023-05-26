package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import java.io.File


class RoomIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalIndenBookingDataSource {

    override suspend fun getAllKeyIds(): Result<List<String>?> {
        TODO("Not yet implemented")
    }

    override suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?> {
        TODO("Not yet implemented")
    }

}