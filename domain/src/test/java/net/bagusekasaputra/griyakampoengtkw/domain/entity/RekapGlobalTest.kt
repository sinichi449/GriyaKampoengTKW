package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import org.junit.Assert
import org.junit.Test

class RekapGlobalTest {

    @Test
    fun sisa_pembayaran_in_parsed_format_correct() {
        val listRekapGlobal = listOf(
            RekapGlobal("Andi Setya Budi", "2", "06/05/2021".toDate(), 250000000L, 130000000L),
            RekapGlobal("Iwan Ferdiyanto", "3", "01/05/2021".toDate(), 210000000L, 30000000L),
            RekapGlobal("Norma Fiki Sugiarto", "4", "13/07/2022".toDate(), 230000000L, 102087227L),
        )
        Assert.assertEquals("120,000,000", listRekapGlobal[0].parsedSisaPembayaran)
        Assert.assertEquals("180,000,000", listRekapGlobal[1].parsedSisaPembayaran)
        Assert.assertEquals("127,912,773", listRekapGlobal[2].parsedSisaPembayaran)
    }

}