package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking

import android.util.Log
import com.google.firebase.database.DatabaseReference
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel

class FirebaseIndenBookingDataSource(
    databaseReference: DatabaseReference
): RemoteIndenBookingDataSource {

    override suspend fun getAll(): Result<List<IndenBookingModel>?> {
        Log.d("DEBUG_ME", "FirebaseIndenBookingDataSource::getAll() -> Called!")

        return Result.success(null)
    }

    override suspend fun insert(model: IndenBookingModel): Result<Nothing?> {
        Log.d("DEBUG_ME", "FirebaseIndenBookingDataSource::insert() -> Called!")

        return Result.success(null)
    }
}