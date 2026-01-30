@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.ConstsSharedPrefs
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext ctx: Context): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(ctx)

    @Provides
    fun provideLocalBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(context)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext applicationContext: Context): ContentResolver {
        return applicationContext.contentResolver
    }

    @RootReference
    @Provides
    fun provideRootStorageReference() = FirebaseStorage.getInstance().reference

    @RootReference
    @Provides
    fun providesRootFirebaseDatabaseReference(
        @ApplicationContext context: Context,
        sharedPrefs: SharedPreferences
    ): DatabaseReference {
        // 1. Check which branch is selected
        val selectedBranch = sharedPrefs.getString(
            ConstsSharedPrefs.SELECTED_BRANCH,
            ConstsSharedPrefs.BRANCH_GKT1
        ) ?: ConstsSharedPrefs.BRANCH_GKT1

        // 2. Select the correct URL
        val targetUrl = if (selectedBranch == ConstsSharedPrefs.BRANCH_GKT2) {
            GriyaNodes.FIREBASE_RDB_GKT2
        } else {
            GriyaNodes.FIREBASE_RDB_GKT1
        }

        // 3. Get the correct Firebase App Instance (Project 1 vs Project 2)
        val firebaseApp = if (selectedBranch == ConstsSharedPrefs.BRANCH_GKT1) {
            com.google.firebase.FirebaseApp.getInstance()
        } else {
            // For GKT2, we try to get the secondary app.
            // It SHOULD be initialized by SplashActivity already, but we add a safety check here.
            try {
                com.google.firebase.FirebaseApp.getInstance("GK2_SECONDARY_APP")
            } catch (e: IllegalStateException) {
                // Safety Fallback: Initialize it manually if it's missing
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApiKey(GriyaNodes.GKT2_API_KEY)
                    .setApplicationId(GriyaNodes.GKT2_APP_ID)
                    .setProjectId(GriyaNodes.GKT2_PROJECT_ID)
                    .setDatabaseUrl(GriyaNodes.FIREBASE_RDB_GKT2)
                    .setStorageBucket(GriyaNodes.FIREBASE_STORAGE_GKT2)
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(context, options, "GK2_SECONDARY_APP")
            }
        }

        // 4. Return the specific reference
        return FirebaseDatabase.getInstance(firebaseApp, targetUrl).reference
    }

}