package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import java.io.File


class RoomIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalIndenBookingDataSource {

    override suspend fun getAllKeyIds(): Result<List<String>?> {
        TODO("Not yet implemented")
    }

}