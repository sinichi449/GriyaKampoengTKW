package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import org.junit.Assert
import org.junit.Test

class MaterialPembangunanTest {

    /**
     * Functionality Test
     */
    @Test
    fun getKelunasanTest() {
        val hargaTotal = 1_250_000L

        val terbayarKurang = 1_000_000L
        val kelunasanKurang = MaterialPembangunan.getKelunasan(terbayarKurang, hargaTotal)
        Assert.assertEquals(MaterialPembangunan.Kelunasan.Partial(terbayarKurang), kelunasanKurang)

        val terbayarPas = hargaTotal
        val kelunasanPas = MaterialPembangunan.getKelunasan(terbayarPas, hargaTotal)
        Assert.assertEquals(MaterialPembangunan.Kelunasan.Lunas(terbayarPas), kelunasanPas)

        val belumTerbayar = 0L
        val kelunasanBelum = MaterialPembangunan.getKelunasan(belumTerbayar, hargaTotal)
        Assert.assertEquals(MaterialPembangunan.Kelunasan.Belum, kelunasanBelum)
    }
}