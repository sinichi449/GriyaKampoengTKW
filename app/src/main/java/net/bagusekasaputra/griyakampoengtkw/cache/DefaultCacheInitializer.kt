package net.bagusekasaputra.griyakampoengtkw.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.delay
import net.bagusekasaputra.griyakampoengtkw.ConstsSharedPrefs
import net.bagusekasaputra.griyakampoengtkw.model.Tahapan

class DefaultCacheInitializer(
    private val sharedPrefs: SharedPreferences,
): CacheInitializer {

    private val TAG = "INIT_CACHE"

    override suspend fun initialize(tahapan: Tahapan): Result<Nothing?> {
        // TODO
        delay(5000L)
        setCacheHasBeenInitialized(true)
        return Result.success(null)
    }

    override fun isUnitialized(): Boolean {
        return sharedPrefs.getBoolean(
            ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED,
            true
        )
    }

    private fun setCacheHasBeenInitialized(initialized: Boolean) {
        sharedPrefs.edit(true) {
            putBoolean(ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED, !initialized)
        }
    }
}