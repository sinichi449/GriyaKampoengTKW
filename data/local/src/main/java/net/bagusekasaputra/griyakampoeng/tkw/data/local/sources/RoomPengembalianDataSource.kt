package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toPengembalianEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toPengembalianModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel

class RoomPengembalianDataSource(
    myRoomDatabase: MyRoomDatabase,
): LocalPengembalianDataSource {

    private val dao by lazy {
        myRoomDatabase.getPengembalianDao()
    }

    override suspend fun get(keyId: String) = runCatching {
        val entity = dao.get(keyId)

        entity?.toPengembalianModel()
    }

    override suspend fun getKeyIds() = runCatching {
        val entities = dao.getAll()
        val keyIds = buildList {
            entities?.forEach { item -> add(item.keyId) }
        }

        keyIds.ifEmpty { null }
    }

    override suspend fun insert(model: PengembalianModel) = runCatching {
        dao.insert(model.toPengembalianEntity())

        Unit
    }

    override suspend fun insertAll(models: List<PengembalianModel>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(keyId: String, newModel: PengembalianModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(keyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun invalidate() = runCatching {
        dao.deleteAll()
    }

    override fun getTableName(): String {
        return "pengembalianPembayaran"
    }
}