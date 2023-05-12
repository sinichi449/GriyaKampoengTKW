package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.bagusekasaputra.griyakampoengtkw.data.remote.InitRemoteImpl
import net.bagusekasaputra.griyakampoengtkw.interfaces.remote.InitRemote

@Module
@InstallIn(ActivityComponent::class)
object DataSourceDeveloperModule {

    @Provides
    fun provideInitRemote(databaseReference: DatabaseReference): InitRemote {
        return InitRemoteImpl(databaseReference)
    }

}