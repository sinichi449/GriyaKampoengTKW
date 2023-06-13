package net.bagusekasaputra.griyakampoeng.tkw.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PembayaranRoomEntity
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    private lateinit var pembayaranRoomDao: PembayaranRoomDao
    private lateinit var griyaDatabase: MyRoomDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        griyaDatabase = Room.inMemoryDatabaseBuilder(
            context, MyRoomDatabase::class.java
        ).build()

        pembayaranRoomDao = griyaDatabase.getPembayaranDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        griyaDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun writePembayaranAndReadInList() {
        // I want to test whether given an old pembayaran with a specific value
        // of kavling and termin and the rest of the properties, and given again a new pembayaran with
        // same kavling and termin and with very different properties will automatically
        // replaced/updated
        val listPembayaran = mutableListOf<PembayaranRoomEntity>(
            PembayaranRoomEntity(
                kavlingKode = "A1",
                termin = "ITJ 1",
                tanggal = "22/07/2022",
                jumlahUangDibayar = 3000000L,
                keterangan = "-",
            ),
            PembayaranRoomEntity(
                kavlingKode = "A1",
                termin = "ITJ 2",
                tanggal = "23/07/2022",
                jumlahUangDibayar = 1500000L,
                keterangan = "-",
            ),
        )

        listPembayaran.forEach {
            pembayaranRoomDao.insert(it)
        }

        val pembayaranKedua = pembayaranRoomDao.getAllPembayaran("A1")
            ?.get(1)
        assertThat(pembayaranKedua?.jumlahUangDibayar, equalTo(1500000L))
    }
}