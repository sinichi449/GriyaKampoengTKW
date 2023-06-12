package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pengembalian

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.timeMillisToDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengembalian.GetPengembalianStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.juta
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository.Companion.DEFAULT_DATA_MODE
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetPengembalianUseCaseTest {

    private val pengembalianRepository = mock<PengembalianRepository>()

    private val useCase = GetPengembalianStreamAsyncUseCase(
        pengembalianRepository = pengembalianRepository,
    )

    private companion object {
        const val KEY_ID_SIZE = 5
        val keyIdsList = buildList {
            repeat(KEY_ID_SIZE) { number ->
                add(alphabetNumberOf(number + 1)!!)
            }
        }

        private fun alphabetNumberOf(i: Int): String? {
            return if (i in 1..26) (i + 64).toChar().toString() else null
        }
    }

    @Before
    fun initMocks() = runTest {
        fun pengembalianFlow(keyId: String) = flow<Result<Pengembalian?>> {
            val bloks = listOf("A", "B", "C")
            val startTimeMillis = "01/01/2020".toDate().time
            val endTimeMillis = "12/06/2023".toDate().time

            val pengembalian = Pengembalian(
                keyId = keyId,
                kavling = "${bloks.random()}${Random.nextInt(from = 1, until = 50)}",
                namaCustomer = "",
                tanggal = Random.nextLong(from = startTimeMillis, until = endTimeMillis)
                    .timeMillisToDate(),
                jumlah = (Random.nextDouble(from = 0.1, until = 10.0)).juta(),
                keterangan = "",
            )

            emit(resultOf(pengembalian))
        }

        whenever(pengembalianRepository.getKeyIds(dataMode = ArgumentMatchers.any() ?: DEFAULT_DATA_MODE))
            .thenReturn(resultOf(keyIdsList))

        whenever(pengembalianRepository.getAsFlow(
            keyId = ArgumentMatchers.anyString(),
            dataMode = ArgumentMatchers.any() ?: DEFAULT_DATA_MODE,
        )).doAnswer { pengembalianFlow(it.getKeyId()) }
    }

    @Test
    fun shouldNotReturnEmptyList() = runTest {
        val request = GetPengembalianStreamAsyncUseCase.Request()
        var pengembalianList = emptyList<Pengembalian>()

        useCase.execute(request).collect { result ->
            result.onFailure { throw it }
            result.onSuccess { list -> list?.let { pengembalianList = it } }
        }

        Assert.assertEquals(false, pengembalianList.isEmpty())
    }



    private fun InvocationOnMock.getKeyId(): String {
        return this.arguments[0]!!.toString()
    }

    private fun <T> resultOf(obj: T): Result<T> {
        return Result.success(obj)
    }
}