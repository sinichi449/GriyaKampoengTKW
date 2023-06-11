package net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling

import net.bagusekasaputra.griyakampoengtkw.domain.entity.misc.UncompletedDomainEntity

@UncompletedDomainEntity
data class ProgressKavling(
    val kavling: String,
    val angsuranBulanan: Long,
    val totalTunggakan: Long,
) {

    fun getPersentaseAngsuran(): Int {
        return 0
    }

    companion object {
        fun EMPTY(kavling: String): ProgressKavling {
            return ProgressKavling(
                kavling = kavling,
                angsuranBulanan = 0L,
                totalTunggakan = 0L,
            )
        }
    }
}