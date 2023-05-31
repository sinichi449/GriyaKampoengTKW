package net.bagusekasaputra.griyakampoengtkw

import android.content.SharedPreferences
import net.bagusekasaputra.griyakampoengtkw.interfaces.CacheInitializer

class MyCacheInitializer(
    private val sharedPrefs: SharedPreferences
): CacheInitializer {

    override suspend fun initialize(): Result<Nothing?> {
        // TODO
        return Result.success(null)
    }

    override fun hasInitialized(): Boolean {
        return sharedPrefs.getBoolean(
            ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED,
            true
        )
    }
}