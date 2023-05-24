package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity

object UiUtils {

    // Method helper for extended floating button on extended or shrink events
    fun extendOrShrinkExtendedFab(extendedFabs: ExtendedFloatingActionButton, anotherFabs: List<FloatingActionButton>, extend: Boolean) {
        if (extend) {
            extendedFabs.extend()
        } else {
            extendedFabs.shrink()
        }

        anotherFabs.forEach { fab ->
            if (extend) fab.show()
            else fab.hide()
        }
    }

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

    fun openWhatsapp(context: Context, phoneNumber: String) {
        val mPhoneNumber = phoneNumber.replace(" ", "") // Remove spaces
        val url = "https://api.whatsapp.com/send?phone=$mPhoneNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun <T> openFotoFull(context: Context, imageTransport: ImageTransport<T>) {
        val fullImageIntent = Intent(context, FullImageActivity::class.java)
        fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)
        context.startActivity(fullImageIntent)
    }
}