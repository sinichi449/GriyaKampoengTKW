package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.device.DeviceFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.room.RoomFotoPembayaranDataSource
import java.io.File
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Qualifier
    annotation class RoomDatabase

    @Qualifier
    annotation class DeviceStorage


    // Foto Pembayaran
    @Provides
    @RoomDatabase
    fun provideRoomFotoPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalFotoPembayaranDataSource {
        return RoomFotoPembayaranDataSource(roomDatabase)
    }

    @Provides
    @DeviceStorage
    fun provideDeviceFotoPembayaranDataSource(externalFilesDir: File?): LocalFotoPembayaranDataSource {
        return DeviceFotoPembayaranDataSource(externalFilesDir)
    }
}