package net.bagusekasaputra.griyakampoengtkw.domain.repository

import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.model.BaselinePembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.DataDiriJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.HargaKavlingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.KavlingJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.PembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.model.TestingDataNodes
import net.bagusekasaputra.griyakampoengtkw.domain.util.nodeReference
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
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

    fun getBlockRepository(): BlockRepository {
        val repository = mock<BlockRepository>()

        return repository
    }

    fun getKavlingRepository(): KavlingRepository {
        val repository = mock<KavlingRepository>()
        whenever(repository.getAsFlow(anyString())).then { invocation ->
            flow {
                val blok = invocation.arguments[0]!!.toString()
                val kavlingNodes = testingFile.nodeReference()
                    ?.getAsJsonObject(TestingDataNodes.KAVLINGS)
                    ?.getAsJsonObject(blok)

                val kavlingList = buildList {
                    kavlingNodes?.keySet()?.forEach { kodeKavling ->
                        kavlingNodes[kodeKavling]?.also { kavlingJson ->
                            Gson().fromJson(kavlingJson, KavlingJson::class.java)?.also {
                                add(it.toDomain())
                            }
                        }
                    }
                }
                if (kavlingList.isNotEmpty()) {
                    kavlingList.forEach {
                        delay(500L)
                        emit(Result.success(it))
                    }
                } else {
                    emit(Result.success(null))
                }
            }
        }
        whenever(repository.getKavlingByBlock(
            blockCode = anyString(),
            dataMode = any() ?: DataMode.ONLINE,
        )).then { invocation ->
            flow {
                val blok = invocation.arguments[0]!!.toString()
                val kavlingNodes = testingFile.nodeReference()
                    ?.getAsJsonObject(TestingDataNodes.KAVLINGS)
                    ?.getAsJsonObject(blok)

                val kavlingList = buildList {
                    kavlingNodes?.keySet()?.forEach { kodeKavling ->
                        kavlingNodes[kodeKavling]?.also { kavlingJson ->
                            Gson().fromJson(kavlingJson, KavlingJson::class.java)?.also { kavling ->
                                add(kavling.toDomain())
                            }
                        }
                    }
                }

                emit(Result.success(kavlingList))
            }
        }

        return repository
    }

    fun getBaselinePembayaranRepository(): BaselinePembayaranRepository {
        val baselineRepository = mock<BaselinePembayaranRepository>()
        whenever(baselineRepository.get(
            kavling = anyString(),
            dataMode = any() ?: DataMode.ONLINE,
        )).then { invocation ->
            flow<Result<BaselinePembayaran?>> {
                val kavling = invocation.arguments[0]!!.toString()
                val baselineNodes = testingFile.nodeReference()
                    ?.getAsJsonObject(TestingDataNodes.BASELINE_PEMBAYARAN)
                    ?.get(kavling)
                val result = baselineNodes?.let { baselineJson ->
                    Gson().fromJson(baselineJson, BaselinePembayaranJson::class.java)
                        ?.toDomain()
                }

                emit(Result.success(result))
            }
        }

        return baselineRepository
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
        whenever(dataDiriRepository.getDataDiri(
            kavlingKode = anyString(),
            dataMode = any() ?: DEFAULT_DATA_MODE
        )).then { invocation ->
            flow {
                val kavling = invocation.arguments[0]!!.toString()
                val dataDiriJson = testingFile.nodeReference()
                    ?.getAsJsonObject(TestingDataNodes.DATA_DIRI)
                    ?.get(kavling)

                val dataDiri = dataDiriJson?.let {
                    Gson().fromJson(it, DataDiriJson::class.java)?.toDomain()
                }

                emit(Result.success(dataDiri))
            }
        }

        return dataDiriRepository
    }

    fun getPembayaranRepository(): PembayaranRepository {
        val pembayaranRepository = mock<PembayaranRepository>()
        val pembayaranNode = testingFile.nodeReference()
            ?.getAsJsonObject(TestingDataNodes.FORM_PEMBAYARAN)

        whenever(pembayaranRepository.onlineBatch(ArgumentMatchers.anyList()))
            .then { invocation ->
                flow<Result<Map<String, List<Pembayaran>?>?>> {
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
        whenever(pembayaranRepository.getAllPembayaran(
            kavlingKode = ArgumentMatchers.anyString(),
            dataMode = ArgumentMatchers.any() ?: DataMode.ONLINE
        )).then { invocation ->
            flow {
                val kavling = invocation.arguments[0].toString()
                val terminNode = pembayaranNode?.getAsJsonObject(kavling)

                val pembayaranList = buildList {
                    terminNode?.keySet()?.forEach { termin ->
                        terminNode[termin]?.let { pembayaranJson ->
                            Gson().fromJson(pembayaranJson, PembayaranJson::class.java)
                                ?.toDomain()
                                ?.also { pembayaran ->
                                    add(pembayaran)
                                }
                        }
                    }
                }

                emit(Result.success(pembayaranList))
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
        whenever(hargaKavlingRepository.getHargaKavling(
            kavlingKode = anyString(),
            dataMode = any() ?: DEFAULT_DATA_MODE,
        )).then { invocation ->
            flow<Result<HargaKavling?>> {
                val kavling = invocation.arguments[0]!!.toString()
                val hargaKavlingJson = testingFile.nodeReference()
                    ?.getAsJsonObject(TestingDataNodes.HARGA_KAVLING)
                    ?.getAsJsonObject(TestingDataNodes.KAVLINGS)

                val hargaKavling = hargaKavlingJson?.let {
                    Gson().fromJson(it, HargaKavlingJson::class.java)?.toDomain()
                }

                emit(Result.success(hargaKavling))
            }
        }

        return hargaKavlingRepository
    }

    companion object {
        val DEFAULT_DATA_MODE = DataMode.ONLINE
    }

}