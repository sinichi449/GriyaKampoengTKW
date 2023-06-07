package net.bagusekasaputra.griyakampoengtkw.domain.repository

import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.model.DataDiriJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.HargaKavlingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.PembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.TestingDataNodes
import net.bagusekasaputra.griyakampoengtkw.domain.util.nodeReference
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

class MockRepository(private val testingFile: File) {

    fun getKavlingKodeList(): List<String> {
        return testingFile.nodeReference()?.getAsJsonObject(TestingDataNodes.KAVLINGS)
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
    }

    fun getDataDiriRepository(): DataDiriRepository {
        val dataDiriRepository = mock<DataDiriRepository>()
        whenever(dataDiriRepository.onlineBatch(ArgumentMatchers.anyList()))
            .then { invocation ->
                flow {
                    val dataDiriNode = testingFile.nodeReference()
                        ?.getAsJsonObject(TestingDataNodes.DATA_DIRI)
                    val kavlingList = invocation.arguments[0] as List<String>

                    val dataDiriMap = buildMap {
                        kavlingList.forEach { kavling ->
                            dataDiriNode?.get(kavling)?.also { dataDiriJson ->
                                Gson().fromJson(dataDiriJson, DataDiriJson::class.java)
                                    ?.toDomain()
                                    ?.also { dataDiri ->
                                        put(kavling, dataDiri)
                                    }
                            }
                        }
                    }

                    emit(Result.success(dataDiriMap))
                }
            }

        return dataDiriRepository
    }

    fun getPembayaranRepository(): PembayaranRepository {
        val pembayaranRepository = mock<PembayaranRepository>()

        whenever(pembayaranRepository.onlineBatch(ArgumentMatchers.anyList()))
            .then { invocation ->
                flow<Result<Map<String, List<Pembayaran>?>?>> {
                    val pembayaranNode = testingFile.nodeReference()
                        ?.getAsJsonObject(TestingDataNodes.FORM_PEMBAYARAN)
                    val kavlingList = invocation.arguments[0] as List<String>
                    val pembayaranMap = buildMap {
                        kavlingList.forEach { kavling ->
                            val terminNode = pembayaranNode?.get(kavling)
                                ?.asJsonObject
                            val pembayaranList = mutableListOf<Pembayaran>()

                            terminNode?.keySet()?.forEach { termin ->
                                terminNode[termin]?.also { pembayaranJson ->
                                    Gson().fromJson(pembayaranJson, PembayaranJson::class.java)
                                        ?.toDomain()
                                        ?.also { pembayaran ->
                                            pembayaranList.add(pembayaran)
                                        }
                                }
                            }

                            put(kavling, pembayaranList)
                        }
                    }

                    emit(Result.success(pembayaranMap))
                }
            }

        return pembayaranRepository
    }

    fun getHargaKavlingRepository(): HargaKavlingRepository {
        val hargaKavlingRepository = mock<HargaKavlingRepository>()
        whenever(hargaKavlingRepository.onlineBatch(ArgumentMatchers.anyList()))
            .thenAnswer { invocation ->
                flow {
                    val hargaKavlingNode = testingFile.nodeReference()
                        ?.getAsJsonObject(TestingDataNodes.HARGA_KAVLING)
                    val kavlingList = invocation.arguments[0] as List<String>

                    val hargaKavlingMap = buildMap {
                        kavlingList.forEach { kavling ->
                            hargaKavlingNode?.get(kavling)?.also { hargaKavlingJson ->
                                Gson().fromJson(hargaKavlingJson, HargaKavlingJson::class.java)
                                    ?.toDomain()
                                    ?.also { hargaKavling ->
                                        put(kavling, hargaKavling)
                                    }
                            }
                        }
                    }

                    emit(Result.success(hargaKavlingMap))
                }
            }

        return hargaKavlingRepository
    }


}