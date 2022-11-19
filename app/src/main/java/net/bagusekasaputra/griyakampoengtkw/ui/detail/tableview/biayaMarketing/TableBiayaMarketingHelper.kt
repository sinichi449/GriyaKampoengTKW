package net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.biayaMarketing

import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil

class TableBiayaMarketingHelper(
    private val listBiayaMarketing: List<BiayaMarketing>?,
) {

    fun getBiayaMarketingColumnHeaders(): ArrayList<String> {
        return ArrayList<String>().apply {
            add("Jenis Biaya")
            add("Harga")
        }
    }

    fun getBiayaMarketingRowHeaders(): ArrayList<String> {
        return if (listBiayaMarketing != null) {
            val numberList = ArrayList<String>()

            listBiayaMarketing.forEachIndexed { index, _ ->
                // The index start from zero, so to make it start from number one,
                // I added plus(1) method
                numberList.add(index.plus(1).toString())
            }

            numberList
        } else {
            ArrayList<String>().apply { add("0") }
        }
    }

    fun getBiayaMarketingCellItems(): ArrayList<ArrayList<String>> {
        val firstOrderItemList = ArrayList<ArrayList<String>>()

        if (listBiayaMarketing != null) {
            for (biayaMarketing in listBiayaMarketing) {
                val secondOrderItemList = ArrayList<String>()

                secondOrderItemList.add(biayaMarketing.jenisBiaya)

                // We need to transform this currency type into a comma separated number
                val transformHarga = NumberUtil.formatLongToString(biayaMarketing.harga.toLong())
                secondOrderItemList.add(transformHarga)

                firstOrderItemList.add(secondOrderItemList)
            }
        } else {
            val secondOrderItemList = ArrayList<String>()

            secondOrderItemList.apply {
                add("-")
                add("-")
            }

            firstOrderItemList.add(secondOrderItemList)
        }

        return firstOrderItemList
    }

    fun getTotalBiayaMarketing(): Long {
        return if (listBiayaMarketing != null)
            listBiayaMarketing.last().totalBiaya.toLong()
        else
            0L
    }

    fun getCuanBiayaMarketing(lastTotalUangMasuk: String, biayaMarketer: String): Long {
        // The "totalUangMasuk" which got from List<Pembayaran> are already parsed into 0,000,000
        // format by the Use Case, so we can't parse it directly by .toLong() method.
        val totalBiayaMarketing = getTotalBiayaMarketing()
        val totalUangMasukTerakhir = NumberUtil.formatStringToLong(lastTotalUangMasuk)
        val parsedBiayaMarketer = NumberUtil.formatStringToLong(biayaMarketer)

        return totalUangMasukTerakhir - parsedBiayaMarketer - totalBiayaMarketing
    }
}