package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.model.BaselinePembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.PembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.TestingDataNodes
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import net.bagusekasaputra.griyakampoengtkw.domain.util.nodeReference
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class GetProgressKavlingUseCaseTest {

    private val baselinePembayaranRepository = mock<BaselinePembayaranRepository>()
    private val pembayaranRepository = mock<PembayaranRepository>()

    private val useCase = GetProgressKavlingAsyncUseCase(
        baselinePembayaranRepository, pembayaranRepository
    )

    private lateinit var kavlingKodeList: List<String>

    private companion object {
        const val BULAN_INI = 6
        const val TAHUN_INI = 2023
    }

    @Before
    fun initializeMock() {
        runTest {
            kavlingKodeList = getTestingFile(this@GetProgressKavlingUseCaseTest)
                .nodeReference()
                ?.getAsJsonObject(TestingDataNodes.KAVLINGS)
                ?.run {
                    val orderedKavlingList = mutableListOf<String>()
                    keySet().forEach { blockKode ->
                        val list = mutableListOf<String>()
                        val blockNodes = getAsJsonObject(blockKode)
                        blockNodes.keySet().forEach { kavlingKode ->
                            list.add(kavlingKode)
                        }

                        orderedKavlingList.addAll(Kavling.sortKodeKavling(list, SingleBlockKavlingSorter()))
                    }

                    orderedKavlingList
                }!!

            whenever(pembayaranRepository.getAllPembayaran(
                kavlingKode = ArgumentMatchers.anyString(),
                dataMode = ArgumentMatchers.any(DataMode::class.java) ?: DataMode.ONLINE,
            )).thenAnswer { invocation ->
                flow {
                    val kavlingKode = invocation.arguments[0].toString()
                    val pembayaranKavlingNode = getTestingFile(this@GetProgressKavlingUseCaseTest)
                        .nodeReference()
                        ?.get(TestingDataNodes.FORM_PEMBAYARAN)
                        ?.asJsonObject
                        ?.get(kavlingKode)
                        ?.asJsonObject
                    val pembayaranList = buildList {
                        pembayaranKavlingNode?.keySet()?.forEach { termin ->
                            pembayaranKavlingNode[termin]?.also { pembayaranJson ->
                                Gson().fromJson(pembayaranJson, PembayaranJson::class.java)?.toDomain().also { pembayaran ->
                                    add(pembayaran)
                                }
                            }
                        }
                    }

                    emit(Result.success(pembayaranList))
                }
            }

            whenever(baselinePembayaranRepository.get(
                kavling = ArgumentMatchers.anyString(),
                dataMode = ArgumentMatchers.any() ?: DataMode.ONLINE,
            )).thenAnswer { invocation ->
                flow {
                    val kavlingKode = invocation.arguments[0].toString()
                    val baselineJson = getTestingFile(this@GetProgressKavlingUseCaseTest)
                        .nodeReference()
                        ?.get(TestingDataNodes.BASELINE_PEMBAYARAN)
                        ?.asJsonObject
                        ?.get(kavlingKode)
                    val baselinePembayaran = baselineJson?.run {
                        Gson().fromJson(this, BaselinePembayaranJson::class.java)
                            ?.toDomain()
                    }

                    emit(Result.success(baselinePembayaran))
                }
            }
        }
    }

    @Test
    fun whenGetProgressKavling_shouldReturnBulanAngsuranInsteadOfTanggalPembayaran() {
        runTest {
            val a4 = progressKavlingOf("A4")

            Assert.assertEquals(0, a4?.persentaseBulanIni())
        }
    }

    private suspend fun progressKavlingOf(kavling: String): ProgressKavling? {
        val request = GetProgressKavlingAsyncUseCase.Request(kavlingKodeList, DataMode.ONLINE)
        val progressKavlingDeferred = CompletableDeferred<Map<String, ProgressKavling?>>()

        useCase.execute(request).collect {
            it.onSuccess {  progressKavlingMap ->
                progressKavlingDeferred.complete(progressKavlingMap!!)
            }
            it.onFailure { throwable ->
                progressKavlingDeferred.completeExceptionally(throwable)
            }
        }

        val progressKavlingMap = progressKavlingDeferred.await()

        return progressKavlingMap[kavling]
    }
}