package net.bagusekasaputra.griyakampoengtkw.domain.usecase.rekap

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarOverviewAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.model.BiayaLainJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.BiayaMarketingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.DataDiriJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.FeeMarketingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.HargaKavlingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.PembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.TestingDataNodes
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import net.bagusekasaputra.griyakampoengtkw.domain.util.nodeReference
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetRekapBesarOverviewUseCaseTest {

    private val blockRepository = Mockito.mock(BlockRepository::class.java)
    private val kavlingRepository = Mockito.mock(KavlingRepository::class.java)
    private val pembayaranRepository = Mockito.mock(PembayaranRepository::class.java)
    private val dataDiriRepository = Mockito.mock(DataDiriRepository::class.java)
    private val hargaKavlingRepository = Mockito.mock(HargaKavlingRepository::class.java)
    private val feeMarketingRepository = Mockito.mock(FeeMarketingRepository::class.java)
    private val biayaMarketingRepository = Mockito.mock(BiayaMarketingRepository::class.java)
    private val biayaLainRepository = Mockito.mock(BiayaLainRepository::class.java)
    private val rekapBesarDetailRepository = Mockito.mock(RekapBesarDetailRepository::class.java)

    private lateinit var kavlingKodeList: List<String>

    private val useCase = GetRekapBesarOverviewAsyncUseCase(blockRepository, kavlingRepository,
        pembayaranRepository, dataDiriRepository, hargaKavlingRepository, feeMarketingRepository,
        biayaMarketingRepository, biayaLainRepository, rekapBesarDetailRepository)

    @Before
    fun initializeMock() {
        val testingFile = getTestingFile(this@GetRekapBesarOverviewUseCaseTest)

        runTest {
            kavlingKodeList = testingFile.nodeReference()?.getAsJsonObject(TestingDataNodes.KAVLINGS)
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

            whenever(rekapBesarDetailRepository.delete()).thenReturn(Result.success(null))
            whenever(rekapBesarDetailRepository.insert(any())).thenReturn(Result.success(null))
            whenever(pembayaranRepository.getBatchOnline(ArgumentMatchers.anyList()))
                .then {
                    flow<Result<Map<String, List<Pembayaran>?>?>> {
                        val pembayaranNode = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.FORM_PEMBAYARAN)

                        val result = mutableMapOf<String, List<Pembayaran>?>()
                        kavlingKodeList.forEach { kavling ->
                            val pembayaranList = mutableListOf<Pembayaran>()
                            val terminNode = pembayaranNode?.getAsJsonObject(kavling)
                            terminNode?.keySet()?.forEach { termin ->
                                val itemPembayaran = terminNode.get(termin)
                                val pembayaranJson = Gson().fromJson(itemPembayaran, PembayaranJson::class.java)

                                pembayaranJson?.also { pembayaranList.add(it.toDomain()) }
                            }

                            result[kavling] = if (pembayaranList.isEmpty()) null else pembayaranList
                        }

                        emit(Result.success(result))
                    }
                }
            whenever(dataDiriRepository.getBatchOnline(ArgumentMatchers.anyList()))
                .then {
                    flow {
                        val rootNode = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.DATA_DIRI)

                        val mapDataDiri = mutableMapOf<String, DataDiri?>()
                        rootNode?.keySet()?.forEach { kavling ->
                            val dataDiriNode = rootNode.get(kavling)
                            val dataDiriJson = Gson().fromJson(dataDiriNode, DataDiriJson::class.java)

                            mapDataDiri[kavling] = dataDiriJson?.toDomain()
                        }

                        emit(Result.success(mapDataDiri))
                    }
                }
            whenever(hargaKavlingRepository.getBatchOnline(ArgumentMatchers.anyList()))
                .then {
                    flow {
                        val rootNodes = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.HARGA_KAVLING)

                        val mapHargaKavling = mutableMapOf<String, HargaKavling?>()
                        rootNodes?.keySet()?.forEach { kavling ->
                            val hargaKavlingNode = rootNodes.get(kavling)
                            val hargaKavlingJson = Gson().fromJson(hargaKavlingNode, HargaKavlingJson::class.java)

                            mapHargaKavling[kavling] = hargaKavlingJson?.toDomain()
                        }

                        emit(Result.success(mapHargaKavling))
                    }
                }
            whenever(feeMarketingRepository.getBatchOnline(ArgumentMatchers.anyList()))
                .then {
                    flow {
                        val rootNode = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.FEE_MARKETING)

                        val mapFeeMarketing = mutableMapOf<String, FeeMarketing?>()
                        rootNode?.keySet()?.forEach { kavling ->
                            val feeMarketingNode = rootNode.get(kavling)
                            val feeMarketingJson = Gson().fromJson(feeMarketingNode, FeeMarketingJson::class.java)

                            mapFeeMarketing[kavling] = feeMarketingJson?.toDomain()
                        }
                        emit(Result.success(mapFeeMarketing))
                    }
                }
            whenever(biayaMarketingRepository.getBatchOnline(ArgumentMatchers.anyList()))
                .then {
                    flow {
                        val rootNode = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.BIAYA_MARKETING)

                        val mapBiayaMarketing = mutableMapOf<String, List<BiayaMarketing>?>()
                        rootNode?.keySet()?.forEach { kavling ->
                            val kavlingNode = rootNode.getAsJsonObject(kavling)
                            val biayaMarketingList = mutableListOf<BiayaMarketing>()
                            kavlingNode?.keySet()?.forEach { jenisBiaya ->
                                val biayaMarketingNode = kavlingNode.get(jenisBiaya)
                                val biayaMarketingJson = Gson().fromJson(biayaMarketingNode, BiayaMarketingJson::class.java)

                                biayaMarketingJson?.also { biayaMarketingList.add(it.toDomain()) }
                            }

                            mapBiayaMarketing[kavling] = if (biayaMarketingList.isEmpty()) null
                                else biayaMarketingList
                        }

                        emit(Result.success(mapBiayaMarketing))
                    }
                }
            whenever(biayaLainRepository.getAllOnline(any()))
                .then {
                    flow {
                        val rootNode = testingFile.nodeReference()
                            ?.getAsJsonObject(TestingDataNodes.BIAYA_LAIN)

                        val biayaLainList = mutableListOf<BiayaLain>()
                        rootNode?.keySet()?.forEach { namaBiaya ->
                            val biayaLainNode = rootNode.get(namaBiaya)
                            val biayaLainJson = Gson().fromJson(biayaLainNode, BiayaLainJson::class.java)

                            biayaLainJson?.also { biayaLainList.add(it.toDomain()) }
                        }
                        emit(Result.success(biayaLainList))
                    }
                }
        }
    }

    @Test
    fun balbalbalbal() {
        runTest {
            val request = GetRekapBesarOverviewAsyncUseCase.Request(
                periodeRekap = PeriodeRekap.SEMUA,
                listKavling = kavlingKodeList,
            )

            val result = useCase.execute(request).first()
            result.onSuccess {
                println(it)
            }
            result.onFailure {
                throw it
            }
        }
    }
}

fun String.toJsonObject(): JsonObject {
    return JsonParser.parseString(this).asJsonObject
}