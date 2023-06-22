package net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import net.bagusekasaputra.griyakampoengtkw.presentation.R

interface ActionMenusDialog {
    data class Menu(
        @DrawableRes val icon: Int,
        val menuTitle: String,
    )

    val header: String
    val menuItems: List<Menu>
}

@Composable
fun ActionMenusDialog(
    modifier: Modifier = Modifier,
    title: String = "Kav. A11 - ITJ 1",
    menuDialogs: List<ActionMenusDialog>,
    onMenuClick: (menuIndex: Int, itemIndex: Int) -> Unit = { _, _ ->},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(menuDialogs) { menuIndex, menu ->
                Text(
                    text = menu.header,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                menu.menuItems.forEachIndexed { itemIndex, item ->
                    ActionMenuRow(
                        modifier = Modifier,
                        icon = item.icon,
                        menuTitle = item.menuTitle,
                        onMenuClick = { onMenuClick(menuIndex, itemIndex) },
                        isInProgress = false,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionMenuRow(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.ic_baseline_camera_alt_24,
    menuTitle: String = "Tambahkan Foto",
    onMenuClick: () -> Unit = {},
    isInProgress: Boolean = false
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onMenuClick
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier),
        ) {
            val (iconAndTitleRef, loadingRef) = createRefs()
            val margin = 16.dp

            // Icon And Title Menu
            Row(
                modifier = Modifier.constrainAs(iconAndTitleRef) {
                    top.linkTo(parent.top, margin)
                    bottom.linkTo(parent.bottom, margin)
                    start.linkTo(parent.start, margin)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = menuTitle,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // Loading Animation
            if (isInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .constrainAs(loadingRef) {
                            top.linkTo(parent.top, margin)
                            bottom.linkTo(parent.bottom, margin)
                            end.linkTo(parent.end, margin)
                        }
                        .size(24.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, group = "layout")
@Composable
fun ActionMenusDialogPreview(
    @PreviewParameter(MenuDialogParameterProvider::class, limit = 1)
    menuDialogs: List<ActionMenusDialog>
) {
    ActionMenusDialog(menuDialogs = menuDialogs)
}

@Preview(showBackground = true, group = "components")
@Composable
fun ActionMenuRowPreview() {
    ActionMenuRow(isInProgress = true)
}

class MenuDialogParameterProvider : PreviewParameterProvider<List<ActionMenusDialog>> {
    override val values: Sequence<List<ActionMenusDialog>>
        get() =  sequenceOf(buildList {
            val fotoPembayaranMenus = buildList {
                add(ActionMenusDialog.Menu(R.drawable.ic_baseline_camera_alt_24, "Tambahkan"))
                add(ActionMenusDialog.Menu(R.drawable.baseline_check_24, "Lihat"))
                add(ActionMenusDialog.Menu(R.drawable.baseline_delete_24, "Hapus"))
            }
            add(object : ActionMenusDialog {
                override val header: String
                    get() = "Foto Pembayaran"
                override val menuItems: List<ActionMenusDialog.Menu>
                    get() = fotoPembayaranMenus
            })

            val dataPembayaranMenus = buildList {
                add(ActionMenusDialog.Menu(R.drawable.ic_baseline_add_24, "Tambahkan"))
                add(ActionMenusDialog.Menu(R.drawable.ic_baseline_edit_24, "Ubah"))
                add(ActionMenusDialog.Menu(R.drawable.baseline_delete_24, "Hapus"))
            }

            add(object : ActionMenusDialog {
                override val header: String
                    get() = "Data Pembayaran"
                override val menuItems: List<ActionMenusDialog.Menu>
                    get() = dataPembayaranMenus
            })
        })
}