package net.bagusekasaputra.griyakampoengtkw.presentation.util.exporter

import android.content.Context
import android.util.Log
import me.kariot.invoicegenerator.data.ModelInvoiceHeader
import me.kariot.invoicegenerator.data.ModelInvoiceInfo
import me.kariot.invoicegenerator.data.ModelInvoiceItem
import me.kariot.invoicegenerator.data.ModelInvoicePriceInfo
import me.kariot.invoicegenerator.data.ModelTableHeader
import me.kariot.invoicegenerator.utils.InvoiceGenerator
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import java.util.Calendar

class InvoicePdfDataExporter(
    private val context: Context,
): DataExporter {

    private lateinit var pdfGenerator: InvoiceGenerator

    override fun exportPembayaran(
        kavlingKode: String,
        dataDiri: DataDiri,
        hargaKavling: HargaKavling,
        pembayarans: List<Pembayaran>
    ) {
        pdfGenerator = InvoiceGenerator(context).apply {
            setCurrency("Rp.")
            setInvoiceHeaderData(getInvoiceHeader())
            setInvoiceInfo(getInvoiceInfo())
            setInvoiceTableHeaderDataSource(getTableHeader())
            setInvoiceTableData(getTableData())
            setPriceInfoData(getPriceInfo())
        }

        val fileUri = pdfGenerator.generatePDF("pembayaran_$kavlingKode.pdf")
        Log.d("DEBUG_ME", "DataExporter: Saving data pembayaran $kavlingKode to $fileUri")
    }

    private fun getInvoiceHeader(): ModelInvoiceHeader {
        val invoiceAddress = ModelInvoiceHeader.ModelAddress(
            "Address Line 1",
            "Address Line 2",
            "Address Line 3",
        )

        return ModelInvoiceHeader(
            "(123) 456-7890",
            "griyakampoengtkw@gmail.com",
            "http://<url>",
            address = invoiceAddress,
        )
    }

    private fun getInvoiceInfo(): ModelInvoiceInfo {
        val costumerInfo = ModelInvoiceInfo.ModelCustomerInfo(
            "Bagus Eka Saputra",
            "Address Line 1",
            "Address Line 2",
            ""
        )
        return ModelInvoiceInfo(
            costumerInfo,
            "000",
            Calendar.getInstance().time.toSlashedDate(),
            "1,000,000",
        )
    }

    private fun getTableHeader(): ModelTableHeader {
        return ModelTableHeader()
    }

    private fun getTableData(): List<ModelInvoiceItem> {
        val data = mutableListOf<ModelInvoiceItem>()
        repeat(100) {
            data.add(ModelInvoiceItem(
                "Item 1",
                "Item 1 Desc",
                "Item 2",
                "Item 3",
                "Item 4",
                "Item 5",
            ))
        }

        return data
    }

    private fun getPriceInfo(): ModelInvoicePriceInfo {
        return ModelInvoicePriceInfo(
            "Subtotal",
            "taxTotal",
            "invoiceTotal",
        )
    }


}
