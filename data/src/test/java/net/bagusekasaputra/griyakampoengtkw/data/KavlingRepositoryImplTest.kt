package net.bagusekasaputra.griyakampoengtkw.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class KavlingRepositoryImplTest {

    private val localKavlingDataSource: LocalKavlingDataSource = mock()
    private val remoteKavlingDataSource: RemoteKavlingDataSource = mock()
    private val backupKavlingDataSource: BackupKavlingDataSource = mock()
    private val localBlockDataSource: LocalBlockDataSource = mock()
    private val cacheHelper = mock<CacheHelper>()

    private val kavlingRepository = KavlingRepositoryImpl(
        localKavlingDataSource, remoteKavlingDataSource,
        backupKavlingDataSource, localBlockDataSource,
        cacheHelper,
    )

    @Before
    fun initializeMocks() = runTest {
        whenever(localKavlingDataSource.deleteAll()).thenReturn(Result.success(null))
        whenever(localKavlingDataSource.addAll(anyList())).thenReturn(Result.success(null))
        whenever(localKavlingDataSource.getAsFlow(anyString()))
            .then { invocation ->
                flow<Result<KavlingModel?>> {
                    val blok = invocation.arguments[0]!!.toString()
                    val kavlingList = getKavlingList(blok)

                    if (kavlingList.isNullOrEmpty()) {
                        emit(Result.success(null))
                    } else {
                        kavlingList.forEach { model ->
                            delay(500L)

                            emit(Result.success(model))
                        }
                    }
                }
            }
        whenever(remoteKavlingDataSource.getAllKavlings(anyString()))
            .doSuspendableAnswer { invocation ->
                val requestedBlok =  invocation.arguments[0]!!.toString()
                delay(1250L)
                Result.success(getKavlingList(requestedBlok))
            }
    }

    @Test
    fun whenLocalCacheIsInvalid_shouldGetAllFromRemote() = runTest {
        setIsInvalidCache(true)

        val kavlingList = mutableListOf<KavlingModel>()
        kavlingRepository.getAsFlow("A")
            .onCompletion {
                Assert.assertEquals(true, kavlingList.isNotEmpty())
            }
            .collectIndexed { index, result ->
                if (index != 0) {
                    setIsInvalidCache(false)
                }
                result.onSuccess { kavling ->
                    kavling?.also { kavlingList.add(MyObjectMapper.mapKavling(it)) }
                }
                result.onFailure {
                    it.printStackTrace()
                }
            }
    }

    @Test
    fun whenLocalDataSourceThrowsException_shouldCatchedInRepository() = runTest {
        val exception = Exception("ERROR IN LOCAL DATABASE!")
        whenever(localKavlingDataSource.getAsFlow(anyString()))
            .thenReturn(flow {
                emit(Result.failure(exception))
            })

        kavlingRepository.getAsFlow("A").collect {
            val result = it.exceptionOrNull()

            Assert.assertEquals(exception, result)
        }
    }

    @Test
    fun balbalbalbal() = runTest {
        val flowA = flow<Int> {
            repeat(10) {
                delay(100L)
                emit(it + 1)
            }
        }.onStart {
            println("Start 1")
        }.onStart {
            println("Start 2")
        }.onStart {
            println("Start 3")
        }

        flowA.collect {
            println(it)
        }
    }

    private suspend fun setIsInvalidCache(invalid: Boolean) {
        whenever(cacheHelper.checkAndInvalidateCache(
            anyString(), anyString(), any()
        )).thenReturn(invalid)
    }

    private fun getKavlingList(blok: String): List<KavlingModel>? {
        val kavlingList = Kavling.getGriyaKavlingList()
        val kavlingsInBlok = kavlingList.filter {
            it.substring(0, 1) == blok
        }

        return if (kavlingsInBlok.isEmpty()) {
            null
        } else {
            buildList {
                kavlingsInBlok.forEach {
                    val model = KavlingModel(
                        kode = it,
                        warna = "",
                        ukuran = "",
                    )
                    add(model)
                }
            }
        }
    }
}