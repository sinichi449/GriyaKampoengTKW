package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.view.View
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabHelper(
    private val fabAction: ExtendedFloatingActionButton,
    private vararg val fabs: FloatingActionButton,
) {

    var isAllFabVisible = false

    fun setupFabs() {
        fabAction.shrink()

        fabs.forEach {
            it.visibility = View.GONE
        }

        fabAction.setOnClickListener {
            if (isAllFabVisible) {
                hideFabs()
            } else {
                showFabs()
            }
        }
    }

    fun hideFabs() {
        fabAction.shrink()

        fabs.forEach { it.hide() }

        isAllFabVisible = false
    }

    fun showFabs() {
        fabAction.extend()

        fabs.forEach { it.show() }

        isAllFabVisible = true
    }

    fun fabOnfflineState() {
        hideFabs()
        fabAction.hide()
    }
}