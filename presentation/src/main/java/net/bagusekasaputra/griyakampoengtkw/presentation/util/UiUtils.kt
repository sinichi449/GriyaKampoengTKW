package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
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

    fun scrollToView(scrollViewParent: NestedScrollView, view: View) {
        val childOffset = Point()
        getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)

        scrollViewParent.smoothScrollTo(0, childOffset.y)
    }

    private fun getDeepChildOffset(mainParent: ViewGroup, parent: ViewParent, child: View, accumulatedOffset: Point) {
        val parentGroup = parent as ViewGroup
        accumulatedOffset.x += child.left
        accumulatedOffset.y += child.top
        if (parentGroup == mainParent) {
            return
        }
        getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
    }
}