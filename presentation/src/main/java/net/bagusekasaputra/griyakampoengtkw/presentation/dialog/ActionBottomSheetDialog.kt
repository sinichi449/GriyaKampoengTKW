package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.ActionMenusDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme

abstract class ActionBottomSheetDialog: BottomSheetDialogFragment() {

    abstract val title: String
    abstract val menuDialogs: List<ActionMenusDialog>

    abstract fun onMenuItemClick(menuIndex: Int, itemIndex: Int)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                GriyaKampoengTkwTheme {
                    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        ActionMenusDialog(
                            menuDialogs = menuDialogs,
                            title = title,
                            onMenuClick = { menuIndex: Int, itemIndex: Int ->
                                onMenuItemClick(menuIndex, itemIndex)
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}