package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri

@Composable
fun DataDiriDetailColumn() {

}

@Composable
fun DataDiriDetailCard(
    modifier: Modifier = Modifier,
    dataDiri: DataDiri = DataDiri.EMPTY(),
) {
    val dataDiriRows = listOf(
        Pair("Nama", dataDiri.nama),
        Pair("Jenis Identitas", dataDiri.jenisIdentitas),
        Pair("No. Identitas", dataDiri.noIdentitas),
        Pair("Negara Bekerja", dataDiri.negaraBekerja),
        Pair("Alamat Bekerja", dataDiri.alamatKerja),
        Pair("Alamat Indo", dataDiri.alamatIndo),
        Pair("No. Hp", dataDiri.noHp),
    )

    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) { 
        dataDiriRows.forEachIndexed { index, row ->
            val evenIndex = index % 2 == 0

            ItemDataDiriRow(
                header = row.first,
                content = row.second,
                isRowNama = index == 0,
                isEvenIndexed = evenIndex,
            )
        }
    }
}

@Composable
fun ItemDataDiriRow(
    modifier: Modifier = Modifier,
    header: String = "Header",
    content: String = "Content",
    isRowNama: Boolean = false, // If true, then use labelMedium, else use bodyMedium style.
    isEvenIndexed: Boolean = false,
) {
    val oddBackground = MaterialTheme.colorScheme.surfaceVariant
    val evenBackground = MaterialTheme.colorScheme.surface

    ConstraintLayout(modifier = Modifier
        .background(if (isEvenIndexed) evenBackground else oddBackground)
        .fillMaxWidth()
        .then(modifier)) {
        val (titleRow, contentRow) = createRefs()
        val rowPadding = 8.dp

        Text(
            text = header,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(titleRow) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(contentRow.start)
                    width = Dimension.fillToConstraints
                }
                .padding(rowPadding),
        )
        Text(
            text = content,
            style = if (isRowNama) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.bodyMedium
            },
            modifier = Modifier
                .constrainAs(contentRow) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(titleRow.end)
                    width = Dimension.fillToConstraints
                }
                .padding(rowPadding),
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true, group = "components")
@Composable
fun ItemDataDiriRowPreview() {
    ItemDataDiriRow(isEvenIndexed = false)
}

@Preview(showBackground = true, group = "components")
@Composable
fun DataDiriDetailCardPreview() {
    DataDiriDetailCard(modifier = Modifier.padding(16.dp))
}