package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.InformasiPembangunan
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme

/**
 * Biaya Pembangunan Main Screen
 */
@Composable
fun BiayaPembangunanKavlingScreen(
    modifier: Modifier = Modifier,
    informasiPembangunan: InformasiPembangunan = InformasiPembangunan.EMPTY("D1"),
    upahPekerjaTableView: @Composable () -> Unit,
    materialPembangunanTableView: @Composable () -> Unit,
) {
    var isExpandedCardPembangunan by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        InformasiPembangunanKavling(
            expanded = isExpandedCardPembangunan,
            informasiPembangunan = informasiPembangunan,
            onClick = {
                isExpandedCardPembangunan = !isExpandedCardPembangunan
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        materialPembangunanTableView()
        Spacer(modifier = Modifier.height(32.dp))
        upahPekerjaTableView()
    }
}


/**
 * Informasi Pembangunan
 */
@Composable
private fun InformasiPembangunanKavling(
    modifier: Modifier = Modifier,
    informasiPembangunan: InformasiPembangunan = InformasiPembangunan.EMPTY("D1"),
    expanded: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .clickable {
                onClick()
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Informasi Pembangunan", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp
                else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        informasiPembangunan.also {
            RowItemInformasiPembangunan(title = "Luas", text = "${it.luas} m2")
            RowItemInformasiPembangunan(title = "Progress", text = "${it.progress}%")
            RowItemInformasiPembangunan(title = "Harga Borong", text = "Rp. ${it.hargaBorong.numericToString()}")
            RowItemInformasiPembangunan(title = "Retensi", text = "${it.persentaseRetensi}%")

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RowItemInformasiPembangunan(
    modifier: Modifier = Modifier,
    title: String,
    text: String
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        val (titleRef, textRef) = createRefs()

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(textRef.start)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(textRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                start.linkTo(titleRef.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
private fun AddendumPembangunanItems(
    modifier: Modifier = Modifier,
    addendum: List<InformasiPembangunan.Addendum> = emptyList(),
) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        items(addendum) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (titleRef, contentRef) = createRefs()

                Text(
                    text = it.keterangan,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(contentRef.start)
                        width = Dimension.fillToConstraints
                    }
                )
                Text(
                    text = "Rp. ${it.jumlahUang.numericToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.constrainAs(contentRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        start.linkTo(titleRef.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }
        }
    }
}

/**
 * Previews
 */
@Preview(showBackground = true, showSystemUi = true, group = "layout")
@Composable
private fun BiayaPembangunanKavlingScreenPreview() {
    GriyaKampoengTkwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BiayaPembangunanKavlingScreen(
                modifier = Modifier.padding(16.dp),
                upahPekerjaTableView = {},
                materialPembangunanTableView = {},
            )
        }
    }
}

@Preview(showBackground = true, group = "components")
@Composable
private fun CardInformasiPembangunanPreview() {
    GriyaKampoengTkwTheme {
        var expanded by remember {
            mutableStateOf(true)
        }
        InformasiPembangunanKavling(
            modifier = Modifier.padding(16.dp),
            expanded = expanded,
            onClick = {
                expanded = !expanded
            }
        )
    }
}

@Preview(showBackground = true, group = "components")
@Composable
private fun AddendumPembangunanItemsPreview(
    @PreviewParameter(AddendumPembangunanParameterProvider::class, 1)
    addendum: List<InformasiPembangunan.Addendum>
) {
    AddendumPembangunanItems(addendum = addendum)
}

private class AddendumPembangunanParameterProvider: PreviewParameterProvider<List<InformasiPembangunan.Addendum>> {
    override val values: Sequence<List<InformasiPembangunan.Addendum>>
        get() = sequenceOf(
            listOf(
                InformasiPembangunan.Addendum(1_200_000L, "Septick Tank"),
                InformasiPembangunan.Addendum(600_000L, "Urug-urug"),
                InformasiPembangunan.Addendum(1_000_000, "Pasturisasi Taman"),
            )
        )

}
