package net.bagusekasaputra.griyakampoengtkw

import com.google.firebase.database.DatabaseReference
import net.bagusekasaputra.griyakampoengtkw.di.PersistentModules

object ConstsSharedPrefs {

    // Key for cache initialization. This key will be modified whenever Local Database (Cache)
    // either destroyed (due to migration) or still untouched (due to launching the app for the
    // first time).
    const val CACHE_UNINITIALIZED_OR_DESTROYED = "CACHE_UNINITIALIZED_OR_DESTROYED"

    const val SELECTED_TAHAPAN = "SELECTED_TAHAPAN"

    // Shared prefs for "standard" or "pembatalan" node
    const val NODE_TYPE = "NODE_TYPE"
    const val NODE_STANDARD = "NODE_STANDARD"
    const val NODE_PEMBATALAN = "NODE_PEMBATALAN"

    /**
     * As a key pointer for [DatabaseReference]' child when user has selected [SplashActivity.DATA_LAMA].
     *
     * @see PersistentModules.provideTahapanFirebaseDatabaseReference
     * @see PersistentModules.provideTahapanStorageReference
     */
    const val BACKUP_NAME = "BACKUP_NAME"

}