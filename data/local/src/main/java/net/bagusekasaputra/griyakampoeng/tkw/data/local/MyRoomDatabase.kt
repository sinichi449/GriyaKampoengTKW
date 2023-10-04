package net.bagusekasaputra.griyakampoeng.tkw.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BackupRestoreDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BackupRestoreEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BaselinePembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BaselinePembayaranRoomFixDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BiayaLainDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BiayaLainRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BiayaMarketingV2RoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BiayaMarketingV2RoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BlockRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BlockRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.CatatanPembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.DataDiriIndenBookingDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.DataDiriIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.DataDiriRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.DataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FeeMarketingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FeeMarketingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoIdentitasIndenBookingDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoIdentitasIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoKuitansiRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoKuitansiRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoPembayaranEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoPembayaranIndenBookingDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoPembayaranIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoTambahanPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoTambahanPembayaranEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.HargaKavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.HargaKavlingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.HargaRumahDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.HargaRumahEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.ImageDataDiriDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.ImageDataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.ImageSprRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.ImageSprRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.IndenBookingAmbilKuitansiDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.IndenBookingAmbilKuitansiEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.IndenBookingCatatanPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.IndenBookingCatatanPembayaranEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.KavlingCatatanPembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.KavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.KavlingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.MetadataDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.MetadataEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranIndenBookingDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PengembalianDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PengembalianEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PengingatRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PengingatRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.StandardAmbilKuitansiDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.StandardAmbilKuitansiEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.TambahanPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.TambahanPembayaranEntity

@Database(
    entities = [
        KavlingRoomEntity::class, ImageDataDiriRoomEntity::class, BlockRoomEntity::class,
        DataDiriRoomEntity::class, FotoKuitansiRoomEntity::class, ImageSprRoomEntity::class,
        FotoPembayaranEntity::class, PembayaranRoomEntity::class, HargaKavlingRoomEntity::class,
        KavlingCatatanPembayaranRoomEntity::class, FeeMarketingRoomEntity::class,
        BiayaMarketingV2RoomEntity::class, PengingatRoomEntity::class, MetadataEntity::class,
        BiayaLainRoomEntity::class, BaselinePembayaranRoomEntity::class,
        FotoIdentitasIndenBookingEntity::class, DataDiriIndenBookingEntity::class,
        PembayaranIndenBookingEntity::class, StandardAmbilKuitansiEntity::class,
        HargaRumahEntity::class, FotoPembayaranIndenBookingEntity::class,
        IndenBookingCatatanPembayaranEntity::class, IndenBookingAmbilKuitansiEntity::class,
        BackupRestoreEntity::class, PengembalianEntity::class,
        FotoTambahanPembayaranEntity::class, TambahanPembayaranEntity::class,
    ],
    version = 36,
    exportSchema = true,
)
abstract class MyRoomDatabase: RoomDatabase() {

    abstract fun getMetadataDao(): MetadataDao

    abstract fun getKavlingDao(): KavlingRoomDao

    abstract fun getImageDataDiriDao(): ImageDataDiriDao

    abstract fun getBlockDao(): BlockRoomDao

    abstract fun getDataDiriDao(): DataDiriRoomDao

    abstract fun getFotoKuitansiDao(): FotoKuitansiRoomDao

    abstract fun getImageSprDao(): ImageSprRoomDao

    abstract fun getFotoPembayaranDao(): FotoPembayaranDao

    abstract fun getPembayaranDao(): PembayaranRoomDao

    abstract fun getHargaKavlingDao(): HargaKavlingRoomDao

    abstract fun getCatatanPembayaranDao(): CatatanPembayaranRoomDao

    abstract fun getBiayaMarketingV2Dao(): BiayaMarketingV2RoomDao

    abstract fun getFeeMarketingDao(): FeeMarketingRoomDao

    abstract fun getPengingatDao(): PengingatRoomDao

    abstract fun getBiayaLainDao(): BiayaLainDao

    abstract fun getBaselinePembayaranDao(): BaselinePembayaranRoomFixDao

    abstract fun getFotoIdentitasIndenBookingDao(): FotoIdentitasIndenBookingDao

    abstract fun getDataDiriIndenBookingDao(): DataDiriIndenBookingDao

    abstract fun getPembayaranIndenBookingDao(): PembayaranIndenBookingDao

    abstract fun getStandardAmbilKuitansiDao(): StandardAmbilKuitansiDao

    abstract fun getHargaRumahDao(): HargaRumahDao

    abstract fun getFotoPembayaranIndenBookingDao(): FotoPembayaranIndenBookingDao

    abstract fun getIndenBookingCatatanPembayaranDao(): IndenBookingCatatanPembayaranDao

    abstract fun getIndenBookingAmbilKuitansiDao(): IndenBookingAmbilKuitansiDao

    abstract fun getBackupRestoreDao(): BackupRestoreDao

    abstract fun getPengembalianDao(): PengembalianDao

    abstract fun getFotoTambahanPembayaranDao(): FotoTambahanPembayaranDao

    abstract fun getTambahanPembayaranDao(): TambahanPembayaranDao

}