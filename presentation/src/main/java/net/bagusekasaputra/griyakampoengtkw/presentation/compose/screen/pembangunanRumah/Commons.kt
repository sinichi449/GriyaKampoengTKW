package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.pembangunanRumah

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.model.MaterialPembangunanUiModel
import kotlin.random.Random

@Composable
fun TitleAndTotal(
    modifier: Modifier = Modifier,
    title: String,
    total: Long,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = if (total == 0L) "Tidak ada data" else "Rp. ${total.numericToString()}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DataPembangunanRumahLayout(
    modifier: Modifier = Modifier,
    title: String,
    total: Long,
    tableView: @Composable () -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        TitleAndTotal(title = "Rincian Material", total = total)
        Spacer(modifier = Modifier.height(16.dp))
        tableView()
    }
}

@Preview(showBackground = true, group = "sub-components")
@Composable
private fun TitleAndTotalPreview() {
    TitleAndTotal(
        title = "Biaya Material",
        total = 28_335_000L,
    )
}

object PembangunanRumahPreviewParams {

    fun materialPembangunan(size: Int): List<MaterialPembangunanUiModel> {
        return buildList {
            repeat(size) {
                val randomQty = Random.nextInt(from = 1, until = 100)
                val randomHarga = Random.nextLong(from = 1, until = 1000) * 1000L

                add(
                    MaterialPembangunanUiModel(
                    nama = "Material $it",
                    qty = randomQty.toDouble(),
                    satuan = "unit",
                    totalHarga = randomHarga,
                )
                )
            }
        }
    }

}