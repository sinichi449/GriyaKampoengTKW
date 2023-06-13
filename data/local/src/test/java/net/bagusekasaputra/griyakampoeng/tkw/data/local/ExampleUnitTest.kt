package net.bagusekasaputra.griyakampoeng.tkw.data.local

import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.KavlingRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun kavling_model_and_entity_is_the_same() {
        val kavlingModel = KavlingModel(
            kode = "A5",
            warna = "#000000",
            active = true,
            ukuran = "6x12",
            type = "Type 36"
        )
        val kavlingEntity = KavlingRoomEntity(
            blockKode = "A",
            kode = "A5",
            warna = "#000000",
            isActive = true,
            ukuran = "6x12",
            type = "Type 36"
        )

        val result = RoomRequestHelper.doInsertPreventDuplicateOperation(
            outerData = kavlingModel,
            targetData = kavlingEntity,
            equalityPredicate = { m, e ->
                ((m.kode == e.kode)
                        and (m.warna == e.warna)
                        and (m.active == e.isActive)
                        and (m.ukuran == e.ukuran)
                        and (m.type == e.type))
            },
            insertWork = {
                assertEquals(1 + 1, 2)
            }
        )

        result.onSuccess {

        }
    }


}