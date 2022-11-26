package net.bagusekasaputra.griyakampoengtkw.presentation.util

import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

object UiUtils {

    fun hideExtendedFabOnVerticalScroll(nestedScrollView: NestedScrollView?, extendedFabs: ExtendedFloatingActionButton?) {
        nestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener
            { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY)
                    extendedFabs?.hide()
                else
                    extendedFabs?.show()
            }
        )
    }
}