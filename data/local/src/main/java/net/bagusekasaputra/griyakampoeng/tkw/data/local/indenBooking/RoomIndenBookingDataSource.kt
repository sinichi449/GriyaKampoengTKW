package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel

class RoomIndenBookingDataSource(
    roomDatabase: MyRoomDatabase
): LocalIndenBookingDataSource {

    override suspend fun getAll(): Result<List<IndenBookingModel>?> {
        Log.d("DEBUG_ME", "RoomIndenBookingDataSource::getAll() -> Called!")

        return Result.success(null)
    }

    override suspend fun insert(model: IndenBookingModel): Result<Nothing?> {
        Log.d("DEBUG_ME", "RoomIndenBookingDataSource::insert() -> Called!")

        return Result.success(null)
    }

    override suspend fun insertAll(listModel: List<IndenBookingModel>): Result<Nothing?> {
        Log.d("DEBUG_ME", "RoomIndenBookingDataSource::insertAll() -> Called!")

        return Result.success(null)
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        Log.d("DEBUG_ME", "RoomIndenBookingDataSource::deleteAll() -> Called!")

        return Result.success(null)
    }
}