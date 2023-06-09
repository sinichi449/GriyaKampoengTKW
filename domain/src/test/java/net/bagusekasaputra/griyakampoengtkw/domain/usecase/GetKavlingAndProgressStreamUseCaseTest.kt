package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndProgressStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetKavlingAndProgressStreamUseCaseTest {

    private val mockRepository = MockRepository(getTestingFile(this))

    private var kavlingRepository = mockRepository.getKavlingRepository()
    private var pembayaranRepository = mockRepository.getPembayaranRepository()
    private var baselineRepository = mockRepository.getBaselinePembayaranRepository()

    private val useCase = GetKavlingAndProgressStreamAsyncUseCase(
        kavlingRepository, pembayaranRepository, baselineRepository
    )

    private val defaultDataMode = DataMode.ONLINE

    @Test
    fun whenExecuted_shouldReturnEqualKavlingSizeAsInRepository() = runTest {
        try {
            val blok = "A"
            val correctKavlingList = kavlingRepository.getKavlingByBlock(blok, defaultDataMode)
                .first()
                .getOrThrow()!!

            var resultKavlingList = emptyList<KavlingAndProgress>()
            val request = GetKavlingAndProgressStreamAsyncUseCase.Request(blok, defaultDataMode)
            useCase.execute(request)
                .collect { result ->
                result.onFailure {
                    throw it
                }
                result.onSuccess { list ->
                    // continuously updating `resultKavlingList`'s value
                    resultKavlingList = list ?: emptyList()
                }
            }

            Assert.assertEquals(correctKavlingList.size, resultKavlingList.size)
        } catch (e: Exception) {
            println(e)

            throw e
        }
    }

    @Test
    fun whenExecuted_shouldKavlingIsSorted() = runTest {
        try {
            val blok = "A"
            val sortedKavlings = kavlingRepository.getKavlingByBlock(blok, defaultDataMode)
                .first()
                .getOrThrow()!!
                .let { Kavling.sortKavling(it, SingleBlockKavlingSorter()) }

            val request = GetKavlingAndProgressStreamAsyncUseCase.Request(blok, defaultDataMode)
            var resultList = emptyList<KavlingAndProgress>()
            useCase.execute(request).collect { result ->
                result.onFailure {
                    throw it
                }
                result.onSuccess {
                    it?.also { items ->
                        resultList = items
                    }
                }
            }

            val kavlingsResult = buildList {
                resultList.forEach {
                    add(it.kavling)
                }
            }
            Assert.assertEquals(sortedKavlings, kavlingsResult)
        } catch (e: Exception) {
            e.printStackTrace()

            throw e
        }
    }

    @Test
    fun whenRepositoriesThrowAnException_shouldCatchThem() = runTest {
        val blok = "B"
        val exception = Exception("Test error!")
        val executeUseCase = {
            launch {
                val request = GetKavlingAndProgressStreamAsyncUseCase.Request(blok, defaultDataMode)
                useCase.execute(request).collect { result ->
                    result.onFailure {
                        Assert.assertEquals(it, exception)
                    }
                    result.onSuccess {
                        throw Exception("Expected to be failed, but it succeeed.")
                    }
                }
            }
        }

        whenever(kavlingRepository.getAsFlow(anyString()))
            .then {
                flow<Result<Kavling?>> {
                    emit(Result.failure(exception))
                }
            }
        executeUseCase()

        kavlingRepository = mockRepository.getKavlingRepository()

        whenever(pembayaranRepository.getAllPembayaran(
            kavlingKode = anyString(),
            dataMode = any() ?: defaultDataMode
        )).thenReturn(flowOf(Result.failure(exception)))

        executeUseCase()

        pembayaranRepository = mockRepository.getPembayaranRepository()

        whenever(baselineRepository.get(
            kavling = anyString(),
            dataMode = any() ?: defaultDataMode
        )).thenReturn(flowOf(Result.failure(exception)))

        executeUseCase()

        baselineRepository = mockRepository.getBaselinePembayaranRepository()
    }
}