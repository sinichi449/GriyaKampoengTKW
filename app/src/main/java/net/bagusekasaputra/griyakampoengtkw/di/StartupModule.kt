package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.data.FirebaseTahapanRepository
import net.bagusekasaputra.griyakampoengtkw.repository.TahapanRepository

@Module
@InstallIn(ViewModelComponent::class)
object StartupModule {

    @Provides
    fun provideTahapanRepository(rootDatabaseReference: DatabaseReference): TahapanRepository {
        return FirebaseTahapanRepository(rootDatabaseReference)
    }

}