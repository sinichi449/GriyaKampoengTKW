package net.bagusekasaputra.griyakampoengtkw.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingSequentiallyByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetSingleProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.juta
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.presentation.model.KavlingWithProgress
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

class MainViewModelTest {

    private val getAllBlocksUseCase = mock<GetAllBlocksAsyncUseCase>()
    private val addNewBlockUseCase = mock<AddNewBlockUseCase>()
    private val getKavlingByBlockUseCase = mock<GetKavlingByBlockAsyncUseCase>()
    private val addKavlingUseCase = mock<AddKavlingUseCase>()
    private val editKavlingUseCase = mock<EditKavlingUseCase>()
    private val removeKavlingUseCase = mock<RemoveKavlingUseCase>()
    private val getProgresKavlingUseCase = mock<GetProgressKavlingAsyncUseCase>()
    private val getAppUpdateInformationUseCase = mock<GetUpdateInformationUseCase>()
    private val getPromotionMessageUseCase = mock<GetPromotionMessageAsyncUseCase>()

    private val getKavlingSequentiallyUseCase = mock<GetKavlingSequentiallyByBlockAsyncUseCase>()
    private val getSingleProgressKavlingUseCase = mock<GetSingleProgressKavlingAsyncUseCase>()

    private val mainViewModel = MainViewModel(
        getAllBlocksUseCase, addNewBlockUseCase, getKavlingByBlockUseCase, getKavlingSequentiallyUseCase,
        addKavlingUseCase, editKavlingUseCase, removeKavlingUseCase, getProgresKavlingUseCase, getSingleProgressKavlingUseCase,
        getAppUpdateInformationUseCase, getPromotionMessageUseCase
    )

    private val sizeProgressKavling = 20
    private val progressKosongNums = listOf(5, 10, 15)

    @Before
    fun initializeMocks() {
        whenever(getKavlingSequentiallyUseCase.execute(
            any() ?: GetKavlingSequentiallyByBlockAsyncUseCase.Request("")
        )).thenAnswer {  invocation ->
            flow<Result<Kavling?>> {
                val blok = (invocation.arguments[0] as GetKavlingSequentiallyByBlockAsyncUseCase.Request)
                    .blok
                val kavlingList = buildList {
                    repeat(sizeProgressKavling) { numKavling ->
                        add(Kavling(
                            kode = "${blok}${numKavling + 1}",
                            belumIsi = false,
                            warna = "",
                            ukuran = "",
                            type = ""
                        ))
                    }
                }

                kavlingList.forEach { kavling ->
                    delay(500L)
                    emit(Result.success(kavling))
                }
            }
        }

        whenever(getSingleProgressKavlingUseCase.execute(
            any() ?: GetSingleProgressKavlingAsyncUseCase.Request("")
        )).thenAnswer { invocation ->
            flow<Result<ProgressKavling?>> {
                val kavling = (invocation.arguments[0] as GetSingleProgressKavlingAsyncUseCase.Request)
                    .kavling
                val progressList = buildList {
                    repeat(sizeProgressKavling) {
                        if (progressKosongNums.contains(it)) {
                            add(ProgressKavling.EMPTY(kavling))
                        } else {
                            val maxPembayaran = 10.0.juta()
                            val angsuranBulanan = Random.nextLong(
                                from = 0.0.juta(), until = maxPembayaran
                            )
                            val uangMasukBulanIni = Random.nextLong(
                                from = 0.0.juta(), until = maxPembayaran
                            )

                            add(ProgressKavling(kavling, angsuranBulanan, uangMasukBulanIni))
                        }
                    }
                }

                delay(1500L)

                val filter = progressList.filter { it.kavling == kavling }
                if (filter.isNotEmpty()) {
                    emit(Result.success(filter.first()))
                } else {
                    emit(Result.success(ProgressKavling.EMPTY(kavling)))
                }
            }
        }
    }

    @Test
    fun whenFetchingKavlingsSequentially_shouldNotCompletedWithOnlyOneObjectOrEmpty() {
        mainViewModel.fetchKavlingListOn(
            blockKode = "A",
            onFailure = { println(it) },
            onProgressFail = { println(it) }
        )

        runTest {
            var list = emptyList<KavlingWithProgress>()
            mainViewModel.kavlingWithProgressList
                .onEach {
                    list = it
                }
                .onCompletion {
                    Assert.assertEquals(sizeProgressKavling, list.size)
                }
        }
    }
}