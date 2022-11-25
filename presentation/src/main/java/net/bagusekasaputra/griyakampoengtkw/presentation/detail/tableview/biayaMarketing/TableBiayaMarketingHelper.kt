package net.bagusekasaputra.griyakampoengtkw.presentation.detail.tableview.biayaMarketing

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing

class TableBiayaMarketingHelper(
    private val listBiayaMarketing: List<BiayaMarketing>?,
) {

    fun getBiayaMarketingColumnHeaders(): ArrayList<String> {
        return ArrayList<String>().apply {
            add("Jenis Biaya")
            add("Harga")
            add("Tanggal")
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

                secondOrderItemList.add(biayaMarketing.getTanggalStr())

                firstOrderItemList.add(secondOrderItemList)
            }
        } else {
            val secondOrderItemList = ArrayList<String>()

            secondOrderItemList.apply {
                add("-")
                add("-")
                add("-")
            }

            firstOrderItemList.add(secondOrderItemList)
        }

        return firstOrderItemList
    }

}